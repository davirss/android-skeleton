package br.com.drss.pokedex.features.home.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.drss.pokedex.features.home.repository.domain.entities.Converter
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType

@Database(entities = [PokemonSummary::class, PokemonType::class], version = 1)
@TypeConverters(Converter::class)
abstract class Database: RoomDatabase() {

    abstract fun summaryDao(): PokemonSummaryDao

}