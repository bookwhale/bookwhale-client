package com.example.bookwhale.screen.splash

import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivitySplashBinding
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.screen.login.LoginActivity
import com.example.bookwhale.screen.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {
    override val viewModel by viewModel<SplashViewModel>()

    override fun getViewBinding(): ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)

    override fun initViews(): Unit = with(binding) {

    }

    override fun observeData() {
        viewModel.splashState.observe(this ) {
            when(it) {
                is SplashState.Uninitialized -> Unit
                is SplashState.Loading -> handleLoading()
                is SplashState.Success -> handleSuccess()
                is SplashState.Error -> handleError(it)
            }
        }
    }

    private fun handleLoading() {
        binding.progressBar.isVisible = true
    }

    private fun handleSuccess() {
        lifecycleScope.launch {
            delay(1500L) // 추후 특정 시간 걸리는 작업을 염두하고 임의로 딜레이를 주었다.
            binding.progressBar.isGone = true
            startActivity(MainActivity.newIntent(this@SplashActivity))
        }
    }

    private fun handleError(state: SplashState.Error) {
        when(state.code) {
            "S_001" -> handleS001()
            "T_001" -> handleGotoLogin()
            "T_002" -> handleGotoLogin()
            "T_003" -> handleGotoLogin()
            "T_004" -> handleGotoLogin()
            else -> handleUnExcepted(state.code!!)
        }
    }

    private fun handleS001() {
        binding.progressBar.isGone = true
        Toast.makeText(this, R.string.error_noConnect, Toast.LENGTH_SHORT).show()
    }

    private fun handleGotoLogin() {
        lifecycleScope.launch {
            delay(1500L) // 추후 특정 시간 걸리는 작업을 염두하고 임의로 딜레이를 주었다.
            binding.progressBar.isGone = true
            startActivity(LoginActivity.newIntent(this@SplashActivity))
        }
    }

    private fun handleUnExcepted(code: String) {
        binding.progressBar.isGone = true
        Toast.makeText(this, getString(R.string.error_unKnown, code), Toast.LENGTH_SHORT).show()
    }
}