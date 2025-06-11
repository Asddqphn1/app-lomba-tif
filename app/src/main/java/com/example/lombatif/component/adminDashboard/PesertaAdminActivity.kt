package com.example.lombatif.component.adminDashboard

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.lombatif.models.get.AnggotaData
import com.example.lombatif.models.get.PesertaAdmin
import com.example.lombatif.ui.theme.LombaTIFTheme
import com.example.lombatif.viewModels.ViewAnggotaTim
import com.example.lombatif.viewModels.ViewPesertaAdmin
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class PesertaAdminActivity : ComponentActivity() {
    private val viewModel: ViewPesertaAdmin by viewModels()
    private val anggotaViewModel: ViewAnggotaTim by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.loadPeserta()

        setContent {
            LombaTIFTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PesertaListScreen(
                        pesertaList = viewModel.pesertaList,
                        isLoading = viewModel.isLoading,
                        errorMessage = viewModel.errorMessage,
                        modifier = Modifier.padding(innerPadding),
                        onLihatAnggota = { peserta ->
                            anggotaViewModel.loadAnggotaTim(peserta.id)
                        },
                        anggotaViewModel = anggotaViewModel
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PesertaListScreen(
    pesertaList: List<PesertaAdmin>,
    isLoading: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier,
    onLihatAnggota: (PesertaAdmin) -> Unit,
    anggotaViewModel: ViewAnggotaTim
) {
    var selectedPeserta by remember { mutableStateOf<PesertaAdmin?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Daftar Peserta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        when {
            isLoading -> {
                items(5) {
                    PesertaItemPlaceholder()
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            errorMessage != null -> {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            pesertaList.isEmpty() -> {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada peserta yang mendaftar.")
                    }
                }
            }
            else -> {
                itemsIndexed(pesertaList, key = { _, peserta -> peserta.id }) { index, peserta ->
                    PesertaItem(
                        index = index + 1,
                        peserta = peserta,
                        onLihatAnggotaClick = {
                            selectedPeserta = peserta
                            onLihatAnggota(peserta)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    selectedPeserta?.let { peserta ->
        AnggotaTimDialog(
            namaPeserta = peserta.nama,
            anggotaTim = anggotaViewModel.anggota,
            isLoading = anggotaViewModel.isLoading,
            onDismiss = {
                selectedPeserta = null
                anggotaViewModel.resetState()
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PesertaItem(
    index: Int,
    peserta: PesertaAdmin,
    onLihatAnggotaClick: (PesertaAdmin) -> Unit
) {
    val lomba = peserta.pesertalomba.firstOrNull()?.lomba

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = peserta.nama,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "No. Pendaftaran: $index",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (lomba != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BadgeKategori(kategori = lomba.jenis_lomba)
                    Text(lomba.nama, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Tanggal Pendaftaran",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Daftar pada: ${peserta.created_at.toReadableDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (lomba?.jenis_lomba?.equals("Tim", ignoreCase = true) == true) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onLihatAnggotaClick(peserta) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Lihat Anggota", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
@Composable
fun AnggotaTimDialog(
    namaPeserta: String,
    anggotaTim: List<AnggotaData>,
    isLoading: Boolean,
    error: String? = null,
    onDismiss: () -> Unit
) {
    // Tutup dialog otomatis jika loading selesai tapi tidak ada anggota
    LaunchedEffect(isLoading, anggotaTim) {
        if (!isLoading && anggotaTim.isEmpty() && error == null) {
            onDismiss()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Anggota Tim", style = MaterialTheme.typography.labelLarge)
                Text(namaPeserta, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator()
                        }
                        error != null -> {
                            Text("Gagal memuat: $error", color = MaterialTheme.colorScheme.error)
                        }
                        anggotaTim.isNotEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                anggotaTim.forEach { anggota ->
                                    Text("• ${anggota.nama}", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }

                if (!isLoading && anggotaTim.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun String.toReadableDate(): String {
    return try {
        // ISO_DATE_TIME cocok untuk format seperti "2023-10-27T10:00:00.000000Z"
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
        val date = LocalDateTime.parse(this, inputFormatter)
        date.format(outputFormatter)
    } catch (e: Exception) {
        this // Kembalikan string asli jika gagal parse
    }
}

// Tambahkan Composable ini di file PesertaAdminActivity.kt
@Composable
fun PesertaItemPlaceholder() {
    val shimmerBrush = ShimmerBrush()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Placeholder untuk No
            ShimmerPlaceholder(modifier = Modifier.height(16.dp).fillMaxWidth(0.2f).background(shimmerBrush))
            Spacer(modifier = Modifier.height(8.dp))

            // Placeholder untuk Nama Peserta
            ShimmerPlaceholder(modifier = Modifier.height(20.dp).fillMaxWidth(0.8f).background(shimmerBrush))
            Spacer(modifier = Modifier.height(8.dp))

            // Placeholder untuk Info Lomba
            ShimmerPlaceholder(modifier = Modifier.height(18.dp).fillMaxWidth(0.5f).background(shimmerBrush))
            Spacer(modifier = Modifier.height(8.dp))

            // Placeholder untuk Tanggal
            ShimmerPlaceholder(modifier = Modifier.height(14.dp).fillMaxWidth(0.4f).background(shimmerBrush))
        }
    }
}
