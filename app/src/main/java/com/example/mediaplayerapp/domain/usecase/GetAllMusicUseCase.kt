package com.example.mediaplayerapp.domain.usecase

import com.example.mediaplayerapp.data.repository.MusicRepository
import com.example.mediaplayerapp.domain.model.Music
import javax.inject.Inject

class GetAllMusicUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(): List<Music> {
        return musicRepository.getAllMusic()
    }
}
