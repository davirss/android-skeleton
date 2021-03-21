package br.com.drss.pokedex.features.details.repo

import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType
import br.com.drss.pokedex.network.PokeApi

interface DetailsRepo {

    suspend fun getPokemonDetail(name: String): PokemonDetail
}

class RemoteDetailsRepo(private val pokeApi: PokeApi): DetailsRepo {

    override suspend fun getPokemonDetail(name: String): PokemonDetail {
        val pokemon = pokeApi.getPokemonData(name)
        return PokemonDetail(
            pokemon.id,
            pokemon.name,
            "",
            pokemon.sprites.other?.officialArtwork?.frontDefault ?: "",
            pokemon.sprites.frontDefault,
            pokemon.types.map { PokemonType(it.type.name) },
            pokemon.stats.map { Stat(it.stat.name, it.base_stat) }
        )
    }

}

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