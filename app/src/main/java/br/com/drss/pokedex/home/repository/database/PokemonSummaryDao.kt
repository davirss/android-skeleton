package br.com.drss.pokedex.home.repository.database

import br.com.drss.pokedex.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.home.repository.domain.entities.PokemonType
import kotlinx.coroutines.flow.Flow

interface PokemonSummaryDao {

    fun getAllSummaries(): Flow<List<PokemonSummary>>
    fun getTypeFilteredSummaries(filter: List<PokemonType>): Flow<List<PokemonSummary>>
    suspend fun findByName(name: String): PokemonSummary?
    suspend fun insertPokemonSummary(pokemonSummary: PokemonSummary)
}
