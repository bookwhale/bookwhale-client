package com.example.bookwhale.screen.login

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.BuildConfig
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivityLoginBinding
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.screen.main.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLogin.mOAuthLoginHandler
import com.nhn.android.naverlogin.OAuthLoginHandler
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    override val viewModel by viewModel<LoginViewModel>()

    override fun getViewBinding(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)

    private lateinit var mOAuthLoginModule: OAuthLogin

    override fun initViews(): Unit = with(binding) {

        setSignInNaver()
        setSignInKaKao()
        tempGetHashCode()
    }

    private fun tempGetHashCode() = with(binding) {
        getKeyHashButton.setOnClickListener {
            var keyHash = Utility.getKeyHash(this@LoginActivity)
            Log.e("keyhash", " : $keyHash")

            val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", "$keyHash")
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this@LoginActivity, "클립보드에 복사하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setSignInKaKao() = with(binding) {

        kakaoLoginButton.setOnClickListener {

            var keyHash = Utility.getKeyHash(this@LoginActivity)
            Log.d("keyhash", " : $keyHash")

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
                UserApiClient.instance.loginWithKakaoTalk(this@LoginActivity, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this@LoginActivity, callback = callback)
            }
        }
    }

    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("로그인 실패-", "$error")
        } else if (token != null) {
            UserApiClient.instance.me { _, _ ->
                viewModel.kakaoLogin(token.accessToken)
                // viewModel?.addKakaoUser(token.accessToken, kakaoId)
            }
            Log.d("로그인성공 - 토큰", token.accessToken)
        }
    }

    private fun setSignInNaver() = with(binding) {

        mOAuthLoginModule = OAuthLogin.getInstance()
        mOAuthLoginModule.init(
            this@LoginActivity, getString(R.string.naver_client_id), BuildConfig.NAVER_CLIENT_SECRET, getString(R.string.naver_client_name)
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
                    } else {
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

        val intent = MainActivity.newIntent(this@LoginActivity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
    private fun handleError() {}

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}
