package com.ssafy.walkforpokemon.data.dataclass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val totalMileage: Int = 0,
    val usedMileage: Int = 0,
    val currentMileage: Int = 0,
    val addedMileage: Int = 0,
    val myPokemons: List<Int> = listOf(),
    val mainPokemon: Int = 0,
) : Parcelable
