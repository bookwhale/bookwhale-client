package com.example.bookwhale.screen.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.my.MyRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.login.TokenRequestDTO
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Splash Api 사용으로 더이상 사용하지 않습니다.
 */

class SplashViewModel(
    private val myRepository: MyRepository
) : BaseViewModel() {

    val splashState = MutableLiveData<SplashState>(SplashState.Uninitialized)

    init {
        myPreferenceManager.getAccessToken()
    }

    override fun fetchData(): Job = viewModelScope.launch {

        splashState.value = SplashState.Loading

        if (!myPreferenceManager.getAccessToken().isNullOrEmpty() &&
            !myPreferenceManager.getRefreshToken().isNullOrEmpty()
        ) {
            val response = myRepository.getMyInfo()
            if (response.status == NetworkResult.Status.SUCCESS) {
                // 엑세스토큰 유효. 로그인 성공.
                splashState.value = SplashState.Success
            } else {
                // 엑세스토큰 만료, 리프레시 검증
                val refreshResponse = loginRepository.getNewTokens(
                    TokenRequestDTO(
                        apiToken = myPreferenceManager.getAccessToken()!!,
                        refreshToken = myPreferenceManager.getRefreshToken()!!
                    )
                )

                if (refreshResponse.status == NetworkResult.Status.SUCCESS) {
                    // 리프레시 토큰 유효. 새로운 토큰 발급받은 후 메인화면 이동.
                    splashState.value = SplashState.Success
                    saveUserPreference(refreshResponse.data?.apiToken!!, refreshResponse.data.refreshToken!!)
                } else {
                    // 리프레시 토큰 만료. 로그인 창으로 재 진입한다.
                    splashState.value = SplashState.Error(refreshResponse.code)
                }
            }
        } else {
            splashState.value = SplashState.Error("T_001")
        }
    }

    private fun saveUserPreference(accessToken: String, refreshToken: String) {
        myPreferenceManager.putAccessToken(accessToken)
        myPreferenceManager.putRefreshToken(refreshToken)
    }

    fun saveDeviceToken(deviceToken: String) {
        myPreferenceManager.putDeviceToken(deviceToken)
    }
}
