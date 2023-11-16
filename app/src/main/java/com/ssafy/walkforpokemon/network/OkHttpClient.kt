package com.ssafy.walkforpokemon.network

import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient

object OkHttpClient {
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    val client = OkHttpClient
        .Builder()
        .addInterceptor(logger)
        .build()
}
