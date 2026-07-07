package com.example.mediaplayerapp.data.repository

import com.example.mediaplayerapp.data.datasource.MusicDataSource
import com.example.mediaplayerapp.domain.model.Music

class MusicRepositoryImpl(
    private val musicDataSource: MusicDataSource
) : MusicRepository {

    override suspend fun getAllMusic(): List<Music> {
        return musicDataSource.getAllMusic()
    }
}
