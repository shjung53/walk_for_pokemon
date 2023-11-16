package com.ssafy.walkforpokemon.network

import retrofit2.converter.gson.GsonConverterFactory

object GsonConverter {
    val gsonConverter: GsonConverterFactory = GsonConverterFactory.create()
}
