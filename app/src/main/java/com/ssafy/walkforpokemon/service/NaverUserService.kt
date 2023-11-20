package com.ssafy.walkforpokemon.service

import com.google.gson.annotations.SerializedName
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.network.ServiceFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface NaverUserService {
    @GET("nid/me")
    suspend fun fetchUserProfile(
        @Header("Authorization") accessToken: String,
    ): UserResponse

    companion object {
        private const val baseUrl = "https://openapi.naver.com/v1/"

        private val naverUserService =
            ServiceFactory.createRetrofitService<NaverUserService>(baseUrl)

        fun getService(): NaverUserService = naverUserService
    }
}

data class UserResponse(
    @SerializedName("response")val response: User,
)
