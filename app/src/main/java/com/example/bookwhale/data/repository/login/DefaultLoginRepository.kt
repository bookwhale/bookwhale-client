package com.example.bookwhale.data.repository.login

import android.util.Log
import com.example.bookwhale.data.entity.login.LoginEntity
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.login.LoginGoogleRequestDTO
import com.example.bookwhale.data.response.login.LoginGoogleResponse
import com.example.bookwhale.data.response.login.TokenRequestDTO
import com.google.gson.annotations.SerializedName
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

    override suspend fun getNaverLoginInfo(code: String): LoginEntity = withContext(ioDispatcher) {
        val response = serverApiService.getNaverLoginInfo(code)

        response.body()?.let{
            return@withContext LoginEntity(
                apiToken = it.apiToken,
                refreshToken = it.refreshToken
            )
        } ?: kotlin.run {
            return@withContext LoginEntity(
                apiToken = null,
                refreshToken = null
            )
        }
    }

    override suspend fun getKaKaoLoginInfo(code: String): LoginEntity = withContext(ioDispatcher) {
        val response = serverApiService.getKaKaoLoginInfo(code)

        response.body()?.let{
            return@withContext LoginEntity(
                apiToken = it.apiToken,
                refreshToken = it.refreshToken
            )
        } ?: kotlin.run {
            return@withContext LoginEntity(
                apiToken = null,
                refreshToken = null
            )
        }
    }

    override suspend fun getNewTokens(tokenRequestDTO: TokenRequestDTO): LoginEntity = withContext(ioDispatcher) {
        val response = serverApiService.getNewTokens(tokenRequestDTO)

        response.body()?.let{
            return@withContext LoginEntity(
                apiToken = it.apiToken,
                refreshToken = it.refreshToken
            )
        } ?: kotlin.run {
            return@withContext LoginEntity(
                apiToken = null,
                refreshToken = null
            )
        }
    }
}