package com.example.bookwhale.screen.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.preference.MyPreferenceManager
import com.example.bookwhale.data.repository.login.LoginRepository
import com.example.bookwhale.data.repository.my.MyRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.login.TokenRequestDTO
import com.example.bookwhale.model.auth.LoginModel
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(
    private val myRepository: MyRepository,
    override val loginRepository: LoginRepository,
    override val myPreferenceManager: MyPreferenceManager
) : BaseViewModel() {

    val loginStateLiveData = MutableLiveData<LoginState>(LoginState.Uninitialized)

    init {
        myPreferenceManager.getAccessToken()
    }

    override fun fetchData(): Job = viewModelScope.launch {

        loginStateLiveData.value = LoginState.Loading

        if (!myPreferenceManager.getAccessToken().isNullOrEmpty() &&
            !myPreferenceManager.getRefreshToken().isNullOrEmpty()
        ) {
            val response = myRepository.getMyInfo()
            if (response.status == NetworkResult.Status.SUCCESS) {
                // 엑세스토큰 유효. 로그인 성공.
                loginStateLiveData.value = LoginState.AutoSuccess
            } else {
                // 엑세스토큰 만료, 리프레시 검증
                val refreshResponse = loginRepository.getNewTokens(
                    TokenRequestDTO(
                        apiToken = myPreferenceManager.getAccessToken()!!,
                        refreshToken = myPreferenceManager.getRefreshToken()!!
                    )
                )

                if (refreshResponse.status == NetworkResult.Status.SUCCESS) {
                    // 리프레시 토큰 유효. 새로운 토큰 발급받은 후 메인화면 유지
                    loginStateLiveData.value = LoginState.AutoSuccess
                    saveUserPreference(refreshResponse.data?.apiToken!!, refreshResponse.data.refreshToken!!)
                } else {
                    // 리프레시 토큰 만료. 로그인 창으로 재 진입한다.
                    loginStateLiveData.value = LoginState.Error(refreshResponse.code)
                }
            }
        } else {
            loginStateLiveData.value = LoginState.Error("T_001")
        }
    }

    private fun saveUserPreference(accessToken: String, refreshToken: String) {
        myPreferenceManager.putAccessToken(accessToken)
        myPreferenceManager.putRefreshToken(refreshToken)
    }

    fun saveDeviceToken(deviceToken: String) {
        myPreferenceManager.putDeviceToken(deviceToken)
    }

    fun naverLogin(code: String) = viewModelScope.launch {
        val response = loginRepository.getNaverLoginInfo(code, myPreferenceManager.getDeviceToken()!!)

        if (response.status == NetworkResult.Status.SUCCESS) {
            loginStateLiveData.value = LoginState.Success(
                LoginModel(
                    apiToken = response.data!!.apiToken,
                    refreshToken = response.data.refreshToken
                )
            )
            response.data.apiToken?.let { myPreferenceManager.putAccessToken(it) }
            response.data.refreshToken?.let { myPreferenceManager.putRefreshToken(it) }
        } else {
            loginStateLiveData.value = LoginState.Error(
                response.code
            )
        }
    }

    fun kakaoLogin(code: String) = viewModelScope.launch {
        val response = loginRepository.getKaKaoLoginInfo(code, myPreferenceManager.getDeviceToken()!!)

        if (response.status == NetworkResult.Status.SUCCESS) {
            loginStateLiveData.value = LoginState.Success(
                LoginModel(
                    apiToken = response.data!!.apiToken,
                    refreshToken = response.data.refreshToken
                )
            )
            response.data.apiToken?.let { myPreferenceManager.putAccessToken(it) }
            response.data.refreshToken?.let { myPreferenceManager.putRefreshToken(it) }
        } else {
            loginStateLiveData.value = LoginState.Error(
                response.code
            )
        }
    }
}
