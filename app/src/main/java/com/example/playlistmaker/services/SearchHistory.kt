package com.example.playlistmaker.services

import android.content.SharedPreferences
import com.example.playlistmaker.configuration.ShareablePreferencesConfig
import com.example.playlistmaker.entities.Track
import com.example.playlistmaker.models.TrackHistory
import com.google.gson.Gson

class SearchHistory(val shareableHistory: SharedPreferences) {
    companion object {
        const val countOfTracks = 10
    }

    fun isEmpty() : Boolean {
        val trackHistory = getHistory()
        return trackHistory.results.isEmpty()
    }

    fun findAll(): MutableList<Track> {
        val trackHistory = getHistory()
        return trackHistory.results
    }

    fun remove() {
        val trackHistory = getHistory()
        trackHistory.results.clear()
        saveHistory(trackHistory)
    }

    fun add(track: Track) {
        val trackHistory = getHistory()
        trackHistory.results.removeIf{historyTrack ->
            historyTrack.trackId == track.trackId
        }
        trackHistory.results.add(0, track)
        saveHistory(trackHistory)
    }

    private fun getHistory() : TrackHistory {
        val history =
            shareableHistory.getString(ShareablePreferencesConfig.HISTORY_LIST, "")
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
        shareableHistory.edit().putString(
            ShareablePreferencesConfig.HISTORY_LIST,
            Gson().toJson(trackHistory)
        ).apply()
    }

}