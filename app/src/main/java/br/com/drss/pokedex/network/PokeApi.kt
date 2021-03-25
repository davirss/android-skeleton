package br.com.drss.pokedex.network

import br.com.drss.pokedex.network.dtos.PagedListResponse
import br.com.drss.pokedex.network.dtos.PagedPokemonDto
import br.com.drss.pokedex.network.dtos.PokemonDto
import br.com.drss.pokedex.network.dtos.Species
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {

    @GET("v2/pokemon?limit=1000")
    suspend fun getPokemonPagedList(): PagedListResponse<PagedPokemonDto>

    @GET("v2/pokemon/{name}")
    suspend fun getPokemonData(@Path("name") name: String): PokemonDto

    @GET("v2/pokemon-species/{name}")
    suspend fun getPokemonSpecies(@Path("name") name: String): Species
}