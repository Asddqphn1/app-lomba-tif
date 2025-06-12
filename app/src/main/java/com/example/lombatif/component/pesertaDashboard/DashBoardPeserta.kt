package com.example.lombatif.component.pesertaDashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lombatif.R
import com.example.lombatif.component.MainActivity
import com.example.lombatif.component.adminDashboard.ProfileScreen // <-- 1. IMPORT PROFILE SCREEN
import com.example.lombatif.models.get.DaftarLomba
import com.example.lombatif.models.get.PesertaLombaData
import com.example.lombatif.models.get.modelsPeserta.KlasemenEntry
import com.example.lombatif.models.get.modelsPeserta.PenilaianDetail
import com.example.lombatif.models.get.modelsPeserta.PesertaLombaDetail
import com.example.lombatif.response.Anggota
import com.example.lombatif.response.PendaftaranRequest
import com.example.lombatif.ui.theme.LombaTIFTheme
import com.example.lombatif.viewModels.*
import com.example.lombatif.viewModels.PesertaModels.KlasemenState
import com.example.lombatif.viewModels.PesertaModels.PenilaianState
import com.example.lombatif.viewModels.PesertaModels.ViewPenilaian
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class DashBoardPeserta : ComponentActivity() {
    private val lombaViewModel: ViewLombaUser by viewModels()
    private val submissionViewModel: ViewSubmit by viewModels()
    private val daftarLombaViewModel: ViewDaftarLomba by viewModels()
    private val profileViewModel: ViewProfile by viewModels()
    private val penilaianViewModel: ViewPenilaian by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("USER_ID")
        if (userId == null) {
            finish()
            return
        }
        setContent {
            LombaTIFTheme {
                MainScreen(
                    lombaViewModel = lombaViewModel,
                    submissionViewModel = submissionViewModel,
                    daftarLombaViewModel = daftarLombaViewModel,
                    profileViewModel = profileViewModel,
                    penilaianViewModel = penilaianViewModel,
                    userId = userId
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    lombaViewModel: ViewLombaUser,
    submissionViewModel: ViewSubmit,
    daftarLombaViewModel: ViewDaftarLomba,
    profileViewModel: ViewProfile,
    penilaianViewModel: ViewPenilaian,
    userId: String
) {
    val navItems = listOf(
        BottomNavItem("Dashboard", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Daftar Lomba", Icons.Filled.List, Icons.Outlined.List),
        BottomNavItem("Submission", Icons.Filled.Send, Icons.Outlined.Send),
        BottomNavItem("Penilaian", Icons.Filled.Star, Icons.Outlined.Star),
        BottomNavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    val daftarLombaState by daftarLombaViewModel.lomba.collectAsState()
    val profileState by profileViewModel.profile.collectAsState()
    val userName = profileState?.profile?.nama ?: "Pengguna"
    val context = LocalContext.current

    Scaffold(
        topBar = {
            // TopAppBar tidak akan ditampilkan untuk halaman profil karena sudah ada di dalam komponennya
            if (selectedItemIndex != 4) {
                TopAppBar(
                    title = { Text(navItems[selectedItemIndex].title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = { selectedItemIndex = index },
                        label = null,
                        icon = {
                            Icon(
                                imageVector = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedItemIndex) {
                0 -> DashboardScreen(
                    lombaViewModel = lombaViewModel,
                    submissionViewModel = submissionViewModel,
                    totalLombaCount = daftarLombaState.size,
                    userName = userName,
                    userId = userId,
                    profileViewModel = profileViewModel
                )
                1 -> AllLombaScreen(viewModel = daftarLombaViewModel, userId = userId)
                2 -> SubmissionListScreen(viewModel = lombaViewModel,
                    userId = userId)
                3 -> PenilaianScreen(viewModel = penilaianViewModel, userId = userId)

                // --- 2. KODE DIPERBARUI DI SINI ---
                4 -> ProfileScreen(
                    viewModel = profileViewModel, // Gunakan instance ViewModel yang sudah ada
                    onBack = {
                        // Jika tombol close di profil ditekan, kembali ke tab dashboard
                        selectedItemIndex = 0
                    },
                    onLogout = {
                        // Hapus data login jika ada (misal: token), lalu kembali ke MainActivity
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        // Tutup activity saat ini
                        (context as? Activity)?.finish()
                    }
                )
                else -> PlaceholderScreen(navItems[selectedItemIndex].title)
            }
        }
    }
}


@Composable
fun DashboardScreen(
    lombaViewModel: ViewLombaUser,
    submissionViewModel: ViewSubmit,
    profileViewModel: ViewProfile,
    totalLombaCount: Int,
    userName: String,
    userId: String
) {
    val lombaState by lombaViewModel.lombaState.collectAsState()
    val submissionState by submissionViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = userId) {
        lombaViewModel.fetchUserLomba(userId)
        submissionViewModel.fetchSubmissions(userId)
        profileViewModel.fetchProfile()
    }

    val lombaCount = if (lombaState is LombaUiState.Success) (lombaState as LombaUiState.Success).lombaList.size else 0
    val submissionCount = if (submissionState is SubmissionUiState.Success) (submissionState as SubmissionUiState.Success).submissions.size else 0

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("Selamat Datang, $userName!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(modifier = Modifier.weight(1f), label = "Lomba Diikuti", count = lombaCount.toString(), icon = Icons.Default.List)
                StatCard(modifier = Modifier.weight(1f), label = "Submission", count = submissionCount.toString(), icon = Icons.Default.Send)
                StatCard(modifier = Modifier.weight(1f), label = "Total Lomba", count = totalLombaCount.toString(), icon = Icons.Default.Star)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            Text("Visualisasi Data", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                if (totalLombaCount > 0) {
                    CustomDoughnutChart(lombaDiikuti = lombaCount, totalLomba = totalLombaCount, modifier = Modifier.padding(16.dp))
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
                        Text("Data lomba belum tersedia.")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllLombaScreen(viewModel: ViewDaftarLomba, userId: String) {
    val allLombaList by viewModel.lomba.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedLombaId by remember { mutableStateOf<String?>(null) }
    val submitState by viewModel.submitState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5))) {
        if (allLombaList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Tidak ada lomba yang tersedia.", color = Color.Gray) }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(allLombaList) { lomba ->
                    AllLombaItemCard(
                        lomba = lomba,
                        onRegisterClick = {
                            selectedLombaId = lomba.id
                            viewModel.selectLomba(lomba.id.toString())
                            showBottomSheet = true
                        }
                    )
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false; viewModel.clearStates() }, sheetState = sheetState) {
            RegistrationFormSheet(
                viewModel = viewModel,
                userId = userId,
                lombaId = selectedLombaId ?: "",
                onSuccess = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                            viewModel.clearStates()
                        }
                    }
                }
            )
        }
    }

    when (val state = submitState) {
        is SubmitState.Success -> {
            AlertDialog(
                onDismissRequest = { viewModel.clearStates() },
                title = { Text("Pendaftaran Berhasil") },
                text = { Text(state.message) },
                confirmButton = { TextButton(onClick = { viewModel.clearStates() }) { Text("OK") } }
            )
        }
        is SubmitState.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.clearStates() },
                title = { Text("Pendaftaran Gagal") },
                text = { Text(state.message) },
                confirmButton = { TextButton(onClick = { viewModel.clearStates() }) { Text("Tutup") } }
            )
        }
        // --- TAMBAHKAN DIALOG BARU DI SINI ---
        is SubmitState.AlreadyRegistered -> {
            AlertDialog(
                onDismissRequest = { viewModel.clearStates() },
                icon = { Icon(Icons.Default.Warning, contentDescription = "Peringatan") },
                title = { Text("Pendaftaran Tidak Dapat Diproses") },
                text = { Text(state.message) }, // Pesan dari server: "Anda sudah terdaftar..."
                confirmButton = {
                    TextButton(onClick = { viewModel.clearStates() }) {
                        Text("Mengerti")
                    }
                }
            )
        }
        else -> {}
    }
}

@Composable
fun RegistrationFormSheet(viewModel: ViewDaftarLomba, userId: String, lombaId: String, onSuccess: () -> Unit) {
    val detailState by viewModel.lombaDetailState.collectAsState()
    val submitState by viewModel.submitState.collectAsState()
    val namaPesertaState = remember { mutableStateOf("") }
    val anggotaStates = remember { mutableStateListOf<String>() }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        when (val state = detailState) {
            is LombaDetailState.Loading -> CircularProgressIndicator()
            is LombaDetailState.Error -> Text(state.message, color = Color.Red)
            is LombaDetailState.Success -> {
                val detail = state.detail
                LaunchedEffect(detail.jumlahAnggota) {
                    anggotaStates.clear()
                    repeat(detail.jumlahAnggota) { anggotaStates.add("") }
                }
                Text(text = "Form Pendaftaran: ${detail.nama}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                if (detail.jenisLomba == "INDIVIDU") {
                    OutlinedTextField(value = namaPesertaState.value, onValueChange = { namaPesertaState.value = it }, label = { Text("Nama Peserta") }, modifier = Modifier.fillMaxWidth())
                } else {
                    OutlinedTextField(value = namaPesertaState.value, onValueChange = { namaPesertaState.value = it }, label = { Text("Nama Tim") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Anggota Tim", style = MaterialTheme.typography.titleMedium)
                    anggotaStates.forEachIndexed { index, _ ->
                        OutlinedTextField(value = anggotaStates[index], onValueChange = { anggotaStates[index] = it }, label = { Text("Nama Anggota ${index + 1}") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        // --- VALIDASI DITAMBAHKAN DI SINI ---
                        if (namaPesertaState.value.isBlank()) {
                            // Tampilkan pesan jika nama peserta/tim kosong
                            Toast.makeText(context, "Nama Peserta atau Tim tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        } else {
                            // Jika tidak kosong, baru proses pendaftaran
                            val request = if (detail.jenisLomba == "INDIVIDU") {
                                PendaftaranRequest(namaPeserta = namaPesertaState.value)
                            } else {
                                PendaftaranRequest(namaPeserta = namaPesertaState.value, anggota = anggotaStates.map { Anggota(it) })
                            }
                            viewModel.submitPendaftaran(userId, lombaId, request)
                        }
                    },
                    enabled = submitState !is SubmitState.Loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (submitState is SubmitState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else { Text("Submit Pendaftaran") }
                }
            }
            else -> {}
        }
    }
    LaunchedEffect(submitState) { if (submitState is SubmitState.Success) { onSuccess() } }
}

@Composable
fun AllLombaItemCard(lomba: DaftarLomba, onRegisterClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // --- PERBAIKAN DI SINI ---
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(lomba.url) // Menggunakan properti .url dari objek lomba
                    .crossfade(true)
                    .placeholder(R.drawable.ic_launcher_background) // Ganti dengan placeholder Anda
                    .error(R.drawable.ic_launcher_background) // Ganti dengan gambar error Anda
                    .build(),
                contentDescription = "Gambar Lomba ${lomba.nama}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // .toString() tidak wajib jika propertinya sudah String, tapi tidak masalah
                Text(lomba.nama.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = Icons.Default.DateRange, text = "Batas Waktu: ${formatTanggal(lomba.batasWaktu.toString())}")
                Spacer(modifier = Modifier.height(4.dp))
                InfoRow(icon = Icons.Default.LocationOn, text = lomba.lokasi.toString())
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Daftar Lomba")
                }
            }
        }
    }
}

// ===================================
// ===== Sisa Composable & Fungsi ====
// ===================================

@Composable
fun StatCard(modifier: Modifier, label: String, count: String, icon: ImageVector) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = count, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
        }
    }
}

@Composable
fun CustomDoughnutChart(lombaDiikuti: Int, totalLomba: Int, modifier: Modifier = Modifier) {
    val sisaLomba = totalLomba - lombaDiikuti
    val data = mapOf("Diikuti" to lombaDiikuti.toFloat(), "Lainnya" to sisaLomba.toFloat())
    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surfaceVariant)
    val totalValue = data.values.sum()
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                data.values.forEachIndexed { index, value ->
                    val sweepAngle = (value / totalValue) * 360f
                    drawArc(color = colors.getOrElse(index) { Color.Gray }, startAngle = startAngle, sweepAngle = sweepAngle, useCenter = false, style = Stroke(width = 60f, cap = StrokeCap.Butt))
                    startAngle += sweepAngle
                }
            }
            if (totalLomba > 0) {
                val percentage = (lombaDiikuti.toFloat() / totalLomba.toFloat() * 100).toInt()
                Text(text = "$percentage%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            LegendaItem(color = colors[0], text = "Lomba Diikuti ($lombaDiikuti)")
            Spacer(modifier = Modifier.width(16.dp))
            LegendaItem(color = colors[1], text = "Lainnya ($sisaLomba)")
        }
    }
}

@Composable
fun LegendaItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(16.dp).background(color, RoundedCornerShape(4.dp)))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5)), contentAlignment = Alignment.Center) {
        Text(text = text, fontSize = 24.sp, color = Color.Gray)
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String, color: Color = Color.Gray) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun SubmissionListScreen(viewModel: ViewLombaUser, userId: String) {
    val listState by viewModel.lombaState.collectAsState()
    val context = LocalContext.current

    // Panggil fetch data hanya sekali saat layar dibuat
    LaunchedEffect(key1 = Unit) {
        viewModel.fetchUserLomba(userId)
    }

    when (val state = listState) {
        is LombaUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        is LombaUiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Error: ${state.message}", color = Color.Red) }
        is LombaUiState.Success -> {
            if (state.lombaList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Anda belum terdaftar di lomba manapun.") }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(state.lombaList) { data ->
                        SubmissionLombaCard(data = data) {
                            // Navigasi ke FormSubmitActivity
                            val intent = Intent(context, FormSubmitActivity::class.java).apply {
                                putExtra("ID_PESERTA_LOMBA", data.idPesertaLomba)
                                putExtra("NAMA_LOMBA", data.lomba.nama)
                            }
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubmissionLombaCard(data: PesertaLombaData, onSubmitClick: () -> Unit) {
    val deadlinePassed = remember(data.lomba.batasWaktu) {
        try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(data.lomba.batasWaktu)?.before(
                Date()
            ) ?: true
        } catch (e: Exception) {
            true
        }
    }
    val deadlineColor = if (deadlinePassed) MaterialTheme.colorScheme.error else Color.Gray

    Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Column {
            AsyncImage(model = data.lomba.url, contentDescription = data.lomba.nama, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(180.dp))
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(data.lomba.nama, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                InfoRow(icon = Icons.Default.Person, text = "Peserta: ${data.lomba.nama}")
                InfoRow(icon = Icons.Default.DateRange, text = "Deadline: ${formatTanggal(data.lomba.batasWaktu)}", color = deadlineColor)
                Button(
                    onClick = onSubmitClick,
                    enabled = !deadlinePassed,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (deadlinePassed) "Submission Ditutup" else "Submit Work")
                }
            }
        }
    }
}


@Composable
fun PenilaianScreen(viewModel: ViewPenilaian, userId: String) {
    val state by viewModel.penilaianState.collectAsState()
    val klasemenState by viewModel.klasemenState.collectAsState()
    var showKlasemenDialog by remember { mutableStateOf(false) }
    var selectedLombaForKlasemen by remember { mutableStateOf<PesertaLombaDetail?>(null) }


    // Tampilkan dialog klasemen jika showKlasemenDialog true
    if (showKlasemenDialog && selectedLombaForKlasemen != null) {
        KlasemenDialog(
            lombaNama = selectedLombaForKlasemen!!.lomba.nama,
            state = klasemenState,
            onDismiss = {
                showKlasemenDialog = false
                viewModel.clearKlasemenState()
            }
        )
    }


    LaunchedEffect(key1 = userId) {
        viewModel.fetchPenilaian(userId)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5))) {
        when (val currentState = state) {
            is PenilaianState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is PenilaianState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(currentState.message, color = Color.Red) }
            is PenilaianState.Success -> {
                // Kita ambil data dari peserta pertama di list (karena ini halaman untuk user yg login)
                val lombaList = currentState.pesertaList.firstOrNull()?.pesertaLomba ?: emptyList()

                if (lombaList.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Anda belum memiliki penilaian.") }
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(lombaList) { pesertaLomba ->
                            PenilaianLombaCard(
                                pesertaLomba = pesertaLomba,
                                onKlasemenClick = {
                                    selectedLombaForKlasemen = pesertaLomba
                                    viewModel.fetchKlasemen(pesertaLomba.lomba.id)
                                    showKlasemenDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PenilaianLombaCard(pesertaLomba: PesertaLombaDetail, onKlasemenClick: () -> Unit) {
    val penilaianList = pesertaLomba.submission?.penilaian ?: emptyList()
    var isDetailExpanded by remember { mutableStateOf(false) } // State untuk expand/collapse

    val averageScore = if (penilaianList.isNotEmpty()) {
        penilaianList.mapNotNull { it.nilaiPenilaian.toDoubleOrNull() }.average()
    } else { 0.0 }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) { /* ... sama seperti sebelumnya ... */ }

            // ... (Detail Nilai dan Tombol) ...
            if (penilaianList.isEmpty()) {
                Text("Karya Anda belum dinilai oleh juri.", color = Color.Gray)
            } else {
                Text("Nilai Rata-rata: ${String.format("%.1f", averageScore)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Dinilai oleh: ${penilaianList.size} Juri", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PERUBAHAN LOGIKA TOMBOL
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { isDetailExpanded = !isDetailExpanded }, // Toggle state expand
                    modifier = Modifier.weight(1f),
                    enabled = penilaianList.isNotEmpty() // Tombol disable jika belum ada penilaian
                ) {
                    Text(if (isDetailExpanded) "Tutup Penilaian" else "Lihat Penilaian")
                }
                OutlinedButton(onClick = onKlasemenClick, modifier = Modifier.weight(1f)) {
                    Text("Lihat Klasemen")
                }
            }

            // PERUBAHAN: Tampilkan detail jika isDetailExpanded true
            AnimatedVisibility(visible = isDetailExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider()
                    penilaianList.forEach { penilaian ->
                        PenilaianDetailItem(penilaian = penilaian)
                    }
                }
            }
        }
    }
}


@Composable
fun KlasemenDialog(
    lombaNama: String,
    state: KlasemenState,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Klasemen", style = MaterialTheme.typography.headlineSmall)
                Text(lombaNama, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp), // Batasi tinggi
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    is KlasemenState.Loading -> CircularProgressIndicator()
                    is KlasemenState.Error -> Text(state.message, color = Color.Red)
                    is KlasemenState.Success -> {
                        if (state.klasemenList.isEmpty()) {
                            Text("Belum ada data klasemen untuk lomba ini.")
                        } else {
                            KlasemenContent(list = state.klasemenList)
                        }
                    }
                    else -> {}
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun KlasemenContent(list: List<KlasemenEntry>) {
    LazyColumn {
        // Header
        item {
            Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Rank", modifier = Modifier.width(50.dp), fontWeight = FontWeight.Bold)
                Text("Tim/Peserta", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("Poin", modifier = Modifier.width(80.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }
            Divider()
        }
        // Data
        items(list) { entry ->
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank
                Text(entry.rank.toString(), modifier = Modifier.width(50.dp), style = MaterialTheme.typography.bodyLarge)

                // Nama
                Text(
                    entry.namaPeserta,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Poin dan Penilaian
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
                    Text(
                        text = entry.rataRataNilai.roundToInt().toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    // BINTANG DIGANTI DENGAN TEKS JUMLAH JURI
                    Text(
                        text = "dari ${entry.jumlahPenilaian} juri",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}


@Composable
fun PenilaianDetailItem(penilaian: PenilaianDetail) {
    Row(
        modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = penilaian.nilaiPenilaian,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                "\"${penilaian.deskripsiPenilaian.ifBlank { "Tidak ada deskripsi." }}\"",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "oleh Juri: ${penilaian.juri.users.nama}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

fun formatTanggal(tanggalString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")) // Format lebih umum
        val date = inputFormat.parse(tanggalString)
        if (date != null) {
            outputFormat.format(date)
        } else { "Tanggal tidak valid" }
    } catch (e: Exception) { "Tanggal tidak valid" }
}