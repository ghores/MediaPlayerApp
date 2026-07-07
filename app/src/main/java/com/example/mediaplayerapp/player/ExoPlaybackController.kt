package com.example.mediaplayerapp.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.mediaplayerapp.domain.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

class ExoPlaybackController(
    context: Context
) : PlaybackController {

    private val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext).build()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var playlist: List<Music> = emptyList()
    private var currentIndex: Int = -1
    private var progressJob: Job? = null

    private var isShuffleEnabled: Boolean = false
    private var repeatMode: RepeatMode = RepeatMode.OFF

    private val _playbackState = MutableStateFlow(PlaybackUiState())
    override val playbackState: StateFlow<PlaybackUiState> = _playbackState.asStateFlow()

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState(isPlaying = isPlaying)
                if (isPlaying) {
                    startProgressUpdates()
                } else {
                    stopProgressUpdates()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    handleTrackEnded()
                }
            }
        })
    }

    override fun play(playlist: List<Music>, startIndex: Int) {
        if (playlist.isEmpty()) return

        this.playlist = playlist
        currentIndex = startIndex.coerceIn(0, playlist.lastIndex)
        prepareAndPlay()
    }

    override fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
            return
        }

        if (player.playbackState == Player.STATE_IDLE && currentMusic != null) {
            prepareAndPlay()
        } else {
            player.play()
        }
    }

    override fun playNext() {
        if (playlist.isEmpty()) return

        currentIndex = nextIndex()
        prepareAndPlay()
    }

    override fun playPrevious() {
        if (playlist.isEmpty()) return

        if (player.currentPosition > SKIP_TO_START_THRESHOLD_MS) {
            player.seekTo(0)
            updatePlaybackState()
            return
        }

        currentIndex = if (currentIndex - 1 < 0) playlist.lastIndex else currentIndex - 1
        prepareAndPlay()
    }

    override fun seekTo(progressFraction: Float) {
        val duration = player.duration
        if (duration <= 0L) return

        val positionMs = (duration * progressFraction.coerceIn(0f, 1f)).toLong()
        player.seekTo(positionMs)
        updatePlaybackState(progressFraction = progressFraction)
    }

    override fun toggleShuffle() {
        isShuffleEnabled = !isShuffleEnabled
        updatePlaybackState()
    }

    override fun cycleRepeatMode() {
        repeatMode = repeatMode.next()
        updatePlaybackState()
    }

    override fun release() {
        stopProgressUpdates()
        scope.cancel()
        player.release()
    }

    private val currentMusic: Music?
        get() = playlist.getOrNull(currentIndex)

    private fun handleTrackEnded() {
        if (playlist.isEmpty()) return

        when (repeatMode) {
            RepeatMode.REPEAT_ONE -> {
                player.seekTo(0)
                player.play()
            }

            RepeatMode.REPEAT_ALL -> {
                currentIndex = nextIndex()
                prepareAndPlay()
            }

            RepeatMode.OFF -> {
                if (isShuffleEnabled) {
                    currentIndex = nextIndex()
                    prepareAndPlay()
                } else if (currentIndex < playlist.lastIndex) {
                    currentIndex++
                    prepareAndPlay()
                } else {
                    player.seekTo(0)
                    player.pause()
                    updatePlaybackState(isPlaying = false, progressFraction = 0f)
                }
            }
        }
    }

    private fun nextIndex(): Int {
        if (isShuffleEnabled && playlist.size > 1) {
            var candidate = currentIndex
            while (candidate == currentIndex) {
                candidate = playlist.indices.random()
            }
            return candidate
        }
        return (currentIndex + 1) % playlist.size
    }

    private fun prepareAndPlay() {
        val music = currentMusic ?: return

        player.setMediaItem(music.toMediaItem())
        player.prepare()
        player.play()
        updatePlaybackState(currentMusic = music, isPlaying = true)
    }

    private fun updatePlaybackState(
        currentMusic: Music? = this.currentMusic,
        isPlaying: Boolean = player.isPlaying,
        progressFraction: Float = calculateProgressFraction()
    ) {
        val durationMs = player.duration.takeIf { it > 0L } ?: 0L
        val positionMs = player.currentPosition.coerceAtLeast(0L)
        _playbackState.update {
            it.copy(
                currentMusic = currentMusic,
                isPlaying = isPlaying,
                progressFraction = progressFraction,
                currentPositionMs = positionMs,
                durationMs = durationMs,
                isShuffleEnabled = isShuffleEnabled,
                repeatMode = repeatMode
            )
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                updatePlaybackState()
                delay(PROGRESS_UPDATE_INTERVAL_MS.milliseconds)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun calculateProgressFraction(): Float {
        val duration = player.duration
        if (duration <= 0L) return 0f
        return player.currentPosition.toFloat() / duration
    }

    private fun Music.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setUri(Uri.fromFile(File(filePath)))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(singerName)
                    .setAlbumTitle(albumName)
                    .build()
            )
            .build()
    }

    private companion object {
        const val SKIP_TO_START_THRESHOLD_MS = 3_000L
        const val PROGRESS_UPDATE_INTERVAL_MS = 500L
    }
}
