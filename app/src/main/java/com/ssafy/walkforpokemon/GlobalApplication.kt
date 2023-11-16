package com.ssafy.walkforpokemon

import android.app.Application
import com.navercorp.nid.NaverIdLoginSDK

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        NaverIdLoginSDK.initialize(this, BuildConfig.NAVER_LOGIN_CLIENT_ID, BuildConfig.NAVER_LOGIN_CLIENT_SECRET, "Walk for Pokemon")
    }
}
