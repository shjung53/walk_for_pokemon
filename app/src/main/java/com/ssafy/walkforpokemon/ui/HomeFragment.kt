package com.ssafy.walkforpokemon.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ssafy.walkforpokemon.R
import com.ssafy.walkforpokemon.databinding.FragmentHomeBinding
import com.ssafy.walkforpokemon.dialogs.LoadingDialog
import com.ssafy.walkforpokemon.dialogs.LoadingView
import com.ssafy.walkforpokemon.util.CustomToast
import com.ssafy.walkforpokemon.viewmodels.DictionaryViewModel
import com.ssafy.walkforpokemon.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment(), LoadingView {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val dictionaryViewModel: DictionaryViewModel by activityViewModels()

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        initObserver()
        initView()
        initClickListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initClickListener() {
        binding.drawBtn.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_drawDialog3)
        }

        binding.refreshStepCountButton.setOnClickListener {
            showLoading()
            CoroutineScope(Dispatchers.Main).launch {
                mainViewModel.refreshStepCount(requireActivity()).fold(
                    onSuccess = { hideLoading() },
                    onFailure = {
                        CustomToast.createAndShow(
                            requireContext(),
                            getString(R.string.fail_refresh_step_count),
                        )
                        hideLoading()
                    },
                )
            }
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

    private fun initView() {
        binding.currentMileage.text =
            String.format("%,d", mainViewModel.currentMileage.value) // 여기 집어넣기
        binding.nowSteps.text = String.format("%,d", mainViewModel.stepCount.value)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initObserver() {
        mainViewModel.mainPokemon.observe(requireActivity()) { mainPokemonId ->
            showLoading()
            dictionaryViewModel.pokemonList.value?.let {
                val mainPokemonImage = it.find { pokemon -> mainPokemonId == pokemon.id }?.image3D
                if (_binding != null) {
                    Glide.with(this).load(mainPokemonImage)
                        .into(binding.mainPokemonImage)
                }
            }
            hideLoading()
        }

        mainViewModel.stepCount.observe(requireActivity()) {
            showLoading()
            lifecycleScope.launch {
                mainViewModel.calculateStepCountToAdd(it).fold(
                    onSuccess = {
                        if (_binding != null) {
                            binding.nowSteps.text =
                                String.format("%,d", mainViewModel.stepCount.value)
                        }
                        hideLoading()
                    },
                    onFailure = {
                        CustomToast.createAndShow(
                            requireContext(),
                            getString(R.string.fail_refresh_step_count),
                        )
                        hideLoading()
                    },
                )
            }
        }

        mainViewModel.currentMileage.observe(requireActivity()) {
            if (_binding != null) {
                binding.currentMileage.text =
                    String.format("%,d", mainViewModel.currentMileage.value) // 여기 집어넣기
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun showLoading() {
        if (!loadingDialog.isShowing) {
            loadingDialog.show()
        }
    }

    override fun hideLoading() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }
}
