package com.example.lombatif.component.adminDashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lombatif.models.get.DaftarUsersAdmin
import com.example.lombatif.models.request.RequestUpdateJuri
import com.example.lombatif.viewModels.ViewUpdateJuri

class UpdateJuri : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val user = intent.getSerializableExtra("user") as? DaftarUsersAdmin
        Log.d("UpdateJuri", "userRaw: ${user?.id}")

        setContent {
            val viewModel: ViewUpdateJuri = viewModel() // <--- Tambahkan ini

            MaterialTheme {
                if (user != null) {
                    UpdateToJudgeForm(
                        user = user,
                        onUpdate = { fullName, competitionId ->
                            Log.d("UpdateJuri", "Tombol Update diklik")
                            viewModel.updateToJuri(
                                userId = user.id ?: "",
                                request = RequestUpdateJuri(
                                    namaJuri = fullName,
                                    id_lomba = competitionId
                                ),
                                onSuccess = {
                                    val resultIntent = Intent().apply {
                                        putExtra("updatedUser", user) // pastikan ini object DaftarUsersAdmin yang sudah di-update
                                    }
                                    setResult(RESULT_OK, resultIntent)
                                    finish()
                                }

                            )
                        },
                        onCancel = {
                            finish()
                        }
                    )
                } else {
                    Text("User tidak ditemukan.")
                }
            }
        }



    }
}

@Composable
fun UpdateToJudgeForm(
    user: DaftarUsersAdmin,
    onUpdate: (String, String) -> Unit, // Parameters: fullName, competitionId
    onCancel: () -> Unit
) {
    var fullName by remember { mutableStateOf(user.nama ?: "") }
    var competitionId by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Judul utama
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Update User Role",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Promote user to Judge",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }

            // Form dalam Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    // Competition ID
                    OutlinedTextField(
                        value = competitionId,
                        onValueChange = { competitionId = it },
                        label = { Text("Competition ID") },
                        placeholder = { Text("e.g. comp123") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Error message
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Tombol aksi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (competitionId.isBlank()) {
                                    errorMessage = "Competition ID is required"
                                } else {
                                    onUpdate(fullName, competitionId)
                                }
                            }
                        ) {
                            Text("Update")
                        }
                    }
                }
            }
        }
    }
}


