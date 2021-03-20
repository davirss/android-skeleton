package br.com.drss.pokedex.home.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.drss.pokedex.R
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokemonListFragment: Fragment(R.layout.fragment_pokemon_list) {

    private val pokemonListViewModel: PokemonListViewModel by viewModel()

    init {

        lifecycleScope.launchWhenResumed {
            pokemonListViewModel.viewState.collect {
                renderUi(it)
            }
        }
    }

    private fun renderUi(viewState: PokemonListViewState) {
        when (viewState) {
            is Loaded -> displayLoadedState(viewState)
            Loading -> displayLoading(viewState)
        }
    }

    private fun displayLoading(viewState: PokemonListViewState) {
    }

    private fun displayLoadedState(viewState: Loaded) {

    }

}