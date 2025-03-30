package com.example.playlistmaker.presentation.ui.media.functions

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.ui.media.fragments.PlaylistListViewModel
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistListScreenState

@Composable
fun PlaylistScreen(
    playlistListViewModel: PlaylistListViewModel,
    onAddNewClick: () -> Unit,
    openPlaylist: (Playlist) -> Unit
) {
    var playlists: List<Playlist>? = null

    val data by playlistListViewModel.getLoadingPlaylistLiveData().asFlow()
        .collectAsState(initial = null)

    if (data is PlaylistListScreenState.PlaylistListContent) {
        playlists = (data as PlaylistListScreenState.PlaylistListContent).playlists
    }

    val textColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.black)
    } else {
        colorResource(id = R.color.white)
    }
    val backgroundColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.black)
    }


    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Button(
                onClick = onAddNewClick,
                shape = RoundedCornerShape(size = 24.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.media_add_new_top),
                        bottom = dimensionResource(id = R.dimen.media_add_new_top),
                    )
            ) {
                Text(text = stringResource(id = R.string.media_add_new_playlist), color = textColor)
            }


            if (playlists == null || playlists?.isEmpty() == true) {
                Image(
                    painter = painterResource(id = R.drawable.not_found),
                    contentDescription = null,
                    modifier = Modifier
                        .width(dimensionResource(id = R.dimen.icon_media_issue))
                        .padding(top = dimensionResource(id = R.dimen.title_bottom_margin))
                )
                Text(
                    text = stringResource(id = R.string.media_playlist_empty),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.margin_top))
                        .fillMaxWidth(fraction = 0.5f),
                    fontWeight = FontWeight(400),
                    fontSize = 19.sp,
                    color = backgroundColor,
                    textAlign = TextAlign.Center,
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = dimensionResource(id = R.dimen.margin_top)),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(playlists.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { playlist ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    PlaylistItem(
                                        playlist = playlist,
                                        onClick = { item ->
                                            openPlaylist(item)
                                        }
                                    )
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

        }
    }

}


@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: (Playlist) -> Unit
) {
    val textColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.black)
    }
    val tracksCount = if (playlist.playlistTracksCount.toInt() == 1) {
        playlist.playlistTracksCount.toString() + " " +
                stringResource(id = R.string.playlist_track)
    } else {
        (playlist.playlistTracksCount.toString()) + " " +
                stringResource(id = R.string.playlist_tracks)
    }
    Column(
        modifier = Modifier
            .padding(bottom = dimensionResource(id = R.dimen.cardview_bottom))
            .clickable(onClick = {
                onClick(playlist)
            }),
    ) {
        AsyncImage(
            model = playlist.playlistImageUrl,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.placeholder),
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = playlist.playlistName,
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold
            ),
            fontSize = dimensionResource(id = R.dimen.playlist_font_size_title).value.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = textColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
        )

        Text(
            text = tracksCount,
            style = MaterialTheme.typography.subtitle1.copy(
                fontWeight = FontWeight.Bold
            ),
            color = textColor,
            fontSize = dimensionResource(id = R.dimen.playlist_font_size_track_size).value.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )
    }
}