package com.example.bookwhale.data.network

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

fun provideChatApiService(retrofit: Retrofit): ChatApiService {
    return retrofit.create(ChatApiService::class.java)
}

fun provideChatRetrofit(
    okHttpClient: OkHttpClient,
    gsonConverterFactory: GsonConverterFactory
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(Url.CHAT_URL)
        .addConverterFactory(gsonConverterFactory)
        .client(okHttpClient)
        .build()
}

fun provideGsonConvertFactory(): GsonConverterFactory {
    return GsonConverterFactory.create()
}

fun buildOkHttpClient(): OkHttpClient {

    val myPreferenceManager = object : KoinComponent { val myPreferenceManager: MyPreferenceManager by inject() }.myPreferenceManager

    val interceptor = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG) {
        interceptor.level = HttpLoggingInterceptor.Level.BODY
    } else {
        interceptor.level = HttpLoggingInterceptor.Level.NONE
    }
    return OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addInterceptor(CustomAuthInterceptor(myPreferenceManager))
        .build()
}

const val CONNECT_TIMEOUT_SECONDS = 5L
