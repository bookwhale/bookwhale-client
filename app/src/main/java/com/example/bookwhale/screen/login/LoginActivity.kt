package com.example.bookwhale.screen.login

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.BuildConfig
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivityLoginBinding
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.screen.main.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
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
    private val roomId by lazy { intent.extras?.get(FCM_DATA_ROOM_ID) }
    private var isDoneGetUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return if (isDoneGetUser) {
                        // The content is ready; start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content is not ready; suspend.
                        false
                    }
                }
            }
        )
    }

    override fun initViews(): Unit = with(binding) {
        getCurrentDeviceToken()

        setSignInNaver()
        setSignInKaKao()
        tempGetHashCode()
    }

    private fun getCurrentDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                Log.d("deviceToken", task.result.toString())
                viewModel.saveDeviceToken(task.result)
            }
        )
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
                        Log.i("Naver Login Error : ", "code : $errorCode, desc : $errorDesc")
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
                is LoginState.AutoSuccess -> handleAutoSuccess()
                is LoginState.Success -> handleSuccess(it)
                is LoginState.Error -> handleError(it)
                else -> Unit
            }
        }
    }

    private fun handleLoading() {
        binding.progressBar.isVisible = true
    }

    private fun handleAutoSuccess() {
        binding.progressBar.isGone = true
        passToMain()
//        val intent = MainActivity.newIntent(this@LoginActivity)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
//                Intent.FLAG_ACTIVITY_CLEAR_TASK or
//                Intent.FLAG_ACTIVITY_CLEAR_TOP
//        startActivity(intent)
    }

    private fun handleSuccess(state: LoginState.Success) {
        binding.progressBar.isGone = true
        Log.i("Access Token : ", "${state.apiTokens}")
        val intent = MainActivity.newIntent(this@LoginActivity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun handleError(state: LoginState.Error) {
        when (state.code) {
            "S_001" -> handleS001()
            "T_001" -> handleAutoLoginFail(state.code)
            "T_002" -> handleAutoLoginFail(state.code)
            "T_003" -> handleAutoLoginFail(state.code)
            "T_004" -> handleAutoLoginFail(state.code)
            else -> handleUnExcepted(state)
        }
    }

    private fun handleS001() {
        Toast.makeText(this, R.string.error_noConnect, Toast.LENGTH_SHORT).show()
    }

    private fun handleAutoLoginFail(code: String) {
        isDoneGetUser = true
        Log.i("Token Expired, 에러코드 :", code)
    }

    private fun handleUnExcepted(state: LoginState.Error) {
        binding.progressBar.isGone = true
        Toast.makeText(this, getString(R.string.error_unKnown, state.code), Toast.LENGTH_SHORT).show()
    }

    private fun passToMain() {
        val intent = MainActivity.newIntent(this@LoginActivity, roomId as String?)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        // isDoneGetUser = true
        finish()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)

        const val FCM_DATA_ROOM_ID = "roomId"
    }
}
