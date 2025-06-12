package com.example.lombatif.component.adminDashboard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class StatCardData(
    val title: String,
    val count: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val contentColor: Color
)
