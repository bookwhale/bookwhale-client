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

//    var testSwitch = 0

    @DelicateCoroutinesApi
    override fun intercept(chain: Interceptor.Chain): Response {

        val token = "Bearer ${myPreferenceManager.getAccessToken()}"

//        lateinit var token : String
//
//        if(testSwitch <= 2) {
//            token = "Bearer ${myPreferenceManager.getAccessToken()}9977"
//            testSwitch++
//        } else {
//            token = "Bearer ${myPreferenceManager.getAccessToken()}"
//        }

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