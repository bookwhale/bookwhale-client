package com.example.bookwhale.screen.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookwhale.databinding.ActivityTestBinding
import com.example.bookwhale.screen.base.BaseActivity
import org.koin.android.viewmodel.ext.android.viewModel

class TestActivity : BaseActivity<TestViewModel, ActivityTestBinding>() {

    override val viewModel by viewModel<TestViewModel>()

    override fun getViewBinding(): ActivityTestBinding = ActivityTestBinding.inflate(layoutInflater)

    override fun observeData() = with(binding) {
        viewModel.testLiveData.observe(this@TestActivity) {
            textView.text = it
            // test
        }
    }

}