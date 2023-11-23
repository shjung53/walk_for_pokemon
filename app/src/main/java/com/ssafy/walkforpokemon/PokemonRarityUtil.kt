package com.ssafy.walkforpokemon

import kotlin.random.Random

enum class PokemonRarity {
    Common, Uncommon, Rare, SuperRare, Special, Epic, Legendary, Mythical
}

object PokemonRarityUtil {

    private val commonList = mutableListOf<Int>()
    private val uncommonList = mutableListOf<Int>()
    private val rareList = mutableListOf<Int>()
    private val superRareList = mutableListOf<Int>()
    private val specialList = mutableListOf<Int>()
    private val epicList = mutableListOf<Int>()
    private val legendaryList = mutableListOf<Int>()
    private val mythicalList = mutableListOf<Int>()

    fun drawGrade(): PokemonRarity {
        val randomValue = Random.nextDouble(0.0, 100.0)
        when {
            randomValue <= 0.1 -> return PokemonRarity.Mythical

            randomValue <= 0.6 -> return PokemonRarity.Legendary

            randomValue <= 3.6 -> return PokemonRarity.Epic

            randomValue <= 10.6 -> return PokemonRarity.Special

            randomValue <= 20.6 -> return PokemonRarity.SuperRare

            randomValue <= 35.6 -> return PokemonRarity.Rare

            randomValue <= 60.6 -> return PokemonRarity.Uncommon

            else -> return PokemonRarity.Common
        }
    }

    fun getGrade(percentage: Double): PokemonRarity {
        when {
            percentage <= 0.1 -> return PokemonRarity.Mythical

            percentage <= 0.5 -> return PokemonRarity.Legendary

            percentage <= 3 -> return PokemonRarity.Epic

            percentage <= 7 -> return PokemonRarity.Special

            percentage <= 10 -> return PokemonRarity.SuperRare

            percentage <= 15 -> return PokemonRarity.Rare

            percentage <= 25 -> return PokemonRarity.Uncommon

            else -> return PokemonRarity.Common
        }
    }

    fun putInGradeList(pokemonId: Int, rarity: PokemonRarity) {
        when (rarity) {
            PokemonRarity.Common -> commonList.add(pokemonId)
            PokemonRarity.Uncommon -> uncommonList.add(pokemonId)
            PokemonRarity.Rare -> rareList.add(pokemonId)
            PokemonRarity.SuperRare -> superRareList.add(pokemonId)
            PokemonRarity.Special -> specialList.add(pokemonId)
            PokemonRarity.Epic -> epicList.add(pokemonId)
            PokemonRarity.Legendary -> legendaryList.add(pokemonId)
            PokemonRarity.Mythical -> mythicalList.add(pokemonId)
        }
    }

    fun getList(rarity: PokemonRarity): List<Int> {
        return when (rarity) {
            PokemonRarity.Common -> commonList
            PokemonRarity.Uncommon -> uncommonList
            PokemonRarity.Rare -> rareList
            PokemonRarity.SuperRare -> superRareList
            PokemonRarity.Special -> specialList
            PokemonRarity.Epic -> epicList
            PokemonRarity.Legendary -> legendaryList
            PokemonRarity.Mythical -> mythicalList
        }
    }
}
