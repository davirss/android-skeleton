package br.com.drss.pokedex.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.drss.pokedex.features.home.repository.OperationStatus
import br.com.drss.pokedex.features.home.repository.PokemonRepository
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonTypeFilter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@FlowPreview
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
                    mutableViewState.value = mutableViewState.value.copy(isFetchingItems = it is OperationStatus.Loading)
                }
                .combine(visibleItemStateFlow) { data, visibleItem ->
                    mutableViewState.value = mutableViewState.value.copy(scrollToTopVisible = visibleItem > 0)
                    data
                }
                .debounce(500)
                .combine(searchChannel) { data, filter ->
                    data to filter
                }
                .catch {
                    viewEventsChannel.emit(PokemonListViewEvents.Error)
                }
                .onCompletion {
                    mutableViewState.value = mutableViewState.value.copy(isFetchingItems = false)
                }
                .collect { (operationStatus, filter) ->
                    when (operationStatus) {
                        is OperationStatus.Error ->  {
                            if (operationStatus is Error) viewEventsChannel.emit(PokemonListViewEvents.Error)
                        }
                        is OperationStatus.Loaded,
                        is OperationStatus.Loading -> {
                            val list =
                                if (operationStatus is OperationStatus.Loading)
                                    operationStatus.intermediaryData
                                else
                                    (operationStatus as OperationStatus.Loaded).finalData
                            val filteredList = list.filter {
                                it.name.contains(filter)
                            }
                            mutableViewState.value = mutableViewState.value.copy(pokemonSummaryList = filteredList)
                        }
                    }
                }
        }
    }

    fun onItemSelected(pokemonSummary: PokemonSummary) {
        viewModelScope.launch(dispatcher) {
            viewEventsChannel.emit(PokemonListViewEvents.DisplayDetails(pokemonSummary.name))
        }
    }

    fun onNameSearch(input: String?) {
        viewModelScope.launch(dispatcher) {
            searchChannel.emit(input ?: "")
        }
    }

    fun setFirstVisibleItemPosition(position: Int) {
        viewModelScope.launch(dispatcher) {
            visibleItemStateFlow.emit(position)
        }
    }

    fun onScrollToTop() {
        viewModelScope.launch(dispatcher) {
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