package com.example.playlistmaker.domain.interactors.media

import com.example.playlistmaker.domain.repositories.media.MediaRepository

class MediaInteractorImpl(private val mediaRepository: MediaRepository) : MediaInteractor {
    override fun init(url: String,
                      preparedListenerCallback: () -> Unit,
                      completionListener: () -> Unit) {
        mediaRepository.initialize(url, preparedListenerCallback, completionListener)
    }
    override fun release() {
        mediaRepository.release()
    }

    override fun stop() {
        mediaRepository.stop()
    }

    override fun start() {
        mediaRepository.start()
    }

    override fun pause() {
        mediaRepository.pause()
    }

    override fun currentPosition(): Int {
        return mediaRepository.currentPosition()
    }

    override fun isPlaying() : Boolean {
        return mediaRepository.isPlaying()
    }
}