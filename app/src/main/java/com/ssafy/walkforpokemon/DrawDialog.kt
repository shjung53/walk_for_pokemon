package com.ssafy.walkforpokemon

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
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
import com.ssafy.walkforpokemon.databinding.DialogDrawBinding

class DrawDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogDrawBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val pokeballFadeOut = ObjectAnimator.ofFloat(binding.pokeballIV, "alpha", 1f, 0f)
        pokeballFadeOut.duration = 2000
        val pokemonFadeIn = ObjectAnimator.ofFloat(binding.drawPokemonIV, "alpha", 0f, 1f)
        pokemonFadeIn.duration = 3000

        pokeballFadeOut.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }
            override fun onAnimationEnd(animation: Animator) {
                // 애니메이션(fade out)이 끝나면 호출
                binding.pokeballIV.visibility = View.GONE
                binding.drawPokemonIV.visibility = View.VISIBLE
                pokemonFadeIn.start()

            }

            override fun onAnimationCancel(animation: Animator) {

            }
            override fun onAnimationRepeat(animation: Animator) {

            }

        })

        val requestOptions = RequestOptions()
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
        requestOptions.skipMemoryCache(false)
        requestOptions.signature(ObjectKey(System.currentTimeMillis()))

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("뽑기!")
            .setMessage("1000마일리지로 포켓몬 뽑기")
            .setPositiveButton("동의", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                    /* TODO
                    마일리지 차감하고, 랜덤 함수 창작 후
                    binding.drawPokemonIV 에다가 랜덤으로 뽑힌 포켓몬 사진 대입
                     */


                    Glide.with(requireContext()).load(R.raw.pokeball_open_temp_slow)
                        .addListener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                (resource as GifDrawable).setLoopCount(0)
                                resource.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                                    override fun onAnimationEnd(drawable: Drawable?) {
                                        Glide.with(requireContext()).clear(binding.pokeballIV)
                                        binding.pokeballIV.visibility = View.GONE
                                        super.onAnimationEnd(drawable)
                                    }
                                })

                                return false
                            }


                        })
                        .apply(requestOptions)
                        .into(binding.pokeballIV)

                    binding.pokeballIV.visibility = View.VISIBLE
                    pokeballFadeOut.start()
                }

            })
            .setNegativeButton("취소", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    parentFragmentManager.beginTransaction()
                        .remove(this@DrawDialog)
                        .commit()
                }

            })

        val alertDialog = builder.create()
        alertDialog.show()




        return binding.root
    }
}
