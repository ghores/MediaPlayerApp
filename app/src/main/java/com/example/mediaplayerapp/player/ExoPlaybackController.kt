package com.example.mediaplayerapp.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mediaplayerapp.domain.model.Music
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
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
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class ExoPlaybackController @Inject constructor(
    @ApplicationContext context: Context
) : PlaybackController {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var playlist: List<Music> = emptyList()
    private var progressJob: Job? = null
    private var pendingPlay: PendingPlay? = null

    private var controller: MediaController? = null
    private val controllerFuture: ListenableFuture<MediaController>

    private val _playbackState = MutableStateFlow(PlaybackUiState())
    override val playbackState: StateFlow<PlaybackUiState> = _playbackState.asStateFlow()

    init {
        val token = SessionToken(context, ComponentName(context, MusicService::class.java))
        controllerFuture = MediaController.Builder(context, token).buildAsync()
        controllerFuture.addListener(
            { onControllerReady(controllerFuture.get()) },
            ContextCompat.getMainExecutor(context)
        )
    }

    private fun onControllerReady(mediaController: MediaController) {
        controller = mediaController
        mediaController.addListener(playerListener)

        pendingPlay?.let { play(it.playlist, it.startIndex) }
        pendingPlay = null

        updatePlaybackState()
        if (mediaController.isPlaying) startProgressUpdates()
    }

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            updatePlaybackState()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) startProgressUpdates() else stopProgressUpdates()
        }
    }

    override fun play(playlist: List<Music>, startIndex: Int) {
        if (playlist.isEmpty()) return

        val activeController = controller
        if (activeController == null) {
            pendingPlay = PendingPlay(playlist, startIndex)
            return
        }

        this.playlist = playlist
        val index = startIndex.coerceIn(0, playlist.lastIndex)
        activeController.setMediaItems(playlist.map { it.toMediaItem() }, index, 0L)
        activeController.prepare()
        activeController.play()
        updatePlaybackState()
    }

    override fun togglePlayPause() {
        val activeController = controller ?: return
        when {
            activeController.isPlaying -> activeController.pause()
            activeController.playbackState == Player.STATE_IDLE -> {
                activeController.prepare()
                activeController.play()
            }

            activeController.playbackState == Player.STATE_ENDED -> {
                activeController.seekToDefaultPosition(0)
                activeController.play()
            }

            else -> activeController.play()
        }
    }

    override fun playNext() {
        controller?.seekToNext()
    }

    override fun playPrevious() {
        controller?.seekToPrevious()
    }

    override fun seekTo(progressFraction: Float) {
        val activeController = controller ?: return
        val duration = activeController.duration
        if (duration <= 0L) return

        val positionMs = (duration * progressFraction.coerceIn(0f, 1f)).toLong()
        activeController.seekTo(positionMs)
        updatePlaybackState()
    }

    override fun toggleShuffle() {
        val activeController = controller ?: return
        activeController.shuffleModeEnabled = !activeController.shuffleModeEnabled
    }

    override fun cycleRepeatMode() {
        val activeController = controller ?: return
        val nextMode = activeController.repeatMode.toRepeatMode().next()
        activeController.repeatMode = nextMode.toPlayerRepeatMode()
    }

    override fun release() {
        stopProgressUpdates()
        controller?.removeListener(playerListener)
        controller = null
        MediaController.releaseFuture(controllerFuture)
        scope.cancel()
    }

    private fun updatePlaybackState() {
        val activeController = controller ?: return

        val music = playlist.getOrNull(activeController.currentMediaItemIndex)
        val durationMs = activeController.duration.takeIf { it > 0L } ?: 0L
        val positionMs = activeController.currentPosition.coerceAtLeast(0L)
        val progressFraction =
            if (durationMs > 0L) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f

        _playbackState.update {
            PlaybackUiState(
                currentMusic = music,
                isPlaying = activeController.isPlaying,
                progressFraction = progressFraction,
                currentPositionMs = positionMs,
                durationMs = durationMs,
                isShuffleEnabled = activeController.shuffleModeEnabled,
                repeatMode = activeController.repeatMode.toRepeatMode()
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

    private fun Int.toRepeatMode(): RepeatMode = when (this) {
        Player.REPEAT_MODE_ONE -> RepeatMode.REPEAT_ONE
        Player.REPEAT_MODE_ALL -> RepeatMode.REPEAT_ALL
        else -> RepeatMode.OFF
    }

    private fun RepeatMode.toPlayerRepeatMode(): Int = when (this) {
        RepeatMode.OFF -> Player.REPEAT_MODE_OFF
        RepeatMode.REPEAT_ALL -> Player.REPEAT_MODE_ALL
        RepeatMode.REPEAT_ONE -> Player.REPEAT_MODE_ONE
    }

    private fun Music.toMediaItem(): MediaItem = MediaItem.Builder()
        .setMediaId(filePath)
        .setUri(Uri.fromFile(File(filePath)))
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(singerName)
                .setAlbumTitle(albumName)
                .setArtworkUri(coverArtUri)
                .build()
        )
        .build()

    private data class PendingPlay(
        val playlist: List<Music>,
        val startIndex: Int
    )

    private companion object {
        const val PROGRESS_UPDATE_INTERVAL_MS = 500L
    }
}
