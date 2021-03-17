package br.com.drss.pokedex.home.repository.network

import br.com.drss.pokedex.home.repository.network.dto.BaseApiListResponse
import br.com.drss.pokedex.home.repository.network.dto.PagedPokemonDto
import retrofit2.http.GET

interface PokeApi {

    @GET("api/v2/pokemon")
    suspend fun getPokemons(): BaseApiListResponse<PagedPokemonDto>
}