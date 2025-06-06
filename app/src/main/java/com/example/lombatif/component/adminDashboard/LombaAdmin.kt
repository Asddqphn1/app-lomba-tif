package com.example.lombatif.component.adminDashboard

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lombatif.models.get.DaftarLomba
import com.example.lombatif.models.request.RequestLomba
import com.example.lombatif.viewModels.ViewHapusLomba
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LombaAdmin(
    lomba: List<DaftarLomba>,
    onHapusLomba: (String) -> Unit,
    onTambahLombaBaru: (RequestLomba) -> Unit,
    isRefreshing: Boolean,
    onRefreshLomba: () -> Unit,
) {
    val showForm = remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Daftar Lomba",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.SansSerif
            )
            Button(
                onClick = { showForm.value = true },
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Lomba"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Tambah Lomba")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card {
            SwipeRefresh(
                state = SwipeRefreshState(isRefreshing),
                onRefresh = onRefreshLomba,
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lomba) { item ->
                        LombaCard(item = item, onHapusBerhasil = {
                            item.id?.let {
                                Log.d("LombaAdmin", "Hapus lomba dengan id: $it")
                                onHapusLomba(it)
                            }
                        })
                    }
                }
            }
        }
    }

    if (showForm.value) {
        TambahLomba(
            onDismiss = { showForm.value = false }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LombaCard(
    item: DaftarLomba,
    onHapusBerhasil: () -> Unit,
    viewModel: ViewHapusLomba = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }

    // State untuk dialog hasil hapus
    var showResultDialog by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            item.url?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Gambar Lomba",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(bottom = 12.dp)
                )
            }

            Text(
                text = item.nama ?: "Tanpa Nama",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(label = "Jenis", value = item.jenisLomba ?: "-")
            InfoRow(label = "Tanggal", value = item.tanggal?.let { formatDateString(it) } ?: "-")
            InfoRow(label = "Lokasi", value = item.lokasi ?: "-")
            InfoRow(label = "Batas Waktu", value = item.batasWaktu?.let { formatDateString(it) } ?: "-")
            InfoRow(label = "Deskripsi", value = item.deskripsi ?: "-", multiline = true)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = Color.Red
                    )
                }
            }
        }
    }

    // Dialog konfirmasi hapus
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah kamu yakin ingin menghapus lomba ${item.nama}? Tindakan ini tidak bisa dibatalkan.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    item.id?.let { id ->
                        viewModel.hapusLombaById(
                            id = id,
                            onSuccess = {
                                Log.d("LombaCard", "Berhasil hapus lomba dengan id: $id")
                                resultMessage = "Lomba berhasil dihapus."
                                isError = false
                                showResultDialog = true
                                onHapusBerhasil()
                            },
                            onError = { err ->
                                Log.e("LombaCard", "Gagal hapus: $err")
                                resultMessage = err
                                isError = true
                                showResultDialog = true
                            }
                        )
                    }
                }) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog hasil hapus sukses/gagal dengan styling
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isError) Icons.Filled.Clear else Icons.Filled.CheckCircle,
                        contentDescription = if (isError) "Error" else "Sukses",
                        tint = if (isError) Color.Red else Color(0xFF4CAF50),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isError) "Error" else "Sukses",
                        color = if (isError) Color.Red else Color(0xFF4CAF50),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = resultMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isError) Color.Red.copy(alpha = 0.8f) else Color(0xFF4CAF50).copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showResultDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isError) Color.Red else Color(0xFF4CAF50)
                    )
                ) {
                    Text("OK", fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
        )
    }
}

@Composable
fun InfoRow(label: String, value: String, multiline: Boolean = false) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.width(100.dp)
        )
        if (multiline) {
            Text(
                text = value,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
                color = Color.DarkGray
            )
        } else {
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateString(dateString: String): String {
    return try {
        val parsedDate = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
        parsedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale("id", "ID")))
    } catch (e: Exception) {
        dateString
    }
}
