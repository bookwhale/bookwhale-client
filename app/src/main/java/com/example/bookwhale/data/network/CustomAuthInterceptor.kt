package com.example.bookwhale.data.network

import android.content.Context
import android.util.Log
import com.example.bookwhale.data.preference.MyPreferenceManager
import kotlinx.coroutines.DelicateCoroutinesApi
import okhttp3.Interceptor
import okhttp3.Response

class CustomAuthInterceptor(
    private val myPreferenceManager: MyPreferenceManager,
    private val context: Context
) : Interceptor {

    @DelicateCoroutinesApi
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "Bearer ${myPreferenceManager.getAccessToken()}"

        Log.e("token is What?", token)
        Log.e("refresh is What?", myPreferenceManager.getRefreshToken().toString())

        try {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()
            val response = chain.proceed(request)

            // 에러코드 대응
            when (response.code) {
                400 -> {
                    Log.e("400","400error")
                    //Bad Request Error
                }
                401 -> {
                    Log.e("401","401error")
                    //Unauthorized Error
                }
                403 -> {
                    Log.e("403","403error")
                    //Forbidden Error
                }
                404 -> {
                    Log.e("404","404error")
                    //NotFound Error
                }
                500 -> {
                    Log.e("500","500error")
                    //INTERNAL SERVER ERROR
                }
                else -> {
                    Log.e("elseError",response.body.toString())
                    // ELSE
                }
            }
            return response

        } catch (SocketTimeoutException: Exception) {
            Log.e("time is Out", "timeOut")
            return chain.proceed(chain.request())
        }
    }

    private fun error500handle() {
        //
    }

}