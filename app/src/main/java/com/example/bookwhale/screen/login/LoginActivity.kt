package com.example.bookwhale.screen.login

import com.example.bookwhale.databinding.ActivityLoginBinding
import com.example.bookwhale.screen.base.BaseActivity
import org.koin.android.viewmodel.ext.android.viewModel
import android.widget.TextView
import com.example.bookwhale.R
import com.example.bookwhale.screen.main.MainActivity
import com.google.android.gms.common.SignInButton

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    override val viewModel by viewModel<LoginViewModel>()

    override fun getViewBinding(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)

    override fun initViews(): Unit = with(binding) {

        setGooglePlusButtonText(googleLoginButton,getString(R.string.signInGoogle))

        naverLoginButton.setOnClickListener {
            startActivity(MainActivity.newIntent(this@LoginActivity))
        }
    }

    private fun setGooglePlusButtonText(signInButton: SignInButton, buttonText: String?) {
        for (i in 0 until signInButton.childCount) {
            val v = signInButton.getChildAt(i)
            if (v is TextView) {
                v.text = buttonText
                return
            }
        }
    }

    override fun observeData() {

    }
}