package com.ssafy.walkforpokemon.data.datasource

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.oauth.NidOAuthPreferencesManager
import com.ssafy.walkforpokemon.data.dataclass.PokemonResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

private const val TAG = "PokemonDataSource"

class PokemonDataSource @Inject constructor() {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun fetchPokemons(): Result<List<PokemonResponse>> {
        val result = suspendCancellableCoroutine { continuation ->
            val pokemonResponseList = arrayListOf<PokemonResponse>()
            Firebase.firestore.collection("pokemon")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        pokemonResponseList.add(
                            PokemonResponse(
                                id = document.data["id"].toString().toInt(),
                                name = document.data["name"].toString(),
                                nameKorean = document.data["nameKorean"].toString(),
                                imageOfficial = document.data["imageOfficial"].toString(),
                                image3D = document.data["image3D"].toString(),
                                isLegendary = document.data["isLegendary"] as Boolean,
                                isMythical = document.data["isMythical"] as Boolean,
                                percentage = document.data["percentage"].toString().toDouble(),
                                type = document.data["type"] as List<String>,
                            ),
                        )
                    }
                    continuation.resume(Result.success(pokemonResponseList), null)
                }
                .addOnFailureListener {
                    continuation.resume(
                        Result.failure(
                            Exception(
                                NidOAuthPreferencesManager.errorDescription ?: "unknown error",
                            ),
                        ),
                        null,
                    )
                    Log.d(TAG, "fetchPokemons: $it")
                }
        }
        return result
    }
}
