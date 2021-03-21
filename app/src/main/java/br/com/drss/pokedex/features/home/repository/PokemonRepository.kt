package br.com.drss.pokedex.features.home.repository

import br.com.drss.pokedex.features.home.repository.database.PokemonSummaryDao
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType
import br.com.drss.pokedex.network.PokeApi
import br.com.drss.pokedex.features.home.repository.network.dto.PokemonDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

interface PokemonRepository {

    fun getPokemonSummaryList(typeFilters: List<PokemonType> = listOf()): Flow<OperationStatus<List<PokemonSummary>>>

}

class PokemonRepositoryImpl(
    private val pokemonSummaryDao: PokemonSummaryDao,
    private val pokemonApi: PokeApi
) : PokemonRepository {

    /**
     * Returns a flow containing the summary list given the list of filters provided. If no filter was provided, the
     * data will be returned in it's entirety.
     *
     * @return [Flow] containing the [List] of [PokemonSummary].
     */
    @ExperimentalCoroutinesApi
    override fun getPokemonSummaryList(typeFilters: List<PokemonType>): Flow<OperationStatus<List<PokemonSummary>>> =
        flow {
            val query =
                if (typeFilters.isNotEmpty())
                    pokemonSummaryDao.getTypeFilteredSummaries(typeFilters)
                else
                    pokemonSummaryDao.getAllSummaries()

            query.collect { list ->
                val intermediaryList = list.toMutableList()
                if (list.isEmpty())
                    pokemonApi.getPokemonPagedList().results
                        .map {
                            val summary = getPokemonSummary(it.name, pokemonSummaryDao, pokemonApi)
                            if (typeFilters.isEmpty() || summary.types.intersect(typeFilters).isNotEmpty()) {
                                intermediaryList.add(summary)
                                emit(Loading(intermediaryList.toList()))
                            }
                        }

                emit(Loaded(intermediaryList.toList()))
            }
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

sealed class OperationStatus<T>(val data: T)
data class Loading<T>(val intermediaryData: T): OperationStatus<T>(intermediaryData)
data class Loaded<T>(val finalData: T): OperationStatus<T>(finalData)

internal fun PokemonDto.toSummary(): PokemonSummary {
    return PokemonSummary(
        this.name,
        this.id,
        this.sprites.frontDefault,
        this.types.map { PokemonType(it.type.name) }
    )
}