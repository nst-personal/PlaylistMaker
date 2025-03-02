package com.example.playlistmaker.presentation.ui.media_player.interfaces

sealed class MediaState(val buttonState: Boolean, val progress: Int) {
    class Default : MediaState(false, 0)
    class Prepared : MediaState(true, 0)
    class Playing(progress: Int) : MediaState(true,  progress)
    class Paused(progress: Int) : MediaState(true,  progress)
}
//object MediaState {
//    const val STATE_DEFAULT = 0
//    const val STATE_PREPARED = 1
//    const val STATE_PLAYING = 2
//    const val STATE_PAUSED = 3
//}