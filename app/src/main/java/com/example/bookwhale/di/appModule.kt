package com.example.bookwhale.di

import com.example.bookwhale.data.network.buildOkHttpClient
import com.example.bookwhale.data.network.provideArticleApiService
import com.example.bookwhale.data.network.provideArticleRetrofit
import com.example.bookwhale.data.network.provideChatApiService
import com.example.bookwhale.data.network.provideChatRetrofit
import com.example.bookwhale.data.network.provideGsonConvertFactory
import com.example.bookwhale.data.preference.MyPreferenceManager
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.repository.article.DetailRepositoryImpl
import com.example.bookwhale.data.repository.article.ModifyArticleRepository
import com.example.bookwhale.data.repository.article.ModifyArticleRepositoryImpl
import com.example.bookwhale.data.repository.article.PostArticleRepository
import com.example.bookwhale.data.repository.article.PostArticleRepositoryImpl
import com.example.bookwhale.data.repository.chat.ChatRepository
import com.example.bookwhale.data.repository.chat.ChatRepositoryImpl
import com.example.bookwhale.data.repository.login.LoginRepository
import com.example.bookwhale.data.repository.login.LoginRepositoryImpl
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.repository.main.ArticleRepositoryImpl
import com.example.bookwhale.data.repository.my.MyRepository
import com.example.bookwhale.data.repository.my.MyRepositoryImpl
import com.example.bookwhale.screen.article.DetailArticleViewModel
import com.example.bookwhale.screen.article.ModifyArticleViewModel
import com.example.bookwhale.screen.article.PostArticleViewModel
import com.example.bookwhale.screen.article.SearchViewModel
import com.example.bookwhale.screen.chatroom.ChatRoomViewModel
import com.example.bookwhale.screen.login.LoginViewModel
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.screen.main.my.MyViewModel
import com.example.bookwhale.screen.splash.SplashViewModel
import com.example.bookwhale.util.EventBus
import com.example.bookwhale.util.MessageChannel
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.util.provider.ResourcesProviderImpl
import com.example.bookwhale.widget.adapter.MainPagingSource
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { MyViewModel(get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { DetailArticleViewModel(get(), get(), get(), get(), get()) }
    viewModel { PostArticleViewModel(get()) }
    viewModel { ModifyArticleViewModel(get(), get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { ChatRoomViewModel(get()) }

    single<MyRepository> { MyRepositoryImpl(get(), get()) }
    single<LoginRepository> { LoginRepositoryImpl(get(), get()) }
    single<ArticleRepository> { ArticleRepositoryImpl(get(), get()) }
    single<ChatRepository> { ChatRepositoryImpl(get(), get(), get(), get()) }
    single<DetailRepository> { DetailRepositoryImpl(get(), get()) }
    single<PostArticleRepository> { PostArticleRepositoryImpl(get(), get()) }
    single<ModifyArticleRepository> { ModifyArticleRepositoryImpl(get(), get()) }

    single { Dispatchers.IO }
    single { Dispatchers.Main }

    // ResourcesProvider
    single<ResourcesProvider> { ResourcesProviderImpl(androidApplication()) }

    // EventBus
    single { EventBus() }

    // MessageChannel
    single { MessageChannel() }

    // SharedPreference
    single { MyPreferenceManager(androidApplication()) }

    // Retrofit
    single(named("article")) { provideArticleRetrofit(get(), get()) }
    single { provideArticleApiService(get(qualifier = named("article"))) }

    single(named("chat")) { provideChatRetrofit(get(), get()) }
    single { provideChatApiService(get(qualifier = named("chat"))) }

    single { provideGsonConvertFactory() }
    single { buildOkHttpClient() }

    // paging3
    single { MainPagingSource(get()) }
}
