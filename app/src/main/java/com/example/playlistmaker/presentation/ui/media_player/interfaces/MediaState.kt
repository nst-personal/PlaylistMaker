package com.example.playlistmaker.presentation.ui.media_player.interfaces

sealed class MediaState(val buttonState: Boolean, val progress: Int) {
    class Default : MediaState(false, 0)
    class Prepared : MediaState(true, 0)
    class Playing(progress: Int) : MediaState(true,  progress)
    class Paused(progress: Int) : MediaState(true,  progress)
}
