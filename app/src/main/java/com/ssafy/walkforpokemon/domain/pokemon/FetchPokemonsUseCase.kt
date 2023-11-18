package com.ssafy.walkforpokemon.domain.pokemon

import com.ssafy.walkforpokemon.data.repository.PokemonRepository
import com.ssafy.walkforpokemon.data.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint

class FetchPokemonsUseCase(
    private val userRepository: UserRepository,
    private val pokemonRepository: PokemonRepository,
) {

}
