package br.com.drss.pokedex.home.repository.network.dto

data class PagedListResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)