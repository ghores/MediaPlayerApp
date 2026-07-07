package com.example.mediaplayerapp.domain.usecase

import com.example.mediaplayerapp.data.repository.MusicRepository
import com.example.mediaplayerapp.domain.model.Music

class GetAllMusicUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(): List<Music> {
        return musicRepository.getAllMusic()
    }
}
