package br.com.drss.pokedex.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.drss.pokedex.features.home.repository.Loading
import br.com.drss.pokedex.features.home.repository.PokemonRepository
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonTypeFilter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

class PokemonListViewModel(
    pokemonRepository: PokemonRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val mutableViewState: MutableStateFlow<PokemonListViewState> =
        MutableStateFlow(Initialized(isFetchingItems = true))
    val viewState: StateFlow<PokemonListViewState> = mutableViewState

    private val viewEventsChannel = MutableSharedFlow<PokemonListViewEvents>(replay = 0)
    val viewEvent = viewEventsChannel

    init {
        viewModelScope.launch(dispatcher) {
            pokemonRepository.getPokemonSummaryList()
                .catch { mutableViewState.value = Error }
                .collect {
                    val currentState = mutableViewState.value as Initialized
                    mutableViewState.value = currentState.copy(
                        isFetchingItems = it is Loading,
                        pokemonSummaryList = it.data
                    )
                }
        }
    }

    fun onItemSelected(pokemonSummary: PokemonSummary) {
        viewModelScope.launch {
            viewEventsChannel.emit(DisplayDetails(pokemonSummary.name))
        }
    }
}

sealed class PokemonListViewState
data class Initialized(
    val isFetchingItems: Boolean = true,
    val pokemonSummaryList: List<PokemonSummary> = listOf(),
    val filter: List<PokemonTypeFilter> = listOf()
): PokemonListViewState()

sealed class PokemonListViewEvents
data class DisplayDetails(val name: String): PokemonListViewEvents()

object Error : PokemonListViewState()