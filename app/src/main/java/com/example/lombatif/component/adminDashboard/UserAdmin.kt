package com.example.lombatif.component.adminDashboard

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lombatif.models.get.DaftarUsersAdmin
import com.example.lombatif.viewModels.ViewHapusUser
import com.example.lombatif.viewModels.ViewUserAdmin
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.io.Serializable

@Composable
fun UserAdmin(
    onHapusBerhasil: () -> Unit,
    onUpdate: (DaftarUsersAdmin) -> Unit,
    onDelete: (DaftarUsersAdmin) -> Unit,
    viewHapusUser: ViewHapusUser = viewModel(),
    viewUserAdmin: ViewUserAdmin = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val users by viewUserAdmin.user.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    // State untuk dialog hasil hapus
    var showResultDialog by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // State untuk menyimpan user yang ingin dihapus
    var userToDelete by remember { mutableStateOf<DaftarUsersAdmin?>(null) }

    val filteredUsers = users.filter {
        it.nama?.contains(searchQuery, ignoreCase = true) == true ||
                it.email?.contains(searchQuery, ignoreCase = true) == true
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val updatedUser = result.data?.getSerializableExtra("updatedUser") as? DaftarUsersAdmin
            updatedUser?.let {
                viewUserAdmin.updateUserInList(it)
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("Daftar User", fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center)
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

        val swipeState = rememberSwipeRefreshState(isRefreshing)
        SwipeRefresh(
            state = swipeState,
            onRefresh = {
                isRefreshing = true
                viewUserAdmin.fetchDaftarUser()
                isRefreshing = false
            }
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(40.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Username", fontWeight = FontWeight.Bold, modifier = Modifier.width(180.dp))
                        Text("Role", fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
                    }
                    Text("Aksi", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp), textAlign = TextAlign.End)
                }

                Divider()

                LazyColumn {
                    items(filteredUsers) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.width(180.dp)) {
                                    Text(user.nama ?: "Unknown", fontWeight = FontWeight.Medium)
                                    Text(user.email ?: "No email", style = MaterialTheme.typography.bodySmall)
                                }
                                Text(
                                    user.role ?: "Unknown",
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .width(80.dp)
                                        .align(Alignment.CenterVertically)
                                )
                            }

                            Row(
                                modifier = Modifier.width(100.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    val intent = Intent(context, UpdateJuri::class.java)
                                    intent.putExtra("user", user as Serializable)
                                    launcher.launch(intent)
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = {
                                    // Simpan user yang mau dihapus dan tampilkan dialog konfirmasi
                                    userToDelete = user
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

    // Dialog konfirmasi hapus
    if (showDialog && userToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                userToDelete = null
            },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah kamu yakin ingin menghapus user ${userToDelete?.nama}? Tindakan ini tidak bisa dibatalkan.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    userToDelete?.id?.let { id ->
                        viewHapusUser.hapusUserById(
                            id = id,
                            onSuccess = {
                                resultMessage = "User berhasil dihapus."
                                isError = false
                                showResultDialog = true
                                onHapusBerhasil()
                                userToDelete = null
                            },
                            onError = { err ->
                                resultMessage = err
                                isError = true
                                showResultDialog = true
                                userToDelete = null
                                Log.e("Hapus User", "Gagal hapus: $err")
                            }
                        )
                    }
                }) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    userToDelete = null
                }) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog hasil hapus sukses/gagal
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




