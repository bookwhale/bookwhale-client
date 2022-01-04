package com.example.bookwhale.data.repository.login

import android.util.Log
import com.example.bookwhale.data.entity.login.LoginEntity
import com.example.bookwhale.data.network.GoogleApiService
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.login.LoginGoogleRequestDTO
import com.example.bookwhale.data.response.login.LoginGoogleResponse
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultLoginRepository(
    private val serverApiService: ServerApiService,
    private val googleApiService: GoogleApiService,
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

    override suspend fun getGoogleLoginInfo(code: String): LoginEntity = withContext(ioDispatcher) {
        val response = serverApiService.getGoogleLoginInfo(code)

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

    override suspend fun fetchGoogleAuthInfo(code: String): LoginGoogleResponse? = withContext(ioDispatcher) {
        val response = googleApiService.fetchGoogleAuthInfo(LoginGoogleRequestDTO(
            code = code,
            client_id = "1042391167372-hks49suv33nb0v6licmhnffr1cvv1k88.apps.googleusercontent.com",
            client_secret = "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",
            redirect_uri = "https://project-bookwhale.firebaseapp.com/__/auth/handler",
            grant_type = "authorization_code"
        ))

        Log.e("what is response", response.toString())

        response.body()?.let {
            return@withContext LoginGoogleResponse(
                access_token = it.access_token,
                expires_in = it.expires_in,
                scope = it.scope,
                token_type = it.token_type,
                id_token = it.id_token
            )
        } ?: kotlin.run {
            null
        }
    }
}