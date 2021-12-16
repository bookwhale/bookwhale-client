package com.example.bookwhale.di

import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.repository.main.DefaultArticleRepository
import com.example.bookwhale.screen.login.LoginViewModel
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.screen.main.home.HomeViewModel
import com.example.bookwhale.screen.main.lielist.LikeListViewModel
import com.example.bookwhale.screen.test.TestViewModel
import com.example.bookwhale.util.provider.DefaultResourcesProvider
import com.example.bookwhale.util.provider.ResourcesProvider
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    viewModel { MainViewModel() }
    viewModel { HomeViewModel(get()) }
    viewModel { TestViewModel() }
    viewModel { LikeListViewModel(get()) }
    viewModel { LoginViewModel() }

    single<ArticleRepository> { DefaultArticleRepository(get(), get()) }

    single { Dispatchers.IO }
    single { Dispatchers.Main }

    //ResourcesProvider
    single<ResourcesProvider> { DefaultResourcesProvider(androidApplication()) }

    //Retrofit
    single(named("article")) { provideArticleRetrofit(get(), get()) }
    single { provideArticleApiService(get(qualifier = named("article"))) }

    single { provideGsonConvertFactory() }
    single { buildOkHttpClient() }


}