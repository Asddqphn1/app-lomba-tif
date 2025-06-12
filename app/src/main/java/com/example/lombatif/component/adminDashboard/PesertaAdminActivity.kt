package com.example.lombatif.component.adminDashboard

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    var selectedKategori by remember { mutableStateOf("Semua") }
    var selectedLomba by remember { mutableStateOf("Semua Lomba") }
    var selectedPeserta by remember { mutableStateOf<PesertaAdmin?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val lombaList = listOf("Semua Lomba") + pesertaList
        .mapNotNull { it.pesertalomba.firstOrNull()?.lomba?.nama }
        .distinct()

    val kategoriOptions = listOf("Semua", "TIM", "INDIVIDU")

    val filteredPesertaList = pesertaList.filter { peserta ->
        val kategoriMatch = when (selectedKategori) {
            "TIM" -> peserta.pesertalomba.firstOrNull()?.lomba?.jenis_lomba.equals("tim", ignoreCase = true)
            "INDIVIDU" -> peserta.pesertalomba.firstOrNull()?.lomba?.jenis_lomba.equals("individu", ignoreCase = true)
            else -> true
        }

        val lombaMatch = if (selectedLomba == "Semua Lomba") true
        else peserta.pesertalomba.firstOrNull()?.lomba?.nama == selectedLomba

        kategoriMatch && lombaMatch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header judul
                Text(
                    text = "Daftar Peserta",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Layout filter Kategori dan Lomba sejajar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filter Kategori - kiri
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        kategoriOptions.forEach { kategori ->
                            val isSelected = selectedKategori == kategori
                            val backgroundColor = when (kategori) {
                                "Semua" -> Color(0xFF58D68D)
                                "TIM" -> Color(0xFFFF6600)
                                "INDIVIDU" -> Color(0xFF5DADE2)
                                else -> Color.LightGray
                            }
                            val textColor = if (isSelected) Color.White else Color.Black

                            Button(
                                onClick = { selectedKategori = kategori },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) backgroundColor else backgroundColor.copy(alpha = 0.5f),
                                    contentColor = textColor
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    text = kategori.uppercase(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Filter Lomba - kanan
                    Box(modifier = Modifier.weight(0.6f), contentAlignment = Alignment.CenterEnd) {
                        TextButton(
                            onClick = { expanded = true },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                selectedLomba,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Filter",
                                tint = Color.Black
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            lombaList.forEach { lomba ->
                                DropdownMenuItem(
                                    text = { Text(lomba) },
                                    onClick = {
                                        selectedLomba = lomba
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Daftar peserta
        when {
            isLoading -> {
                items(5) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        PesertaItemPlaceholder()
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            errorMessage != null -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            filteredPesertaList.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada peserta yang sesuai filter.")
                    }
                }
            }

            else -> {
                itemsIndexed(filteredPesertaList, key = { _, peserta -> peserta.id }) { index, peserta ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        PesertaItem(
                            index = index + 1,
                            peserta = peserta,
                            onLihatAnggotaClick = {
                                selectedPeserta = peserta
                                onLihatAnggota(peserta)
                            }
                        )
                    }
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
    val isTim = lomba?.jenis_lomba?.equals("Tim", ignoreCase = true) == true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Nomor dan nama peserta
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${index}.",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = peserta.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Kategori Lomba (badge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Kategori: ", style = MaterialTheme.typography.bodySmall)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isTim) Color(0xFFFF6600)
                            else Color(0xFFD6E7FF)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isTim) "TIM" else "INDIVIDU",
                        color = if (isTim) Color.White else Color.Black,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Nama Lomba
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Nama Lomba",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = lomba?.nama ?: "-",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Tanggal Daftar
            Text(
                text = "Tanggal Daftar: ${peserta.created_at.toReadableDate()}",
                style = MaterialTheme.typography.bodySmall
            )

            // Tombol Aksi
            if (isTim) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onLihatAnggotaClick(peserta) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Lihat Anggota", color = Color.White)
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
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
        val date = LocalDateTime.parse(this, inputFormatter)
        date.format(outputFormatter)
    } catch (e: Exception) {
        this
    }
}

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
