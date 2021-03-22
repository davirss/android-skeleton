package br.com.drss.pokedex.features.home.repo

import br.com.drss.pokedex.features.home.repository.database.PokemonSummaryDao
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InMemorySummaryDao : PokemonSummaryDao {

    private val inMemoryDb = mutableListOf<PokemonSummary>()

    override fun getAllSummaries(): Flow<List<PokemonSummary>> = flow {
        emit(inMemoryDb)
    }

    override fun getTypeFilteredSummaries(filter: List<PokemonType>): Flow<List<PokemonSummary>> = flow {
        emit(inMemoryDb.filter { it.types.intersect(filter).isNotEmpty() })
    }

    override suspend fun findByName(name: String): PokemonSummary? {
        return inMemoryDb.find { it.name == name }
    }

    override suspend fun insertPokemonSummary(pokemonSummary: PokemonSummary) {
        inMemoryDb.add(pokemonSummary)
    }
}