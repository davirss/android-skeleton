package br.com.drss.pokedex.home.repo

import br.com.drss.pokedex.home.repository.network.PokeApi
import br.com.drss.pokedex.home.repository.network.dto.PagedListResponse
import br.com.drss.pokedex.home.repository.network.dto.PagedPokemonDto
import br.com.drss.pokedex.home.repository.network.dto.PokemonDto
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PokemonRepoTest  {

    fun `Given the PokemonSummary data is not cached When I request the information, Then I should request data from the API And I should have the data stored locally`() {

    }

    fun `Given the PokemonSummary is cached When I request the information Then the API should not be called`() {

    }
}