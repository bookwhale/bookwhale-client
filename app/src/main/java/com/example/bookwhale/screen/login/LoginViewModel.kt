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
    private val loginRepository: LoginRepository,
    private val myPreferenceManager: MyPreferenceManager
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

    fun getGoogleAccesToken(code: String) = viewModelScope.launch {
        val response = loginRepository.fetchGoogleAuthInfo(code)

        Log.e("response is", response.toString())

        googleLogin(response!!.access_token)
    }

    fun googleLogin(code: String) = viewModelScope.launch {
        val response = loginRepository.getGoogleLoginInfo(code)

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