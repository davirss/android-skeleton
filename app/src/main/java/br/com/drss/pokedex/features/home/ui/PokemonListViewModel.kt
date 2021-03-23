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
        MutableStateFlow(PokemonListViewState(isFetchingItems = true))
    val viewState: StateFlow<PokemonListViewState> = mutableViewState

    private val viewEventsChannel = MutableSharedFlow<PokemonListViewEvents>(replay = 0)
    val viewEvent: SharedFlow<PokemonListViewEvents> = viewEventsChannel

    private val searchChannel = MutableStateFlow("")
    private val visibleItemStateFlow = MutableStateFlow<Int>(0)

    init {
        loadSummaryList()
    }

    private fun loadSummaryList() {
        viewModelScope.launch(dispatcher) {
            pokemonRepository.getPokemonSummaryList()
                .onEach {
                    mutableViewState.value = mutableViewState.value.copy(isFetchingItems = it is Loading)
                }
                .combine(visibleItemStateFlow) { data, visibleItem ->
                    mutableViewState.value = mutableViewState.value.copy(scrollToTopVisible = visibleItem > 0)
                    data
                }
                .combine(searchChannel) { data, filter ->
                    if (filter.isEmpty()) {
                        data.data
                    } else {
                        data.data.filter { it.name.contains(filter) }
                    }
                }
                .catch {
                    viewEventsChannel.emit(PokemonListViewEvents.Error)
                }
                .collect {
                    val currentState = mutableViewState.value
                    mutableViewState.value = currentState.copy(
                        pokemonSummaryList = it
                    )
                }
        }
    }

    fun onItemSelected(pokemonSummary: PokemonSummary) {
        viewModelScope.launch(dispatcher) {
            viewEventsChannel.emit(PokemonListViewEvents.DisplayDetails(pokemonSummary.name))
        }
    }

    fun onNameSearch(input: String?) {
        viewModelScope.launch {
            searchChannel.emit(input ?: "")
        }
    }

    fun setFirstVisibleItemPosition(position: Int) {
        viewModelScope.launch {
            visibleItemStateFlow.emit(position)
        }
    }

    fun scrollToTop() {
        viewModelScope.launch {
            viewEventsChannel.emit(PokemonListViewEvents.ScrollTop)
        }
    }

}

data class PokemonListViewState(
    val isFetchingItems: Boolean = true,
    val pokemonSummaryList: List<PokemonSummary> = listOf(),
    val filter: List<PokemonTypeFilter> = listOf(),
    val scrollToTopVisible: Boolean = false
)

sealed class PokemonListViewEvents {
    data class DisplayDetails(val name: String) : PokemonListViewEvents()
    object Error : PokemonListViewEvents()
    object ScrollTop : PokemonListViewEvents()
}