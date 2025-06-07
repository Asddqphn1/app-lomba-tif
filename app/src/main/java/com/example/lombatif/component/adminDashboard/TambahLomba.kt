package com.example.lombatif.component.adminDashboard

import android.app.DatePickerDialog
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lombatif.models.request.JenisLomba
import com.example.lombatif.models.request.RequestLomba
import com.example.lombatif.viewModels.SubmitStatus
import com.example.lombatif.viewModels.ViewTambahLomba
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahLomba(
    onDismiss: () -> Unit,
    viewModel: ViewTambahLomba = viewModel()
) {
    val id = remember { mutableStateOf("") }
    val nama = remember { mutableStateOf("") }
    val tanggal = remember { mutableStateOf("") }
    val lokasi = remember { mutableStateOf("") }
    val deskripsi = remember { mutableStateOf("") }
    val batasWaktu = remember { mutableStateOf("") }
    val jumlahAnggota = remember { mutableStateOf("") }

    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val imageUrl = remember { mutableStateOf<String?>(null) }

    val jenisLomba = remember { mutableStateOf<JenisLomba?>(null) }
    var expandedJenis by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri.value = uri
        uri?.let {
            uploadImageToCloudinary(context, it) { url ->
                imageUrl.value = url
            }
        }
    }

    val submitState by viewModel.submitState.collectAsState()

    LaunchedEffect(submitState) {
        if (submitState is SubmitStatus.Success) {
            onDismiss()
        }
    }

    val calendar = Calendar.getInstance()

    val tanggalDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            tanggal.value = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val batasWaktuDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            batasWaktu.value = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val jenis = jenisLomba.value ?: return@Button
                    val url = imageUrl.value ?: return@Button
                    val jumlahTim = if (jenis == JenisLomba.TIM) {
                        val jumlah = jumlahAnggota.value.toIntOrNull()
                        if (jumlah == null || jumlah <= 0) {
                            // Bisa munculkan error atau return supaya tidak lanjut submit
                            Log.e("Validation", "Jumlah anggota tidak valid")
                            return@Button
                        }
                        jumlah
                    } else {
                        1
                    }


                    val request = RequestLomba(
                        id = id.value,
                        nama = nama.value,
                        tanggal = tanggal.value,
                        lokasi = lokasi.value,
                        url = url,
                        bataswaktu = batasWaktu.value,
                        deskripsi = deskripsi.value,
                        jenis_lomba = jenis.name,
                        jumlah_anggota = jumlahTim
                    )
                    Log.d("ANJAY", "RequestLomba: $request")
                    viewModel.submitLomba(request)
                },
                enabled = submitState !is SubmitStatus.Loading
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Batal")
            }
        },
        title = { Text(text = "Tambah Lomba") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (submitState is SubmitStatus.Error) {
                    Text("Error: ${(submitState as SubmitStatus.Error).message}", color = MaterialTheme.colorScheme.error)
                }

                if (submitState is SubmitStatus.Loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                OutlinedTextField(value = id.value, onValueChange = { id.value = it }, label = { Text("Lomba Id") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = nama.value, onValueChange = { nama.value = it }, label = { Text("Nama Lomba") }, modifier = Modifier.fillMaxWidth())

                OutlinedTextField(
                    value = tanggal.value,
                    onValueChange = {},
                    label = { Text("Tanggal") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Pilih Tanggal",
                            modifier = Modifier.clickable { tanggalDialog.show() }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { tanggalDialog.show() }
                )

                OutlinedTextField(value = lokasi.value, onValueChange = { lokasi.value = it }, label = { Text("Lokasi") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = deskripsi.value, onValueChange = { deskripsi.value = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())

                ExposedDropdownMenuBox(
                    expanded = expandedJenis,
                    onExpandedChange = { expandedJenis = !expandedJenis },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = jenisLomba.value?.name ?: "Pilih Jenis Lomba",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jenis Lomba") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJenis) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedJenis,
                        onDismissRequest = { expandedJenis = false }
                    ) {
                        JenisLomba.values().forEach { jenis ->
                            DropdownMenuItem(
                                text = { Text(jenis.name) },
                                onClick = {
                                    jenisLomba.value = jenis
                                    expandedJenis = false
                                }
                            )
                        }
                    }
                }

                if (jenisLomba.value == JenisLomba.TIM) {
                    OutlinedTextField(
                        value = jumlahAnggota.value,
                        onValueChange = { jumlahAnggota.value = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Jumlah Anggota") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = batasWaktu.value,
                    onValueChange = {},
                    label = { Text("Batas Waktu") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Pilih Batas Waktu",
                            modifier = Modifier.clickable { batasWaktuDialog.show() }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { batasWaktuDialog.show() }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Pilih Gambar")
                }

                imageUrl.value?.let {
                    Text("Gambar berhasil diupload: $it")
                }
            }
        }
    )
}

// Fungsi upload ke Cloudinary (tidak diubah)
fun uploadImageToCloudinary(context: android.content.Context, uri: Uri, onComplete: (String?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val cloudName = "dmqd8xz2y"
            val uploadPreset = "LombaTif"

            val file = FileUtils.getFileFromUri(context, uri)
            if (file == null) {
                onComplete(null)
                return@launch
            }

            val client = OkHttpClient()
            val mediaType = "image/*".toMediaTypeOrNull()
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name, file.asRequestBody(mediaType))
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val url = parseCloudinaryUrlFromResponse(json)
                onComplete(url)
            } else {
                Log.e("Upload", "Upload failed: ${response.message}")
                onComplete(null)
            }
        } catch (e: Exception) {
            Log.e("Upload", "Exception: ${e.message}")
            onComplete(null)
        }
    }
}

fun parseCloudinaryUrlFromResponse(json: String?): String? {
    if (json == null) return null
    val regex = """"secure_url":"(.*?)"""".toRegex()
    return regex.find(json)?.groups?.get(1)?.value
}
