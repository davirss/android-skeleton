package br.com.drss.pokedex.di

import androidx.room.Room
import br.com.drss.pokedex.BuildConfig
import br.com.drss.pokedex.home.repository.PokemonRepository
import br.com.drss.pokedex.home.repository.PokemonRepositoryImpl
import br.com.drss.pokedex.home.repository.database.Database
import br.com.drss.pokedex.home.repository.network.PokeApi
import br.com.drss.pokedex.home.ui.PokemonListViewModel
import br.com.drss.pokedex.retrofit.provideJsonConverterFactory
import br.com.drss.pokedex.retrofit.provideOkHttpClient
import br.com.drss.pokedex.retrofit.provideOkHttpLoggingInterceptor
import br.com.drss.pokedex.retrofit.provideRetrofitInstance
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit


val repositoryModule = module {
    single<PokemonRepository> {
        PokemonRepositoryImpl(get(), get())
    }
    single<Database> {
        Room.databaseBuilder(androidApplication(), Database::class.java, "pokedex-db").build()
    }
    single {
        get<Database>().summaryDao()
    }
}

val networkModule = module {
    single<Retrofit> {
        val jsonConverterFactory = provideJsonConverterFactory()
        provideRetrofitInstance(get(), listOf(jsonConverterFactory))
    }
    single<PokeApi> {
        val retrofit: Retrofit = get()
        retrofit.create(PokeApi::class.java)
    }
    factory {
        provideOkHttpLoggingInterceptor()
    }
    factory {
        val interceptions: MutableList<Interceptor> = mutableListOf()
        if (BuildConfig.DEBUG) interceptions.add(get<HttpLoggingInterceptor>())

        provideOkHttpClient(interceptions)
    }

}

val applicationModule = module {
    viewModel {
        PokemonListViewModel(get())
    }
}