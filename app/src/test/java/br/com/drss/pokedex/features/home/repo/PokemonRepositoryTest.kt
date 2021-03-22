package br.com.drss.pokedex.features.home.repo

import br.com.drss.pokedex.data.bulbasaur_summary
import br.com.drss.pokedex.data.pokemonList
import br.com.drss.pokedex.features.home.repository.Loaded
import br.com.drss.pokedex.features.home.repository.PokemonRepositoryImpl
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType
import br.com.drss.pokedex.network.PokeApi
import br.com.drss.pokedex.network.dtos.PagedListResponse
import br.com.drss.pokedex.network.dtos.PagedPokemonDto
import br.com.drss.pokedex.network.dtos.PokemonDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class PokemonRepositoryTest {

    private val coroutineTestScope = TestCoroutineScope()

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

        PokemonRepositoryImpl(inMemorySummaryDao, FakePokeApi(pokemonList), coroutineTestScope)

        inMemorySummaryDao.getAllSummaries().collect {
            assertTrue(it.isEmpty())
        }
    }

    @Test
    fun `Given the PokemonSummary data is not cached When I request the information, Then I should request data from the API And I should have the data stored locally`(): Unit =
        runBlocking {
            val fakeDao = InMemorySummaryDao()

            val pokemonRepository = PokemonRepositoryImpl(fakeDao, FakePokeApi(pokemonList), coroutineTestScope)

            fakeDao.getAllSummaries().collect {
                assertTrue(it.isEmpty())
            }

            pokemonRepository.getPokemonSummaryList().collect {
                if (it is Loaded) {
                    assertEquals(pokemonList.size, it.finalData.size)
                }
            }
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

            val pokemonRepository = PokemonRepositoryImpl(fakeDao, exceptionRaiserApi, coroutineTestScope)
            pokemonRepository.getPokemonSummaryList().collect {
                if (it is Loaded) {
                    assert(it.data.contains(bulbasaur_summary))
                }
            }
        }

    @Test
    fun `Given I have the poison filter selected, When I get the data Then I should see only the filtered results`(): Unit =
        runBlocking {
            val pokemonTypeList = listOf(PokemonType("poison"))

            val pokemonRepository = PokemonRepositoryImpl(InMemorySummaryDao(), FakePokeApi(pokemonList), coroutineTestScope)
            pokemonRepository.getPokemonSummaryList(pokemonTypeList).collect {
                if (it is Loaded) {
                    it.finalData.forEach {
                        assert(it.types.intersect(pokemonTypeList).isNotEmpty())
                    }
                }
            }
        }
}



