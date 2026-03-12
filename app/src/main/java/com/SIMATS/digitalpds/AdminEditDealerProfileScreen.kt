package com.SIMATS.digitalpds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditDealerProfileScreen(
    dealer: DealerInfo,
    onBackClick: () -> Unit,
    onSaveClick: (DealerInfo) -> Unit
) {
    var name by remember { mutableStateOf(dealer.name) }
    var handle by remember { mutableStateOf(dealer.handle) }
    var location by remember { mutableStateOf(dealer.location) }
    var phone by remember { mutableStateOf(dealer.phone) }

    var centerName by remember { mutableStateOf("Sector 4 PDS Center") }
    var licenseNo by remember { mutableStateOf("LIC-45667") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Dealer Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = Color(0xFFF8FBFF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(120.dp)) {
                Image(
                    painter = painterResource(id = dealer.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            EditField(label = "Full Name", value = name, onValueChange = { name = it })
            Spacer(modifier = Modifier.height(16.dp))

            EditField(label = "Username", value = handle, onValueChange = { handle = it })
            Spacer(modifier = Modifier.height(16.dp))

            EditField(label = "Phone Number", value = phone, onValueChange = { phone = it })
            Spacer(modifier = Modifier.height(16.dp))

            EditField(label = "Location", value = location, onValueChange = { location = it })
            Spacer(modifier = Modifier.height(16.dp))

            EditField(label = "PDS Center Name", value = centerName, onValueChange = { centerName = it })
            Spacer(modifier = Modifier.height(16.dp))

            EditField(label = "License Number", value = licenseNo, onValueChange = { licenseNo = it })

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    onSaveClick(
                        dealer.copy(
                            name = name,
                            handle = handle,
                            location = location,
                            phone = phone
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun EditField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}