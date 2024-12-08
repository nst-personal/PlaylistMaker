package com.example.playlistmaker.data.repositories.favorites

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.convertors.TrackDbConvertor
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.domain.repositories.favorites.FavoriteTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteTrackRepository {

    override fun favoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromMovieEntity(tracks))
    }

    override fun favoriteTrackIds(): Flow<List<Long>> = flow {
        val trackIds = appDatabase.trackDao().getTrackIds()
        emit(trackIds)
    }


    override suspend fun addTrack(track: Track) {
        appDatabase.trackDao().insertTrack(
            trackDbConvertor.map(track)
        )
    }

    override suspend fun removeTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(
            trackDbConvertor.map(track)
        )
    }

    private fun convertFromMovieEntity(movies: List<TrackEntity>): List<Track> {
        return movies.map { track -> trackDbConvertor.map(track) }.sortedBy { track -> track.isFavorite }
    }
}