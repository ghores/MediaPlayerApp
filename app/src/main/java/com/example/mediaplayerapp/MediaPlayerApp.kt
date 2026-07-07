package com.example.mediaplayerapp

import android.app.Application
import com.example.mediaplayerapp.di.AppContainer

class MediaPlayerApp : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
