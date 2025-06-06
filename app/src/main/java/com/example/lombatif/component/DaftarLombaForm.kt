package com.example.lombatif.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lombatif.viewModels.DaftarLombaUiState
import com.example.lombatif.viewModels.ViewDaftarLombaPeserta

@Composable
fun DaftarLombaForm(
    idUser: String,
    idLomba: String,
    jumlahTim: Int?,  // nullable
    viewModel: ViewDaftarLombaPeserta = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val totalTim = jumlahTim ?: 1

    // State untuk nama tim (kalau tim)
    var namaTim by remember { mutableStateOf("") }

    // State untuk nama anggota (kalau tim)
    var anggotaNames = remember { mutableStateListOf<String>() }

    val scrollState = rememberScrollState()

    LaunchedEffect(totalTim) {
        anggotaNames.clear()
        repeat(if (totalTim > 1) totalTim else 1) {
            anggotaNames.add("")
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        Text("Form Daftar Lomba", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(12.dp))

        if (totalTim > 1) {
            // Input Nama Tim
            OutlinedTextField(
                value = namaTim,
                onValueChange = { namaTim = it },
                label = { Text("Nama Tim") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input Nama Anggota
            for (index in 0 until totalTim) {
                OutlinedTextField(
                    value = anggotaNames.getOrNull(index) ?: "",
                    onValueChange = {
                        anggotaNames[index] = it
                    },
                    label = { Text("Nama Anggota ${index + 1}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    singleLine = true
                )
            }
        } else {
            // Input Nama Peserta Individu
            OutlinedTextField(
                value = anggotaNames.getOrNull(0) ?: "",
                onValueChange = {
                    if (anggotaNames.isEmpty()) {
                        anggotaNames.add(it)
                    } else {
                        anggotaNames[0] = it
                    }
                },
                label = { Text("Nama Peserta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val filteredNames = anggotaNames.filter { it.isNotBlank() }

                if (totalTim > 1) {
                    if (namaTim.isBlank()) {
                        // Misal kasih toast atau error message
                        // Tapi di sini cukup return biar gak lanjut
                        return@Button
                    }
                    viewModel.daftarLomba(idUser, idLomba, namaTim, filteredNames)
                } else {
                    if (filteredNames.isEmpty()) {
                        // Error: Nama peserta harus diisi
                        return@Button
                    }
                    // Kirim namaTim sebagai nama peserta individu
                    viewModel.daftarLomba(idUser, idLomba, filteredNames.firstOrNull() ?: "")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Daftar Sekarang")
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (uiState) {
            is DaftarLombaUiState.Loading -> {
                CircularProgressIndicator()
            }
            is DaftarLombaUiState.Success -> {
                Text(
                    text = (uiState as DaftarLombaUiState.Success).message,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is DaftarLombaUiState.Error -> {
                Text(
                    text = (uiState as DaftarLombaUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }
    }
}

