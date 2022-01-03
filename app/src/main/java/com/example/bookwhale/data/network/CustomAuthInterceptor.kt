package com.example.bookwhale.data.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.bookwhale.R
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

        try {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()
            val response = chain.proceed(request)

            // 코드 대응
            when (response.code) {
                200 -> {
                    Log.e("200",response.code.toString())
                }
                201 -> {
                    Log.e("201",response.code.toString())
                }
                400 -> {
                    handle400Error()
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
                    Log.e("else",response.code.toString())
                    // ELSE
                }
            }
            return response

        } catch (SocketTimeoutException: Exception) {
            Log.e("time is Out", "timeOut")
            return chain.proceed(chain.request())
        }
    }

    private fun handle400Error() {
        //
    }
    private fun handle500Error() {
        //
    }

}