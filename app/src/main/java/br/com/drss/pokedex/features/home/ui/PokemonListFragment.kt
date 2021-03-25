package br.com.drss.pokedex.features.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.drss.pokedex.MainActivity
import br.com.drss.pokedex.NavigationActions
import br.com.drss.pokedex.R
import br.com.drss.pokedex.databinding.FragmentPokemonListBinding
import br.com.drss.pokedex.extensions.getInteger
import br.com.drss.pokedex.features.home.ui.adapters.PokemonSummaryListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
class PokemonListFragment : Fragment() {

    private lateinit var gridLayoutManager: GridLayoutManager
    private val pokemonListViewModel: PokemonListViewModel by viewModel()
    private lateinit var binding: FragmentPokemonListBinding
    private val pokemonSummaryListAdapter = PokemonSummaryListAdapter { summary ->
        pokemonListViewModel.onItemSelected(summary)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pokemonListViewModel.viewState.asLiveData().observe(this) {
            displayLoadedState(it)
        }
        pokemonListViewModel.viewEvent.asLiveData().observe(this) {
            processEvent(it)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupToolbar()

        binding.pokemonListScrollToTop.setOnClickListener {
            pokemonListViewModel.onScrollToTop()
        }
    }

    private fun processEvent(event: PokemonListViewEvents) {
        when (event) {
            is PokemonListViewEvents.DisplayDetails -> {
                val activity = requireActivity() as MainActivity
                activity.navigateTo(NavigationActions.DisplayPokeDetails(event.name))
            }
            PokemonListViewEvents.Error -> {
                Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_SHORT).show()
            }
            PokemonListViewEvents.ScrollTop -> {
                binding.recyclerViewPokemonSummary.smoothScrollToPosition(0)
            }
        }
    }

    private fun setupRecyclerView() {
        val columnCount = getInteger(R.integer.column_count)
        gridLayoutManager = GridLayoutManager(requireContext(), columnCount)

        binding.recyclerViewPokemonSummary.apply {
            adapter = pokemonSummaryListAdapter
            layoutManager = gridLayoutManager
            addItemDecoration(SpaceItemDecoration(8, columnCount))

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemPosition = gridLayoutManager.findFirstCompletelyVisibleItemPosition()
                    pokemonListViewModel.setFirstVisibleItemPosition(visibleItemPosition)
                }
            })
        }
    }


    private fun setupToolbar() {
        val searchItem = binding.toolbar.menu.findItem(R.id.searchBar)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                pokemonListViewModel.onNameSearch(newText)
                return true
            }

        })
    }

    private fun displayLoadedState(viewState: PokemonListViewState) {
        binding.contentLoading.visibility =
            if (viewState.isFetchingItems) View.VISIBLE else View.GONE

        binding.pokemonListScrollToTop.apply {
            val newVisibility = if (viewState.scrollToTopVisible) View.VISIBLE else View.GONE
            if (newVisibility == visibility) return@apply

            val animationId = if (viewState.scrollToTopVisible) R.anim.anim_grow else R.anim.anim_shrink
            val growAnimation = AnimationUtils.loadAnimation(requireContext(), animationId)
            growAnimation.fillBefore = true
            growAnimation.fillAfter = true

            startAnimation(growAnimation)
            animate()
            visibility =
                if (viewState.scrollToTopVisible) View.VISIBLE else View.GONE

        }

        pokemonSummaryListAdapter.submitList(viewState.pokemonSummaryList)
    }
}

