package br.com.drss.pokedex.features.details.ui

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.drss.pokedex.MainActivity
import br.com.drss.pokedex.NavigationActions
import br.com.drss.pokedex.R
import br.com.drss.pokedex.databinding.FragmentPokemonDetailBinding
import br.com.drss.pokedex.databinding.ItemStatBinding
import br.com.drss.pokedex.extensions.getColorResource
import br.com.drss.pokedex.extensions.getIconId
import br.com.drss.pokedex.features.details.repo.Stat
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*


const val DETAIL_NAME_ARGUMENT = "name"

class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by viewModel {
        parametersOf(requireArguments().get(DETAIL_NAME_ARGUMENT))
    }

    private lateinit var binding: FragmentPokemonDetailBinding

    val statsAdapter = StatListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPokemonDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (requireActivity() as MainActivity).navigateTo(NavigationActions.PopBack)
            }

        })
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                renderUi(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.statsList.apply {
            adapter = statsAdapter
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
            }
        }
    }

    private fun renderUi(it: DetailViewState) {
        when (it) {
            is Loaded -> {
                binding.pokemonNumber.text = String.format(
                    getString(R.string.pokemon_number),
                    it.pokemonDetail.number
                )
                binding.textView.text = it.pokemonDetail.name.capitalize(Locale.getDefault())

                Glide.with(this)
                    .load(it.pokemonDetail.artwork)
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource?.let {
                                Palette.Builder(it.toBitmap()).generate {
                                    it?.lightVibrantSwatch?.let {
                                        binding.root.setBackgroundColor(it.rgb)
                                        binding.pokemonNumber.setTextColor(it.titleTextColor)
                                    }
                                }
                            }
                            return false
                        }

                    })
                    .into(
                        binding.pokemonArtwork
                    )
                binding.firstTypeContet.setTypeTextViewContent(it.pokemonDetail.types.first())
                if (it.pokemonDetail.types.size == 2) binding.secondTypeContent.setTypeTextViewContent(
                    it.pokemonDetail.types.last()
                )

                statsAdapter.submitList(it.pokemonDetail.states)

            }
        }
    }

    private fun TextView.setTypeTextViewContent(type: PokemonType) {
        text = type.name.toUpperCase(Locale.getDefault())
        setCompoundDrawablesRelativeWithIntrinsicBounds(type.getIconId(), 0, 0, 0)
        backgroundTintList = ResourcesCompat.getColorStateList(
            resources,
            type.getColorResource(),
            null
        )
    }
}

class StatListAdapter : ListAdapter<Stat, StatListAdapter.StatViewHolder>(PokemonStatDiff()) {
    inner class StatViewHolder(private val itemStatBinding: ItemStatBinding) :
        RecyclerView.ViewHolder(itemStatBinding.statLabel) {

        fun bind(state: Stat) {
            itemStatBinding.statLabel.apply {
                val upperCasedName = context.resources.getString(R.string.stat_label)
                text = String.format(
                    upperCasedName,
                    state.name.toUpperCase(Locale.getDefault()),
                    state.value
                )
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val itemStatView =
            ItemStatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatViewHolder(itemStatView)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PokemonStatDiff : DiffUtil.ItemCallback<Stat>() {
    override fun areItemsTheSame(oldItem: Stat, newItem: Stat): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Stat, newItem: Stat): Boolean {
        return oldItem.value == oldItem.value
    }

}