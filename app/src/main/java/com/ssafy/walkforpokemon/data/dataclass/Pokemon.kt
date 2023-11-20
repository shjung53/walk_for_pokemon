package com.ssafy.walkforpokemon.data.dataclass

data class Pokemon(
    val id: Int = 0,
    val name: String = "",
    val nameKorean: String = "",
    val imageOfficial: String = "",
    val image3D: String = "",
    val isLegendary: Boolean = false,
    val isMythical: Boolean = false,
    val percentage: Double = 0.0,
    val type: List<String> = listOf(),
    val isActive: Boolean = false,
    val isMain: Boolean = false
)
