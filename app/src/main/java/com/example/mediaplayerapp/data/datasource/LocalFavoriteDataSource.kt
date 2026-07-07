package com.example.mediaplayerapp.data.datasource

import android.content.Context
import androidx.core.net.toUri
import com.example.mediaplayerapp.domain.model.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class LocalFavoriteDataSource(
    context: Context
) : FavoriteDataSource {

    private val preferences = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun getFavorites(): List<Music> = withContext(Dispatchers.IO) {
        val raw = preferences.getString(KEY_FAVORITES, null) ?: return@withContext emptyList()

        runCatching {
            val jsonArray = JSONArray(raw)
            (0 until jsonArray.length()).map { index ->
                jsonArray.getJSONObject(index).toMusic()
            }
        }.getOrDefault(emptyList())
    }

    override suspend fun saveFavorites(favorites: List<Music>) = withContext(Dispatchers.IO) {
        val jsonArray = JSONArray()
        favorites.forEach { jsonArray.put(it.toJson()) }
        preferences.edit().putString(KEY_FAVORITES, jsonArray.toString()).apply()
    }

    private fun Music.toJson(): JSONObject = JSONObject().apply {
        put(KEY_TITLE, title)
        put(KEY_SINGER, singerName)
        put(KEY_ALBUM, albumName)
        put(KEY_PATH, filePath)
        put(KEY_COVER, coverArtUri.toString())
    }

    private fun JSONObject.toMusic(): Music = Music(
        title = optString(KEY_TITLE),
        singerName = optString(KEY_SINGER),
        albumName = optString(KEY_ALBUM),
        filePath = optString(KEY_PATH),
        coverArtUri = optString(KEY_COVER).toUri()
    )

    private companion object {
        const val PREFS_NAME = "favorites_prefs"
        const val KEY_FAVORITES = "favorite_songs"
        const val KEY_TITLE = "title"
        const val KEY_SINGER = "singer"
        const val KEY_ALBUM = "album"
        const val KEY_PATH = "path"
        const val KEY_COVER = "cover"
    }
}
