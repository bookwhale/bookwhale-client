package com.example.bookwhale.data.network

import android.content.Context
import com.example.bookwhale.BuildConfig
import com.example.bookwhale.data.preference.MyPreferenceManager
import com.example.bookwhale.data.url.Url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


fun provideArticleApiService(retrofit: Retrofit): ServerApiService {
    return retrofit.create(ServerApiService::class.java)
}


fun provideArticleRetrofit(
    okHttpClient: OkHttpClient,
    gsonConverterFactory: GsonConverterFactory
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(Url.SERVER_URL)
        .addConverterFactory(gsonConverterFactory)
        .client(okHttpClient)
        .build()
}

fun provideGsonConvertFactory(): GsonConverterFactory {
    return GsonConverterFactory.create()
}

fun buildOkHttpClient(): OkHttpClient {

    val myPreferenceManager = object: KoinComponent {val myPreferenceManager: MyPreferenceManager by inject()}.myPreferenceManager
    val context = object: KoinComponent {val context: Context by inject()}.context

    val interceptor = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG) {
        interceptor.level = HttpLoggingInterceptor.Level.BODY
    } else {
        interceptor.level = HttpLoggingInterceptor.Level.NONE
    }
    return OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addInterceptor(CustomAuthInterceptor(myPreferenceManager, context))
        .build()
}