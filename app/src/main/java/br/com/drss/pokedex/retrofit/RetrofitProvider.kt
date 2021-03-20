package br.com.drss.pokedex.retrofit

import retrofit2.Retrofit

fun buildRetrofitInstance(): Retrofit {
    return Retrofit.Builder().build()
}