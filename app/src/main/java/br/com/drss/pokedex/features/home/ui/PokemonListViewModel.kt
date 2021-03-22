package br.com.drss.pokedex.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.drss.pokedex.features.home.repository.Loading
import br.com.drss.pokedex.features.home.repository.PokemonRepository
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonTypeFilter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PokemonListViewModel(
    private val pokemonRepository: PokemonRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val mutableViewState: MutableStateFlow<PokemonListViewState> =
        MutableStateFlow(Initialized(isFetchingItems = true))
    val viewState: StateFlow<PokemonListViewState> = mutableViewState

    private val viewEventsChannel = MutableSharedFlow<PokemonListViewEvents>(replay = 0)
    val viewEvent: SharedFlow<PokemonListViewEvents> = viewEventsChannel

    init {
        loadSummaryList()
    }

    fun loadSummaryList() {
        viewModelScope.launch(dispatcher) {
            pokemonRepository.getPokemonSummaryList()
                .catch {
                    viewEventsChannel.emit(PokemonListViewEvents.Error)
                }
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
        viewModelScope.launch(dispatcher) {
            viewEventsChannel.emit(PokemonListViewEvents.DisplayDetails(pokemonSummary.name))
        }
    }
}

sealed class PokemonListViewState
data class Initialized(
    val isFetchingItems: Boolean = true,
    val pokemonSummaryList: List<PokemonSummary> = listOf(),
    val filter: List<PokemonTypeFilter> = listOf()
) : PokemonListViewState()

sealed class PokemonListViewEvents {
    data class DisplayDetails(val name: String) : PokemonListViewEvents()
    object Error : PokemonListViewEvents()
}