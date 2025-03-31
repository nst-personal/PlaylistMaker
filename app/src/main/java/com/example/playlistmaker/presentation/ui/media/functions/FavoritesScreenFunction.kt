package com.example.playlistmaker.presentation.ui.media.functions

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.common.TrackItem
import com.example.playlistmaker.presentation.ui.media.fragments.FavoriteViewModel
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.FavoriteTrackScreenState

@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel, openMediaPlayer: (
        track: Track
    ) -> Unit
) {
    val backgroundColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.black)
    }

    val screenState by viewModel.getLoadingTrackLiveData().asFlow().collectAsState(initial = null)

    val tracks = remember(screenState) {
        (screenState as? FavoriteTrackScreenState.FavoriteContent)?.tracks
    }

    if (tracks == null || tracks?.isEmpty() == true) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.not_found),
                    contentDescription = null,
                    modifier = Modifier
                        .width(dimensionResource(id = R.dimen.icon_media_issue))
                        .padding(top = dimensionResource(id = R.dimen.settings_description_top))
                )
                Text(
                    text = stringResource(id = R.string.media_favorites_empty),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.margin_top)),
                    fontWeight = FontWeight(400),
                    fontSize = 19.sp,
                    color = backgroundColor,
                    textAlign = TextAlign.Center,
                )
            }
        }
    } else {
        TracksList(tracks = tracks!!, { track ->
            openMediaPlayer(track)
        })
    }
}


@Composable
fun TracksList(
    tracks: List<Track>, openMediaPlayer: (
        track: Track
    ) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = dimensionResource(id = R.dimen.list_bottom),
                bottom = dimensionResource(id = R.dimen.list_bottom)
            ),
    ) {
        items(tracks) { track ->
            TrackItem(track = track, openMediaPlayer)
        }
    }
}