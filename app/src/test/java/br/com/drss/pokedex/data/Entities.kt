package br.com.drss.pokedex.data

import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType

val bulbasaur_summary = PokemonSummary(
    "bulbasaur",
    1,
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
    listOf(PokemonType("grass"), PokemonType("poison"))
)