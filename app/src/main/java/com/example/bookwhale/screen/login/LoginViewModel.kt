package com.example.bookwhale.screen.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.entity.login.LoginEntity
import com.example.bookwhale.data.preference.MyPreferenceManager
import com.example.bookwhale.data.repository.login.LoginRepository
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(
    override val loginRepository: LoginRepository,
    override val myPreferenceManager: MyPreferenceManager
): BaseViewModel() {

    val loginStateLiveData = MutableLiveData<LoginState>(LoginState.Uninitialized)

    fun naverLogin(code: String) = viewModelScope.launch {
        val response = loginRepository.getNaverLoginInfo(code)

        response.apiToken?.let{
            loginStateLiveData.value = LoginState.Success(LoginEntity(
                apiToken = response.apiToken,
                refreshToken = response.refreshToken
            ))
            myPreferenceManager.putAccessToken(response.apiToken)
            myPreferenceManager.putRefreshToken(response.refreshToken!!)
        } ?: kotlin.run {
            loginStateLiveData.value = LoginState.Error
        }
    }

    fun kakaoLogin(code: String) = viewModelScope.launch {
        val response = loginRepository.getKaKaoLoginInfo(code)

        response.apiToken?.let{
            loginStateLiveData.value = LoginState.Success(LoginEntity(
                apiToken = response.apiToken,
                refreshToken = response.refreshToken
            ))
            myPreferenceManager.putAccessToken(response.apiToken)
            myPreferenceManager.putRefreshToken(response.refreshToken!!)
        } ?: kotlin.run {
            loginStateLiveData.value = LoginState.Error
        }
    }
}