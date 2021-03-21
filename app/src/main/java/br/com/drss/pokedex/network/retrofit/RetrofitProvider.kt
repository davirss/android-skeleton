package br.com.drss.pokedex.network.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit

const val POKEAPI_URL = "https://pokeapi.co/api/"

@ExperimentalSerializationApi
fun provideRetrofitInstance(
    okHttpClient: OkHttpClient,
    converterList: List<Converter.Factory>
): Retrofit {
    val retrofitBuilder = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(POKEAPI_URL)

    converterList.forEach {
        retrofitBuilder.addConverterFactory(it)
    }

    return retrofitBuilder.build()
}


@ExperimentalSerializationApi
fun provideJsonConverterFactory(): Converter.Factory {
    val mediaType = "application/json".toMediaType()
    return Json {
        ignoreUnknownKeys = true
    }.asConverterFactory(mediaType)
}

fun provideOkHttpClient(interceptors: List<Interceptor>): OkHttpClient {

    val okHttpBuilder = OkHttpClient.Builder()

    interceptors.forEach {
        okHttpBuilder.addInterceptor(it)
    }

    return okHttpBuilder.build()
}

fun provideOkHttpLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
}