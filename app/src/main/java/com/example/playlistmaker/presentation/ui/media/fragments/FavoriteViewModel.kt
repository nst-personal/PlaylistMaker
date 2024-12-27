package com.example.playlistmaker.presentation.ui.media.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.interactors.favorites.FavoriteTrackInteractor
import com.example.playlistmaker.domain.interactors.track.TrackHistoryInteractor
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.FavoriteTrackScreenState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavoriteViewModel (
    private val trackHistoryInteractor: TrackHistoryInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
): ViewModel() {
    private var loadingTrackLiveData = MutableLiveData<FavoriteTrackScreenState>()
    fun getLoadingTrackLiveData(): LiveData<FavoriteTrackScreenState> = loadingTrackLiveData

    init {
        showFavorites()
    }

    fun saveTrack(track: Track) {
        trackHistoryInteractor.updateLast(track)
    }

    fun showFavorites() {
        viewModelScope.launch {
            favoriteTrackInteractor.getFavoriteTracks()
                .onEach { data ->
                    loadingTrackLiveData.postValue(FavoriteTrackScreenState.FavoriteContent(data))
                }
                .launchIn(this)
        }
    }

}