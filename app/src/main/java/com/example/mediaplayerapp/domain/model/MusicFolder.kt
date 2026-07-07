package com.example.mediaplayerapp.domain.model

data class MusicFolder(
    val name: String,
    val path: String,
    val songs: List<Music>
) {
    val songCount: Int get() = songs.size
}

/** Absolute path of the directory that contains this track. */
val Music.folderPath: String
    get() = filePath.substringBeforeLast('/', "")
