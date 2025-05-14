package com.example.lombatif.component

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lombatif.R

import com.example.lombatif.ui.theme.LombaTIFTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LombaTIFTheme {
                Login()
            }
        }
    }
}

@Composable
fun Login(){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    Box (modifier = Modifier.fillMaxSize()) {
        Column (modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(60.dp))

            Image(
                painter = painterResource(id = R.drawable.logotiflomba),
                contentDescription = "logoAPP",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)

            )

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "LombaTIF",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Competition Management Platfrom",
                fontSize = 15.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Welcome Back",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Please Login to Your Account",
                fontSize = 15.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // EMAIL
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1C3ED3),
                        focusedTextColor = Color.Black,
                        focusedPlaceholderColor = Color(0xFF000000)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // PASSWORD
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1C3ED3),
                        focusedTextColor = Color.Black,
                        focusedPlaceholderColor = Color(0xFF000000)
                    )
                )

                Spacer(modifier = Modifier.height(25.dp))

                Button(
                    onClick = ({}),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C3ED3)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Login",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Show(){
    Login()
}



