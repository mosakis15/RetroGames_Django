package com.example.retrogames

import android.content.Intent
import android.os.Bundle
import android.app.DatePickerDialog
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.retrogames.ui.theme.RetroGamesTheme
import com.example.retrogames.ui.results.ResultsActivity
import com.example.retrogames.ui.add.NewGameActivity
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RetroGamesTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var dateFrom by remember { mutableStateOf("") }
    var dateTo by remember { mutableStateOf("") }

    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                onDateSelected(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Αναζήτηση Retro Games") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Τίτλος") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showDatePicker { dateFrom = it } },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (dateFrom.isEmpty()) "Από" else dateFrom)
                }
                Button(
                    onClick = { showDatePicker { dateTo = it } },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (dateTo.isEmpty()) "Έως" else dateTo)
                }
            }
            Button(
                onClick = {
                    val intent = Intent(context, ResultsActivity::class.java).apply {
                        putExtra("title", title)
                        putExtra("dateFrom", dateFrom)
                        putExtra("dateTo", dateTo)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Αναζήτηση")
            }
            OutlinedButton(
                onClick = { context.startActivity(Intent(context, NewGameActivity::class.java)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Προσθήκη Νέου Παιχνιδιού")
            }
        }
    }
}
