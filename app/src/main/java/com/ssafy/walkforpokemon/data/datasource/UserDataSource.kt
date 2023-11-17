package com.ssafy.walkforpokemon.data.datasource

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.errorDescription
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.service.NaverUserService
import kotlinx.coroutines.suspendCancellableCoroutine

class UserDataSource {
    private val db = Firebase.firestore

    suspend fun retrieveUserId(): String {
        val response = NaverUserService.getService()
            .fetchUserProfile("Bearer " + NaverIdLoginSDK.getAccessToken().toString())
        Log.d("확인", response.toString())
        return response.response.id
    }

    suspend fun fetchUser(userId: String): Result<User> {
        val result = suspendCancellableCoroutine { continuation ->
            var user = User("")
            db.collection("user").whereEqualTo("id", userId).get()
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
                    Log.d("fetchUserResult", user.toString())
                }.addOnFailureListener { exception ->
                    continuation.resume(
                        Result.failure(Exception(errorDescription ?: "unknown error")),
                        null,
                    )
                    Log.d("fetchUser", "Error getting documents.", exception)
                }
        }

        return result
    }

    suspend fun registerUser(id: String): Result<SuccessOrFailure> {
        val user = User(id)

        val result = suspendCancellableCoroutine { continuation ->
            db.collection("user").add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("dbInsert", "DocumentSnapshot added with ID: ${documentReference.id}")
                    continuation.resume(Result.success(SuccessOrFailure.Success), null)
                }.addOnFailureListener { e ->
                    continuation.resume(
                        Result.failure(
                            Exception(
                                errorDescription ?: "unknown error"
                            )
                        ), null
                    )
                    Log.w("dbInsert", "Error adding document", e)
                }
        }
        return result
    }

}
