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
import com.example.mediaplayerapp.model.Music
import kotlin.math.sin

class MusicAdapter(var musicList: MutableList<Music>, var context: Context) :
    RecyclerView.Adapter<MusicAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicAdapter.ViewHolder, position: Int) {
        holder.apply {
            title.text = musicList[position].title
            singerName.text = musicList[position].singerName
            Glide.with(context).load(musicList[position].coverArtUri).transforms(
                CenterCrop(),
                RoundedCorners(25)
            ).into(image)
        }
    }

    override fun getItemCount(): Int = musicList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val singerName: TextView
        val image: ImageView

        init {
            view.apply {
                title = findViewById(R.id.titleTxt)
                singerName = findViewById(R.id.artistTxt)
                image = findViewById(R.id.coverArt)
            }
        }
    }
}