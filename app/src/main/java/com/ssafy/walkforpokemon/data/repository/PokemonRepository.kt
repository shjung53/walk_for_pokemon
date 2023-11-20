package com.ssafy.walkforpokemon.data.repository

import com.ssafy.walkforpokemon.data.datasource.PokemonDataSource
import javax.inject.Inject

class PokemonRepository @Inject constructor(private val pokemonDataSource: PokemonDataSource) {

//    suspend fun fetchPokemons(): Result<List<Pokemon>> {
//        pokemonDataSource.fetchPokemons().fold(
//            onSuccess = {
//                val pokemonList = arrayListOf<Pokemon>()
//                return Result.success(it)
//            },
//            onFailure = { return Result.failure(it) }
//        )
//    }
}
