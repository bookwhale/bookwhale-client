package com.example.bookwhale.data.network

import android.content.Context
import android.util.Log
import com.example.bookwhale.data.preference.MyPreferenceManager
import kotlinx.coroutines.DelicateCoroutinesApi
import okhttp3.Interceptor
import okhttp3.Response

class CustomAuthInterceptor(
    private val myPreferenceManager: MyPreferenceManager
) : Interceptor {

    @DelicateCoroutinesApi
    override fun intercept(chain: Interceptor.Chain): Response {

        val token = "Bearer ${myPreferenceManager.getAccessToken()}"

        try {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()

            return chain.proceed(request)

        } catch (SocketTimeoutException: Exception) {
            Log.e("time is Out", "timeOut")
            return chain.proceed(chain.request())
        }
    }

}