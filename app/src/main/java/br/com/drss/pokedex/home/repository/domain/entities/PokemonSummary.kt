package br.com.drss.pokedex.home.repository.domain.entities

data class PokemonSummary(
    val name: String,
    val number: Int,
    val types: List<PokemonType>,
    val artwork: String
)

data class PokemonType(val name: String)

data class PokemonTypeFilter(val pokemonType: PokemonType, val selected: Boolean)