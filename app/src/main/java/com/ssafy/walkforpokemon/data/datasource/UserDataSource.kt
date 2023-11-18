package com.ssafy.walkforpokemon.data.datasource

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.errorDescription
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.service.NaverUserService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

private const val TAG = "UserDataSource"

class UserDataSource @Inject constructor() {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun authenticateWithNaver(
        context: Context,
    ): Result<SuccessOrFailure> {
        val result = suspendCancellableCoroutine { continuation ->
            val callback = object : OAuthLoginCallback {

                override fun onSuccess() {
                    continuation.resume(Result.success(SuccessOrFailure.Success), null)
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Log.d(
                        TAG,
                        "onFailure() called with: httpStatus = $httpStatus, message = $message"
                    )
                    continuation.resume(
                        Result.failure(
                            Exception(errorDescription ?: "unknown error"),
                        ),
                        null,
                    )
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            }

            NaverIdLoginSDK.authenticate(context, callback)
        }

        return result
    }

    fun getLoginStatus() =
        NaverIdLoginSDK.getState()

    suspend fun fetchUserId(): Result<String> {
        val response = NaverUserService.getService()
            .fetchUserProfile("Bearer " + NaverIdLoginSDK.getAccessToken().toString())
        return Result.success(response.response.id)
    }

    suspend fun fetchUser(userId: String): Result<User> {
        val result = suspendCancellableCoroutine { continuation ->
            var user = User("")
            Firebase.firestore.collection("user").whereEqualTo("id", userId).get()
                .addOnSuccessListener { snapShot ->
                    for (document in snapShot) {
                        user = User(
                            id = document.data["id"] as String,
                            totalMileage = document.data["totalMileage"].toString().toInt(),
                            usedMileage = document.data["usedMileage"].toString().toInt(),
                            currentMileage = document.data["currentMileage"].toString().toInt(),
                            addedMileage = document.data["addedMileage"].toString().toInt(),
                            myPokemons = document.data["myPokemons"] as List<Int>,
                            mainPokemon = document.data["mainPokemon"].toString().toInt(),
                        )
                    }
                    continuation.resume(Result.success(user), null)
                    Log.d(TAG, "fetchUser() called with: snapShot = $snapShot")
                }.addOnFailureListener { exception ->
                    continuation.resume(
                        Result.failure(Exception(errorDescription ?: "unknown error")),
                        null,
                    )
                    Log.d(TAG, "fetchUser() called with: exception = $exception")
                }
        }

        return result
    }

    suspend fun registerUser(id: String): Result<SuccessOrFailure> {
        val user = User(id)

        val result = suspendCancellableCoroutine { continuation ->
            Firebase.firestore.collection("user").add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "registerUser() called with: documentReference = $documentReference")
                    continuation.resume(Result.success(SuccessOrFailure.Success), null)
                }.addOnFailureListener { e ->
                    continuation.resume(
                        Result.failure(
                            Exception(
                                errorDescription ?: "unknown error"
                            )
                        ), null
                    )
                    Log.d(TAG, "registerUser() called with: e = $e")
                }
        }
        return result
    }

}
