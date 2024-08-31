package com.example.playlistmaker.data.dto.history

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.search.TrackDto

class TrackResponse (val resultCount: Number,
                     val results: List<TrackDto>?) : Response()