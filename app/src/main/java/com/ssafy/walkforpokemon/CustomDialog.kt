package com.ssafy.walkforpokemon

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ssafy.walkforpokemon.databinding.DialogDrawBinding
import com.ssafy.walkforpokemon.dto.Pokemon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "CustomDialog"
class CustomDialog(private val context: Context, private val binding: DialogDrawBinding) : AppCompatActivity() {

    private val dialog = Dialog(context)

    private lateinit var positiveButton: Button
    private lateinit var negativeButton: Button

    private val requestOptions = RequestOptions()



    fun build(bodyMessage: String, context: Context) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(R.layout.draw_certification_dialog)

        dialog.findViewById<TextView>(R.id.dialogMessageTV).text = bodyMessage

        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
        requestOptions.skipMemoryCache(false)
        requestOptions.signature(ObjectKey(System.currentTimeMillis()))

        positiveButton = dialog.findViewById(R.id.positiveBtn)
        positiveButton.setOnClickListener {

            /* TODO
                뽑기했을 때
                1. 마일리지 깎기
                2. 뽑은 포켓몬 도감에 추가하기(user의 가진 포켓몬 리스트에 추가하기)
             */

            letsDraw(context)

            Glide.with(context).load(R.raw.pokeball_open_temp_slow)
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
                                Glide.with(context).clear(binding.pokeballIV)
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

            val pokeballFadeOut = ObjectAnimator.ofFloat(binding.pokeballIV, "alpha", 1f, 0f)
            pokeballFadeOut.duration = 2000
            val pokemonFadeIn = ObjectAnimator.ofFloat(binding.drawPokemonIV, "alpha", 0f, 1f)
            pokemonFadeIn.duration = 3000
            pokeballFadeOut.start()

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

            dialog.dismiss()
        }

        negativeButton = dialog.findViewById(R.id.negativeBtn)
        negativeButton.setOnClickListener {

            binding.root.findNavController().popBackStack()
            dialog.dismiss()
        }


        dialog.setCancelable(false)
        dialog.show()
    }

    fun letsDraw(context: Context) {

        /* TODO
            1. firebase data에서 percentage 조정해서 저장해놓기
            2. 도감 데이터 받아와서 로컬에 저장해놓은 거에서 퍼센티지 받아오기
         */


        var pokeDataList = mutableListOf<Pokemon>()
        var remainPercentageList = mutableListOf<Double>()
        var remainPercentage = 0.0
        var resultId = 0  // 뽑은 포켓몬 아이디
        val db = Firebase.firestore

        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("pokemon")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            pokeDataList.add(
                                Pokemon(
                                    id = document.data.get("id").toString().toInt(),
                                    name = document.data.get("name") as String,
                                    nameKorean = document.data.get("nameKorean") as String,
                                    imageOfficial = document.data.get("imageOfficial") as String,
                                    image3D = document.data.get("image3D") as String,
                                    isLegendary = document.data.get("isLegendary") as Boolean,
                                    isMythical = document.data.get("isMythical") as Boolean,
                                    percentage = document.data.get("percentage") as Double,
                                    type = document.data.get("type") as List<String>
                                )
                            )
                            remainPercentage += document.data.get("percentage") as Double
                            remainPercentageList.add(remainPercentage)
//                            Log.d(
//                                TAG,
//                                "onViewCreated: ${document.id} => ${pokeDataList.size} , ${pokeDataList[pokeDataList.size - 1]}"
//                            )

                        }
                    }
                    .addOnFailureListener {
                        Log.w(TAG, "onViewCreated: ", it)
                    }.await()

                withContext(Dispatchers.Main) {
                    pokeDataList.sortBy { it.id }
                    Log.d(TAG, "my first : ${pokeDataList[0]} , ${pokeDataList.size}")

                    val pick = Math.random() * remainPercentage
                    for (i in 0 until 151) {
                        if (pick <= remainPercentageList[i]) {
                            resultId = i
                            break
                        }
                    }

                    Log.d(TAG, "letsDraw: 불렸니..? ${resultId}, ${context}")

                    Glide.with(context).load(pokeDataList[resultId].imageOfficial).into(binding.drawPokemonIV)

                }
            } catch (e: Exception) {
                Log.d(TAG, "onViewCreated: 통신 에러.... ${e}")
            }
        }
    }
}
