package br.com.drss.pokedex.home.vm

import br.com.drss.pokedex.data.pokemonList
import br.com.drss.pokedex.home.repo.InMemorySummaryDao
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
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PokemonListVMTest {


    class FakePokeRepo: PokemonRepository {

        override fun getPokemonSummaryList(typeFilters: List<PokemonType>): Flow<List<PokemonSummary>> {
            return channelFlow {
                offer(pokemonList.map {
                    it.toSummary()
                })
            }
        }
    }

    @Test
    fun test() = runBlocking {

        val vm = PokemonListViewModel(FakePokeRepo())

        vm.viewState.take(1).collect {
            assertEquals(it, Loading)
        }

        vm.viewState.take(2).collect {
            assertTrue(it is Loaded)
            val loadedState = it as Loaded
            assertTrue(it.pokemonSummaryList.isNotEmpty())
        }

    }
}