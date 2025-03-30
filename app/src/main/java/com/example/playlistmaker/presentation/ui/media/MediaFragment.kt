package com.example.playlistmaker.presentation.ui.media

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.asFlow
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.media.fragments.FavoriteViewModel
import com.example.playlistmaker.presentation.ui.media.fragments.PlaylistListViewModel
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.FavoriteTrackScreenState
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistListScreenState
import com.example.playlistmaker.presentation.ui.media_player.MediaPlayerActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class MediaFragment : Fragment() {
    private lateinit var binding: FragmentMediaBinding
    private val viewModel: FavoriteViewModel by viewModel()
    private val playlistListViewModel: PlaylistListViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun openMediaPlayer(track: Track) {
        viewModel.saveTrack(track)
        val displayMediaIntent = Intent(requireContext(), MediaPlayerActivity::class.java)
        startActivity(displayMediaIntent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.showFavorites()
        playlistListViewModel.showPlaylist()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.media) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.composeView.setContent {
            MediaScreen(viewModel, playlistListViewModel, { track ->
                openMediaPlayer(track)
            }, {
                findNavController().navigate(R.id.action_media_to_playlistCreateFragment3)
            }, { playlistItem ->
                val bundle = Bundle().apply {
                    putLong("playlistId", playlistItem.playlistId)
                }
                findNavController().navigate(R.id.action_media_to_playListDetailsFragment, bundle)
            })
        }
    }

}

@Composable
fun MediaScreen(
    viewModel: FavoriteViewModel,
    playlistListViewModel: PlaylistListViewModel,
    openMediaPlayer: (
        track: Track
    ) -> Unit,
    onAddNewClick: () -> Unit,
    openPlaylist: (Playlist) -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    var selectedTabIndex by remember { mutableStateOf(pagerState.currentPage) }

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    val textColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.black)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.common_main_header_small))
            .background(MaterialTheme.colors.background)
            .padding(
                start = dimensionResource(id = R.dimen.main_top_padding_left),
                bottom = dimensionResource(id = R.dimen.media_bottom_margin)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.media_title),
            style = MaterialTheme.typography.h6,
            fontSize = dimensionResource(id = R.dimen.font_size).value.sp,
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.button_margin))
        )
    }
    Row(
        modifier = Modifier
            .padding(
                top = dimensionResource(id = R.dimen.main_top_padding_top),
            )
            .padding(
                horizontal = dimensionResource(id = R.dimen.main_top_padding_horizontal),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.Transparent,
                contentColor = textColor,
            ) {
                Tab(
                    selected = (selectedTabIndex == 0),
                    onClick = {
                        scope.launch {
                            selectedTabIndex = 0
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.media_tab_title_favorites),
                            color = textColor
                        )
                    },
                )
                Tab(
                    selected = (selectedTabIndex == 1),
                    onClick = {
                        scope.launch {
                            selectedTabIndex = 1
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.media_tab_title_playlist),
                            color = textColor
                        )
                    },
                )
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) { page ->
                when (page) {
                    0 -> FavoriteScreen(viewModel, openMediaPlayer)
                    1 -> PlaylistScreen(
                        playlistListViewModel,
                        onAddNewClick,
                        openPlaylist
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel, openMediaPlayer: (
        track: Track
    ) -> Unit
) {
    var tracks by remember { mutableStateOf<List<Track>?>(null) }

    val screenState by viewModel.getLoadingTrackLiveData().asFlow().collectAsState(initial = null)

    if (screenState is FavoriteTrackScreenState.FavoriteContent) {
        tracks = (screenState as FavoriteTrackScreenState.FavoriteContent).tracks
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
                    textAlign = TextAlign.Center,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(bottom = dimensionResource(id = R.dimen.margin_top)),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistItem(
                            playlist = playlist,
                            onClick = { item ->
                                openPlaylist(item)
                            }
                        )
                    }
                }
            }

        }
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

@Composable
fun TrackItem(
    track: Track,
    openMediaPlayer: (
        track: Track
    ) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = dimensionResource(id = R.dimen.item_top_padding),
                bottom = dimensionResource(id = R.dimen.item_top_padding)
            )
            .clickable {
                openMediaPlayer(track)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.placeholder)
        )

        Spacer(modifier = Modifier.width(16.dp))

        TrackInfo(
            track = track,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Image(
            painter = painterResource(id = R.drawable.forward),
            contentDescription = "",
            modifier = Modifier
                .width(24.dp)
        )
    }
}

@Composable
fun TrackInfo(
    track: Track,
) {
    val textColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.black)
    }

    Column(
        modifier = Modifier.fillMaxWidth(0.85f)
    ) {
        Text(
            color = textColor,
            text = track.trackName,
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                color = textColor,
                text = track.artistName,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.45f)
                    .wrapContentWidth(align = Alignment.Start)
            )

            Spacer(modifier = Modifier.width(2.dp))

            Image(
                painter = painterResource(id = R.drawable.point),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(2.dp))

            Text(
                color = textColor,
                text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis),
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: (Playlist) -> Unit
) {
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
                .width(160.dp)
                .height(160.dp)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
        )

        playlist.playlistDescription?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold
                ),
                fontSize = dimensionResource(id = R.dimen.playlist_font_size_track_size).value.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }
    }
}