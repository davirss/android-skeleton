package br.com.drss.pokedex.home.repository.network.dto

data class PagedListResponse<T>(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T>
)