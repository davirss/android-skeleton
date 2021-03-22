package br.com.drss.pokedex.di

import androidx.room.Room
import br.com.drss.pokedex.BuildConfig
import br.com.drss.pokedex.features.details.repo.DetailsRepo
import br.com.drss.pokedex.features.details.repo.RemoteDetailsRepo
import br.com.drss.pokedex.features.details.ui.DetailFragment
import br.com.drss.pokedex.features.details.ui.DetailViewModel
import br.com.drss.pokedex.features.home.repository.PokemonRepository
import br.com.drss.pokedex.features.home.repository.PokemonRepositoryImpl
import br.com.drss.pokedex.features.home.repository.database.Database
import br.com.drss.pokedex.features.home.ui.PokemonListFragment
import br.com.drss.pokedex.network.PokeApi
import br.com.drss.pokedex.features.home.ui.PokemonListViewModel
import br.com.drss.pokedex.network.retrofit.provideJsonConverterFactory
import br.com.drss.pokedex.network.retrofit.provideOkHttpClient
import br.com.drss.pokedex.network.retrofit.provideOkHttpLoggingInterceptor
import br.com.drss.pokedex.network.retrofit.provideRetrofitInstance
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit


val repositoryModule = module {
    single<Database> {
        Room.databaseBuilder(androidApplication(), Database::class.java, "pokedex-db").build()
    }
    single {
        get<Database>().summaryDao()
    }
    single<PokemonRepository> {
        PokemonRepositoryImpl(get(), get())
    }
    single<DetailsRepo> {
        RemoteDetailsRepo(get())
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

    viewModel {
        (pokemonName: String ) -> DetailViewModel(pokemonName, get())
    }
}