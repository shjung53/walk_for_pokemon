package com.ssafy.walkforpokemon

import android.app.Application
import com.google.firebase.FirebaseApp
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NaverIdLoginSDK.initialize(
            this,
            BuildConfig.NAVER_LOGIN_CLIENT_ID,
            BuildConfig.NAVER_LOGIN_CLIENT_SECRET,
            "Walk for Pokemon",
        )
        FirebaseApp.initializeApp(this)
    }
}
