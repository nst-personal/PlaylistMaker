package com.example.playlistmaker.data.repositories

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.configuration.ShareablePreferencesConfig
import com.example.playlistmaker.data.entities.Track
import com.example.playlistmaker.domain.repositories.TrackHistoryRepository
import com.example.playlistmaker.data.dto.history.TrackHistory
import com.google.gson.Gson

class TrackHistoryRepositoryImpl(private val context: Context) : TrackHistoryRepository {
    companion object {
        const val countOfTracks = 10
    }

    override fun isEmpty() : Boolean {
        val trackHistory = getHistory()
        return trackHistory.results.isEmpty()
    }

    override fun findAll(): MutableList<Track> {
        val trackHistory = getHistory()
        return trackHistory.results
    }

    override fun remove() {
        val trackHistory = getHistory()
        trackHistory.results.clear()
        saveHistory(trackHistory)
    }

    override fun add(track: Track) {
        val trackHistory = getHistory()
        trackHistory.results.removeIf{historyTrack ->
            historyTrack.trackId == track.trackId
        }
        trackHistory.results.add(0, track)
        saveHistory(trackHistory)
    }

    private fun getHistory() : TrackHistory {
        val history =
            context.getSharedPreferences(ShareablePreferencesConfig.HISTORY_LIST,
                AppCompatActivity.MODE_PRIVATE
            ).getString(ShareablePreferencesConfig.HISTORY_LIST, "")
        var trackHistory = TrackHistory(arrayListOf());
        if (!history.isNullOrEmpty()) {
            trackHistory = Gson().fromJson(history, TrackHistory::class.java)
        }
        if (trackHistory.results.size >= countOfTracks) {
            trackHistory.results = trackHistory.results.subList(0, countOfTracks - 1).toMutableList()
        }
        return trackHistory
    }

    private fun saveHistory(trackHistory: TrackHistory) {
        context.getSharedPreferences(ShareablePreferencesConfig.HISTORY_LIST,
            AppCompatActivity.MODE_PRIVATE
        ).edit().putString(
            ShareablePreferencesConfig.HISTORY_LIST,
            Gson().toJson(trackHistory)
        ).apply()
    }

}