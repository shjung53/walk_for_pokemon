package com.ssafy.walkforpokemon.data.repository

import com.ssafy.walkforpokemon.data.dataclass.Pokemon
import com.ssafy.walkforpokemon.data.datasource.PokemonDataSource
import javax.inject.Inject

class PokemonRepository @Inject constructor(private val pokemonDataSource: PokemonDataSource) {

    suspend fun fetchPokemons(): Result<List<Pokemon>> {
        pokemonDataSource.fetchPokemons().fold(
            onSuccess = {
                val pokemonList =
                    it.map { pokemonResponse ->
                        Pokemon(
                            id = pokemonResponse.id,
                            name = pokemonResponse.name,
                            nameKorean = pokemonResponse.nameKorean,
                            imageOfficial = pokemonResponse.imageOfficial,
                            image3D = pokemonResponse.image3D,
                            isLegendary = pokemonResponse.isLegendary,
                            isMythical = pokemonResponse.isMythical,
                            percentage = pokemonResponse.percentage,
                            type = pokemonResponse.type,
                        )
                    }.sortedBy { pokemon -> pokemon.id }
                return Result.success(pokemonList)
            },
            onFailure = { return Result.failure(it) },
        )
    }
}
