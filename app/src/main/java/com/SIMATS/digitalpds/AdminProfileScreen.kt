package com.SIMATS.digitalpds

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsConditionsClick: () -> Unit = {},
    onHelpSupportClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    notificationsEnabled: Boolean = true,
    onNotificationsToggle: (Boolean) -> Unit = {},
    adminName: String = "Super Admin",
    adminEmail: String = "admin@digitalpds.gov.in",
    adminPhone: String = "+91 98765 43210",
    adminId: Int = 0,
    adminLocation: String = "Central Headquarters"
) {
    val primaryRed = Color(0xFFD32F2F)

    Scaffold(
        bottomBar = {
            AdminBottomNavigationBar(currentScreen = "Profile", onNavigate = onNavigate)
        },
        containerColor = Color(0xFFF8FBFF)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Premium Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryRed, primaryRed.copy(alpha = 0.85f), primaryRed.copy(alpha = 0.6f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TopAppBar(
                    title = { Text("Admin Profile", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Profile Card Overlapping
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x26000000)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFEBEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = adminName.firstOrNull()?.toString()?.uppercase() ?: "A",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryRed
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(adminName, fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextBlack)
                            Surface(
                                color = Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(
                                    "ADMIN",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryRed
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(adminEmail, fontSize = 14.sp, color = TextGray)
                            Text(adminPhone, fontSize = 14.sp, color = TextGray)
                            Text("ID: AD-${if (adminId > 0) adminId else "7721"}", fontSize = 12.sp, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
                        }
                    }

                    // Account Details Section
                    ProfileSection(title = "Account Details") {
                        ProfileInfoRow(label = "Full Name", value = adminName)
                        ProfileInfoRow(label = "Email Address", value = adminEmail)
                        ProfileInfoRow(label = "Phone Number", value = adminPhone)
                        ProfileInfoRow(label = "Region", value = adminLocation)
                    }

                    // Support Section
                    ProfileSection(title = "Support & Privacy") {
                        ProfileMenuRow(icon = Icons.Default.PrivacyTip, label = "Privacy Policy", iconBg = Color(0xFFF3E5F5), iconTint = Color(0xFF7B1FA2), onClick = onPrivacyPolicyClick)
                        ProfileMenuRow(icon = Icons.Default.Description, label = "Terms & Conditions", iconBg = Color(0xFFE0F7FA), iconTint = Color(0xFF00838F), onClick = onTermsConditionsClick)
                    }

                    // Logout Button
                    Button(
                        onClick = onLogoutClick,
                        modifier = Modifier.fillMaxWidth().height(56.dp).shadow(2.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryRed, contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Logout Session", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1A000000)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text(label, fontSize = 12.sp, color = TextGray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextBlack)
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF3F4F6), thickness = 1.dp)
}

@Composable
private fun ProfileMenuRow(icon: ImageVector, label: String, value: String? = null, iconBg: Color, iconTint: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = iconBg
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = iconTint)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextBlack, modifier = Modifier.weight(1f))
        if (value != null) {
            Text(value, fontSize = 14.sp, color = TextGray, modifier = Modifier.padding(end = 8.dp))
        }
        Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.LightGray)
    }
}

@Composable
private fun ProfileToggleRow(icon: ImageVector, label: String, iconBg: Color, iconTint: Color, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = iconBg
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = iconTint)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextBlack, modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White, 
                checkedTrackColor = Color(0xFFD32F2F),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminProfileScreenPreview() {
    AdminProfileScreen()
}
