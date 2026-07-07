package com.example.mediaplayerapp.data.repository

import com.example.mediaplayerapp.domain.model.Music
import kotlinx.coroutines.flow.StateFlow

interface FavoriteRepository {
    val favorites: StateFlow<List<Music>>

    suspend fun toggleFavorite(music: Music)
    fun isFavorite(filePath: String): Boolean
}
