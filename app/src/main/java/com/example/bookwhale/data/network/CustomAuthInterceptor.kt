package com.example.bookwhale.data.network

import android.util.Log
import com.example.bookwhale.data.preference.MyPreferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException

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
        } catch (socketTimeoutException: SocketTimeoutException) {
            Log.e("SocketTimeoutException", "$socketTimeoutException")
            chain.proceed(chain.request())
        }
    }
}
