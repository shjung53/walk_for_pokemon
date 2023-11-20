package com.ssafy.walkforpokemon.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
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
            if (!data.isActive) binding.image.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
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

    override fun getItemCount(): Int {
        return itemList.count()
    }

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    private lateinit var itemClickListener: onItemClickListener

    fun setItemClickListeneer(itemClickListener: onItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}
