package br.com.drss.pokedex.data

import br.com.drss.pokedex.features.home.repository.network.dto.PokemonDto
import br.com.drss.pokedex.features.home.repository.network.dto.PokemonTypeDto
import br.com.drss.pokedex.features.home.repository.network.dto.Sprites
import br.com.drss.pokedex.features.home.repository.network.dto.TypeSlot

val pokemonList = listOf<PokemonDto>(
    PokemonDto(
        "bulbasaur",
        1,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"),
        listOf(PokemonTypeDto("grass"), PokemonTypeDto("poison")).toSlotList()
    ),
    PokemonDto(
        "charmander",
        4,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"),
        listOf(PokemonTypeDto("fire")).toSlotList()
    ),
    PokemonDto(
        "squirtle",
        7,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png"),
        listOf(PokemonTypeDto("water")).toSlotList()
    ),
    PokemonDto(
        "pikachu",
        25,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"),
        listOf(PokemonTypeDto("electric")).toSlotList()
    )
)

internal fun List<PokemonTypeDto>.toSlotList(): List<TypeSlot> {
    return  mapIndexed { index, type ->
        TypeSlot(index, type)
    }
}