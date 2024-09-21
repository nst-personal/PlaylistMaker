package com.example.playlistmaker.presentation.ui.media_player

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creators.track.TrackHistoryCreator
import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.domain.interactors.track.TrackHistoryInteractor

class MediaViewModel(
    trackHistoryInteractor: TrackHistoryInteractor,
): ViewModel() {
    private var loadingTrackLiveData = MutableLiveData<Track>()
    fun getLoadingTrackLiveData(): LiveData<Track> = loadingTrackLiveData
    init {
        val data = trackHistoryInteractor.findLast()
        loadingTrackLiveData.postValue(data)
    }
    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val interactor = TrackHistoryCreator.provideTrackHistoryManager(context)
                MediaViewModel(
                    interactor,
                )
            }
        }
    }

}