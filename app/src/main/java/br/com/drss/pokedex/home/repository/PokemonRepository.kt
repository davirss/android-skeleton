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
    suspend fun getPokemonSummary(name: String): PokemonSummary

}

class PokemonRepositoryImpl(
    private val pokemonSummaryDao: PokemonSummaryDao,
    private val pokemonApi: PokeApi
) : PokemonRepository {

    /**
     *
     */
    @ExperimentalCoroutinesApi
    override fun getPokemonSummaryList(typeFilters: List<PokemonType>): Flow<List<PokemonSummary>> =
        channelFlow {
            val query = if (typeFilters.isNotEmpty()) pokemonSummaryDao.getTypeFilteredSummaries(typeFilters) else pokemonSummaryDao.getAllSummaries()

            query.collect {
                if (it.isEmpty()) {
                    offer(it)
                } else {
                    pokemonApi.getPokemonPagedList().results
                        .map { getPokemonSummary(it.name) }
                        .filter { it.types.intersect(typeFilters).isNotEmpty() }
                }
            }
        }

    /**
     * Returns the Pokemon Summary given the [name] provided.
     *
     * The method will first try fetching from the local database, if nothing is found
     * the search will occur on the API.
     *
     * @param [name] the name of the Pokemon.
     *
     * returns: PokemonSummary
     */
    override suspend fun getPokemonSummary(name: String): PokemonSummary {
        pokemonSummaryDao.findByName(name)?.let {
            return it
        }

        val pokemonSummary = pokemonApi.getPokemonData(name).toSummary()
        pokemonSummaryDao.insertPokemonSummary(pokemonSummary)

        return pokemonSummary
    }
}

internal fun PokemonDto.toSummary(): PokemonSummary {
    return PokemonSummary(
        this.name,
        this.id,
        this.types.map { PokemonType(it.name) },
        this.sprites.frontDefault
    )
}