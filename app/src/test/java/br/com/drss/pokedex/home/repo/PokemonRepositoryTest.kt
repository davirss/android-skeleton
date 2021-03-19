package br.com.drss.pokedex.home.repo

import br.com.drss.pokedex.home.repository.PokemonRepositoryImpl
import br.com.drss.pokedex.home.repository.database.PokemonSummaryDao
import br.com.drss.pokedex.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.home.repository.domain.entities.PokemonType
import br.com.drss.pokedex.home.repository.domain.entities.PokemonTypeFilter
import br.com.drss.pokedex.home.repository.network.PokeApi
import br.com.drss.pokedex.home.repository.network.dto.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class PokemonRepositoryTest {

    class FakePokemonSummaryDao : PokemonSummaryDao {

        private val inMemoryDb = mutableListOf<PokemonSummary>()

        override fun getAllSummaries(): Flow<List<PokemonSummary>> = flow {
            emit(inMemoryDb)
        }

        override fun getTypeFilteredSummaries(typeFilterList: List<PokemonType>): Flow<List<PokemonSummary>> = flow {
            emit(inMemoryDb.filter { it.types.intersect(typeFilterList).isNotEmpty() })
        }

        override suspend fun findByName(name: String): PokemonSummary? {
            return inMemoryDb.find { it.name == name }
        }

        override suspend fun insertPokemonSummary(pokemonSummary: PokemonSummary) {
            inMemoryDb.add(pokemonSummary)
        }
    }

    class FakePokeApi(val data: List<PokemonDto>) : PokeApi {

        override suspend fun getPokemonPagedList(): PagedListResponse<PagedPokemonDto> {
            return PagedListResponse<PagedPokemonDto>(
                count = data.size,
                results = data.map { PagedPokemonDto(it.name) }
            )
        }

        override suspend fun getPokemonData(name: String): PokemonDto =
            data.find { it.name == name }!!
    }


    @Test
    fun `Given the PokemonSummary data is not cached When I request the information, Then I should request data from the API And I should have the data stored locally`(): Unit =
        runBlocking {
            val fakeDao = FakePokemonSummaryDao()

            val pokemonRepository = PokemonRepositoryImpl(fakeDao, FakePokeApi(pokemonList))

            fakeDao.getAllSummaries().collect {
                assert(it.isEmpty())
            }

            val pokemonSummaryList = pokemonRepository.getPokemonSummaryList().toList()

            val expectedPokemonSummary = PokemonSummary(
                "bulbasaur",
                1,
                listOf(PokemonType("grass"), PokemonType("poison")),
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
            )
            assert(pokemonSummaryList[0].first() == expectedPokemonSummary)
        }

    @Test
    fun `Given the PokemonSummary is cached When I request the information Then the API should not be called`(): Unit =
        runBlocking {
            val fakeDao = FakePokemonSummaryDao()
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

            assert(pokemonSummaryList[0].first() == bulbasaur_summary)
        }

    @Test
    fun `Given I have the grass filter selected, When I get the data Then I should see only the filtered results`(): Unit =
        runBlocking {
            val poisonPokemonType = PokemonType("poison")

            val pokemonRepository = PokemonRepositoryImpl(FakePokemonSummaryDao(), FakePokeApi(pokemonList))
            val pokemonSummaryList = pokemonRepository.getPokemonSummaryList(listOf(poisonPokemonType)).toList()

            val results = pokemonSummaryList[0].filter { !it.types.contains(PokemonType("poison")) }
            assert(results.isEmpty())
        }
}

val bulbasaur_summary = PokemonSummary(
    "bulbasaur",
    1,
    listOf(PokemonType("grass"), PokemonType("poison")),
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
)

val pokemonList = listOf<PokemonDto>(
    PokemonDto(
        "bulbasaur",
        1,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"),
        listOf(PokemonTypeDto("grass"), PokemonTypeDto("poison"))
    ),
    PokemonDto(
        "charmander",
        4,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"),
        listOf(PokemonTypeDto("fire"))
    ),
    PokemonDto(
        "squirtle",
        7,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png"),
        listOf(PokemonTypeDto("water"))
    ),
    PokemonDto(
        "pikachu",
        25,
        Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"),
        listOf(PokemonTypeDto("electric"))
    )
)