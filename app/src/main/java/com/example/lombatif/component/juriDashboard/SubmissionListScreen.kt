// file: component/juriDashboard/SubmissionListScreen.kt

package com.example.lombatif.component.juriDashboard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lombatif.response.responseJuri.SubmissionData
import com.example.lombatif.utils.formatTanggal
import com.example.lombatif.viewModels.JuriModels.ViewSubmission


@Composable
fun SubmissionListScreen(viewModel: ViewSubmission = viewModel()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Halaman Daftar Submission")
    }
    val submissions by viewModel.submissions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Latar belakang dengan gradasi halus untuk kesan modern
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceContainerLowest,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // --- Header Kustom ---
        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)) {
            Text(
                text = "Submisi Penilaian",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            val perluDinilai = submissions.count { it.penilaian.isEmpty() }
            Text(
                text = "Anda memiliki $perluDinilai tugas baru.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // --- Daftar Kartu Submission ---
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(
                        "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                submissions.isEmpty() && !isLoading -> {
                    Text(
                        "Tidak ada submission untuk dinilai saat ini.",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.animateContentSize()
                    ) {
                        items(submissions, key = { it.id }) { submission ->
                            SubmissionItemCard(submission)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionItemCard(submission: SubmissionData) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Halaman Daftar Submission")
    }
    val isBelumDinilai = submission.penilaian.isEmpty()
    // Gradasi warna untuk kartu yang belum dinilai
    val cardBrush = if (isBelumDinilai) {
        Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                MaterialTheme.colorScheme.surface
            )
        )
    } else {
        Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = { /* TODO: Navigasi ke halaman penilaian */ },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.background(cardBrush)) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // --- Baris Atas: Nama & Status ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = submission.pesertaLomba.peserta.nama,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    StatusChip(status = if (isBelumDinilai) "Perlu Dinilai" else "Selesai")
                }

                // --- Info Kategori & Tanggal dengan Ikon ---
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip(
                        icon = Icons.Default.List,
                        text = submission.pesertaLomba.lomba.nama.trim(),
                        color = MaterialTheme.colorScheme.primary
                    )
                    InfoChip(
                        icon = Icons.Default.DateRange,
                        text = formatTanggal(submission.submissionTime, "EEEE, d MMMM yyyy"),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // --- Tombol Aksi ---
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Button(
                        onClick = { /* TODO: Navigasi ke halaman penilaian */ },
                        enabled = isBelumDinilai,
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text("Detail & Nilai")
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Icon(Icons.Outlined.ArrowForward, contentDescription = "Beri Nilai")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(icon: ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = color,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StatusChip(status: String) {
    val backgroundColor = when (status) {
        "Perlu Dinilai" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val textColor = when (status) {
        "Perlu Dinilai" -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
