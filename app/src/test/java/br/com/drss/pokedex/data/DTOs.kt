package br.com.drss.pokedex.data

import br.com.drss.pokedex.network.dtos.*


val pokemonList = listOf<PokemonDto>(
    PokemonDto(
        "bulbasaur",
        1,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"),
        listOf(PokemonTypeDto("grass"), PokemonTypeDto("poison")).toSlotList(),
        listOf(StatDto(10, StatType("HP")))
    ),
    PokemonDto(
        "charmander",
        4,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"),
        listOf(PokemonTypeDto("fire")).toSlotList(),
        listOf(StatDto(10, StatType("HP")))
    ),
    PokemonDto(
        "squirtle",
        7,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png"),
        listOf(PokemonTypeDto("water")).toSlotList(),
        listOf(StatDto(10, StatType("HP")))
    ),
    PokemonDto(
        "pikachu",
        25,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"),
        listOf(PokemonTypeDto("electric")).toSlotList(),
        listOf(StatDto(10, StatType("HP")))
    )
)

internal fun List<PokemonTypeDto>.toSlotList(): List<TypeSlot> {
    return  mapIndexed { index, type ->
        TypeSlot(index, type)
    }
}