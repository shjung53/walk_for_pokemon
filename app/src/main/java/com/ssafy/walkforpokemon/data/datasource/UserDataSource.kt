package com.ssafy.walkforpokemon.data.datasource

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.errorDescription
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.dataclass.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "UserDataSource"

class UserDataSource @Inject constructor() {

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
                            myPokemons = document.data["myPokemons"].let {
                                it as List<Long>
                            }.map { it.toInt() },
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
                                errorDescription ?: "unknown error",
                            ),
                        ),
                        null,
                    )
                    Log.d(TAG, "registerUser() called with: e = $e")
                }
        }
        return result
    }

    suspend fun addPokemonToUser(
        userId: String,
        pokemonId: Int,
    ): Result<SuccessOrFailure> {
        var documentId = ""
        Firebase.firestore.collection("user").whereEqualTo("id", userId).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    documentId = document.id
                }
            }.await()

        try {
            val result = suspendCancellableCoroutine { continuation ->
                Firebase.firestore.collection("user").document(documentId).update(
                    "myPokemons",
                    FieldValue.arrayUnion(pokemonId),
                ).addOnSuccessListener {
                    continuation.resume(Result.success(SuccessOrFailure.Success), null)
                }.addOnFailureListener { e ->
                    continuation.resume(
                        Result.failure(
                            Exception(
                                errorDescription ?: "unknown error",
                            ),
                        ),
                        null,
                    )
                    Log.d(TAG, "updateUserPokemonList() called with: e = $e")
                }
            }
            return result
        } catch (e: Exception) {
            Log.d(TAG, "addPokemonToUser() called with: userId = $userId, pokemonId = $pokemonId")
            return Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun updateMainPokemon(
        userId: String,
        pokemonId: Int,
    ): Result<SuccessOrFailure> {
        var documentId = ""
        Firebase.firestore.collection("user").whereEqualTo("id", userId).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    documentId = document.id
                }
            }.await()

        try {
            val result = suspendCancellableCoroutine { continuation ->
                Firebase.firestore.collection("user").document(documentId).update(
                    "mainPokemon",
                    pokemonId,
                ).addOnSuccessListener { documentReference ->
                    continuation.resume(Result.success(SuccessOrFailure.Success), null)
                    Log.d(
                        TAG,
                        "updateMainPokemon() called with: documentReference = $documentReference",
                    )
                }.addOnFailureListener { e ->
                    continuation.resume(
                        Result.failure(
                            Exception(
                                errorDescription ?: "unknown error",
                            ),
                        ),
                        null,
                    )
                    Log.d(TAG, "updateMainPokemon() called with: e = $e")
                }
            }
            return result
        } catch (e: Exception) {
            Log.d(TAG, "updateMainPokemon() called with: userId = $userId, pokemonId = $pokemonId")
            return Result.failure(e)
        }
    }

    suspend fun updateCurrentMileageUseCase(
        userId: String,
        newCurrentMileage: Int,
    ): Result<SuccessOrFailure> {
        var documentId = ""
        Firebase.firestore.collection("user").whereEqualTo("id", userId).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    documentId = document.id
                }
            }.await()

        try {
            val result = suspendCancellableCoroutine<Result<SuccessOrFailure>> { continuation ->
                Firebase.firestore.collection("user").document(documentId).update(
                    "currentMileage",
                    newCurrentMileage,
                ).addOnSuccessListener {
                    continuation.resume(Result.success(SuccessOrFailure.Success), null)
                }.addOnFailureListener { e ->
                    continuation.resume(
                        Result.failure(
                            Exception(
                                errorDescription ?: "unknown error",
                            ),
                        ),
                        null,
                    )
                    Log.d(TAG, "addMileage() called with: e = $e")
                }
            }
            return result
        } catch (e: Exception) {
            Log.d(
                TAG,
                "updateMileageUseCase() called with: userId = $userId, newCurrentMileage = $newCurrentMileage",
            )
            return Result.failure(e)
        }
    }

    suspend fun updateCurrentAndAddedMileageUseCase(
        userId: String,
        newCurrentMileage: Int,
        newAddedMileage: Int,
    ): Result<SuccessOrFailure> {
        var documentId = ""
        Firebase.firestore.collection("user").whereEqualTo("id", userId).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    documentId = document.id
                }
            }.await()

        try {
            val result = suspendCancellableCoroutine<Result<SuccessOrFailure>> { continuation ->
                Firebase.firestore.collection("user").document(documentId).update(
                    mapOf(
                        "currentMileage" to newCurrentMileage,
                        "addedMileage" to newAddedMileage,
                    ),
                ).addOnSuccessListener {
                    continuation.resume(Result.success(SuccessOrFailure.Success), null)
                }.addOnFailureListener { e ->
                    continuation.resume(
                        Result.failure(
                            Exception(
                                errorDescription ?: "unknown error",
                            ),
                        ),
                        null,
                    )
                    Log.d(TAG, "addMileage() called with: e = $e")
                }
            }
            return result
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
