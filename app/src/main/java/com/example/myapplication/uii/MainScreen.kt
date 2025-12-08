package com.example.myapplication.uii

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.material.Button
//import androidx.compose.material.CircularProgressIndicator
//import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val ctx = LocalContext.current
    val state by viewModel.state.collectAsState()

    when (state) {
        // Состояние: Загрузка
        UiState.Loading -> Box(
            Modifier.fillMaxSize(),
            Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        // Состояние: Ошибка
        is UiState.Error -> Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text((state as UiState.Error).message)
            Button(onClick = { viewModel.loadNextPage() }) {
                Text("Повторить")
            }
        }

        is UiState.Success -> {
            val items = (state as UiState.Success).images

            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Отображение загруженных картинок
                itemsIndexed(items) { index, item ->
                    Image(
                        painter = rememberAsyncImagePainter(item.url),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clickable {
                                Toast.makeText(
                                    ctx,
                                    "Картинка №${index + 1}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        contentScale = ContentScale.Crop
                    )
                }

                // Индикатор загрузки следующей страницы
                item {
                    viewModel.loadNextPage()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}