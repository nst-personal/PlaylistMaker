package com.example.playlistmaker.presentation.ui.media.functions

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.media.fragments.FavoriteViewModel
import com.example.playlistmaker.presentation.ui.media.fragments.PlaylistListViewModel
import kotlinx.coroutines.launch


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
            .padding(
                start = dimensionResource(id = R.dimen.main_top_padding_left),
                bottom = dimensionResource(id = R.dimen.media_bottom_margin),
                top = dimensionResource(id = R.dimen.button_margin)
            ),
    ) {
        Text(
            text = stringResource(id = R.string.media_title),
            style = MaterialTheme.typography.h6,
            fontSize = dimensionResource(id = R.dimen.font_size).value.sp,
            color = textColor,
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
