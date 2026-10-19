package com.findit.app.presentation.found

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.findit.app.domain.model.ItemType
import com.findit.app.navigation.Routes
import com.findit.app.presentation.report.ReportState
import com.findit.app.presentation.report.ReportViewModel
import com.findit.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFoundScreen(navController: NavController) {
    val viewModel: ReportViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ReportViewModel(com.findit.app.di.AppModule.itemRepository) as T
            }
        }
    )

    val authViewModel: com.findit.app.presentation.auth.AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return com.findit.app.presentation.auth.AuthViewModel(com.findit.app.di.AppModule.authRepository) as T
            }
        }
    )

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val reportState by viewModel.reportState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(reportState) {
        when (reportState) {
            is ReportState.Error -> {
                errorMessage = (reportState as ReportState.Error).message
                showError = true
            }
            else -> {}
        }
    }

    if (reportState is ReportState.Success) {
        Column(
            modifier = Modifier.fillMaxSize().background(White).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(50)).background(Green100), contentAlignment = Alignment.Center) {
                Text("✔", fontSize = 40.sp, color = Green500)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Report Submitted!", fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Thank you for helping! The owner has been notified. We'll match it to lost reports automatically.", fontSize = 14.sp, color = Gray500, lineHeight = 20.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.navigate(Routes.HOME) { popUpTo(0) } }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Violet600)) {
                Text("Back to Home", fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily)
            }
        }
        return
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = { navController.navigate(Routes.REPORT_TYPE) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gray600) }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Report Found Item", fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                    Text("Help someone get their item back", fontSize = 13.sp, color = Gray500)
                }
            }
        },
        containerColor = White
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            FormField("Item Name", true) { OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("e.g., Blue JanSport Backpack", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true) }
            FormField("Category", true) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select category", color = Gray400) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("Electronics", "IDs", "Bags", "Wallets", "Keys", "Books", "Clothing", "Other").forEach { item ->
                            DropdownMenuItem(text = { Text(item) }, onClick = { category = item; expanded = false })
                        }
                    }
                }
            }
            FormField("Location Found", true) { OutlinedTextField(value = location, onValueChange = { location = it }, placeholder = { Text("e.g., Library, 2nd Floor", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true) }
            FormField("Date Found", true) { OutlinedTextField(value = date, onValueChange = { date = it }, placeholder = { Text("e.g., Jan 15, 2026", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true) }
            FormField("Description", true) { OutlinedTextField(value = description, onValueChange = { description = it }, placeholder = { Text("Describe the item in detail: color, brand, distinguishing marks, contents...", color = Gray400) }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), maxLines = 4) }
            
            Column {
                Text("Add Photo", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray700)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(110.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (imageUri != null) Violet400 else Gray200)
                ) {
                    if (imageUri != null) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Selected Photo",
                                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column {
                                Text("Photo Attached", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Violet600)
                                Text("Tap to change photo", fontSize = 12.sp, color = Gray400)
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📷", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tap to add photo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Gray400)
                            Text("Optional but helps with identification", fontSize = 12.sp, color = Gray400)
                        }
                    }
                }
            }

            if (showError) {
                Text(
                    text = errorMessage,
                    color = Red500,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    val finalCategory = category.ifBlank { "Other" }
                    val finalDate = date.ifBlank { "Today" }
                    val finalDesc = description.ifBlank { "No detailed description provided." }

                    if (name.isBlank()) {
                        errorMessage = "Please enter an Item Name"
                        showError = true
                    } else if (location.isBlank()) {
                        errorMessage = "Please enter a Location Found"
                        showError = true
                    } else {
                        showError = false
                        viewModel.reportItem(
                            name = name,
                            category = finalCategory,
                            location = location,
                            date = finalDate,
                            time = null,
                            description = finalDesc,
                            type = ItemType.FOUND,
                            reportedBy = currentUser?.name ?: "Unknown User"
                        )
                    }
                },
                enabled = reportState !is ReportState.Loading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green500),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (reportState is ReportState.Loading) {
                    CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Submit Found Report", fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily)
                }
            }

            if (showError) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    color = Red500,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun FormField(label: String, required: Boolean, content: @Composable () -> Unit) {
    Column {
        Text(text = "$label ${if (required) "*" else ""}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray700)
        Spacer(modifier = Modifier.height(6.dp))
        content()
    }
}
