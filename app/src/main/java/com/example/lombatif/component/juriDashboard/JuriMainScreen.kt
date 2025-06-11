package com.example.lombatif.component.juriDashboard

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lombatif.viewModels.ViewProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuriMainScreen() {
    val navController = rememberNavController()
    val screens = listOf(JuriScreen.Dashboard, JuriScreen.Submissions)
    val profileViewModel: ViewProfile = viewModel()
    val onLogout: () -> Unit = { // <-- TAMBAHKAN TIPE SECARA EKSPLISIT
        Log.d("JuriMainScreen", "Logout diklik!")
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
                // Baris ini memanggil fungsi di bawah
                JuriDashboardScreen(
                    onLogout = onLogout,
                    profileViewModel = profileViewModel
                )
            }
            composable(JuriScreen.Submissions.route) { SubmissionListScreen() }
        }
    }
}