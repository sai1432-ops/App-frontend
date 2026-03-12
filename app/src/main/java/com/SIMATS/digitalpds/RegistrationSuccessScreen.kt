package com.SIMATS.digitalpds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationSuccessScreen(
    householdId: String = "#HH-98210",
    onBackClick: () -> Unit,
    onViewDashboardClick: () -> Unit,
    onRegisterAnotherClick: () -> Unit,
    onHomeClick: () -> Unit,
    onBeneficiariesClick: () -> Unit,
    onStockClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registration Successful", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        bottomBar = {
            AppBottomNavigationBar(currentScreen = "Beneficiaries", onNavigate = { screen ->
                when (screen) {
                    "Home" -> onHomeClick()
                    "Beneficiary" -> onBeneficiariesClick()
                    "Stock" -> onStockClick()
                    "Profile" -> onProfileClick()
                }
            })
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F7F7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Registration Successful!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Household ID Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("New Household ID", fontSize = 14.sp, color = TextGray)
                    Text(householdId, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Text("Copy", fontSize = 14.sp, color = PrimaryBlue, fontWeight = FontWeight.Medium)
                }
                Image(
                    painter = painterResource(id = R.drawable.howp), // Placeholder for the map icon
                    contentDescription = null,
                    modifier = Modifier.size(80.dp, 60.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Kits Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Kits Allocated: 4 Units", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Text("Ration Card Status: Linked & Verified", fontSize = 14.sp, color = TextGray)
                }
                Box(
                    modifier = Modifier
                        .size(80.dp, 60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F7F7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📦", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE9F1F1))
            ) {
                Text("Download PDF Receipt", color = TextBlack, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onHomeClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE9F1F1))
            ) {
                Text("Back to Home", color = TextBlack, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onViewDashboardClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("View Household Dashboard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onRegisterAnotherClick) {
                Text("Register Another Household", color = TextGray, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationSuccessScreenPreview() {
    DigitalpdsTheme {
        RegistrationSuccessScreen(
            onBackClick = {},
            onViewDashboardClick = {},
            onRegisterAnotherClick = {},
            onHomeClick = {},
            onBeneficiariesClick = {},
            onStockClick = {},
            onProfileClick = {}
        )
    }
}
