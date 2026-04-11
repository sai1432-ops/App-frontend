package com.SIMATS.digitalpds

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealerProfileScreen(
    dealerName: String = "Dealer Name",
    dealerEmail: String = "dealer@example.com",
    dealerPhone: String = "+91 00000 00000",
    dealerId: Int = 0,
    companyName: String = "",
    address: String = "",
    city: String = "",
    state: String = "",
    pincode: String = "",
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onChangePasswordClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // Enhanced Gradient Header Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DealerGreen,
                            DealerGreen.copy(alpha = 0.8f),
                            BackgroundWhite
                        )
                    )
                )
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Dealer Profile",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                AppBottomNavigationBar(currentScreen = "Profile", onNavigate = onNavigate)
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Modern Profile Image Section (Initials Avatar)
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .size(130.dp)
                            .shadow(24.dp, CircleShape, spotColor = DealerGreen.copy(alpha = 0.3f)),
                        shape = CircleShape,
                        border = BorderStroke(4.dp, Color.White),
                        color = SurfaceLight
                    ) {
                        InitialsAvatar(
                            name = dealerName,
                            modifier = Modifier.size(126.dp),
                            fontSize = 48.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Name and Status Badge
                Text(
                    text = dealerName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = TextBlack,
                    letterSpacing = (-0.5).sp
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFE8F5E9),
                        border = BorderStroke(1.dp, Color(0xFFC8E6C9))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "OFFICIAL DEALER",
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "ID: D-${if (dealerId > 0) dealerId else "9824"}",
                        fontSize = 14.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Modern Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, SurfaceLight)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        InfoRow(
                            icon = Icons.Default.Email,
                            label = "Business Email",
                            value = dealerEmail,
                            iconColor = DealerGreen
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = SurfaceLight
                        )
                        InfoRow(
                            icon = Icons.Default.Phone,
                            label = "Contact Number",
                            value = dealerPhone,
                            iconColor = DealerAccent
                        )
                    }
                }

                if (companyName.isNotBlank() || address.isNotBlank()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Business Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = BorderStroke(1.dp, SurfaceLight)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            if (companyName.isNotBlank()) {
                                InfoRow(
                                    icon = Icons.Default.Business,
                                    label = "Company Name",
                                    value = companyName,
                                    iconColor = DealerGreen
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    color = SurfaceLight
                                )
                            }
                            
                            val fullAddress = listOfNotNull(
                                address.takeIf { it.isNotBlank() },
                                city.takeIf { it.isNotBlank() },
                                state.takeIf { it.isNotBlank() },
                                pincode.takeIf { it.isNotBlank() }
                            ).joinToString(", ")
                            
                            if (fullAddress.isNotBlank()) {
                                InfoRow(
                                    icon = Icons.Default.LocationOn,
                                    label = "Location",
                                    value = fullAddress,
                                    iconColor = DealerAccent
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Settings Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Account Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextBlack
                    )
                    Text(
                        "Manage",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DealerGreen
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Refined Settings Items
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    subtitle = "Modify your store and contact info",
                    onClick = { onNavigate("dealer_edit_profile") }
                )
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Change Password",
                    subtitle = "Update your account password",
                    onClick = onChangePasswordClick
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Premium Log Out Button
                Surface(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFFEBEE),
                    border = BorderStroke(1.dp, Color(0xFFFFCDD2))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Logout from Device",
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    "DigitalPDS Version 2.4.0",
                    fontSize = 12.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color = DealerAccent
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextGray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextBlack
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, SurfaceLight)
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
                    .clip(RoundedCornerShape(14.dp))
                    .background(DealerSecondary.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DealerGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextBlack
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextGray.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DealerProfileScreenPreview() {
    DigitalpdsTheme {
        DealerProfileScreen(
            onBackClick = {}, 
            onNavigate = {}, 
            onLogoutClick = {}, 
            onChangePasswordClick = {}
        )
    }
}
