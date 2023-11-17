package com.ssafy.walkforpokemon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.walkforpokemon.R
import com.ssafy.walkforpokemon.data.dataclass.Pokemon

class DictionaryAdapter(val context: Context, val itemList: List<Pokemon>) :
    RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder>() {

    inner class DictionaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 개별 아이템 (그 칸) 에서 연결할 애들 이름 받아오기
        val pokemonNameTV = itemView.findViewById<TextView>(R.id.pokemonNameTV)
        val pokemonIV = itemView.findViewById<ImageView>(R.id.pokemonIV)
        val pokemonTypeTV = itemView.findViewById<TextView>(R.id.pokemonTypeTV)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DictionaryAdapter.DictionaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dictionary_item_view, parent, false)
        return DictionaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DictionaryAdapter.DictionaryViewHolder, position: Int) {
        Glide.with(context).load(itemList[position].imageOfficial).into(holder.pokemonIV)
        holder.pokemonNameTV.text =
            "no." + (itemList[position].id + 1).toString() + " " + itemList[position].nameKorean
        holder.pokemonTypeTV.text = itemList[position].type[0]
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
