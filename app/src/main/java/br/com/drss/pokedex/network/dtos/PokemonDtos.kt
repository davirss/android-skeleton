package br.com.drss.pokedex.network.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedPokemonDto(val name: String)

@Serializable
data class PokemonDto(
    val name: String,
    val id: Int,
    val sprites: Sprites,
    val types: List<TypeSlot>,
    val states: List<StatDto>
)

@Serializable
data class StatDto(
    val base_state: Int,
    val name: StatType
)

@Serializable
data class StatType(
    val name: String
)

@Serializable
data class TypeSlot(
    val slot: Int,
    val type: PokemonTypeDto
)

@Serializable
data class PokemonTypeDto(
    val name: String
)

@Serializable
data class Sprites(
    @SerialName("front_default") val frontDefault: String,
    val other: OtherSprites
)

@Serializable
data class OtherSprites(
    @SerialName("official_artwork") val officialArtowkr: Sprites
)
