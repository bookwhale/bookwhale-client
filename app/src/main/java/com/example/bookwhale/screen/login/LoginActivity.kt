package com.example.bookwhale.screen.login

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.example.bookwhale.databinding.ActivityLoginBinding
import com.example.bookwhale.screen.base.BaseActivity
import org.koin.android.viewmodel.ext.android.viewModel
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.R
import com.example.bookwhale.screen.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLogin.mOAuthLoginHandler
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.data.OAuthLoginState
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    override val viewModel by viewModel<LoginViewModel>()

    override fun getViewBinding(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)

    private lateinit var mOAuthLoginModule : OAuthLogin


    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode("1042391167372-hks49suv33nb0v6licmhnffr1cvv1k88.apps.googleusercontent.com")
            .build()
    }

    private val gsc by lazy { GoogleSignIn.getClient(this, gso) }

    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                task.getResult(ApiException::class.java)?.let { account ->
                    Log.e("serverauthCode",account.serverAuthCode!!)
                    viewModel.getGoogleAccesToken(account.serverAuthCode!!)
                }
            } catch (e: Exception) {
                Log.e("idToken2","ididididid")
                e.printStackTrace()
            }
        } else {
            Log.e("idToken3",result.data.toString())
            Log.e("idToken3",result.resultCode.toString())
        }
    }

    override fun initViews(): Unit = with(binding) {

        setGooglePlusButtonText(googleLoginButton,getString(R.string.signInGoogle))


        signInNaver()

        googleLoginButton.setOnClickListener {
            signInGoogle()
        }

    }

    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }

    private fun signInNaver() = with(binding) {

        mOAuthLoginModule = OAuthLogin.getInstance()
        mOAuthLoginModule.init(
            this@LoginActivity
            ,getString(R.string.naver_client_id)
            ,getString(R.string.naver_client_secret)
            ,getString(R.string.naver_client_name)
        )

        naverLoginButton.setOnClickListener {

            @SuppressLint("HandlerLeak")
            val mOAuthLoginHandler: OAuthLoginHandler = object : OAuthLoginHandler() {
                override fun run(success: Boolean) {
                    if (success) {
                        val accessToken: String = mOAuthLoginModule.getAccessToken(baseContext)

                        lifecycleScope.launch {
                            viewModel.naverLogin(accessToken).join()
                        }

                    }
                    else {
                        val errorCode: String = mOAuthLoginModule.getLastErrorCode(this@LoginActivity).code
                        val errorDesc = mOAuthLoginModule.getLastErrorDesc(this@LoginActivity)
                    }
                }

            }
            mOAuthLoginModule.startOauthLoginActivity(this@LoginActivity, mOAuthLoginHandler)
        }

        naverLogout.setOnClickListener {
            mOAuthLoginModule.logout(this@LoginActivity)
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

    override fun observeData() = with(binding) {
        viewModel.loginStateLiveData.observe(this@LoginActivity) {
            when (it) {
                is LoginState.Loading -> handleLoading()
                is LoginState.Success -> handleSuccess(it)
                is LoginState.Error -> handleError()
                else -> Unit
            }
        }
    }

    private fun handleLoading() {}
    private fun handleSuccess(state: LoginState.Success) {
        startActivity(MainActivity.newIntent(this))
    }
    private fun handleError() {}
}