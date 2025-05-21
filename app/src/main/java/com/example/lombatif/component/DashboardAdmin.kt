package com.example.lombatif.component

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lombatif.models.get.DaftarLomba
import com.example.lombatif.models.get.DaftarUsersAdmin
import com.example.lombatif.ui.theme.LombaTIFTheme
import com.example.lombatif.viewModels.ViewDaftarLomba
import com.example.lombatif.viewModels.ViewUserAdmin

class DashboardAdmin : ComponentActivity() {
    private val viewModel = ViewUserAdmin()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LombaTIFTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    DashboardScreen(viewModel, viewDaftarLomba = ViewDaftarLomba())
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: ViewUserAdmin, viewDaftarLomba: ViewDaftarLomba) {
    var selectedTab by remember { mutableStateOf(0) } // Fixed state management
    val user by viewModel.user.collectAsState()
    val lomba by viewDaftarLomba.lomba.collectAsState()

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavBar(selectedTab) { selectedTab = it } }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            StatsSection(
                userCount = user.size,
                lombaCount = lomba.size
            )
            TabSection(selectedTab) { selectedTab = it }
            SearchBar()

            when (selectedTab) {
                0 -> UserAdmin(user)
                1 -> PlaceholderContent("Data Peserta belum tersedia")
                2 -> PlaceholderContent("Data Juri belum tersedia")
                3 -> LombaAdmin(
                    lomba = lomba,
                    onHapusLomba = { /* TODO */ },
                    onTambahLomba = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun PlaceholderContent(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.Gray)
    }
}
@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Teknik Informatika", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Icon(Icons.Default.AccountCircle, contentDescription = "User", modifier = Modifier.size(28.dp))
    }
}

@Composable
fun StatsSection(
    userCount: Int,
    lombaCount: Int
) {
    val cards = listOf(
        StatCardData("Total User", userCount.toString(), Color(0xFFD8E9FF), Icons.Default.Face),
        StatCardData("Total Peserta", "0", Color(0xFFDFF6E3), Icons.Default.Person),
        StatCardData("Total Juri", "0", Color(0xFFE8DFFF), Icons.Default.Check),
        StatCardData("Total Lomba", lombaCount.toString(), Color(0xFFFFF4D8), Icons.Default.ThumbUp)
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cards.forEach { card ->
            StatCard(
                title = card.title,
                count = card.count,
                background = card.backgroundColor,
                icon = card.icon
            )
        }
    }
}

data class StatCardData(
    val title: String,
    val count: String,
    val backgroundColor: Color,
    val icon: ImageVector
)

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

@Composable
fun TabSection(selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    val titles = listOf("Manajemen User", "Manajemen Peserta", "Manajemen Juri", "Manajemen Lomba")
    TabRow(selectedTabIndex = selectedIndex) {
        titles.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                text = { Text(title, fontSize = 12.sp) }
            )
        }
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Cari user...") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
    )
}


@Composable
fun UserAdmin(users: List<DaftarUsersAdmin>) {
    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("NAMA", fontWeight = FontWeight.SemiBold)
                Text("EMAIL", fontWeight = FontWeight.SemiBold)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        items(users) { user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(user.nama ?: "Unknown")
                Text(user.email ?: "No email")
            }
        }
    }
}

@Composable
fun LombaAdmin(
    lomba: List<DaftarLomba>,
    onHapusLomba: (DaftarLomba) -> Unit,
    onTambahLomba: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Daftar Lomba", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onTambahLomba) {
                Text("Tambah Lomba")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(lomba) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.nama ?: "Tanpa Nama",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            IconButton(onClick = { onHapusLomba(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Jenis: ${item.jenisLomba ?: "-"}")
                        Text("Tanggal: ${item.tanggal ?: "-"}")
                        Text("Lokasi: ${item.lokasi ?: "-"}")
                        Text("Batas Waktu: ${item.batasWaktu ?: "-"}")
                        Text("Deskripsi: ${item.deskripsi ?: "-"}")
                    }
                }
            }
        }
    }
}


@Composable
fun BottomNavBar(selected: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf(
        Pair("User", Icons.Default.Person),
        Pair("Peserta", Icons.Default.ThumbUp),
        Pair("Juri", Icons.Default.ThumbUp),
        Pair("Lomba", Icons.Default.DateRange)
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selected == index,
                onClick = { onItemSelected(index) },
                icon = { Icon(item.second, contentDescription = item.first) },
                label = { Text(item.first) }
            )
        }
    }
}

