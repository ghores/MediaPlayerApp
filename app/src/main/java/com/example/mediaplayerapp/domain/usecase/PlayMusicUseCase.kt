package com.example.mediaplayerapp.domain.usecase

import com.example.mediaplayerapp.domain.model.Music
import com.example.mediaplayerapp.player.PlaybackController
import javax.inject.Inject

class PlayMusicUseCase @Inject constructor(
    private val playbackController: PlaybackController
) {
    operator fun invoke(playlist: List<Music>, startIndex: Int) {
        playbackController.play(playlist, startIndex)
    }
}
