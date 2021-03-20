package br.com.drss.pokedex.home.repository.domain.entities

import androidx.room.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(indices = [Index(value = arrayOf("number"), unique = true)])
data class PokemonSummary(
    @PrimaryKey val name: String,
    val number: Int,
    val artwork: String,
    val types: List<PokemonType>
)

@Entity
data class PokemonType(@PrimaryKey val name: String)

data class PokemonTypeFilter(val pokemonType: PokemonType, val selected: Boolean)

class Converter {

    @TypeConverter
    fun fromPokemonTypeListToString(value: List<PokemonType>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun fromStringToPokemonTypeList(value: String): List<PokemonType> {
        return Json.decodeFromString(value)
    }

}