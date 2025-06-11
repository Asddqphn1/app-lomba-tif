package com.example.lombatif.component.juriDashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class JuriScreens(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : JuriScreen("dashboard", "Dashboard", Icons.Default.Home)
    object Submissions : JuriScreen("submissions", "Submissions", Icons.Default.List)
}