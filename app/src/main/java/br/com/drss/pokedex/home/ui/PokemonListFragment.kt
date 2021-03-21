package br.com.drss.pokedex.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.drss.pokedex.R
import br.com.drss.pokedex.databinding.FragmentPokemonListBinding
import br.com.drss.pokedex.databinding.ItemSummaryBinding
import br.com.drss.pokedex.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.home.repository.domain.entities.PokemonType
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokemonListFragment: Fragment() {

    private val pokemonListViewModel: PokemonListViewModel by viewModel()
    private lateinit var binding: FragmentPokemonListBinding
    private val pokemonSummaryListAdapter = Adapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            is Initialized -> displayLoadedState(viewState)
            Error -> displayLoading()
        }
    }

    private fun displayLoading() {

    }

    private fun displayLoadedState(viewState: Initialized) {
        pokemonSummaryListAdapter.submitList(viewState.pokemonSummaryList)
    }

    inner class Adapter: ListAdapter<PokemonSummary, Adapter.PokemonItem>(PokemonSummaryDiff()) {

        inner class PokemonItem(private val summaryBinding: ItemSummaryBinding) : RecyclerView.ViewHolder(
            summaryBinding.root
        ) {
            fun bind(pokemonSummary: PokemonSummary) {
                summaryBinding.idTextView.text = "#${pokemonSummary.number.toString().padStart(
                    3,
                    '0'
                )}"
                summaryBinding.pokemonName.text = pokemonSummary.name.capitalize()
                Glide.with(summaryBinding.frontSprite).load(pokemonSummary.artwork).centerCrop().into(
                    summaryBinding.frontSprite
                )
                summaryBinding.firstSlotTypeImageView.setImageResource(pokemonSummary.types.first().getIconId())

                if (pokemonSummary.types.size > 1) {
                    summaryBinding.se.setImageResource(pokemonSummary.types[1].getIconId())
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

    fun PokemonType.getIconId(): Int {
        return R.drawable::class.java.getId("ic_$name")
    }
}

class PokemonSummaryDiff: DiffUtil.ItemCallback<PokemonSummary>() {

    override fun areItemsTheSame(oldItem: PokemonSummary, newItem: PokemonSummary): Boolean {
        return oldItem.name == newItem.name

    }

    override fun areContentsTheSame(oldItem: PokemonSummary, newItem: PokemonSummary): Boolean {
        return oldItem == newItem

    }

}

inline fun <reified T: Class<*>> T.getId(resourceName: String): Int {
    return try {
        val idField = getDeclaredField(resourceName)
        idField.getInt(idField)
    } catch (e:Exception) {
        e.printStackTrace()
        -1
    }
}