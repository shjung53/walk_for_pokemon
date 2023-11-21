package com.ssafy.walkforpokemon

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.ssafy.walkforpokemon.databinding.FragmentDrawBinding
import com.ssafy.walkforpokemon.viewmodels.DictionaryViewModel

class DrawFragment() : DialogFragment() {

    private val requestOptions = RequestOptions()

    private var _binding: FragmentDrawBinding? = null
    private val binding : FragmentDrawBinding get() = _binding!!

    private val dictionaryViewModel : DictionaryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrawBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.isCancelable = false

        val pokemonId = arguments?.getInt("pokemonId")

        val pokemon = dictionaryViewModel.pokemonList.value?.find { it.id == pokemonId }

        pokemon?.let { it ->
            binding.pokemonName.text = it.nameKorean
            binding.type1.text =it.type[0]
            val type1Color = requireActivity().resources.getIdentifier(
                "type_${it.type[0]}",
                "color",
                requireActivity().packageName,
            )
            binding.type1.setBackgroundResource(type1Color)

            if (it.type.size == 1) {
                binding.type2.visibility = View.GONE
            } else {
                binding.type2.text = it.type[1]
                val type2Color = requireActivity().resources.getIdentifier(
                    "type_${it.type[1]}",
                    "color",
                    requireActivity().packageName,
                )
                binding.type2.setBackgroundResource(type2Color)
            }

            Glide.with(requireActivity()).load(it.imageOfficial)
                .into(binding.pokemonImage)
        }
        setDrawAnimation()
    }

    private fun setDrawAnimation() {
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
        requestOptions.skipMemoryCache(false)
        requestOptions.signature(ObjectKey(System.currentTimeMillis()))

        Glide.with(requireActivity()).load(R.raw.pokeball_open_temp_slow)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    (resource as GifDrawable).setLoopCount(0)
                    resource.registerAnimationCallback(object :
                        Animatable2Compat.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable?) {
                            Glide.with(requireActivity()).clear(binding.drawGif)
                            binding.drawGif.visibility = View.GONE
                            super.onAnimationEnd(drawable)
                        }
                    })

                    return false
                }
            })
            .apply(requestOptions)
            .into(binding.drawGif)

        binding.drawGif.visibility = View.VISIBLE

        val pokeballFadeOut = ObjectAnimator.ofFloat(binding.drawGif, "alpha", 1f, 0f)
        pokeballFadeOut.duration = 2000
        val pokemonFadeIn = ObjectAnimator.ofFloat(binding.pokemonImage, "alpha", 0f, 1f)
        pokemonFadeIn.duration = 3000
        val pokeTextFadeIn = ObjectAnimator.ofFloat(
            binding.pokeTextLinearLayout,
            "alpha",
            0f,
            1f,
        )
        pokeTextFadeIn.duration = 3000
        pokeballFadeOut.start()

        pokeballFadeOut.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                // 애니메이션(fade out)이 끝나면 호출
                binding.drawGif.visibility = View.GONE
                binding.pokemonImage.visibility = View.VISIBLE
                binding.pokeTextLinearLayout.visibility = View.VISIBLE
                pokemonFadeIn.start()
                pokeTextFadeIn.start()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }

    fun newInstance(pokemonId: Int): DrawFragment{
        val args = Bundle()
        args.putInt("pokemonId", pokemonId)
        val fragment = DrawFragment()
        fragment.arguments = args
        return fragment
    }
}
