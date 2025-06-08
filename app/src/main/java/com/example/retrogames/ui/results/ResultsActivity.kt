package com.example.retrogames.ui.results

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.retrogames.data.model.RetroGame
import com.example.retrogames.ui.detail.DetailActivity
import com.example.retrogames.ui.theme.RetroGamesTheme
import com.example.retrogames.util.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = intent.getStringExtra("title")
        val dateFrom = intent.getStringExtra("dateFrom")
        val dateTo = intent.getStringExtra("dateTo")

        setContent {
            RetroGamesTheme {
                ResultsScreen(title, dateFrom, dateTo)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(title: String?, dateFrom: String?, dateTo: String?) {
    val context = LocalContext.current
    var games by remember { mutableStateOf<List<RetroGame>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("FILTERS", "title=$title, from=$dateFrom, to=$dateTo")

                val result = RetrofitClient.apiService.getGames(
                    title = title?.takeIf { it.isNotBlank() },
                    dateFrom = dateFrom?.takeIf { it.isNotBlank() },
                    dateTo = dateTo?.takeIf { it.isNotBlank() }
                )

                Log.d("API_RESULT", result.toString())
                games = result
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error fetching games: ${e.message}", e)
                snackbarHostState.showSnackbar("Σφάλμα φόρτωσης δεδομένων")
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Αποτελέσματα Αναζήτησης") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                }
            } else {
                if (games.isEmpty()) {
                    Text("Δεν βρέθηκαν αποτελέσματα", style = MaterialTheme.typography.bodyLarge)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(games) { game ->
                            GameItem(game)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameItem(game: RetroGame) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        onClick = {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("gameId", game.id)
            }
            context.startActivity(intent)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(game.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Πλατφόρμα: ${game.platform}", style = MaterialTheme.typography.bodyMedium)
            Text("Ημερομηνία: ${game.release_date}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
