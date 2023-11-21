package com.ssafy.walkforpokemon.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.walkforpokemon.data.dataclass.Pokemon
import com.ssafy.walkforpokemon.databinding.ItemDictionaryBinding

class DictionaryAdapter(val context: Context, var itemList: List<Pokemon>) :
    RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder>() {

    inner class DictionaryViewHolder(private val binding: ItemDictionaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Pokemon) {
            binding.number.text = "no.${data.id}"
            binding.name.text = data.nameKorean
            Glide.with(context).load(data.imageOfficial).into(binding.image)
            val typeColor = context.resources.getIdentifier(
                "type_${data.type[0]}",
                "color",
                context.packageName,
            )
            if (!data.isActive) {
                binding.image.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
                binding.card.strokeColor = Color.GRAY
            } else {
                binding.image.colorFilter = null
                binding.card.strokeColor = context.getColor(typeColor)
            }
            if (data.isMain) {
                binding.medal.visibility = View.VISIBLE
            } else {
                binding.medal.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DictionaryAdapter.DictionaryViewHolder {
        val binding =
            ItemDictionaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DictionaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DictionaryAdapter.DictionaryViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    private lateinit var itemClickListener: onItemClickListener

    fun setItemClickListeneer(itemClickListener: onItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}
