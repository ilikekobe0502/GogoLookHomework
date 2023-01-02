package com.example.gogolookhomework.di

import android.content.Context
import com.example.gogolookhomework.model.repository.SearchRepository
import com.example.gogolookhomework.model.repository.SearchRepositoryImpl
import com.example.gogolookhomework.network.ApiModule
import com.example.gogolookhomework.network.AuthInterceptor
import com.example.gogolookhomework.ui.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class KoinModules {
    companion object {
        fun initKoin(app: Context) {
            startKoin {
                androidContext(app)
                modules(
                    listOf(
                        networkModule,
                        viewModelModule,
                        dbModule
                    )
                )
            }
        }
    }
}

val networkModule = module {
    single { AuthInterceptor() }
    single { ApiModule.provideOkHttpClient(get()) }
    single { ApiModule.provideRetrofit(get()) }
    single { ApiModule.provideAirStatusApi(get()) }
    factory<SearchRepository> { SearchRepositoryImpl(get()) }
}

val dbModule = module {
//    single { CurrencyDatabase.build(get()) }
//    factory<CurrencyDbRepository> { CurrencyDbRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { SearchViewModel(get()) }
}