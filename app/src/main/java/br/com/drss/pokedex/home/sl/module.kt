package br.com.drss.pokedex.home.sl

import org.koin.dsl.module
import retrofit2.Retrofit


val repositoryModule = module {
    single {

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