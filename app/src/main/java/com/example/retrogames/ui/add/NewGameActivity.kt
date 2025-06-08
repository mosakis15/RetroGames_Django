package com.example.retrogames.ui.add

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.retrogames.ui.theme.RetroGamesTheme
import com.example.retrogames.util.RetrofitClient
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import androidx.activity.compose.rememberLauncherForActivityResult



class NewGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RetroGamesTheme {
                NewGameScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameScreen() {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("") }
    var releaseDate by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageData by remember { mutableStateOf<ByteArray?>(null) }
    var submitting by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val bitmap = result.data?.extras?.get("data") as? Bitmap
        bitmap?.let {
            imageBitmap = it
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            imageData = stream.toByteArray()
        }
    }

    fun pickDate(onDateSelected: (String) -> Unit) {
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
        topBar = { TopAppBar(title = { Text("Νέο Retro Game") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
            OutlinedTextField(
                value = platform,
                onValueChange = { platform = it },
                label = { Text("Πλατφόρμα") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { pickDate { releaseDate = it } }, modifier = Modifier.fillMaxWidth()) {
                Text(if (releaseDate.isEmpty()) "Επιλογή Ημερομηνίας" else releaseDate)
            }
            Button(
                onClick = {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraLauncher.launch(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Λήψη Φωτογραφίας")
            }
            imageBitmap?.let {
                Image(bitmap = it.asImageBitmap(), contentDescription = "Captured Image", modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp))
            }
            Button(
                onClick = {
                    if (title.isBlank() || platform.isBlank() || releaseDate.isBlank() || imageData == null) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Συμπλήρωσε όλα τα πεδία και τράβηξε φωτογραφία.")
                        }
                        return@Button
                    }

                    submitting = true

                    coroutineScope.launch {
                        try {
                            val titlePart = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
                            val platformPart = RequestBody.create("text/plain".toMediaTypeOrNull(), platform)
                            val datePart = RequestBody.create("text/plain".toMediaTypeOrNull(), releaseDate)
                            val imagePart = MultipartBody.Part.createFormData(
                                "cover_image", "image.jpg",
                                RequestBody.create("image/*".toMediaTypeOrNull(), imageData!!)
                            )

                            val response = withContext(Dispatchers.IO) {
                                RetrofitClient.apiService.createGame(
                                    titlePart, platformPart, datePart, imagePart
                                )
                            }

                            if (response.isSuccessful) {
                                snackbarHostState.showSnackbar("Το παιχνίδι αποθηκεύτηκε με επιτυχία!")
                                title = ""
                                platform = ""
                                releaseDate = ""
                                imageBitmap = null
                                imageData = null
                            } else {
                                snackbarHostState.showSnackbar("Σφάλμα ${response.code()}: Δεν ολοκληρώθηκε η αποθήκευση.")
                            }

                        } catch (e: HttpException) {
                            snackbarHostState.showSnackbar("Σφάλμα δικτύου: ${e.code()}")
                        } catch (e: IOException) {
                            snackbarHostState.showSnackbar("Δεν υπάρχει σύνδεση. Δοκίμασε ξανά.")
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Άγνωστο σφάλμα: ${e.localizedMessage}")
                        } finally {
                            submitting = false
                        }
                    }
                },
                enabled = !submitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (submitting) "Υποβολή..." else "Αποθήκευση")
            }
        }
    }
}
