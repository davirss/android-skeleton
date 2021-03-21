package br.com.drss.pokedex.features.details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.drss.pokedex.databinding.FragmentPokemonDetailBinding
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment: Fragment(){

    private val viewModel: DetailViewModel by viewModel()

    private lateinit var binding: FragmentPokemonDetailBinding

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
        lifecycleScope.launchWhenResumed {
            viewModel.viewState.collect {
                renderUi(it)
            }
        }
    }

    private fun renderUi(it: DetailViewState) {

    }
}