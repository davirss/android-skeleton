package br.com.drss.pokedex.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.drss.pokedex.home.repository.Loaded
import br.com.drss.pokedex.home.repository.Loading
import br.com.drss.pokedex.home.repository.PokemonRepository
import br.com.drss.pokedex.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.home.repository.domain.entities.PokemonTypeFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PokemonListViewModel(
    pokemonRepository: PokemonRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val mutableViewState: MutableStateFlow<PokemonListViewState> =
        MutableStateFlow(Initialized(isFetchingItems = true))
    val viewState: StateFlow<PokemonListViewState> = mutableViewState

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
}

sealed class PokemonListViewState
data class Initialized(
    val isFetchingItems: Boolean = true,
    val pokemonSummaryList: List<PokemonSummary> = listOf(),
    val filter: List<PokemonTypeFilter> = listOf()
): PokemonListViewState()

object Error : PokemonListViewState()