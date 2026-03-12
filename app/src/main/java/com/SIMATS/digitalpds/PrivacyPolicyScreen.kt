package com.SIMATS.digitalpds

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBackClick: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FBFF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                "Last Updated: February 2024",
                fontSize = 14.sp,
                color = TextGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            PolicySection(
                title = "1. Information We Collect",
                content = "We collect information you provide directly to us, such as when you create or modify your account, request kits, or contact support. This includes name, email, phone number, and location data relevant to the distribution of oral kits."
            )
            
            PolicySection(
                title = "2. How We Use Information",
                content = "We use the information to manage the Digital PDS program, process stock requests, track kit distributions to beneficiaries, and improve our services. Your data helps ensure that resources reach the intended communities efficiently."
            )
            
            PolicySection(
                title = "3. Data Sharing",
                content = "Your information is only shared with authorized personnel within the Digital PDS program. We do not sell your personal data to third parties. Statistical, non-identifiable data may be used for program reporting."
            )
            
            PolicySection(
                title = "4. Data Security",
                content = "We implement industry-standard security measures to protect your data. However, no method of transmission over the internet is 100% secure, and we cannot guarantee absolute security."
            )
            
            PolicySection(
                title = "5. Your Rights",
                content = "You have the right to access, correct, or delete your personal information within the app settings. For further assistance, contact our administration office."
            )
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
        Spacer(modifier = Modifier.height(8.dp))
        Text(content, fontSize = 15.sp, color = TextGray, lineHeight = 22.sp)
    }
}
