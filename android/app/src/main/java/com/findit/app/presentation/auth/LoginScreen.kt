package com.findit.app.presentation.auth

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.findit.app.data.repository.FirebaseAuthRepositoryImpl
import com.findit.app.navigation.Routes
import com.findit.app.ui.theme.*

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(FirebaseAuthRepositoryImpl()) as T
            }
        }
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(value = false) }

    val authState by viewModel.authState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            if (currentUser?.email == "admin@school.edu") {
                navController.navigate(Routes.ADMIN) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            } else {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState()),
    ) {
        // Violet hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Violet700)
                .padding(bottom = 64.dp)
        ) {
            Box(modifier = Modifier.offset(x = 100.dp, y = (-48).dp).size(176.dp).clip(RoundedCornerShape(50)).background(Violet600.copy(alpha = 0.5f)))
            Box(modifier = Modifier.offset(x = (-80).dp, y = 40.dp).size(144.dp).clip(RoundedCornerShape(50)).background(Violet800.copy(alpha = 0.4f)))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Fi", fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Violet600)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("FindIt", fontSize = 28.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = White)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Welcome back \uD83D\uDC4B", fontSize = 14.sp, color = Violet200)
            }
        }

        // Form card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(White)
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Text("Sign In", fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
            Spacer(modifier = Modifier.height(2.dp))
            Text("Use your school account credentials", fontSize = 14.sp, color = Gray400)
            Spacer(modifier = Modifier.height(28.dp))

            var validationError by remember { mutableStateOf<String?>(null) }

            if (authState is AuthState.Error || validationError != null) {
                Text(
                    text = validationError ?: (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Email field
            Text("Email Address", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray700)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; validationError = null },
                placeholder = { Text("you@school.edu", color = Gray400) },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = Gray400) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Gray200,
                    focusedBorderColor = Violet400,
                    unfocusedContainerColor = Gray50,
                    focusedContainerColor = Gray50
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            Text("Password", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray700)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; validationError = null },
                placeholder = { Text("\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022", color = Gray400) },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = Gray400) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null, tint = Gray400
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Gray200,
                    focusedBorderColor = Violet400,
                    unfocusedContainerColor = Gray50,
                    focusedContainerColor = Gray50
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = {}) {
                    Text("Forgot Password?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Violet600)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sign In button
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        validationError = "Please enter both email and password"
                    } else {
                        viewModel.signIn(email, password)
                    }
                },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Violet600),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("New to FindIt? ", fontSize = 14.sp, color = Gray400)
                TextButton(onClick = { navController.navigate(Routes.REGISTER) }) {
                    Text("Create Account", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Violet600)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            // Admin hint
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Amber50)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Admin Access", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Amber800)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sign in with admin@school.edu to open the Admin Panel",
                        fontSize = 12.sp, color = Amber700, textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
