package br.com.drss.pokedex.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.drss.pokedex.databinding.FragmentPokemonListBinding
import br.com.drss.pokedex.databinding.ItemSummaryBinding
import br.com.drss.pokedex.home.repository.domain.entities.PokemonSummary
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokemonListFragment: Fragment() {

    private val pokemonListViewModel: PokemonListViewModel by viewModel()
    private lateinit var binding: FragmentPokemonListBinding
    private val pokemonSummaryListAdapter = Adapter()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewPokemonSummary.apply {
            adapter = pokemonSummaryListAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(SpaceItemDecoration(8, 3))
        }
    }

    private fun calculateSpanCount(): Int {
        val displayMetrics = requireContext().resources.displayMetrics
        return ((displayMetrics.widthPixels / displayMetrics.density) / 160).toInt()
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
        pokemonSummaryListAdapter.setItems(viewState.pokemonSummaryList)
    }

    inner class Adapter: RecyclerView.Adapter<Adapter.PokemonItem>() {

        private val list = mutableListOf<PokemonSummary>()

        inner class PokemonItem(private val summaryBinding: ItemSummaryBinding) : RecyclerView.ViewHolder(summaryBinding.root) {
            fun bind(pokemonSummary: PokemonSummary) {
                summaryBinding.idTextView.text = "#${pokemonSummary.number.toString().padStart(3, '0')}"
                summaryBinding.pokemonName.text = pokemonSummary.name.capitalize()
                Glide.with(summaryBinding.frontSprite).load(pokemonSummary.artwork).centerCrop().into(summaryBinding.frontSprite)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonItem {
            val view = ItemSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PokemonItem(view)
        }

        override fun onBindViewHolder(holder: PokemonItem, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount(): Int {
            return list.size
        }

        fun setItems(listSummary: List<PokemonSummary>) {
            list.addAll(listSummary)
            notifyDataSetChanged()
        }
    }
}