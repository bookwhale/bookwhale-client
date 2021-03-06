package com.example.bookwhale.data.repository.login

import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.login.TokenRequestDTO
import com.example.bookwhale.model.auth.LoginModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LoginRepositoryImpl(
    private val serverApiService: ServerApiService,
    private val ioDispatcher: CoroutineDispatcher
) : LoginRepository {

    override suspend fun getNaverLoginInfo(code: String, deviceToken: String): NetworkResult<LoginModel> = withContext(ioDispatcher) {
        val response = serverApiService.getNaverLoginInfo(code, deviceToken)

        if (response.isSuccessful) {
            NetworkResult.success(
                LoginModel(
                    apiToken = response.body()!!.apiToken,
                    refreshToken = response.body()!!.refreshToken
                )
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun getKaKaoLoginInfo(code: String, deviceToken: String): NetworkResult<LoginModel> = withContext(ioDispatcher) {
        val response = serverApiService.getKaKaoLoginInfo(code, deviceToken)

        if (response.isSuccessful) {
            NetworkResult.success(
                LoginModel(
                    apiToken = response.body()!!.apiToken,
                    refreshToken = response.body()!!.refreshToken
                )
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun getNewTokens(tokenRequestDTO: TokenRequestDTO): NetworkResult<LoginModel> = withContext(ioDispatcher) {
        val response = serverApiService.getNewTokens(tokenRequestDTO)

        if (response.isSuccessful) {
            NetworkResult.success(
                LoginModel(
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
