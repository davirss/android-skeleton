package br.com.drss.pokedex.features.details.ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import androidx.transition.*
import br.com.drss.pokedex.MainActivity
import br.com.drss.pokedex.NavigationActions
import br.com.drss.pokedex.NavigationManager
import br.com.drss.pokedex.R
import br.com.drss.pokedex.databinding.FragmentPokemonDetailBinding
import br.com.drss.pokedex.extensions.getColorResource
import br.com.drss.pokedex.extensions.getIconId
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType
import com.bumptech.glide.Glide
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
    private val statsAdapter = StatListAdapter()

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
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.navigateBack()
                }

            })
        viewModel.viewState.asLiveData().observe(this) {
            renderUi(it)
        }

        viewModel.viewEvent.asLiveData().observe(this) {
            if (it is DetailViewActions.NavigateBack) popBack()
        }

    }

    private fun popBack() {
        (requireActivity() as NavigationManager).navigateTo(NavigationActions.PopBack)
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
            is DetailViewState.Loaded -> {
                displayLoadedContent(it)

            }
            DetailViewState.Loading -> {
                displayLoading()
            }
            DetailViewState.Error -> {
                displayError()

            }
        }
    }

    private fun displayError() {
        hideLoadingFrame()
        binding.pokemonNumber.text = getString(R.string.error_unkown_number)
        binding.detailPokemonName.text = getString(R.string.error_missingno)
        binding.description.text = getString(R.string.error_message)

        binding.pokemonArtwork.setImageResource(R.drawable.image_error)
    }

    private fun hideLoadingFrame() {
        binding.frameContentLoading.animate().alpha(0f).setDuration(200).withEndAction {
            binding.frameContentLoading.visibility = View.GONE
        }.start()
    }

    private fun displayLoading() {
        binding.frameContentLoading.visibility = View.VISIBLE

    }

    private fun displayLoadedContent(details: DetailViewState.Loaded) {
        hideLoadingFrame()
        binding.pokemonNumber.text = String.format(
            getString(R.string.pokemon_number),
            details.pokemonDetail.number
        )
        binding.detailPokemonName.text = details.pokemonDetail.name.capitalize(Locale.getDefault())
        binding.description.text = details.pokemonDetail.description

        Glide.with(this)
            .load(details.pokemonDetail.artwork)
            .addListener(GlidePaletteGeneratorListener(::updateViewsPalette))
            .into(
                binding.pokemonArtwork
            )
        binding.firstTypeContet.setTypeTextViewContent(details.pokemonDetail.types.first())
        if (details.pokemonDetail.types.size == 2) binding.secondTypeContent.setTypeTextViewContent(
            details.pokemonDetail.types.last()
        )

        statsAdapter.submitList(details.pokemonDetail.states)
    }

    private fun updateViewsPalette(palette: Palette) {
        palette.lightVibrantSwatch?.let {

            val typedValue = TypedValue()
            val theme = requireContext().theme
            theme.resolveAttribute(R.attr.colorSurface, typedValue, true)
            @ColorInt val color = typedValue.data
            val colorAnimation =
                ValueAnimator.ofObject(ArgbEvaluator(), color, it.rgb)
            colorAnimation.duration = 200L
            colorAnimation.addUpdateListener { animator -> binding.root.setBackgroundColor(animator.animatedValue as Int) }
            colorAnimation.start()

            binding.pokemonNumber.setTextColor(it.titleTextColor)

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
