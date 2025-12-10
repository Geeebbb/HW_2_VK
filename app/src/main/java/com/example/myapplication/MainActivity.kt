package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.data.ApiService
import com.example.myapplication.data.DataRepository
import com.example.myapplication.uii.MainScreen
import com.example.myapplication.uii.MainViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/*
//import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            MyApplicationTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//}
*/

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}

class MainActivity : ComponentActivity() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.thecatapi.com/") //хост передаем прям вот сюда
        .addConverterFactory(
            Json { ignoreUnknownKeys = true }
                .asConverterFactory("application/json; charset=UTF8".toMediaType()) //для таких форматов используй конвектор котлин сериализации
        )
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val api = retrofit.create(ApiService::class.java)
        val repo = DataRepository(api)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repo, SavedStateHandle()) as T
            }
        }

        setContent {
            val vm: MainViewModel = viewModel(factory = factory)
            MainScreen(vm)
        }
    }
}