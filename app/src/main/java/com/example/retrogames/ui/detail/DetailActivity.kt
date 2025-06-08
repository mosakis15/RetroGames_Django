package com.example.retrogames.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.retrogames.data.model.RetroGame
import com.example.retrogames.ui.theme.RetroGamesTheme
import com.example.retrogames.util.RetrofitClient
import kotlinx.coroutines.Dispatchers

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameId = intent.getIntExtra("gameId", -1)
        setContent {
            RetroGamesTheme {
                if (gameId != -1) {
                    DetailScreen(gameId)
                } else {
                    Text("Μη έγκυρο παιχνίδι.", modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(gameId: Int) {
    val context = LocalContext.current
    var game by remember { mutableStateOf<RetroGame?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(gameId) {
        kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                game = RetrofitClient.apiService.getGameDetails(gameId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Λεπτομέρειες Παιχνιδιού") }) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                game?.let {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(it.title, style = MaterialTheme.typography.headlineMedium)
                        Text("Πλατφόρμα: ${it.platform}", style = MaterialTheme.typography.bodyLarge)
                        Text("Ημερομηνία: ${it.release_date}", style = MaterialTheme.typography.bodyLarge)
                        Image(
                            painter = rememberAsyncImagePainter(it.cover_image),
                            contentDescription = "Cover Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.youtube_video))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Δες Trailer στο YouTube")
                        }
                    }
                } ?: Text("Παιχνίδι δεν βρέθηκε", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
