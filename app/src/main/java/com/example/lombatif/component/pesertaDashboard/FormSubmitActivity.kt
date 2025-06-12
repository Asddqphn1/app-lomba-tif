
package com.example.lombatif.component.pesertaDashboard

import android.util.Patterns
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.lombatif.response.responsePeserta.SubmissionData
import com.example.lombatif.ui.theme.LombaTIFTheme
import com.example.lombatif.viewModels.PesertaModels.ActionState
import com.example.lombatif.viewModels.PesertaModels.SubmissionCheckState
import com.example.lombatif.viewModels.PesertaModels.ViewSubmission


class FormSubmitActivity : ComponentActivity() {
    // Daftarkan ViewModel
    private val viewModel: ViewSubmission by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val idPesertaLomba = intent.getStringExtra("ID_PESERTA_LOMBA")
        val namaLomba = intent.getStringExtra("NAMA_LOMBA") ?: "Submission"

        if (idPesertaLomba == null) {
            finish()
            return
        }

        setContent {
            LombaTIFTheme {
                FormSubmitScreen(
                    viewModel = viewModel,
                    idPesertaLomba = idPesertaLomba,
                    namaLomba = namaLomba
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormSubmitScreen(
    viewModel: ViewSubmission,
    idPesertaLomba: String,
    namaLomba: String
) {
    val checkState by viewModel.submissionCheckState.collectAsState()

    // Cek status submission saat layar pertama kali dibuka
    LaunchedEffect(key1 = idPesertaLomba) {
        viewModel.checkSubmissionStatus(idPesertaLomba)
    }

    Scaffold(topBar = { TopAppBar(title = { Text(namaLomba) }) }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val state = checkState) {
                is SubmissionCheckState.Loading -> CircularProgressIndicator()
                is SubmissionCheckState.Error -> Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                is SubmissionCheckState.Success -> {
                    if (state.submission != null) {
                        SubmittedView(
                            submissionData = state.submission,
                            viewModel = viewModel,
                            // Teruskan idPesertaLomba ke SubmittedView
                            idPesertaLomba = idPesertaLomba
                        )
                    } else {
                        // Jika belum, tampilkan form submit URL
                        SubmitUrlForm(
                            idPesertaLomba = idPesertaLomba,
                            viewModel = viewModel
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

// Composable untuk menampilkan form submit URL
@Composable
fun SubmitUrlForm(idPesertaLomba: String, viewModel: ViewSubmission) {
    var url by remember { mutableStateOf("") }
    val submitState by viewModel.submitState.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Masukkan Link URL Submission") },
            modifier = Modifier.fillMaxWidth(),
            // Tambahkan isError untuk memberikan feedback visual jika URL tidak valid
            isError = url.isNotBlank() && !Patterns.WEB_URL.matcher(url).matches()
        )
        // Tambahkan helper text untuk memberitahu user jika formatnya salah
        if (url.isNotBlank() && !Patterns.WEB_URL.matcher(url).matches()) {
            Text(
                text = "Format URL tidak valid.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // --- AWAL PERUBAHAN VALIDASI ---
                if (url.isBlank()) {
                    Toast.makeText(context, "URL tidak boleh kosong", Toast.LENGTH_SHORT).show()
                } else if (!Patterns.WEB_URL.matcher(url).matches()) {
                    // Cek apakah format URL valid menggunakan Patterns
                    Toast.makeText(context, "Format URL tidak valid", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.submitUrl(idPesertaLomba, url)
                }
                // --- AKHIR PERUBAHAN VALIDASI ---
            },
            enabled = submitState !is ActionState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (submitState is ActionState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Submit")
            }
        }
    }

    // Tampilkan dialog saat submit sukses/gagal
    when (val state = submitState) {
        is ActionState.Success -> {
            // Setelah submit berhasil, kita harus refresh statusnya
            AlertDialog(
                onDismissRequest = {
                    viewModel.resetActionStates()
                    // Refresh status untuk menampilkan SubmittedView
                    viewModel.checkSubmissionStatus(idPesertaLomba)
                },
                title = { Text("Sukses") },
                text = { Text(state.message) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetActionStates()
                        // Refresh status untuk menampilkan SubmittedView
                        viewModel.checkSubmissionStatus(idPesertaLomba)
                    }) {
                        Text("OK")
                    }
                }
            )
        }
        is ActionState.Error -> {
            AlertDialog(onDismissRequest = { viewModel.resetActionStates() }, title = { Text("Gagal") }, text = { Text(state.message) }, confirmButton = { TextButton(onClick = { viewModel.resetActionStates() }) { Text("Tutup") }})
        }
        else -> {}
    }
}
@Composable
fun SubmittedView(
    submissionData: SubmissionData,
    viewModel: ViewSubmission,
    // Tambahkan parameter idPesertaLomba untuk bisa refresh state
    idPesertaLomba: String
) {
    val deleteState by viewModel.deleteState.collectAsState()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    // State untuk menampilkan dialog konfirmasi hapus
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    // State untuk menampilkan dialog sukses hapus
    var showDeleteSuccessDialog by remember { mutableStateOf<String?>(null) }
    // State untuk menampilkan dialog error hapus
    var showDeleteErrorDialog by remember { mutableStateOf<String?>(null) }


    // --- AWAL PERUBAHAN: Mengganti Toast dengan Dialog ---
    // Menggunakan LaunchedEffect untuk merespon perubahan deleteState
    LaunchedEffect(deleteState) {
        when (val state = deleteState) {
            is ActionState.Success -> {
                // Tampilkan pesan sukses di dialog
                showDeleteSuccessDialog = state.message
            }
            is ActionState.Error -> {
                // Tampilkan pesan error di dialog
                showDeleteErrorDialog = state.message
            }
            else -> {}
        }
    }

    // Dialog untuk Sukses Hapus
    if (showDeleteSuccessDialog != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteSuccessDialog = null
                viewModel.resetActionStates()
                // REFRESH SCREEN STATE! Ini bagian paling penting.
                viewModel.checkSubmissionStatus(idPesertaLomba)
            },
            title = { Text("Sukses") },
            text = { Text(showDeleteSuccessDialog!!) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteSuccessDialog = null
                    viewModel.resetActionStates()
                    // REFRESH SCREEN STATE!
                    viewModel.checkSubmissionStatus(idPesertaLomba)
                }) {
                    Text("OK")
                }
            }
        )
    }

    // Dialog untuk Gagal Hapus
    if (showDeleteErrorDialog != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteErrorDialog = null
                viewModel.resetActionStates()
            },
            title = { Text("Gagal") },
            text = { Text(showDeleteErrorDialog!!) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteErrorDialog = null
                    viewModel.resetActionStates()
                }) {
                    Text("Tutup")
                }
            }
        )
    }
    // --- AKHIR PERUBAHAN ---

    // Dialog konfirmasi sebelum menghapus (kode ini sudah benar)
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus submission ini? Aksi ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        // Panggil fungsi delete dari ViewModel
                        viewModel.deleteSubmission(submissionData.id, submissionData.pesertaLomba.lomba.id)
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Tampilan utama untuk detail submission (tidak ada perubahan di sini)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Detail Submission Anda", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Divider()
            InfoRow(icon = Icons.Default.Info, text = "Lomba: ${submissionData.pesertaLomba.lomba.nama}")
            InfoRow(icon = Icons.Default.DateRange, text = "Waktu Submit: ${formatTanggal(submissionData.submissionTime)}")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ThumbUp, contentDescription = "Link", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                ClickableText(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )) {
                            append(submissionData.fileUrl)
                        }
                    },
                    onClick = {
                        try {
                            uriHandler.openUri(submissionData.fileUrl)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Tidak dapat membuka link", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Divider()
            Button(
                onClick = { showDeleteConfirmDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth(),
                enabled = deleteState !is ActionState.Loading
            ) {
                if (deleteState is ActionState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus Submission")
                }
            }
        }
    }
}