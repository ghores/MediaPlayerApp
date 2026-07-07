package com.example.mediaplayerapp

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.media3.exoplayer.ExoPlayer
import com.example.mediaplayerapp.model.Music

fun getAllAudioFromDevice(context: Context) {
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf<String?>(
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.ALBUM,
        MediaStore.Audio.ArtistColumns.ARTIST,
        MediaStore.Audio.Media.ALBUM_ID,
    )

    // if want fetch all files
    val cursor = context.contentResolver.query(
        uri,
        projection,
        null,
        null,
        null
    )

    if (cursor != null) {
        while (cursor.moveToNext()) {
            val title = cursor.getString(1)
            val singerName = cursor.getString(3)
            val albumName = cursor.getString(2)
            val path = cursor.getString(0)

            val pathUri = "content://media/external/audio/albumart".toUri()
            val albumArtUri = ContentUris.withAppendedId(pathUri, cursor.getLong(4))
            allMusicList.add(Music(title, singerName, albumName, path, albumArtUri))
        }
        cursor.close()
    }
}
fun playMusic(context: Context) {
    val player = ExoPlayer.Builder(context).build()
}
