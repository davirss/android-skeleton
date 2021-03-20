package br.com.drss.pokedex.home.repository.network

import br.com.drss.pokedex.home.repository.network.dto.PagedListResponse
import br.com.drss.pokedex.home.repository.network.dto.PagedPokemonDto
import br.com.drss.pokedex.home.repository.network.dto.PokemonDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {

    @GET("api/v2/pokemon")
    suspend fun getPokemonPagedList(): PagedListResponse<PagedPokemonDto>

    @GET("api/v2/pokemon/{name}")
    suspend fun getPokemonData(@Path("name") name: String): PokemonDto
}