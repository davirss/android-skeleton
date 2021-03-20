package br.com.drss.pokedex.data

import br.com.drss.pokedex.home.repository.network.dto.PokemonDto
import br.com.drss.pokedex.home.repository.network.dto.PokemonTypeDto
import br.com.drss.pokedex.home.repository.network.dto.Sprites

val pokemonList = listOf<PokemonDto>(
    PokemonDto(
        "bulbasaur",
        1,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"),
        listOf(PokemonTypeDto("grass"), PokemonTypeDto("poison"))
    ),
    PokemonDto(
        "charmander",
        4,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"),
        listOf(PokemonTypeDto("fire"))
    ),
    PokemonDto(
        "squirtle",
        7,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png"),
        listOf(PokemonTypeDto("water"))
    ),
    PokemonDto(
        "pikachu",
        25,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"),
        listOf(PokemonTypeDto("electric"))
    )
)