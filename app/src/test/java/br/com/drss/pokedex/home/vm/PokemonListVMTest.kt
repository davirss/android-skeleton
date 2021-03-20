package br.com.drss.pokedex.home.vm

import br.com.drss.pokedex.data.pokemonList
import br.com.drss.pokedex.home.repository.PokemonRepository
import br.com.drss.pokedex.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.home.repository.domain.entities.PokemonType
import br.com.drss.pokedex.home.repository.toSummary
import br.com.drss.pokedex.home.ui.Loaded
import br.com.drss.pokedex.home.ui.Loading
import br.com.drss.pokedex.home.ui.PokemonListViewModel
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class PokemonListVMTest {

    val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setupTests() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    class FakePokeRepo: PokemonRepository {

        override fun getPokemonSummaryList(typeFilters: List<PokemonType>): Flow<List<PokemonSummary>> {
            return channelFlow {
                kotlinx.coroutines.delay(1000)
                offer(pokemonList.map {
                    it.toSummary()
                })
            }
        }
    }

    @Test
    fun `Given the ViewModel is initialized Then I must have a list of pokemons`() = dispatcher.runBlockingTest {

        val vm = PokemonListViewModel(FakePokeRepo())

        val firstState = vm.viewState.first()
        assertEquals(Loading, firstState)

        val firstLoadedState = vm.viewState.first {
            it is Loaded
        } as Loaded
        assertTrue(firstLoadedState.pokemonSummaryList.isNotEmpty())

    }
}