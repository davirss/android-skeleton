package br.com.drss.pokedex.features.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import br.com.drss.pokedex.MainActivity
import br.com.drss.pokedex.NavigationActions
import br.com.drss.pokedex.R
import br.com.drss.pokedex.databinding.FragmentPokemonListBinding
import br.com.drss.pokedex.extensions.getInteger
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.features.home.ui.adapters.PokemonSummaryListAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokemonListFragment : Fragment() {

    private lateinit var job: Job
    private val pokemonListViewModel: PokemonListViewModel by viewModel()
    private lateinit var binding: FragmentPokemonListBinding
    private val pokemonSummaryListAdapter = PokemonSummaryListAdapter {
        pokemonListViewModel.onItemSelected(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = lifecycleScope.launchWhenStarted {
            pokemonListViewModel.viewEvent.onEach {
                processEvent(it)
            }.launchIn(lifecycleScope)

            pokemonListViewModel.viewState.collect {
                renderUi(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pokemonListViewModel.loadSummaryList()
    }

    override fun onStop() {
        job.cancel()
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPokemonListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun processEvent(event: PokemonListViewEvents) {
        when (event) {
            is PokemonListViewEvents.DisplayDetails -> {
                val activity = requireActivity() as MainActivity
                activity.navigateTo(NavigationActions.DisplayPokeDetails(event.name))
            }
            PokemonListViewEvents.Error -> {
                Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewPokemonSummary.apply {
            val columnCount = getInteger(R.integer.column_count)
            adapter = pokemonSummaryListAdapter
            layoutManager = GridLayoutManager(requireContext(), columnCount)
            addItemDecoration(SpaceItemDecoration(8, columnCount))
        }
    }

    private fun renderUi(viewState: PokemonListViewState) {
        when (viewState) {
            is Initialized -> displayLoadedState(viewState)
        }
    }

    private fun displayLoadedState(viewState: Initialized) {
        binding.contentLoading.visibility =
            if (viewState.isFetchingItems) View.VISIBLE else View.GONE
        pokemonSummaryListAdapter.submitList(viewState.pokemonSummaryList)
    }
}

