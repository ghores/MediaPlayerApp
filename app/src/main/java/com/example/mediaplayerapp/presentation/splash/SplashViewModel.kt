package com.example.mediaplayerapp.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SplashViewModel : ViewModel() {

    private val _navigateToHome = Channel<Unit>(Channel.BUFFERED)
    val navigateToHome = _navigateToHome.receiveAsFlow()

    init {
        viewModelScope.launch {
            delay(SPLASH_DELAY_MS.milliseconds)
            _navigateToHome.send(Unit)
        }
    }

    private companion object {
        const val SPLASH_DELAY_MS = 3_000L
    }
}
