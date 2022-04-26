package com.example.bookwhale.screen.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.preference.MyPreferenceManager
import com.example.bookwhale.data.repository.login.LoginRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.auth.LoginModel
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(
    override val loginRepository: LoginRepository,
    override val myPreferenceManager: MyPreferenceManager
) : BaseViewModel() {

    val loginStateLiveData = MutableLiveData<LoginState>(LoginState.Uninitialized)

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
