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
fun TermsConditionsScreen(onBackClick: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms & Conditions", fontWeight = FontWeight.Bold) },
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
                "Welcome to Digital PDS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Please read these terms and conditions carefully before using our application.",
                fontSize = 14.sp,
                color = TextGray,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            TermsSection(
                title = "1. Acceptance of Terms",
                content = "By accessing and using this application, you accept and agree to be bound by the terms and provision of this agreement. Any participation in this service will constitute acceptance of this agreement."
            )
            
            TermsSection(
                title = "2. User Conduct",
                content = "As an administrator or dealer, you are responsible for maintaining the confidentiality of your account and password. You agree to accept responsibility for all activities that occur under your account."
            )
            
            TermsSection(
                title = "3. Distribution Guidelines",
                content = "All kit distributions must be recorded accurately in the system. Misuse of the platform or falsification of distribution records may lead to account suspension or legal action."
            )
            
            TermsSection(
                title = "4. Program Integrity",
                content = "The Digital PDS program is designed to provide oral care resources to beneficiaries. Users must use the system for its intended purpose and not for any unauthorized commercial gains."
            )
            
            TermsSection(
                title = "5. Termination",
                content = "We reserve the right to terminate or suspend access to our service immediately, without prior notice or liability, for any reason whatsoever, including without limitation if you breach the Terms."
            )
        }
    }
}

@Composable
private fun TermsSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
        Spacer(modifier = Modifier.height(8.dp))
        Text(content, fontSize = 15.sp, color = TextGray, lineHeight = 22.sp)
    }
}
