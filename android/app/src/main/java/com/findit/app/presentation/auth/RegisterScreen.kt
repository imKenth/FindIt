package com.findit.app.presentation.auth

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.findit.app.navigation.Routes
import com.findit.app.ui.theme.*

@Composable
fun RegisterScreen(navController: NavController) {

    val viewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(com.findit.app.di.AppModule.authRepository) as T
            }
        }
    )

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success){
            if (currentUser?.email == "admin@school.edu") {
                navController.navigate(Routes.ADMIN) {
                    popUpTo(Routes.REGISTER) { inclusive = true}
                }
            } else {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.REGISTER) { inclusive = true}
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Violet700)
                .padding(bottom = 64.dp)
        ) {
            Box(modifier = Modifier.offset(x = 100.dp, y = (-40).dp).size(160.dp).clip(RoundedCornerShape(50)).background(Violet600.copy(alpha = 0.5f)))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Fi", fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Violet600)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Join FindIt", fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = White)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Create your school account", fontSize = 14.sp, color = Violet200)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(White)
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Text("Create Account", fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
            Spacer(modifier = Modifier.height(2.dp))
            Text("Join your school's lost & found community", fontSize = 14.sp, color = Gray400)
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

            FormField(label = "Full Name", required = true) {
                OutlinedTextField(value = name, onValueChange = { name = it; validationError = null }, placeholder = { Text("Alex Rivera", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true)
            }
            Spacer(modifier = Modifier.height(16.dp))
            FormField(label = "School Email", required = true) {
                OutlinedTextField(value = email, onValueChange = { email = it; validationError = null }, placeholder = { Text("you@school.edu", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true)
            }
            Spacer(modifier = Modifier.height(16.dp))
            FormField(label = "Password", required = true) {
                OutlinedTextField(value = password, onValueChange = { password = it; validationError = null }, placeholder = { Text("Min. 8 characters", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true)
            }
            Spacer(modifier = Modifier.height(16.dp))
            FormField(label = "Confirm Password", required = true) {
                OutlinedTextField(value = confirm, onValueChange = { confirm = it; validationError = null }, placeholder = { Text("Repeat your password", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "By creating an account you agree to our Terms of Service and Privacy Policy.",
                fontSize = 12.sp, color = Gray400, lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        validationError = "Please fill all required fields"
                    } else if (password != confirm) {
                        validationError = "Passwords do not match"
                    } else if (password.length < 8) {
                        validationError = "Password must be at least 8 characters"
                    } else {
                        viewModel.signUp(name, email, password)
                    }
                },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Violet600),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily)
                }

            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Already have an account? ", fontSize = 14.sp, color = Gray400)
                TextButton(onClick = { navController.navigate(Routes.LOGIN) }) {
                    Text("Sign In", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Violet600)
                }
            }
        }
    }
}

@Composable
private fun FormField(label: String, required: Boolean, content: @Composable () -> Unit) {
    Column {
        Text(
            text = "$label ${if (required) "*" else ""}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Gray700
        )
        Spacer(modifier = Modifier.height(6.dp))
        content()
    }
}
