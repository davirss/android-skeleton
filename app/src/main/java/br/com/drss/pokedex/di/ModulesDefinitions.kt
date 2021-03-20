package br.com.drss.pokedex.di

import br.com.drss.pokedex.home.repository.PokemonRepository
import br.com.drss.pokedex.home.repository.PokemonRepositoryImpl
import br.com.drss.pokedex.retrofit.buildRetrofitInstance
import org.koin.dsl.module


val repositoryModule = module {
    single<PokemonRepository> {
        PokemonRepositoryImpl(get(), get())
    }
}

val networkModule = module {
    single {
        buildRetrofitInstance()
    }
}

val applicationModule = module {

}