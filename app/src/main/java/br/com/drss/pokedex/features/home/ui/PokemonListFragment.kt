package br.com.drss.pokedex.features.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntegerRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.drss.pokedex.MainActivity
import br.com.drss.pokedex.NavigationActions
import br.com.drss.pokedex.R
import br.com.drss.pokedex.databinding.FragmentPokemonListBinding
import br.com.drss.pokedex.databinding.ItemSummaryBinding
import br.com.drss.pokedex.extensions.getIconId
import br.com.drss.pokedex.extensions.getId
import br.com.drss.pokedex.extensions.getInteger
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class PokemonListFragment : Fragment() {

    private val pokemonListViewModel: PokemonListViewModel by viewModel()
    private lateinit var binding: FragmentPokemonListBinding
    private val pokemonSummaryListAdapter = Adapter {
        pokemonListViewModel.onItemSelected(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            pokemonListViewModel.viewEvent.onEach {
                when (it) {
                    is DisplayDetails -> {
                        val activity = requireActivity() as MainActivity
                        activity.navigateTo(NavigationActions.DisplayPokeDetails(it.name))
                    }
                }
            }.launchIn(lifecycleScope)

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
            val columnCount = getInteger(R.integer.column_count)
            adapter = pokemonSummaryListAdapter
            layoutManager = GridLayoutManager(requireContext(), columnCount)
            addItemDecoration(SpaceItemDecoration(8, columnCount))
        }
    }

    private fun renderUi(viewState: PokemonListViewState) {
        when (viewState) {
            is Initialized -> displayLoadedState(viewState)
            Error -> displayLoading()
        }
    }

    private fun displayLoading() {

    }

    private fun displayLoadedState(viewState: Initialized) {
        pokemonSummaryListAdapter.submitList(viewState.pokemonSummaryList)
    }

    inner class Adapter(val itemClickAction: (pokemonSummary: PokemonSummary) -> Unit) :
        ListAdapter<PokemonSummary, Adapter.PokemonItem>(PokemonSummaryDiff()) {

        inner class PokemonItem(private val summaryBinding: ItemSummaryBinding) :
            RecyclerView.ViewHolder(
                summaryBinding.root
            ) {
            fun bind(pokemonSummary: PokemonSummary) {
                summaryBinding.idTextView.text = String.format(getString(R.string.pokemon_number), pokemonSummary.number)
                summaryBinding.pokemonName.text = pokemonSummary.name.capitalize(Locale.getDefault())
                Glide.with(summaryBinding.frontSprite).load(pokemonSummary.artwork).centerCrop()
                    .into(
                        summaryBinding.frontSprite
                    )
                summaryBinding.firstSlotTypeImageView.setImageResource(
                    pokemonSummary.types.first().getIconId()
                )

                if (pokemonSummary.types.size > 1) {
                    summaryBinding.se.setImageResource(pokemonSummary.types[1].getIconId())
                }
                summaryBinding.root.setOnClickListener {
                    itemClickAction(pokemonSummary)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonItem {
            val view = ItemSummaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return PokemonItem(view)
        }

        override fun onBindViewHolder(holder: PokemonItem, position: Int) {
            holder.bind(getItem(position))
        }
    }


}



class PokemonSummaryDiff : DiffUtil.ItemCallback<PokemonSummary>() {

    override fun areItemsTheSame(oldItem: PokemonSummary, newItem: PokemonSummary): Boolean {
        return oldItem.name == newItem.name

    }

    override fun areContentsTheSame(oldItem: PokemonSummary, newItem: PokemonSummary): Boolean {
        return oldItem == newItem

    }

}

