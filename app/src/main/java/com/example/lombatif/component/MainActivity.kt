package com.example.lombatif.component

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lombatif.R
import com.example.lombatif.component.adminDashboard.MainDashboard

import com.example.lombatif.ui.theme.LombaTIFTheme
import com.example.lombatif.viewModels.ViewLogin

import com.example.lombatif.viewModels.ViewProfile


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewmodel = ViewModelProvider(this)[ViewLogin::class.java]
        enableEdgeToEdge()
        setContent {
            LombaTIFTheme {
                Login(viewmodel) {
                    finish()
                }
            }
        }
    }
}

@Composable
fun Login(viewLogin: ViewLogin = viewModel(), onLoginSuccess: () -> Unit) {
    val loginState = viewLogin.loginState
    val viewProfile: ViewProfile = viewModel()
    val profileState by viewProfile.profile.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Reset state saat pertama kali masuk ke layar login
    LaunchedEffect(Unit) {
        viewLogin.resetState()
        viewProfile.stateUI = ""
    }

    var hasFetchedProfile by remember { mutableStateOf(false) }

    // Efek saat loginState berubah
    LaunchedEffect(loginState.value) {
        when (val state = loginState.value) {
            is ViewLogin.LoginState.Success -> {
                if (!hasFetchedProfile) {
                    viewProfile.fetchProfile()
                    hasFetchedProfile = true
                }
            }
            is ViewLogin.LoginState.Error -> {
                showDialog.value = true
            }
            else -> {}
        }
    }

    // Efek saat profileState berhasil diambil
    LaunchedEffect(profileState) {
        if (profileState != null && hasFetchedProfile) {
            val role = profileState?.profile?.role ?: ""
            when (role) {
                "ADMIN" -> {
                    context.startActivity(Intent(context, MainDashboard::class.java))
                    onLoginSuccess()
                }
                "USERS" -> {
                    context.startActivity(Intent(context, DaftarLomba::class.java))
                    onLoginSuccess()
                }
                "PESERTA" -> {
                    context.startActivity(Intent(context, DashBoardPeserta::class.java))
                    onLoginSuccess()
                }
                else -> {
                    showDialog.value = true
                    viewLogin.resetState()
                    hasFetchedProfile = false
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                text = "Competition Management Platform",
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1C3ED3),
                        focusedTextColor = Color.Black,
                        focusedPlaceholderColor = Color(0xFF000000)
                    )
                )

                Spacer(modifier = Modifier.height(25.dp))

                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewLogin.postLogin(email, password, context)
                        }
                    },
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
                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    buildAnnotatedString {
                        append("Don't have an account yet? ")
                        withStyle(
                            style = SpanStyle(
                                color = Color.Blue,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Sign Up")
                        }
                    },
                    fontSize = 15.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        val intent = Intent(context, Registrasi::class.java)
                        context.startActivity(intent)
                    }
                )

                // Handle login error state
                when (val state = loginState.value) {
                    is ViewLogin.LoginState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is ViewLogin.LoginState.Error -> {
                        LaunchedEffect(state) {
                            showDialog.value = true
                        }
                        ErrorDialog(
                            message = state.message,
                            showDialog = showDialog,
                            onDismiss = {
                                showDialog.value = false
                                viewLogin.resetState()
                            }
                        )
                    }
                    else -> {}
                }

                // Handle profile error
                if (viewProfile.stateUI.isNotEmpty()) {
                    LaunchedEffect(viewProfile.stateUI) {
                        showDialog.value = true
                    }
                    ErrorDialog(
                        message = viewProfile.stateUI,
                        showDialog = showDialog,
                        onDismiss = {
                            showDialog.value = false
                            viewProfile.stateUI = ""
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorDialog(
    message: String,
    showDialog: MutableState<Boolean>,
    onDismiss: () -> Unit = { showDialog.value = false }
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("OK")
                }
            },
            title = {
                Text(
                    text = "Login Gagal",
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = message,
                    color = Color.White
                )
            },
            containerColor = Color(0xFFD32F2F),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}




