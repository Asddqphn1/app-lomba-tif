package com.example.lombatif.component

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    var selectedTab by remember { mutableStateOf(0) }
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
            if (selectedTab == 0) {
                StatsSection(user.size, lomba.size)
            }

            when (selectedTab) {
                0 -> PlaceholderContent("")
                1 -> UserAdmin(
                    users = user,
                    onUpdate = { selectedUser ->
                        // Contoh: aksi saat edit user
                        println("Edit user: ${selectedUser.nama}")
                        // Bisa juga navigasi, buka dialog, dll
                    },
                    onDelete = { selectedUser ->
                        // Contoh: aksi saat hapus user
                        println("Hapus user: ${selectedUser.nama}")
                        // Bisa tampilkan konfirmasi lalu hapus dari list atau database
                    }
                )
                2 -> PlaceholderContent("Data Juri belum tersedia")
                3 -> LombaAdmin(lomba, onHapusLomba = { /* TODO */ }, onTambahLomba = { /* TODO */ })
            }

        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Lomba Tif", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Icon(Icons.Default.AccountCircle, contentDescription = "User", modifier = Modifier.size(28.dp))
    }
}

@Composable
fun StatsSection(userCount: Int, lombaCount: Int) {
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
        StatCardData("Total Peserta", "0", Color(0xFFDFF6E3), Icons.Default.Person),
        StatCardData("Total Juri", "0", Color(0xFFE8DFFF), Icons.Default.Check),
        StatCardData("Total Lomba", lombaCount.toString(), Color(0xFFFFF4D8), Icons.Default.ThumbUp)
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cards.forEach { StatCard(it.title, it.count, it.backgroundColor, it.icon) }
    }
}

data class StatCardData(val title: String, val count: String, val backgroundColor: Color, val icon: ImageVector)

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
fun LombaAdmin(lomba: List<DaftarLomba>, onHapusLomba: (DaftarLomba) -> Unit, onTambahLomba: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Daftar Lomba", style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.SansSerif)
            Button(
                onClick = onTambahLomba,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.Add, // icon plus
                    contentDescription = "Tambah Lomba"
                )
                Spacer(modifier = Modifier.width(4.dp)) // kasih jarak antara icon dan text
                Text(text = "Tambah Lomba")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(lomba) { item ->
                    LombaCard(item = item, onHapus = { onHapusLomba(item) })
                }
            }
        }
    }
}
@Composable
fun UserAdmin(
    users: List<DaftarUsersAdmin>,
    onUpdate: (DaftarUsersAdmin) -> Unit,
    onDelete: (DaftarUsersAdmin) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredUsers = users.filter {
        it.nama?.contains(searchQuery, ignoreCase = true) == true ||
                it.email?.contains(searchQuery, ignoreCase = true) == true
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Search bar

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween


        ) {
            Row(modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
                ) {
                Text("Daftar User", fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.width(60.dp)) // Untuk action
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Cari pengguna...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )





        LazyColumn {
            items(filteredUsers) { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(user.nama ?: "Unknown", fontWeight = FontWeight.Medium)
                            Text(user.email ?: "No email", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    // Update & Delete buttons
                    Row {
                        IconButton(onClick = { onUpdate(user) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { onDelete(user) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
                Divider()
            }
        }
    }
}


@Composable
fun LombaCard(item: DaftarLomba, onHapus: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.nama ?: "Tanpa Nama",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onHapus) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(label = "Jenis", value = item.jenisLomba ?: "-")
            InfoRow(label = "Tanggal", value = item.tanggal.toString() ?: "-")
            InfoRow(label = "Lokasi", value = item.lokasi ?: "-")
            InfoRow(label = "Batas Waktu", value = item.batasWaktu ?: "-")
            InfoRow(label = "Deskripsi", value = item.deskripsi ?: "-", multiline = true)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, multiline: Boolean = false) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.width(100.dp)
        )
        if (multiline) {
            Text(
                text = value,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
                color = Color.DarkGray
            )
        } else {
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}


@Composable
fun PesertaScreen() {
    val tabTitles = listOf("Semua", "Disetujui", "Ditolak")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (selectedTabIndex) {
            0 -> TabContent("Menampilkan semua peserta.")
            1 -> TabContent("Menampilkan peserta yang disetujui.")
            2 -> TabContent("Menampilkan peserta yang ditolak.")
        }
    }
}

@Composable
fun TabContent(content: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Text(text = content)
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
fun BottomNavBar(selected: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf(
        Pair("Dashboard", Icons.Default.Home),
        Pair("User", Icons.Default.Person),
        Pair("Juri", Icons.Default.Face),
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