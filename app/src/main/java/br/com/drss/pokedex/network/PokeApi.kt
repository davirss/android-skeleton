package br.com.drss.pokedex.network

import br.com.drss.pokedex.network.dtos.PagedListResponse
import br.com.drss.pokedex.network.dtos.PagedPokemonDto
import br.com.drss.pokedex.network.dtos.PokemonDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {

    @GET("v2/pokemon?limit=1000")
    suspend fun getPokemonPagedList(): PagedListResponse<PagedPokemonDto>

    @GET("v2/pokemon/{name}")
    suspend fun getPokemonData(@Path("name") name: String): PokemonDto
}