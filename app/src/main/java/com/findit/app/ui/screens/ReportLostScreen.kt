package com.findit.app.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.findit.app.data.model.ItemCategory
import com.findit.app.ui.navigation.Routes
import com.findit.app.ui.theme.*

@Composable
fun ReportLostScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(value = false) }

    if (submitted) {
        Column(
            modifier = Modifier.fillMaxSize().background(White).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(50)).background(Green100), contentAlignment = Alignment.Center) {
                Text("\u2714", fontSize = 40.sp, color = Green500)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Report Submitted!", fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Your lost item report is now live. We'll notify you if someone finds a match.", fontSize = 14.sp, color = Gray500, lineHeight = 20.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.navigate(Routes.HOME) { popUpTo(0) } }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Violet600)) {
                Text("Back to Home", fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily)
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = { navController.navigate(Routes.MY_REPORTS) }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp)) {
                Text("View My Reports", fontWeight = FontWeight.Bold)
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
                    Text("Report Lost Item", fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                    Text("Lost", fontSize = 12.sp, color = Red500, fontWeight = FontWeight.SemiBold)
                }
                Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(12.dp)).background(Red100), contentAlignment = Alignment.Center) {
                    Text("\u2716", fontSize = 16.sp, color = Red500)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(White).padding(padding).padding(horizontal = 20.dp, vertical = 20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FormField("Item Name", true) { OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("e.g. \"iPhone 14 Pro\"", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true) }
            FormField("Category", true) { OutlinedTextField(value = category, onValueChange = { category = it }, placeholder = { Text("Select a category", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true) }
            FormField("Last Known Location", true) { OutlinedTextField(value = location, onValueChange = { location = it }, placeholder = { Text("e.g. \"Library, 2nd Floor\"", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true) }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) { FormField("Date Lost", true) { OutlinedTextField(value = date, onValueChange = { date = it }, placeholder = { Text("mm/dd/yyyy", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true) } }
                Column(modifier = Modifier.weight(1f)) { FormField("Approx. Time", false) { OutlinedTextField(value = time, onValueChange = { time = it }, placeholder = { Text("hh:mm", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true) } }
            }
            FormField("Description", true) { OutlinedTextField(value = description, onValueChange = { description = it }, placeholder = { Text("Describe the item in detail: color, brand, distinguishing marks, contents...", color = Gray400) }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), maxLines = 4) }
            Column {
                Text("Add Photo", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray700)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Gray200)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\uD83D\uDCF7", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap to add photo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Gray400)
                        Text("Optional but helps with identification", fontSize = 12.sp, color = Gray400)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { submitted = true }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Red500), elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)) {
                Text("Submit Lost Report", fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily)
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
