package com.example.mediaplayerapp.domain.usecase

import com.example.mediaplayerapp.domain.model.Music
import com.example.mediaplayerapp.player.PlaybackController

class PlayMusicUseCase(
    private val playbackController: PlaybackController
) {
    operator fun invoke(playlist: List<Music>, startIndex: Int) {
        playbackController.play(playlist, startIndex)
    }
}
