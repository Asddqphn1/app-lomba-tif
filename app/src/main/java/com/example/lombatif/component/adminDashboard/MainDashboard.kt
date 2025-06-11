package com.example.lombatif.component.adminDashboard

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            if (showProfile) {
                ProfileScreen(
                    onBack = { showProfile = false },
                    onLogout = {}
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
                }
            }
        }
    }
}

@Composable
fun TopBar(onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Lomba Tif", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        IconButton(onClick = onProfileClick) {
            Icon(Icons.Default.AccountCircle, contentDescription = "User", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun StatsSection(userCount: Int, pesertaCount: Int, juriCount: Int, lombaCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text("Selamat Datang Admin", fontSize = 18.sp, fontFamily = FontFamily.SansSerif)
    }

    val cards = listOf(
        StatCardData("Total User", userCount.toString(), Color(0xFFD8E9FF), Icons.Default.Face),
        StatCardData("Total Peserta", pesertaCount.toString(), Color(0xFFDFF6E3), Icons.Default.Person),
        StatCardData("Total Juri", juriCount.toString(), Color(0xFFE8DFFF), Icons.Default.Check),
        StatCardData("Total Lomba", lombaCount.toString(), Color(0xFFFFF4D8), Icons.Default.ThumbUp)
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cards.forEach { StatCard(it.title, it.count, it.backgroundColor, it.icon) }
    }
}

@Composable
fun StatCard(title: String, count: String, background: Color, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 14.sp)
                Text(count, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}
