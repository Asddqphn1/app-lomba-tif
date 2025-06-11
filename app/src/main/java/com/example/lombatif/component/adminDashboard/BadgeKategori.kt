package com.example.lombatif.component.adminDashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BadgeKategori(kategori: String) {
    val bgColor = when (kategori.uppercase()) {
        "TIM" -> Color(0xFFFFA500)
        "INDIVIDU" -> Color(0xFF2196F3)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = kategori.uppercase(),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
