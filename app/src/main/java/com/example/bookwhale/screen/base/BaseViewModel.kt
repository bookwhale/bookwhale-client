package com.example.bookwhale.screen.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.preference.MyPreferenceManager
import com.example.bookwhale.data.repository.login.LoginRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.login.TokenRequestDTO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject


abstract class BaseViewModel: ViewModel() {

    protected var stateBundle: Bundle? = null

    open val myPreferenceManager = object: KoinComponent {val myPreferenceManager: MyPreferenceManager by inject()}.myPreferenceManager
    open val loginRepository = object: KoinComponent {val loginRepository: LoginRepository by inject()}.loginRepository

    open fun fetchData(): Job = viewModelScope.launch {

    }

    open fun storeState(stateBundle: Bundle) {
        this.stateBundle = stateBundle
    }

    fun getNewTokens() = viewModelScope.launch {
        val response = loginRepository.getNewTokens(TokenRequestDTO(
            apiToken = myPreferenceManager.getAccessToken()!!,
            refreshToken = myPreferenceManager.getRefreshToken()!!
        ))

        if (response.status == NetworkResult.Status.SUCCESS) {
            myPreferenceManager.putAccessToken(response.data?.apiToken!!)
            myPreferenceManager.putRefreshToken(response.data.refreshToken!!)
        } else {
            // error
        }

    }
}