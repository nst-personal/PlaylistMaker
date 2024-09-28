package com.example.playlistmaker.domain.repositories.media

interface MediaRepository {
    fun initialize(
        url: String,
        preparedListenerCallback: () -> Unit,
        completionListener: () -> Unit)
    fun release()
    fun start()
    fun pause()
    fun currentPosition() : Int
}