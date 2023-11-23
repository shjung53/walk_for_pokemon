package com.ssafy.walkforpokemon


enum class PokemonType constructor(val english: String, val Korean: String) {
    Normal("normal", "노말") ,
    Fire("fire", "불꽃"),
    Water("water", "물"),
    Electric("electric", "전기"),
    Grass("grass", "풀"),
    Ice("ice", "얼음"),
    Fighting("fighting", "격투"),
    Poison("poison", "독"),
    Ground("ground", "땅"),
    Flying("flying", "비행"),
    Psychic("psychic", "에스퍼"),
    Bug("bug", "벌레"),
    Rock("rock", "바위"),
    Ghost("ghost", "고스트"),
    Dragon("dragon", "드래곤"),
    Dark("dark", "악"),
    Steel("steel", "강철"),
    Fairy("fairy", "페어리")
}

object TypeTranslator {

    fun translate(type: String): PokemonType {
        return when (type) {
            "fire" -> PokemonType.Fire
            "water" -> PokemonType.Water
            "electric" -> PokemonType.Electric
            "grass" ->PokemonType.Grass
            "ice" -> PokemonType.Ice
            "fighting" -> PokemonType.Fighting
            "poison" ->PokemonType.Poison
            "ground" ->PokemonType.Ground
            "flying" -> PokemonType.Flying
            "psychic" -> PokemonType.Psychic
            "bug" -> PokemonType.Bug
            "rock" -> PokemonType.Rock
            "ghost" -> PokemonType.Ghost
            "dragon" -> PokemonType.Dragon
            "dark" ->PokemonType.Dark
            "steel" -> PokemonType.Steel
            "fairy" -> PokemonType.Fairy
            else -> PokemonType.Normal
        }
    }
}
