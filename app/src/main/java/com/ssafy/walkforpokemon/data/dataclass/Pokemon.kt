package com.ssafy.walkforpokemon.data.dataclass

data class Pokemon (
    val id : Int,
    val name : String,
    val nameKorean: String,
    val imageOfficial: String,
    val image3D: String,
    val isLegendary: Boolean,
    val isMythical: Boolean,
    val percentage: Double,
    val type: List<String>
)
