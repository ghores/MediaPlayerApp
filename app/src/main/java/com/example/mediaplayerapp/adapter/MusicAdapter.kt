package com.example.mediaplayerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.mediaplayerapp.R
import com.example.mediaplayerapp.domain.model.Music

class MusicAdapter(
    private var musicList: List<Music>,
    private val context: Context,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    fun submitList(newList: List<Music>) {
        musicList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = musicList[position]
        holder.title.text = music.title
        holder.singerName.text = music.singerName
        Glide.with(context).load(music.coverArtUri).transforms(
            CenterCrop(),
            RoundedCorners(25)
        ).into(holder.image)

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = musicList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.titleTxt)
        val singerName: TextView = view.findViewById(R.id.artistTxt)
        val image: ImageView = view.findViewById(R.id.coverArt)
    }
}
