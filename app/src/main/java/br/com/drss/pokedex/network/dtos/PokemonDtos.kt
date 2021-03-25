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
    val stats: List<StatDto>
)

@Serializable
data class StatDto(
    val base_stat: Int,
    val stat: StatType
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
    val other: OtherSprites? = null
)

@Serializable
data class OtherSprites(
    @SerialName("official-artwork") val officialArtwork: Sprites?
)

@Serializable
data class Species(
    @SerialName("flavor_text_entries") val flavorTextEntries: List<FlavorText>
)

@Serializable
data class FlavorText(
    @SerialName("flavor_text") val flavorText: String
)