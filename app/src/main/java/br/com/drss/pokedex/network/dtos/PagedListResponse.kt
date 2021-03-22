package br.com.drss.pokedex.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PagedListResponse<T>(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T>
)