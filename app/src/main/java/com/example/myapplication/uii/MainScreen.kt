package com.example.myapplication.uii

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.R
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.snapshotFlow

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val ctx = LocalContext.current
    val state by viewModel.state.collectAsState()
    val gridState = rememberLazyStaggeredGridState()

    when (state) {
        is UiState.LoadingInitial -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        is UiState.Error -> {
            val err = state as UiState.Error
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(err.message)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.retry() }) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }

        is UiState.Success -> {
            val success = state as UiState.Success
            val items = success.images
            val isLoadingMore = success.isLoadingMore

            Box(Modifier.fillMaxSize()) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(items) { index, item ->
                        val aspect = item.width?.toFloat()?.let { w ->
                            item.height?.let { h -> if (h > 0) w / h else 1f }
                        } ?: 1f

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(aspect)
                                .clickable {
                                    Toast.makeText(ctx, ctx.getString(R.string.image_number, index + 1), Toast.LENGTH_SHORT).show()
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            AsyncImage(
                                model = item.url,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    item {
                        if (isLoadingMore) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }

                LaunchedEffect(gridState) {
                    snapshotFlow { gridState.layoutInfo }
                        .map { it.totalItemsCount to it.visibleItemsInfo.map { v -> v.index }.maxOrNull() }
                        .distinctUntilChanged()
                        .collect { (totalItems, lastVisible) ->
                            val last = lastVisible ?: 0
                            if (totalItems > 0 && last >= totalItems - 6) {
                                viewModel.loadNextPage(initial = false)
                            }
                        }
                }
            }
        }
    }
}
