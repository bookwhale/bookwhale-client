package com.example.bookwhale.di

import com.example.bookwhale.data.db.provideArticleDao
import com.example.bookwhale.data.db.provideDB
import com.example.bookwhale.data.network.*
import com.example.bookwhale.data.preference.MyPreferenceManager
import com.example.bookwhale.data.repository.article.DefaultDetailRepository
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.repository.chat.ChatRepository
import com.example.bookwhale.data.repository.chat.DefaultChatRepository
import com.example.bookwhale.data.repository.login.DefaultLoginRepository
import com.example.bookwhale.data.repository.login.LoginRepository
import com.example.bookwhale.data.repository.main.DefaultArticleRepository
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.repository.my.DefaultMyRepository
import com.example.bookwhale.data.repository.my.MyRepository
import com.example.bookwhale.screen.article.DetailArticleViewModel
import com.example.bookwhale.screen.main.my.MyViewModel
import com.example.bookwhale.screen.login.LoginViewModel
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.screen.main.chat.ChatViewModel
import com.example.bookwhale.screen.splash.SplashViewModel
import com.example.bookwhale.screen.test.TestViewModel
import com.example.bookwhale.util.paging.MainPagingSource
import com.example.bookwhale.util.provider.DefaultResourcesProvider
import com.example.bookwhale.util.provider.ResourcesProvider
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { TestViewModel() }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { MyViewModel(get()) }
    viewModel { SplashViewModel(get(), get(), get()) }
    viewModel { ChatViewModel(get())}
    viewModel { DetailArticleViewModel(get(),get())}

    single<MyRepository> { DefaultMyRepository(get(), get()) }
    single<LoginRepository> { DefaultLoginRepository(get(), get())}
    single<ArticleRepository> { DefaultArticleRepository(get(), get(), get()) }
    single<ChatRepository> { DefaultChatRepository(get(), get())}
    single<DetailRepository> { DefaultDetailRepository(get(), get())}

    single { Dispatchers.IO }
    single { Dispatchers.Main }

    //ResourcesProvider
    single<ResourcesProvider> { DefaultResourcesProvider(androidApplication()) }

    //SharedPreference
    single { MyPreferenceManager(androidApplication()) }

    //Retrofit
    single(named("article")) { provideArticleRetrofit(get(), get()) }
    single { provideArticleApiService(get(qualifier = named("article"))) }

    single { provideGsonConvertFactory() }
    single { buildOkHttpClient() }

    // Room
    single { provideDB(androidApplication()) }
    single { provideArticleDao(get()) }

    // paging3
    single { MainPagingSource(get()) }
}