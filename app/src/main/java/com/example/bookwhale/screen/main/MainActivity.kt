package com.example.bookwhale.screen.main

import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivityMainBinding
import com.example.bookwhale.screen.base.BaseActivity
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val viewModel by viewModel<MainViewModel>()

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun observeData() {
    }


}