package com.ssafy.walkforpokemon.domain.pokemon

import com.ssafy.walkforpokemon.data.dataclass.Pokemon
import com.ssafy.walkforpokemon.data.repository.PokemonRepository
import javax.inject.Inject

class InitPokemonListUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository,
) {
    suspend operator fun invoke(): Result<List<Pokemon>> {
        pokemonRepository.fetchPokemons().fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }
}
