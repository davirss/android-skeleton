package br.com.drss.pokedex.home.repo

import br.com.drss.pokedex.data.bulbasaur_summary
import br.com.drss.pokedex.data.pokemonList
import br.com.drss.pokedex.home.repository.PokemonRepositoryImpl
import br.com.drss.pokedex.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.home.repository.domain.entities.PokemonType
import br.com.drss.pokedex.home.repository.network.PokeApi
import br.com.drss.pokedex.home.repository.network.dto.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class PokemonRepositoryTest {

    class FakePokeApi(private val availableData: List<PokemonDto> = pokemonList) : PokeApi {

        override suspend fun getPokemonPagedList(): PagedListResponse<PagedPokemonDto> {
            return PagedListResponse(
                count = availableData.size,
                results = availableData.map { PagedPokemonDto(it.name) }
            )
        }

        override suspend fun getPokemonData(name: String): PokemonDto =
            availableData.find { it.name == name }!!
    }

    @Test
    fun `Given the Repository is just initialized Then it should not fetch any data`() = runBlocking {
        val inMemorySummaryDao = InMemorySummaryDao()

        PokemonRepositoryImpl(inMemorySummaryDao, FakePokeApi(pokemonList))

        inMemorySummaryDao.getAllSummaries().collect {
            assertTrue(it.isEmpty())
        }
    }

    @Test
    fun `Given the PokemonSummary data is not cached When I request the information, Then I should request data from the API And I should have the data stored locally`(): Unit =
        runBlocking {
            val fakeDao = InMemorySummaryDao()

            val pokemonRepository = PokemonRepositoryImpl(fakeDao, FakePokeApi(pokemonList))

            fakeDao.getAllSummaries().collect {
                assertTrue(it.isEmpty())
            }

            val pokemonSummaryList = pokemonRepository.getPokemonSummaryList().toList()

            val expectedPokemonSummary = PokemonSummary(
                "bulbasaur",
                1,
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
                listOf(PokemonType("grass"),PokemonType("poison"))
            )
            assertEquals(pokemonSummaryList[0].first(), expectedPokemonSummary)
        }

    @Test
    fun `Given the PokemonSummary is cached When I request the information Then the API should not be called`(): Unit =
        runBlocking {
            val fakeDao = InMemorySummaryDao()
            fakeDao.insertPokemonSummary(bulbasaur_summary)

            val exceptionRaiserApi = object : PokeApi {
                override suspend fun getPokemonPagedList(): PagedListResponse<PagedPokemonDto> {
                    throw IllegalAccessException()
                }

                override suspend fun getPokemonData(name: String): PokemonDto {
                    throw IllegalAccessException()
                }
            }

            val pokemonRepository = PokemonRepositoryImpl(fakeDao, exceptionRaiserApi)
            val pokemonSummaryList = pokemonRepository.getPokemonSummaryList().toList()

            assertEquals(pokemonSummaryList[0].first(), bulbasaur_summary)
        }

    @Test
    fun `Given I have the grass filter selected, When I get the data Then I should see only the filtered results`(): Unit =
        runBlocking {
            val poisonPokemonType = PokemonType("poison")

            val pokemonRepository = PokemonRepositoryImpl(InMemorySummaryDao(), FakePokeApi(pokemonList))
            val pokemonSummaryList = pokemonRepository.getPokemonSummaryList(listOf(poisonPokemonType)).toList()

            val results = pokemonSummaryList[0].filter { !it.types.contains(PokemonType("poison")) }
            assertTrue(results.isEmpty())
        }
}



