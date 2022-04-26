package com.example.bookwhale

import android.app.Application
import android.content.Context
import com.example.bookwhale.di.appModule
import com.kakao.sdk.common.KakaoSdk
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)

        startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appContext = null
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}
