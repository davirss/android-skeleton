package br.com.drss.pokedex.home.repository

import br.com.drss.pokedex.home.repository.database.PokemonSummaryDao
import br.com.drss.pokedex.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.home.repository.domain.entities.PokemonType
import br.com.drss.pokedex.home.repository.network.PokeApi
import br.com.drss.pokedex.home.repository.network.dto.PokemonDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

interface PokemonRepository {

    fun getPokemonSummaryList(typeFilters: List<PokemonType> = listOf()): Flow<List<PokemonSummary>>

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
    override fun getPokemonSummaryList(typeFilters: List<PokemonType>): Flow<List<PokemonSummary>> =
        channelFlow {
            val query =
                if (typeFilters.isNotEmpty())
                    pokemonSummaryDao.getTypeFilteredSummaries(typeFilters)
                else
                    pokemonSummaryDao.getAllSummaries()

            query.collect {
                if (it.isNotEmpty()) {
                    offer(it)
                } else {
                    pokemonApi.getPokemonPagedList().results
                        .map { getPokemonSummary(it.name, pokemonSummaryDao, pokemonApi) }
                        .filter { typeFilters.isEmpty() || it.types.intersect(typeFilters).isNotEmpty() }
                        .run { offer(this) }
                }
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
    private suspend fun getPokemonSummary(name: String, localSource: PokemonSummaryDao, remoteSource: PokeApi): PokemonSummary {
        localSource.findByName(name)?.let {
            return it
        }

        val pokemonSummary = remoteSource.getPokemonData(name).toSummary()
        localSource.insertPokemonSummary(pokemonSummary)

        return pokemonSummary
    }
}

internal fun PokemonDto.toSummary(): PokemonSummary {
    return PokemonSummary(
        this.name,
        this.id,
        this.sprites.frontDefault,
        this.types.map { PokemonType(it.name) }
    )
}