package com.example.bookwhale.di

import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.util.provider.DefaultResourcesProvider
import com.example.bookwhale.util.provider.ResourcesProvider
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { MainViewModel() }

//    single<MainRepository> { DefaultMainRepository(get(), get()) }

    single { Dispatchers.IO }
    single { Dispatchers.Main }

    //ResourcesProvider
    single<ResourcesProvider> { DefaultResourcesProvider(androidApplication()) }

}