package br.com.drss.pokedex.features.home.repository

import android.util.Log
import br.com.drss.pokedex.features.home.repository.database.PokemonSummaryDao
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType
import br.com.drss.pokedex.network.PokeApi
import br.com.drss.pokedex.network.dtos.PokemonDto
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.Exception

interface PokemonRepository {

    fun getPokemonSummaryList(typeFilters: List<PokemonType> = listOf()): Flow<OperationStatus<List<PokemonSummary>>>

}

class PokemonRepositoryImpl(
    private val pokemonSummaryDao: PokemonSummaryDao,
    private val pokemonApi: PokeApi,
    private val coroutineScope: CoroutineScope,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PokemonRepository {

    /**
     * Returns a flow containing the summary list given the list of filters provided. If no filter was provided, the
     * data will be returned in it's entirety.
     *
     * @return [Flow] that emits a [OperationStatus] with a [List] of [PokemonSummary].
     */
    @ExperimentalCoroutinesApi
    override fun getPokemonSummaryList(typeFilters: List<PokemonType>): Flow<OperationStatus<List<PokemonSummary>>> =
        flow {
            val query =
                if (typeFilters.isNotEmpty())
                    pokemonSummaryDao.getTypeFilteredSummaries(typeFilters)
                else
                    pokemonSummaryDao.getAllSummaries()

            query
                .combine(fetchMissingPokemon(pokemonApi)) { bdList, cacheRefreshingStatus ->
                    if (cacheRefreshingStatus is OperationStatus.Error) {
                        emit(cacheRefreshingStatus)
                    }
                    if (cacheRefreshingStatus is OperationStatus.Loading)
                        OperationStatus.Loading(bdList)
                    else
                        OperationStatus.Loaded(bdList)
                }
                .collect { list ->
                    emit(list)
                }
        }.flowOn(coroutineDispatcher)


    /**
     * Updates the local pokemon data source based on the list returned by the remote.
     * Returns a flow containing the status of the operation.
     *
     * @param [remoteSource] the API that the pokemon list will be queried.
     * @return [Flow] of [OperationStatus] without any data, only the state of the operation.
     */
    private fun fetchMissingPokemon(remoteSource: PokeApi): Flow<OperationStatus<*>> = flow<OperationStatus<Any>> {
        val pokemonList = remoteSource.getPokemonPagedList()
        pokemonList.results.forEach {
            try {
                getPokemonSummary(it.name, pokemonSummaryDao, remoteSource)
            } catch (e: Exception) {
                Log.d("SUMMARY", "Failed to fetch pokemon")
            }
        }
        return@flow
    }.onStart {
        emit(OperationStatus.Loading(Any()))
    }.onCompletion {
        emit(OperationStatus.Loaded(Any()))
    }.catch { cause ->
        emit(OperationStatus.Error(cause as Exception))
    }


    /**
     * Returns the Pokemon Summary given the [name] provided.
     *
     * The method will first try fetching from the [localSource], if nothing is found
     * the search will occur on the [remoteSource].
     *
     * @param [name] the name of the Pokemon.
     * @param [localSource] the local DAO in which the cache will be checked.
     * @param [remoteSource] the remote Repository for the data to be fetched.
     *
     * @return [PokemonSummary]
     */
    private suspend fun getPokemonSummary(
        name: String,
        localSource: PokemonSummaryDao,
        remoteSource: PokeApi
    ): PokemonSummary {
        localSource.findByName(name)?.let {
            return it
        }

        val pokemonSummary = remoteSource.getPokemonData(name).toSummary()
        localSource.insertPokemonSummary(pokemonSummary)

        return pokemonSummary
    }
}

sealed class OperationStatus<out T> {
    data class Loading<T>(val intermediaryData: T) : OperationStatus<T>()
    data class Loaded<T>(val finalData: T) : OperationStatus<T>()
    data class Error(val exception: Exception) : OperationStatus<Nothing>()
}

internal fun PokemonDto.toSummary(): PokemonSummary {
    return PokemonSummary(
        this.name,
        this.id,
        this.sprites.frontDefault,
        this.types.map { PokemonType(it.type.name) }
    )
}