package com.example.mediaplayerapp.data.datasource

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.core.net.toUri
import com.example.mediaplayerapp.domain.model.Music
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalMusicDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : MusicDataSource {

    override suspend fun getAllMusic(): List<Music> = withContext(Dispatchers.IO) {
        val musicList = mutableListOf<Music>()
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
        )

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val title = cursor.getString(1).orEmpty()
                val albumName = cursor.getString(2).orEmpty()
                val singerName = cursor.getString(3).orEmpty()
                val filePath = cursor.getString(0).orEmpty()
                val albumArtUri = ContentUris.withAppendedId(
                    ALBUM_ART_BASE_URI,
                    cursor.getLong(4)
                )

                musicList.add(
                    Music(
                        title = title,
                        singerName = singerName,
                        albumName = albumName,
                        filePath = filePath,
                        coverArtUri = albumArtUri
                    )
                )
            }
        }

        musicList
    }

    private companion object {
        val ALBUM_ART_BASE_URI = "content://media/external/audio/albumart".toUri()
    }
}
