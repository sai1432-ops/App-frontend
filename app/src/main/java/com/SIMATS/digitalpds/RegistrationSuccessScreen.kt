package com.SIMATS.digitalpds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
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
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F7FB))) {
        // Aesthetic Top Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(DealerGreen, Color(0xFFF4F7FB))
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                AppBottomNavigationBar(currentScreen = "Beneficiaries", onNavigate = { screen ->
                    when (screen) {
                        "Home" -> onHomeClick()
                        "Beneficiary" -> { /* Already here */ }
                        "Stock" -> onStockClick()
                        "Profile" -> onProfileClick()
                    }
                })
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Success Lottie-like Icon
                Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(120.dp),
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = DealerGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Registration Successful!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    color = TextBlack
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    "The household has been registered and verified successfully in the Digital PDS system.",
                    fontSize = 15.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Household ID Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "ASSIGNED HOUSEHOLD ID",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DealerGreen,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                householdId,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = TextBlack,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 16.dp),
                                letterSpacing = 2.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            "Save this ID for future distributions.",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Default Password Notice
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF9C4), // Light Yellow
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBC02D))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFF57F17),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Default Password",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D4037)
                            )
                            Text(
                                "The initial password for this account is 'welcome@123'.",
                                fontSize = 13.sp,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action Buttons
                Button(
                    onClick = onHomeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DealerGreen)
                ) {
                    Text("Return to Dashboard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onRegisterAnotherClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DealerGreen)
                ) {
                    Text("Register Another Household", color = DealerGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
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
