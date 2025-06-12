package com.example.lombatif.component.juriDashboard

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lombatif.viewModels.JuriModels.ViewSubmission
import com.example.lombatif.viewModels.ViewProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuriMainScreen() {
    val navController = rememberNavController()
    val screens = listOf(JuriScreen.Dashboard, JuriScreen.Submissions)
    val context = LocalContext.current

    // ViewModel diinisialisasi di sini agar bisa diakses oleh semua screen di bawahnya
    val profileViewModel: ViewProfile = viewModel()
    val submissionViewModel: ViewSubmission = viewModel()

    val onLogout: () -> Unit = {
        Log.d("JuriMainScreen", "Logout diklik!")
        // TODO: Tambahkan logika logout di sini
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = JuriScreen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(JuriScreen.Dashboard.route) {
                JuriDashboardScreen(
                    onLogout = onLogout,
                    profileViewModel = profileViewModel,
                    navController = navController
                )
            }

            composable(JuriScreen.Submissions.route) {
                // Ambil juriId yang sudah valid dari submissionViewModel
                val juriId by submissionViewModel.juriId.collectAsState()

                SubmissionListScreen(
                    viewModel = submissionViewModel,
                    onNavigateToPenilaian = { submissionId ->
                        // Kirim juriId yang valid saat navigasi
                        if (!juriId.isNullOrBlank()) {
                            navController.navigate("penilaian_screen/$submissionId/$juriId")
                        } else {
                            Toast.makeText(context, "Data juri belum siap, coba lagi.", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Definisikan route dan arguments untuk menerima submissionId dan juriId
            composable(
                route = "penilaian_screen/{submissionId}/{juriId}",
                arguments = listOf(
                    navArgument("submissionId") { type = NavType.StringType },
                    navArgument("juriId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val submissionId = backStackEntry.arguments?.getString("submissionId")
                val juriId = backStackEntry.arguments?.getString("juriId")

                if (submissionId != null && juriId != null) {
                    // Berikan juriId ke PenilaianScreen dan HAPUS profileViewModel
                    PenilaianScreen(
                        submissionId = submissionId,
                        juriId = juriId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}