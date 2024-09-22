package com.example.playlistmaker.creators.media

import android.media.MediaPlayer
import com.example.playlistmaker.data.repositories.media.MediaRepositoryImpl
import com.example.playlistmaker.domain.interactors.media.MediaInteractor
import com.example.playlistmaker.domain.interactors.media.MediaInteractorImpl
import com.example.playlistmaker.domain.repositories.media.MediaRepository

object MediaCreator {
    private fun getMediaRepository(): MediaRepository {
        return MediaRepositoryImpl(MediaPlayer())
    }

    fun provideMediaInteractor(): MediaInteractor {
        return MediaInteractorImpl(getMediaRepository())
    }
}