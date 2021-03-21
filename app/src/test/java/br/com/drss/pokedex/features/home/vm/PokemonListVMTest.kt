package br.com.drss.pokedex.features.home.vm

import br.com.drss.pokedex.data.pokemonList
import br.com.drss.pokedex.features.home.repository.*
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType
import br.com.drss.pokedex.features.home.ui.Initialized
import br.com.drss.pokedex.features.home.ui.PokemonListViewModel
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class PokemonListVMTest {

    private val dispatcher = TestCoroutineDispatcher()

    class FakePokeRepo: PokemonRepository {

        override fun getPokemonSummaryList(typeFilters: List<PokemonType>): Flow<OperationStatus<List<PokemonSummary>>> {
            return flow {
                val mutableList = mutableListOf<PokemonSummary>()
                pokemonList.forEach {
                    delay(10)
                    mutableList.add(it.toSummary())
                    emit(Loading(mutableList.toList()))
                }
                emit(Loaded(mutableList.toList()))
            }
        }
    }

    @Test
    fun `Given the ViewModel is initialized When the data finishes loading Then I must have a list of pokemons`() = runBlocking {

        val vm = PokemonListViewModel(FakePokeRepo(), dispatcher)

        val firstState = vm.viewState.first()
        assertTrue(firstState is Initialized)
        assertEquals((firstState as Initialized).isFetchingItems, true)

        vm.viewState.collect {
            if (!(it as Initialized).isFetchingItems) {
                assertEquals(pokemonList.size, it.pokemonSummaryList.size)
            }
        }
    }
}