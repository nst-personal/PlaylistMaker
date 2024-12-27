package com.example.playlistmaker.data.repositories.track

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.configuration.ShareablePreferencesConfig
import com.example.playlistmaker.data.dto.history.TrackHistory
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repositories.track.TrackHistoryRepository
import com.google.gson.Gson

class TrackHistoryRepositoryImpl(private val context: Context,
    private val gson: Gson
) : TrackHistoryRepository {
    companion object {
        const val countOfTracks = 10
    }

    override fun isEmpty() : Boolean {
        val trackHistory = getHistory()
        return trackHistory.results.isEmpty()
    }

    override fun findAll(): MutableList<Track> {
        val trackHistory = getHistory()
        return trackHistory.results;
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

    override fun findLast() : Track {
        val media = context.getSharedPreferences(ShareablePreferencesConfig.CURRENT_MEDIA,
            AppCompatActivity.MODE_PRIVATE
        )
        return gson.fromJson(media.getString(ShareablePreferencesConfig.CURRENT_MEDIA, null), Track::class.java)
    }

    override fun updateLast(track: Track) {
        val sharedPreferences = context.getSharedPreferences(ShareablePreferencesConfig.CURRENT_MEDIA,
            AppCompatActivity.MODE_PRIVATE
        )
        sharedPreferences.edit().putString(ShareablePreferencesConfig.CURRENT_MEDIA, gson.toJson(track)).apply()
    }

    private fun getHistory() : TrackHistory {
        val history =
            context.getSharedPreferences(ShareablePreferencesConfig.HISTORY_LIST,
                AppCompatActivity.MODE_PRIVATE
            ).getString(ShareablePreferencesConfig.HISTORY_LIST, "")
        var trackHistory = TrackHistory(arrayListOf());
        if (!history.isNullOrEmpty()) {
            trackHistory = gson.fromJson(history, TrackHistory::class.java)
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
            gson.toJson(trackHistory)
        ).apply()
    }

}