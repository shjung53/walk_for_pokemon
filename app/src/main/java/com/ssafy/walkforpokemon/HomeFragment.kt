package com.ssafy.walkforpokemon

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ssafy.walkforpokemon.databinding.FragmentHomeBinding
import com.ssafy.walkforpokemon.viewmodels.DictionaryViewModel
import com.ssafy.walkforpokemon.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val dictionaryViewModel: DictionaryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.mainPokemon.observe(requireActivity()) { mainPokemonId ->
            dictionaryViewModel.pokemonList.value?.let { it ->
                val mainPokemonImage = it.find { pokemon -> mainPokemonId == pokemon.id }?.image3D
                Glide.with(this).load(
                    mainPokemonImage,
                ).into(binding.mainPokemonImage)
            }
        }

        /* TODO
            1. user data에서 마일리지, 걸음 수 받아와서 xml에 집어넣기
         */

        binding.currentMileage.text =
            String.format("%,d", mainViewModel.currentMileage.value) // 여기 집어넣기
        binding.nowSteps.text = String.format("%,d", mainViewModel.stepCount.value)

        binding.refreshStepCountButton.setOnClickListener {
            mainViewModel.refreshStepCount(requireActivity())
        }

        mainViewModel.stepCount.observe(requireActivity()) {
            mainViewModel.calculateStepCountToAdd(it)
            binding.nowSteps.text = String.format("%,d", mainViewModel.stepCount.value)
        }

        mainViewModel.currentMileage.observe(requireActivity()) {
            binding.currentMileage.text =
                String.format("%,d", mainViewModel.currentMileage.value) // 여기 집어넣기
        }

        setNavigation()
    }

    private fun setNavigation() {
        val navController = findNavController()

        binding.drawBtn.setOnClickListener {
            navController.navigate(R.id.action_home_to_drawDialog3)
        }

        val dictionaryTransitionOption = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.slide_in_up)
            .setExitAnim(R.anim.stay)
            .setPopExitAnim(R.anim.slide_out_down)
            .setExitAnim(R.anim.stay)
            .build()

        binding.dictionaryBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_home_to_dictionary,
                null,
                dictionaryTransitionOption,
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
