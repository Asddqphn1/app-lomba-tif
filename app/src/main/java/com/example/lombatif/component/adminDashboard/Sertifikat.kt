package com.example.lombatif.component.adminDashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.lombatif.ui.theme.LombaTIFTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lombatif.component.pesertaDashboard.formatTanggal
import com.example.lombatif.models.get.DaftarLomba
import com.example.lombatif.models.request.RequestSertifikat
import com.example.lombatif.viewModels.StatusSubmit
import com.example.lombatif.viewModels.ViewDaftarLomba
import com.example.lombatif.viewModels.ViewKirimSertifikat


class Sertifikat : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LombaTIFTheme {
                LombaScreen()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LombaScreen(
    viewModel: ViewDaftarLomba = androidx.lifecycle.viewmodel.compose.viewModel(),
    viewModelKirim: ViewKirimSertifikat = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val lombaList by viewModel.lomba.collectAsState()
    val submitState by viewModelKirim.submitState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedLombaId by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    val filteredList = remember(lombaList, searchQuery) {
        if (searchQuery.isBlank()) lombaList else lombaList.filter {
            it.nama?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    LaunchedEffect(submitState) {
        when (val state = submitState) {
            is StatusSubmit.Success -> {
                snackbarHostState.showSnackbar(state.message)
                viewModelKirim.resetSubmitState()
            }
            is StatusSubmit.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModelKirim.resetSubmitState()
            }
            else -> {
                // Do nothing for Idle or Loading states
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedLombaId == null) "Daftar Lomba" else "Pengiriman Sertifikat",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (selectedLombaId != null) {
                        IconButton(onClick = { selectedLombaId = null }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        if (selectedLombaId == null) {
            Column(modifier = Modifier.padding(paddingValues)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Cari Lomba...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList, key = { it.id!! }) { lomba ->
                        LombaItemCard(
                            lomba = lomba,
                            onDetailClick = { id -> selectedLombaId = id }
                        )
                    }
                }
            }
        } else {
            val isLoading = submitState is StatusSubmit.Loading
            SertifikatScreen(
                modifier = Modifier.padding(paddingValues),
                isLoading = isLoading,
                onKirimSertifikat = { url ->
                    if (!isLoading) {
                        viewModelKirim.kirimSertifikat(selectedLombaId!!, RequestSertifikat(url))
                    }
                }
            )
            if (submitState is StatusSubmit.Success) {
                selectedLombaId = null
            }
        }
    }
}


@Composable
fun SertifikatScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onKirimSertifikat: (url: String) -> Unit,
) {
    var sertifikatUrl by remember { mutableStateOf("") }
    var isUrlEmptyError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Masukkan URL sertifikat Anda untuk memulai proses pengiriman",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = "https://readdy.ai/api/search-image?query=A%20professional%20certificate%20with%20elegant%20design%2C%20gold%20seal%2C%20and%20official%20stamp%20on%20a%20clean%20white%20background.%20The%20certificate%20has%20decorative%20borders%20and%20appears%20formal%20and%20prestigious.%20High%20quality%2C%20professional%20photography&width=800&height=400&seq=1&orientation=landscape",
                    contentDescription = "Banner Sertifikat",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(MaterialTheme.shapes.medium)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sistem kami akan memproses sertifikat Anda dan mengirimkannya ke penerima yang terdaftar. Pastikan URL yang Anda masukkan valid dan dapat diakses.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = sertifikatUrl,
                    onValueChange = {
                        sertifikatUrl = it
                        if (it.isNotBlank()) {
                            isUrlEmptyError = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("URL Sertifikat") },
                    placeholder = { Text("https://example.com/certificates/your-id") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "URL Icon"
                        )
                    },
                    singleLine = true,
                    isError = isUrlEmptyError
                )
                if (isUrlEmptyError) {
                    Text(
                        text = "URL Sertifikat tidak boleh kosong",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (sertifikatUrl.isNotBlank()) {
                            onKirimSertifikat(sertifikatUrl)
                        } else {
                            isUrlEmptyError = true
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = "Kirim Ikon",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kirim Sertifikat", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "© 2025 Sistem Pengiriman Sertifikat. Hak Cipta Dilindungi.",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
fun LombaItemCard(
    lomba: DaftarLomba,
    onDetailClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(lomba.url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Gambar ${lomba.nama}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = lomba.nama ?: "Nama Lomba",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(Color(0xFFE3F3E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aktif",
                            color = Color(0xFF3B8C5B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                InfoRow(icon = Icons.Rounded.CalendarToday, text = formatTanggal(lomba.tanggal.toString()))
                InfoRow(icon = Icons.Rounded.LocationOn, text = lomba.lokasi ?: "Lokasi")

                val isIndividu = lomba.jenisLomba?.lowercase() == "individu"
                val jenisLombaText = if (isIndividu) {
                    "Individu"
                } else {
                    "${lomba.jenisLomba ?: ""} - ${lomba.jumlah_tim ?: 0} Anggota"
                }
                val jenisLombaIcon = if (isIndividu) {
                    Icons.Default.Person
                } else {
                    Icons.Rounded.Groups
                }
                InfoRow(icon = jenisLombaIcon, text = jenisLombaText)
            }

            val isSertifikatKosong = lomba.sertifikat.isNullOrEmpty()

            Button(
                onClick = { onDetailClick(lomba.id ?: "") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSertifikatKosong) Color.Black else Color.Gray
                ),
                enabled = isSertifikatKosong
            ) {
                Icon(Icons.Rounded.Send, contentDescription = "Detail Lomba", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSertifikatKosong) "Kirim Sertifikat" else "Sudah Dikirim",
                    color = Color.White
                )
            }
        }
    }
}



@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.DarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}