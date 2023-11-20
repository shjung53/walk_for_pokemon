package com.ssafy.walkforpokemon.domain.pokemon

import com.ssafy.walkforpokemon.data.dataclass.Pokemon
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.data.repository.PokemonRepository
import javax.inject.Inject

class FetchUserPokemonListUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository,
) {
    suspend operator fun invoke(user: User): Result<List<Pokemon>> {
        val myPokemons = user.myPokemons
        val mainPokemon = user.mainPokemon

        pokemonRepository.fetchPokemons().fold(
            onSuccess = {
                val result =
                    it.map { pokemon ->
                        pokemon.copy(
                            isActive = myPokemons.contains(pokemon.id),
                            isMain = mainPokemon == pokemon.id,
                        )
                    }
                return Result.success(result)
            },
            onFailure = { return Result.failure(it) },
        )
    }
}
