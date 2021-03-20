package br.com.drss.pokedex.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.drss.pokedex.databinding.FragmentPokemonListBinding
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokemonListFragment: Fragment() {

    private val pokemonListViewModel: PokemonListViewModel by viewModel()
    private lateinit var binding: FragmentPokemonListBinding

    init {
        lifecycleScope.launchWhenResumed {
            pokemonListViewModel.viewState.collect {
                renderUi(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPokemonListBinding.inflate(inflater)
        return binding.root
    }

    private fun renderUi(viewState: PokemonListViewState) {
        when (viewState) {
            is Loaded -> displayLoadedState(viewState)
            Loading -> displayLoading()
        }
    }

    private fun displayLoading() {

    }

    private fun displayLoadedState(viewState: Loaded) {
        viewState.pokemonSummaryList.forEach {
            print(it.toString())
        }
    }

}