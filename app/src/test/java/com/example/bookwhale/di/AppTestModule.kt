package com.example.bookwhale.di

import com.example.bookwhale.data.repository.FakeArticleRepository
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.screen.main.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appTestModule = module {

    viewModel { MainViewModel(get()) }

    single <ArticleRepository> { FakeArticleRepository() }

}