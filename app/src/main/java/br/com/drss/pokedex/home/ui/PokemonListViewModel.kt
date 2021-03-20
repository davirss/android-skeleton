package br.com.drss.pokedex.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.drss.pokedex.home.repository.PokemonRepository
import br.com.drss.pokedex.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.home.repository.domain.entities.PokemonTypeFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PokemonListViewModel(pokemonRepository: PokemonRepository) : ViewModel() {

    private val mutableViewState: MutableStateFlow<PokemonListViewState> = MutableStateFlow(Loading)
    val viewState = mutableViewState

    init {
        viewModelScope.launch {
            pokemonRepository.getPokemonSummaryList().collect {
                mutableViewState.emit(Loaded(it))
            }
        }
    }
}

sealed class PokemonListViewState
object Loading : PokemonListViewState()
data class Loaded(val pokemonSummaryList: List<PokemonSummary>, val filter: List<PokemonTypeFilter> = listOf()): PokemonListViewState()