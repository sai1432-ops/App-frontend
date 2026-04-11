package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealerHelpSupportScreen(
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {
        // Top Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DealerGreen, BackgroundWhite)
                    )
                )
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Help & Support",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "How can we help you?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Text(
                "Contact us through any of these channels",
                fontSize = 14.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            SupportChannelItem(
                icon = Icons.Default.Call,
                title = "Customer Care",
                subtitle = "Available 24/7 for urgent queries",
                onClick = {}
            )
            SupportChannelItem(
                icon = Icons.Default.Email,
                title = "Email Support",
                subtitle = "support@digitalpds.gov.in",
                onClick = {}
            )
            SupportChannelItem(
                icon = Icons.AutoMirrored.Filled.HelpCenter,
                title = "FAQs",
                subtitle = "Find quick answers to common questions",
                onClick = {}
            )
            SupportChannelItem(
                icon = Icons.Default.Description,
                title = "User Manual",
                subtitle = "Detailed guide for dealer operations",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "System Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("App Version", color = TextGray)
                Text("v2.4.1 (Stable)", fontWeight = FontWeight.Bold, color = TextBlack)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Device Status", color = TextGray)
                Text("Verified", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
}

@Composable
fun SupportChannelItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0D000000))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(DealerSecondary),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = DealerGreen)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextBlack)
            Text(subtitle, fontSize = 12.sp, color = TextGray)
        }
        Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = TextGray)
    }
}
}

@Preview(showBackground = true)
@Composable
fun DealerHelpSupportScreenPreview() {
    DigitalpdsTheme {
        DealerHelpSupportScreen(onBackClick = {})
    }
}
