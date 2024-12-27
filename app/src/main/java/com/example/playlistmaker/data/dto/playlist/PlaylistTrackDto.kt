package com.example.playlistmaker.data.dto.playlist

import com.example.playlistmaker.data.dto.search.TrackDto

data class PlaylistTrackDto(
    val tracks: MutableList<TrackDto>
)