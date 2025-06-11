package com.example.lombatif.component.adminDashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lombatif.models.get.JuriAdmin
import com.example.lombatif.viewModels.ViewHapusJuri
import com.example.lombatif.viewModels.ViewJuriAdmin
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun JuriAdminPage(
    onUpdate: (JuriAdmin) -> Unit,
    onDelete: (JuriAdmin) -> Unit,
    viewHapusJuri: ViewHapusJuri = viewModel(),
    viewJuriAdmin: ViewJuriAdmin = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var juriToDelete by remember { mutableStateOf<JuriAdmin?>(null) }

    var showResultDialog by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    var isRefreshing by remember { mutableStateOf(false) }
    // State baru untuk loading awal
    var isLoading by remember { mutableStateOf(true) }

    // Ambil daftar juri dari view model
    val juriList by viewJuriAdmin.juri.collectAsState()

    // Efek untuk fetch data saat pertama kali komposabel ditampilkan
    LaunchedEffect(Unit) {
        isLoading = true
        viewJuriAdmin.fetchJuriAdmin {
            isLoading = false
        }
    }

    val filteredJuri = juriList.filter {
        it.nama?.contains(searchQuery, ignoreCase = true) == true ||
                it.users?.email?.contains(searchQuery, ignoreCase = true) == true
    }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Daftar Juri",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Cari pengguna...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        // Tampilkan loading indicator atau daftar juri
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    isRefreshing = true
                    viewJuriAdmin.fetchJuriAdmin {
                        isRefreshing = false
                    }
                }
            ) {
                LazyColumn {
                    items(filteredJuri) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(user.nama ?: "Unknown", fontWeight = FontWeight.Medium)
                                    Text(user.users?.email ?: "No email", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Row {
                                IconButton(onClick = { onUpdate(user) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = {
                                    juriToDelete = user
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                        Divider()
                    }
                }
            }
        }
    }


    // Dialog konfirmasi dan hasil hapus tetap sama
    if (showDialog && juriToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah kamu yakin ingin menghapus ${juriToDelete?.nama} sebagai juri? Tindakan ini tidak bisa dibatalkan.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val id = juriToDelete!!.id
                    if (id != null) {
                        // Tampilkan loading di sini jika diperlukan
                        viewHapusJuri.hapusJuriById(
                            id = id,
                            onSuccess = {
                                resultMessage = "Juri berhasil dihapus."
                                isError = false
                                showResultDialog = true
                                onDelete(juriToDelete!!)
                                viewJuriAdmin.fetchJuriAdmin {} // Refresh list
                            },
                            onError = { err ->
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
            }
        )
    }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isError) Icons.Default.Delete else Icons.Default.CheckCircle,
                        contentDescription = if (isError) "Error" else "Sukses",
                        tint = if (isError) Color.Red else Color(0xFF4CAF50),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isError) "Error" else "Sukses",
                        color = if (isError) Color.Red else Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = resultMessage,
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
                    Text("OK")
                }
            }
        )
    }
}