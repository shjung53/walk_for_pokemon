package com.ssafy.walkforpokemon.ui

import android.content.Intent
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            CoroutineScope(Dispatchers.Main).launch {
                showLoading()
                mainViewModel.refreshStepCount(requireActivity()).fold(
                    onSuccess = { hideLoading() },
                    onFailure = {
                        hideLoading()
                        CustomToast.createAndShow(
                            requireContext(),
                            getString(R.string.fail_refresh_step_count),
                        )
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

        binding.logoutButton.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()

            val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

            val gsa = GoogleSignIn.getLastSignedInAccount(requireContext())

            if (gsa != null && gsa.id != null) {
                mGoogleSignInClient.signOut()
                    .addOnCompleteListener(requireActivity()) {
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
            }
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
            lifecycleScope.launch {
                delay(300)
                showLoading()
                mainViewModel.calculateStepCountToAdd(it).fold(
                    onSuccess = {
                        hideLoading()
                        if (_binding != null) {
                            binding.nowSteps.text =
                                String.format("%,d", mainViewModel.stepCount.value)
                        }
                    },
                    onFailure = {
                        if (it.message == "needInit") {
                            if (_binding != null) {
                                binding.nowSteps.text =
                                    String.format("%,d", mainViewModel.stepCount.value)
                            }
                        } else {
                            hideLoading()
                            CustomToast.createAndShow(
                                requireContext(),
                                getString(R.string.fail_refresh_step_count),
                            )
                        }
                    },
                )
            }
        }

        mainViewModel.currentMileage.observe(requireActivity()) {
            showLoading()
            if (_binding != null) {
                binding.currentMileage.text =
                    String.format("%,d", mainViewModel.currentMileage.value) // 여기 집어넣기
            }
            hideLoading()
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
