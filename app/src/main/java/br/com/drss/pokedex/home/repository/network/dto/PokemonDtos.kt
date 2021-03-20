package br.com.drss.pokedex.home.repository.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedPokemonDto(val name: String)

@Serializable
data class PokemonDto(
    val name: String,
    val id: Int,
    val sprites: Sprites,
    val types: List<TypeSlot>
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
    @SerialName("front_default") val frontDefault: String
)
