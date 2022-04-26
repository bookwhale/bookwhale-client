package com.example.bookwhale.data.network

import android.util.Log
import com.example.bookwhale.data.preference.MyPreferenceManager
import okhttp3.Interceptor
import okhttp3.Response

class CustomAuthInterceptor(
    private val myPreferenceManager: MyPreferenceManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = "Bearer ${myPreferenceManager.getAccessToken()}"

        return try {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()

            chain.proceed(request)
        } catch (SocketTimeoutException: Exception) {
            Log.e("SocketTimeoutException", "$SocketTimeoutException")
            chain.proceed(chain.request())
        }
    }
}
