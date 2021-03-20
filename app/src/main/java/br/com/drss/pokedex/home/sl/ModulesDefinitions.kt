package br.com.drss.pokedex.home.sl

import br.com.drss.pokedex.home.repository.PokemonRepository
import br.com.drss.pokedex.home.repository.PokemonRepositoryImpl
import org.koin.dsl.module
import retrofit2.Retrofit


val repositoryModule = module {
    single<PokemonRepository> {
        PokemonRepositoryImpl(get(), get())
    }
}

val networkModule = module {
    single {
        getRetrofit()
    }
}

val applicationModule = module {

}


fun getRetrofit(): Retrofit {
    return Retrofit.Builder().build()
}