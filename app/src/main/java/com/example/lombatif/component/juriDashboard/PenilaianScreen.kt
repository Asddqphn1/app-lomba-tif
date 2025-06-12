package com.example.lombatif.component.juriDashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lombatif.response.DetailSubmissionData
import com.example.lombatif.utils.formatTanggal
import com.example.lombatif.viewModels.JuriModels.PenilaianViewModel
import com.example.lombatif.viewModels.ViewProfile // PERBAIKAN: Import ViewProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenilaianScreen(
    submissionId: String,
    onNavigateBack: () -> Unit,
    viewModel: PenilaianViewModel = viewModel(),
    // PERBAIKAN: Tambahkan ProfileViewModel untuk mendapatkan ID Juri
    profileViewModel: ViewProfile = viewModel()
) {
    val context = LocalContext.current

    // PERBAIKAN: Mengambil state dari ProfileViewModel untuk mendapatkan ID Juri
    val profileState by profileViewModel.profile.collectAsState()
    val juriId = profileState?.profile?.id ?: ""

    val submissionDetail by viewModel.submissionDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var nilaiState by remember { mutableStateOf("") }
    var catatanState by remember { mutableStateOf("") }

    // Memastikan profile diambil saat screen dibuka
    LaunchedEffect(Unit) {
        if (juriId.isEmpty()) {
            profileViewModel.fetchProfile()
        }
    }

    LaunchedEffect(key1 = submissionId) {
        viewModel.getDetailSubmission(submissionId)
    }

    LaunchedEffect(error) {
        error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "Penilaian berhasil disimpan!", Toast.LENGTH_SHORT).show()
            viewModel.resetSaveStatus()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Penilaian Submission") },
                navigationIcon = {
                    IconButton(onClick = { if (!isSaving) onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (submissionDetail != null) {
                ContentPenilaian(
                    data = submissionDetail!!,
                    nilai = nilaiState,
                    onNilaiChange = { nilaiState = it },
                    catatan = catatanState,
                    onCatatanChange = { catatanState = it },
                    isSaving = isSaving,
                    onSimpan = {
                        val nilaiInt = nilaiState.toIntOrNull()
                        if (nilaiInt == null || nilaiInt !in 1..100) {
                            Toast.makeText(context, "Nilai harus angka antara 1-100", Toast.LENGTH_SHORT).show()
                        } else {
                            // PERBAIKAN: Mengirim juriId yang sudah didapatkan
                            viewModel.simpanPenilaian(submissionId, juriId, nilaiInt, catatanState)
                        }
                    },
                    onReset = {
                        nilaiState = ""
                        catatanState = ""
                    }
                )
            } else if(error != null) {
                Text(text = "Gagal memuat data. Coba lagi.", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun ContentPenilaian(
    data: DetailSubmissionData,
    nilai: String,
    onNilaiChange: (String) -> Unit,
    catatan: String,
    onCatatanChange: (String) -> Unit,
    isSaving: Boolean,
    onSimpan: () -> Unit,
    onReset: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Detail Submission", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Row {
                    Column(Modifier.weight(1f)) {
                        DetailItem("Nama Peserta", data.pesertaLomba?.peserta?.nama ?: "Tidak ada data")
                        Spacer(Modifier.height(12.dp))
                        // PERBAIKAN: Menggunakan .jenisLomba dari data class yang benar
                        DetailItem("Kategori Lomba", data.pesertaLomba?.lomba?.jenisLomba ?: "Tidak ada data")
                    }
                    Column(Modifier.weight(1f)) {
                        DetailItem("Lomba", data.pesertaLomba?.lomba?.nama ?: "Tidak ada data")
                        Spacer(Modifier.height(12.dp))
                        // PERBAIKAN: Menggunakan .submissionTime
                        DetailItem("Tanggal Submit", formatTanggal(data.submissionTime ?: "", "dd MMMM<y_bin_46>, HH:mm 'WIB'"))
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    // PERBAIKAN: Menggunakan .fileUrl dari data class yang benar
                    text = data.fileUrl ?: "Tidak ada file",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    onClick = {
                        data.fileUrl?.let { url ->
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Tidak dapat membuka link", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !data.fileUrl.isNullOrBlank()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Buka File")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Form Penilaian", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = nilai,
                    onValueChange = { if (it.all(Char::isDigit) && it.length <= 3) onNilaiChange(it) },
                    label = { Text("Nilai *") },
                    placeholder = { Text("Masukkan nilai antara 1-100") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isSaving
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = catatan,
                    onValueChange = onCatatanChange,
                    label = { Text("Catatan / Komentar (Opsional)") },
                    placeholder = { Text("Berikan catatan atau komentar untuk peserta...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    enabled = !isSaving
                )
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onReset, enabled = !isSaving) { Text("Reset") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onSimpan, enabled = !isSaving) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Simpan Penilaian")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}
