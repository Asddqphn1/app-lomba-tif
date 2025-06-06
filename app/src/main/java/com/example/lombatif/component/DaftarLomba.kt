package com.example.lombatif.component

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lombatif.models.get.DaftarLomba
import com.example.lombatif.ui.theme.LombaTIFTheme
import com.example.lombatif.viewModels.ViewDaftarLomba
import java.text.SimpleDateFormat
import java.util.*

class DaftarLomba : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LombaTIFTheme {
                DaftarLombaScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarLombaScreen(
    viewModel: ViewDaftarLomba = viewModel()
) {
    val context = LocalContext.current
    val daftarLomba by viewModel.lomba.collectAsState()
    var selectedLomba by remember { mutableStateOf<DaftarLomba?>(null) }
    val success = viewModel.stateUI
    val userId = remember {
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getString("USER_ID", null)
    }

    if (success == "HTTP 401 Unauthorized") {
        AdminAccessDeniedBanner()
    } else {
        when {
            selectedLomba != null -> {
                val lomba = selectedLomba!!
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(onClick = { selectedLomba = null }) {
                        Text("← Kembali ke daftar lomba", fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (userId == null) {
                        Text(
                            "Gagal mendapatkan ID user. Silakan login ulang.",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            DaftarLombaForm(
                                idUser = userId,
                                idLomba = lomba.id ?: "",
                                jumlahTim = lomba.jumlah_tim ?: 1,
                                viewModel = viewModel()
                            )
                        }
                    }
                }
            }

            else -> {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Daftar Lomba",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 30.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0C1C4A))
                        )
                    }
                ) { padding ->
                    LazyColumn(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .background(Color(0xFFF9F9F9))
                    ) {
                        items(daftarLomba) { lomba ->
                            lomba?.let {
                                LombaCard(it) {
                                    selectedLomba = it
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun LombaCard(lomba: DaftarLomba, onDaftarClick: () -> Unit) {
    val context = LocalContext.current

    val formattedTanggal = lomba.tanggal?.let {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(it)
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
            formatter.format(date!!)
        } catch (e: Exception) {
            it
        }
    } ?: "-"

    val batasWaktuFormatted = lomba.batasWaktu?.let {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(it)
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
            "Pendaftaran ditutup pada ${formatter.format(date!!)}"
        } catch (e: Exception) {
            "Pendaftaran ditutup: $it"
        }
    } ?: "-"

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(lomba.url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Gambar Lomba",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = lomba.nama ?: "-",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formattedTanggal,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = lomba.lokasi ?: "-",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = lomba.jenisLomba ?: "-",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = lomba.deskripsi ?: "-",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = batasWaktuFormatted,
                fontSize = 14.sp,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDaftarClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C1C4A))
            ) {
                Text("Daftar Sekarang", color = Color.White)
            }
        }
    }
}

@Composable
fun AdminAccessDeniedBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE),
            contentColor = Color(0xFFC62828)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Admin Only",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "ADMIN ACCESS REQUIRED",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your account doesn't have administrator privileges",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
