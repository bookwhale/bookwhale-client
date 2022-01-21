package com.example.bookwhale.data.repository.login

import android.util.Log
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.entity.login.LoginEntity
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
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

    override suspend fun getNewTokens(tokenRequestDTO: TokenRequestDTO): NetworkResult<LoginEntity> = withContext(ioDispatcher) {
        val response = serverApiService.getNewTokens(tokenRequestDTO)

        if(response.isSuccessful) {
            NetworkResult.success(
                LoginEntity(
                    apiToken = response.body()!!.apiToken,
                    refreshToken = response.body()!!.refreshToken
                )
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }

    }
}