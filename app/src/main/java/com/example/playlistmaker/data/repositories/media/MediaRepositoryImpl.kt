package com.example.playlistmaker.data.repositories.media

import android.media.MediaPlayer
import com.example.playlistmaker.domain.repositories.media.MediaRepository

class MediaRepositoryImpl(private val mediaPlayer: MediaPlayer) : MediaRepository {
    override fun initialize(
        url: String,
        preparedListenerCallback: () -> Unit,
        completionListener: () -> Unit) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            preparedListenerCallback()
        }
        mediaPlayer.setOnCompletionListener {
            completionListener()
        }
    }


    override fun release() {
         mediaPlayer.release()
    }

    override fun stop() {
        mediaPlayer.stop()
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun currentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun isPlaying() : Boolean {
        return mediaPlayer.isPlaying
    }
}