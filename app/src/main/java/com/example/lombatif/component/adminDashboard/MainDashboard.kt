package com.example.lombatif.component.adminDashboard

import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.EmojiPeople
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lombatif.component.MainActivity
import com.example.lombatif.models.get.PesertaAdmin
import com.example.lombatif.ui.theme.LombaTIFTheme
import com.example.lombatif.viewModels.ViewAnggotaTim
import com.example.lombatif.viewModels.ViewDaftarLomba
import com.example.lombatif.viewModels.ViewJuriAdmin
import com.example.lombatif.viewModels.ViewPesertaAdmin
import com.example.lombatif.viewModels.ViewUserAdmin

class MainDashboard : ComponentActivity() {
    private val viewModel = ViewUserAdmin()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LombaTIFTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    DashboardScreen(
                        viewModel,
                        viewJuriAdmin = ViewJuriAdmin(),
                        viewDaftarLomba = ViewDaftarLomba()
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    viewModel: ViewUserAdmin,
    viewJuriAdmin: ViewJuriAdmin,
    viewDaftarLomba: ViewDaftarLomba
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showProfile by remember { mutableStateOf(false) }

    val user by viewModel.user.collectAsState()
    val juri by viewJuriAdmin.juri.collectAsState()
    val lomba by viewDaftarLomba.lomba.collectAsState()

    val viewPesertaAdmin = remember { ViewPesertaAdmin() }
    val anggotaViewModel = remember { ViewAnggotaTim() }

    var selectedPeserta by remember { mutableStateOf<PesertaAdmin?>(null) }

    val pesertaCount = viewPesertaAdmin.pesertaList.size
    val juriCount = juri.size

    LaunchedEffect(Unit) {
        viewPesertaAdmin.loadPeserta()
        viewJuriAdmin.fetchJuriAdmin()
    }

    Scaffold(
        topBar = { TopBar(onProfileClick = { showProfile = true }) },
        bottomBar = {
            if (!showProfile) {
                BottomNavBar(selectedTab) { selectedTab = it }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            val context = LocalContext.current
            if (showProfile) {
                ProfileScreen(
                    onBack = { showProfile = false },
                    onLogout = {

                        // Clear SharedPreferences
                        val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                        sharedPref.edit().clear().apply()

                        // Intent ke MainActivity
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                    }
                )
            } else {
                if (selectedTab == 0) {
                    StatsSection(
                        userCount = user.size,
                        pesertaCount = pesertaCount,
                        juriCount = juriCount,
                        lombaCount = lomba.size
                    )
                }

                when (selectedTab) {
                    0 -> PlaceholderContent("")
                    1 -> LombaAdmin(
                        lomba = lomba,
                        onHapusLomba = { },
                        onTambahLombaBaru = { },
                        isRefreshing = false,
                        onRefreshLomba = { viewDaftarLomba.fetchDaftarLomba() }
                    )
                    2 -> {
                        PesertaListScreen(
                            pesertaList = viewPesertaAdmin.pesertaList,
                            isLoading = viewPesertaAdmin.isLoading,
                            errorMessage = viewPesertaAdmin.errorMessage,
                            onLihatAnggota = { peserta ->
                                selectedPeserta = peserta
                                anggotaViewModel.loadAnggotaTim(peserta.id)
                            },
                            anggotaViewModel = anggotaViewModel
                        )

                        selectedPeserta?.let { peserta ->
                            AnggotaTimDialog(
                                namaPeserta = peserta.nama,
                                anggotaTim = anggotaViewModel.anggota,
                                isLoading = anggotaViewModel.isLoading,
                                onDismiss = {
                                    selectedPeserta = null
                                    anggotaViewModel.resetState()
                                }
                            )
                        }
                    }
                    3 -> JuriAdminPage(
                        onUpdate = { selectedUser -> println("Edit user: ${selectedUser.nama}") },
                        onDelete = { selectedUser -> println("Hapus user: ${selectedUser.nama}") }
                    )
                    4 -> UserAdmin(
                        onHapusBerhasil = { viewModel.fetchDaftarUser() },
                        onUpdate = { updatedUser -> println("Edit user: ${updatedUser.id}") },
                        onDelete = { deletedUser -> println("Hapus user: ${deletedUser.id}") }
                    )
                    5 -> LombaScreen(

                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onProfileClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "WELCOME, ADMIN! ",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "User Profile",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun StatsSection(userCount: Int, pesertaCount: Int, juriCount: Int, lombaCount: Int) {
    val cards = listOf(
        StatCardData(
            title = "Total User",
            count = userCount.toString(),
            icon = Icons.Rounded.Groups,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        StatCardData(
            title = "Total Peserta",
            count = pesertaCount.toString(),
            icon = Icons.Rounded.Person,
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        StatCardData(
            title = "Total Juri",
            count = juriCount.toString(),
            icon = Icons.Rounded.Gavel, // Ikon palu hakim untuk juri
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        StatCardData(
            title = "Total Lomba",
            count = lombaCount.toString(),
            icon = Icons.Rounded.EmojiEvents, // Ikon untuk kategori/jenis lomba
            backgroundColor = Color(0xFFFFF4D8), // Warna custom bisa tetap dipakai jika suka
            contentColor = Color(0xFF6D5B23)
        )
    )

    Column(modifier = Modifier.fillMaxWidth()) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(1), // Ubah ke 2 untuk tampilan 2 kolom
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(cards) { cardData ->
                StatCard(
                    title = cardData.title,
                    count = cardData.count,
                    icon = cardData.icon,
                    backgroundColor = cardData.backgroundColor,
                    contentColor = cardData.contentColor
                )
            }
        }
    }
}


@Composable
fun StatCard(
    title: String,
    count: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        // DIUBAH: Menggunakan warna dari parameter dan menambahkan elevasi untuk efek 'mengambang'
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large // Sudut kartu yang lebih rounded
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp), // Padding yang sedikit lebih besar
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Memberi jarak antara grup ikon & teks dengan angka
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // DIUBAH: Ikon diberi background agar lebih menonjol
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(40.dp), // Ukuran ikon diperbesar
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge, // Menggunakan style dari tema
                        color = contentColor
                    )
                    Text(
                        text = count,
                        style = MaterialTheme.typography.headlineMedium, // Angka dibuat lebih besar
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }
        }
    }
}
