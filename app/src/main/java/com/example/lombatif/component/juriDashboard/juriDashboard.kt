// file: component/juriDashboard/juriDashboard.kt

package com.example.lombatif.component.juriDashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.lombatif.ui.theme.LombaTIFTheme

class juriDashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LombaTIFTheme {
                // Panggil Composable utama yang akan berisi Scaffold dan Navigasi
                JuriMainScreen()
            }
        }
    }
}