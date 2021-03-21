package br.com.drss.pokedex.features.details.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.drss.pokedex.features.details.repo.DetailsRepo
import br.com.drss.pokedex.features.details.repo.PokemonDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class DetailViewModel(private val pokemonName: String, private val detailsRepo: DetailsRepo, dispatcher: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    private val mutableViewState = MutableStateFlow<DetailViewState>(Loading)
    val viewState = mutableViewState

    init {
        viewModelScope.launch(dispatcher) {
            val pokeDetail = detailsRepo.getPokemonDetail(pokemonName)
            mutableViewState.value = Loaded(pokeDetail)
        }
    }
}

sealed class DetailViewState
object Loading: DetailViewState()
data class Loaded(val pokemonDetail: PokemonDetail): DetailViewState()