package com.example.mediaplayerapp.data.datasource

import com.example.mediaplayerapp.domain.model.Music

interface MusicDataSource {
    suspend fun getAllMusic(): List<Music>
}
