package br.com.drss.pokedex.home.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.drss.pokedex.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.home.repository.domain.entities.PokemonType
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonSummaryDao {

    @Query("SELECT * FROM PokemonSummary")
    fun getAllSummaries(): Flow<List<PokemonSummary>>

    @Query("SELECT * FROM PokemonSummary WHERE types IN (:filter)")
    fun getTypeFilteredSummaries(filter: List<PokemonType>): Flow<List<PokemonSummary>>

    @Query("SELECT * FROM PokemonSummary WHERE name = :name")
    suspend fun findByName(name: String): PokemonSummary?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonSummary(pokemonSummary: PokemonSummary)
}