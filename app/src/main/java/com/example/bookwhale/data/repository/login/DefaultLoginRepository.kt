package com.example.bookwhale.data.repository.login

import android.util.Log
import com.example.bookwhale.data.entity.login.NaverLoginEntity
import com.example.bookwhale.data.network.ServerApiService
import com.nhn.android.naverlogin.data.OAuthLoginState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultLoginRepository(
    private val serverApiService: ServerApiService,
    private val ioDispatcher: CoroutineDispatcher
):LoginRepository {
    override suspend fun test() {
        val response = serverApiService.testNaver()

        Log.e("response??",response.toString())
    }

    override suspend fun test2() {
        val response = serverApiService.testGoogle()

        Log.e("responseGoogle",response.toString())
    }

    override suspend fun getNaverLoginInfo(code: String): NaverLoginEntity = withContext(ioDispatcher) {
        val response = serverApiService.getNaverLoginInfo(code)

        response.body()?.let{
            return@withContext NaverLoginEntity(
                apiToken = it.apiToken,
                refreshToken = it.refreshToken
            )
        } ?: kotlin.run {
            return@withContext NaverLoginEntity(
                apiToken = null,
                refreshToken = null
            )
        }
    }
}