package br.com.drss.pokedex.features.details.repo.entities

import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType

data class Stat(
    val name: String,
    val value: Int
)

data class PokemonDetail(
    val number: Int,
    val name: String,
    val description: String,
    val artwork: String,
    val frontSprite: String,
    val types: List<PokemonType>,
    val states: List<Stat>
)