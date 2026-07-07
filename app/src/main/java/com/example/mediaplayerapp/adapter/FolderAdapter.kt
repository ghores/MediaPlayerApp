package com.example.mediaplayerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayerapp.R
import com.example.mediaplayerapp.domain.model.MusicFolder

class FolderAdapter(
    private var folders: List<MusicFolder>,
    private val onItemClick: (MusicFolder) -> Unit
) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    fun submitList(newList: List<MusicFolder>) {
        folders = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folders[position]
        holder.name.text = folder.name
        holder.path.text = folder.path
        holder.count.text = holder.itemView.context
            .getString(R.string.folder_song_count, folder.songCount)

        holder.itemView.setOnClickListener { onItemClick(folder) }
    }

    override fun getItemCount(): Int = folders.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.folderName)
        val path: TextView = view.findViewById(R.id.folderPath)
        val count: TextView = view.findViewById(R.id.folderCount)
    }
}
