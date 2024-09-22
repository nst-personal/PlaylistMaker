package com.example.playlistmaker.domain.interactors.media

interface MediaInteractor {
    fun init(url: String,
             preparedListenerCallback: () -> Unit,
             completionListener: () -> Unit)
    fun release()
    fun start()
    fun pause()
    fun currentPosition() : Int
}