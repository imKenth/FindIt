package com.findit.app.ui.screens

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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.findit.app.ui.navigation.Routes
import com.findit.app.ui.theme.*

@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

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

            FormField(label = "Full Name", required = true) {
                OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("Alex Rivera", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true)
            }
            Spacer(modifier = Modifier.height(16.dp))
            FormField(label = "School Email", required = true) {
                OutlinedTextField(value = email, onValueChange = { email = it }, placeholder = { Text("you@school.edu", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true)
            }
            Spacer(modifier = Modifier.height(16.dp))
            FormField(label = "Password", required = true) {
                OutlinedTextField(value = password, onValueChange = { password = it }, placeholder = { Text("Min. 8 characters", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true)
            }
            Spacer(modifier = Modifier.height(16.dp))
            FormField(label = "Confirm Password", required = true) {
                OutlinedTextField(value = confirm, onValueChange = { confirm = it }, placeholder = { Text("Repeat your password", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "By creating an account you agree to our Terms of Service and Privacy Policy.",
                fontSize = 12.sp, color = Gray400, lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } } },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Violet600),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily)
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
