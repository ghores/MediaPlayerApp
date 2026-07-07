package com.example.mediaplayerapp.data.repository

import com.example.mediaplayerapp.domain.model.Music

interface MusicRepository {
    suspend fun getAllMusic(): List<Music>
}
