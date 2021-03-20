package br.com.drss.pokedex.home.repository.network.dto

data class PagedPokemonDto(val name: String)

data class PokemonDto(
    val name: String,
    val id: Int,
    val sprites: Sprites,
    val types: List<PokemonTypeDto>
)

data class PokemonTypeDto(
    val name: String
)

data class Sprites(
    val frontDefault: String
)
