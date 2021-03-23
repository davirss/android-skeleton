package br.com.drss.pokedex.features.details.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.drss.pokedex.features.details.repo.DetailsRepo
import br.com.drss.pokedex.features.details.repo.entities.PokemonDetail
import br.com.drss.pokedex.features.home.ui.PokemonListViewEvents
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.lang.Exception


class DetailViewModel(private val pokemonName: String, private val detailsRepo: DetailsRepo, dispatcher: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    private val mutableViewState = MutableStateFlow<DetailViewState>(DetailViewState.Loading)
    val viewState = mutableViewState

    private val viewEventsChannel = MutableSharedFlow<DetailViewActions>(replay = 0)
    val viewEvent: SharedFlow<DetailViewActions> = viewEventsChannel

    init {
        viewModelScope.launch(dispatcher) {
            try {
                mutableViewState.value = DetailViewState.Loading
                val pokeDetail = detailsRepo.getPokemonDetail(pokemonName)
                mutableViewState.value = DetailViewState.Loaded(pokeDetail)
            } catch (e: Exception) {
                mutableViewState.value = DetailViewState.Error
            }
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            viewEventsChannel.emit(DetailViewActions.NavigateBack)
        }
    }
}

sealed class DetailViewState {
    object Loading: DetailViewState()
    data class Loaded(val pokemonDetail: PokemonDetail): DetailViewState()
    object Error: DetailViewState()
}

sealed class DetailViewActions {
    object NavigateBack: DetailViewActions()
}