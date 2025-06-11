package com.example.lombatif.component.adminDashboard

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lombatif.models.get.DaftarUsersAdmin
import com.example.lombatif.viewModels.ViewHapusUser
import com.example.lombatif.viewModels.ViewUserAdmin
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.io.Serializable

@OptIn(ExperimentalMaterial3Api::class)
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

    // State untuk dialog
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var isErrorResult by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<DaftarUsersAdmin?>(null) }

    val filteredUsers = remember(users, searchQuery) {
        users
            .filter {
                it.role?.equals("USERS", ignoreCase = true) == true
            }
            .filter {
                it.nama?.contains(searchQuery, ignoreCase = true) == true ||
                        it.email?.contains(searchQuery, ignoreCase = true) == true
            }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val updatedUser = result.data?.getSerializableExtra("updatedUser") as? DaftarUsersAdmin
            updatedUser?.let {
                viewUserAdmin.updateUserInList(it)
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari pengguna...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // List Pengguna
            val swipeState = rememberSwipeRefreshState(isRefreshing)
            SwipeRefresh(
                state = swipeState,
                onRefresh = {
                    isRefreshing = true
                    viewUserAdmin.fetchDaftarUser()
                    isRefreshing = false // Idealnya, ini di-set false setelah data selesai di-load
                }
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredUsers, key = { it.id!! }) { user ->
                        UserItemCard(
                            user = user,
                            onEditClick = {
                                val intent = Intent(context, UpdateJuri::class.java)
                                intent.putExtra("user", user as Serializable)
                                launcher.launch(intent)
                            },
                            onDeleteClick = {
                                userToDelete = user
                                showConfirmationDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog Konfirmasi Hapus
    if (showConfirmationDialog && userToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showConfirmationDialog = false
                userToDelete = null
            },
            title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus pengguna '${userToDelete?.nama}'? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        userToDelete?.id?.let { id ->
                            viewHapusUser.hapusUserById(
                                id = id,
                                onSuccess = {
                                    resultMessage = "Pengguna berhasil dihapus."
                                    isErrorResult = false
                                    showResultDialog = true
                                    onHapusBerhasil() // refresh list
                                },
                                onError = { err ->
                                    resultMessage = err
                                    isErrorResult = true
                                    showResultDialog = true
                                    Log.e("Hapus User", "Gagal hapus: $err")
                                }
                            )
                        }
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showConfirmationDialog = false
                    userToDelete = null
                }) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog Hasil (Sukses/Gagal)
    if (showResultDialog) {
        ResultDialog(
            isError = isErrorResult,
            message = resultMessage,
            onDismiss = { showResultDialog = false }
        )
    }
}

@Composable
private fun UserItemCard(
    user: DaftarUsersAdmin,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        ListItem(
            headlineContent = {
                Text(user.nama ?: "Unknown", fontWeight = FontWeight.Bold)
            },
            supportingContent = {
                Column {
                    Text(
                        text = user.email ?: "No email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    RoleBadge(role = user.role ?: "Unknown")
                }
            },
            leadingContent = {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "User Avatar",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingContent = {
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
private fun RoleBadge(role: String) {
    val backgroundColor = when (role.lowercase()) {
        "admin" -> MaterialTheme.colorScheme.tertiaryContainer
        "juri" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (role.lowercase()) {
        "admin" -> MaterialTheme.colorScheme.onTertiaryContainer
        "juri" -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = role,
            color = contentColor,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ResultDialog(
    isError: Boolean,
    message: String,
    onDismiss: () -> Unit
) {
    val icon = if (isError) Icons.Filled.Clear else Icons.Filled.CheckCircle
    val title = if (isError) "Gagal" else "Berhasil"
    val containerColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val contentColor = if (isError) Color.Red else Color(0xFF388E3C)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = containerColor,
        shape = RoundedCornerShape(16.dp),
        icon = { Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(48.dp)) },
        title = {
            Text(title, fontWeight = FontWeight.Bold, color = contentColor)
        },
        text = {
            Text(message, style = MaterialTheme.typography.bodyMedium, color = contentColor.copy(alpha = 0.8f))
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = contentColor)
            ) {
                Text("OK")
            }
        }
    )
}