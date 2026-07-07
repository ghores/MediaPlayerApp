package com.example.mediaplayerapp.data.datasource

import com.example.mediaplayerapp.domain.model.Music

interface FavoriteDataSource {
    suspend fun getFavorites(): List<Music>
    suspend fun saveFavorites(favorites: List<Music>)
}
