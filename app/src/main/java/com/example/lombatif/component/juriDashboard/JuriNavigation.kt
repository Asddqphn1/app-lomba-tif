// Lokasi file: app/src/main/java/com/example/lombatif/component/juriDashboard/JuriNavigation.kt

package com.example.lombatif.component.juriDashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class ini mendefinisikan semua layar yang ada di dalam
 * bagian Juri beserta propertinya (route, title, icon).
 * Ini membuat navigasi menjadi type-safe dan terpusat.
 */
sealed class JuriScreen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : JuriScreen(
        route = "juri_dashboard",
        title = "Dashboard",
        icon = Icons.Default.Home
    )
    object Submissions : JuriScreen(
        route = "juri_submissions",
        title = "Submissions",
        icon = Icons.Default.List
    )
}