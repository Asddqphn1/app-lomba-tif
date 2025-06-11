
package com.example.lombatif.component.pesertaDashboard

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
import com.example.lombatif.response.SubmissionData
import com.example.lombatif.ui.theme.LombaTIFTheme
import com.example.lombatif.viewModels.ActionState
import com.example.lombatif.viewModels.SubmissionCheckState
import com.example.lombatif.viewModels.ViewSubmission


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
                        // Jika sudah ada submission, tampilkan detailnya
                        SubmittedView(
                            submissionData = state.submission,
                            viewModel = viewModel
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
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (url.isBlank()) {
                    Toast.makeText(context, "URL tidak boleh kosong", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.submitUrl(idPesertaLomba, url)
                }
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
            AlertDialog(onDismissRequest = { viewModel.resetActionStates() }, title = { Text("Sukses") }, text = { Text(state.message) }, confirmButton = { TextButton(onClick = { viewModel.resetActionStates() }) { Text("OK") }})
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
    viewModel: ViewSubmission
) {
    val deleteState by viewModel.deleteState.collectAsState()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    // State untuk menampilkan dialog konfirmasi hapus
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Menampilkan pop-up Toast saat delete berhasil/gagal
    LaunchedEffect(deleteState) {
        when (val state = deleteState) {
            is ActionState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetActionStates()
            }
            is ActionState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetActionStates()
            }
            else -> {}
        }
    }

    // Dialog konfirmasi sebelum menghapus
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

    // Tampilan utama untuk detail submission
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
            // Info Lomba
            InfoRow(icon = Icons.Default.Info, text = "Lomba: ${submissionData.pesertaLomba.lomba.nama}")
            // Info Waktu Submit
            InfoRow(icon = Icons.Default.DateRange, text = "Waktu Submit: ${formatTanggal(submissionData.submissionTime)}")
            // Info Link Submission (Bisa Diklik)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ThumbUp, contentDescription = "Link", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                // Menggunakan ClickableText agar link bisa ditekan
                ClickableText(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                        ) {
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
            // Tombol Hapus
            Button(
                onClick = { showDeleteConfirmDialog = true }, // Tampilkan dialog konfirmasi
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth(),
                enabled = deleteState !is ActionState.Loading // Disable tombol saat proses hapus
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