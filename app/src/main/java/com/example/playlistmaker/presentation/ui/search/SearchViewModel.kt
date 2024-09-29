package com.example.playlistmaker.presentation.ui.search

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.interactors.track.TrackHistoryInteractor
import com.example.playlistmaker.presentation.ui.search.interfaces.TrackScreenState

class SearchViewModel(
    private val trackHistoryInteractor: TrackHistoryInteractor,
    private val trackInteractor: TrackInteractor
): ViewModel() {

    private var loadingTrackLiveData = MutableLiveData<TrackScreenState>()
    fun getLoadingTrackLiveData(): LiveData<TrackScreenState> = loadingTrackLiveData

    private val trackListHandler = Handler(Looper.getMainLooper())
    private val searchHandler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null

    override fun onCleared() {
        searchHandler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    init {
        showHistory()
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        if (changedText.isEmpty()) {
            return;
        }

        this.latestSearchText = changedText
        searchHandler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { handleSearchTracks(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        searchHandler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun handleSearchTracks(savedSearchValue: String) {
        trackInteractor.searchTracks(savedSearchValue, object : TrackInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?) {
                trackListHandler.postDelayed({
                    loadingTrackLiveData.postValue(TrackScreenState.SearchContent(foundTracks, savedSearchValue))
                }, SEARCH_HANDLE_DEBOUNCE_DELAY)
            }
        })
    }

    fun searchTracks(savedSearchValue: String) {
        trackInteractor.searchTracks(savedSearchValue, object : TrackInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?) {
                trackListHandler.postDelayed({
                    loadingTrackLiveData.postValue(TrackScreenState.SearchContent(foundTracks, savedSearchValue))
                }, SEARCH_HANDLE_DEBOUNCE_DELAY)
            }
        })
    }

    fun saveTrack(track: Track) {
        trackHistoryInteractor.updateLast(track)
    }

    fun clearHistory() {
        trackHistoryInteractor.remove()
    }

    fun addTrack(track: Track) {
        trackHistoryInteractor.add(track)
    }

    fun showHistory() {
        loadingTrackLiveData.postValue(TrackScreenState.HistoryContent(trackHistoryInteractor.findAll()))
    }

    companion object {
        const val SEARCH = "SEARCH"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val SEARCH_HANDLE_DEBOUNCE_DELAY = 1000L
        const val ITEM_BUTTON_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

}