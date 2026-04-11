package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.network.AdminDashboardStats
import com.SIMATS.digitalpds.network.AdminNotification
import com.SIMATS.digitalpds.ui.theme.*

@Composable
fun AdminDashboardScreen(
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    adminViewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val token = remember { SessionManager(context).getAccessToken() ?: "" }

    LaunchedEffect(Unit) {
        adminViewModel.fetchDashboardStats(token)
    }

    AdminDashboardContent(
        stats = adminViewModel.dashboardStats,
        isLoading = adminViewModel.isLoading,
        errorMessage = adminViewModel.errorMessage,
        onProfileClick = onProfileClick,
        onNavigate = onNavigate,
        onRetry = { adminViewModel.fetchDashboardStats(token) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    stats: AdminDashboardStats?,
    isLoading: Boolean,
    errorMessage: String?,
    onProfileClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onRetry: () -> Unit = {}
) {
    var showTechnicalDetails by remember { mutableStateOf(false) }
    val currentTab = "Home"
    val primaryRed = Color(0xFFD32F2F)

    Scaffold(
        bottomBar = {
            AdminBottomNavigationBar(currentScreen = currentTab, onNavigate = onNavigate)
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
                // Top App Bar
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Admin Dashboard",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )

                if (isLoading && stats == null) {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (stats != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Stats Cards Grid
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            AdminStatCard(
                                title = "Total Dealers",
                                value = stats.totalDealers ?: "0",
                                icon = Icons.Default.Storefront,
                                iconBg = Color(0xFFE3F2FD),
                                iconTint = Color(0xFF1976D2),
                                onClick = { onNavigate("Dealers") },
                                modifier = Modifier.weight(1f)
                            )
                            AdminStatCard(
                                title = "Beneficiaries",
                                value = stats.activeBeneficiaries ?: "0",
                                icon = Icons.Default.Groups,
                                iconBg = Color(0xFFE8F5E9),
                                iconTint = Color(0xFF2E7D32),
                                onClick = { onNavigate("Beneficiaries") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            AdminStatCard(
                                title = "Distributions",
                                value = stats.totalDistributions ?: "0",
                                icon = Icons.Default.LocalShipping,
                                iconBg = Color(0xFFF3E5F5),
                                iconTint = Color(0xFF7B1FA2),
                                onClick = { onNavigate("Distributions") },
                                modifier = Modifier.weight(1f)
                            )
                            AdminStatCard(
                                title = "Returned Kits",
                                value = stats.returnRate ?: "0%",
                                icon = Icons.Default.AssignmentReturn,
                                iconBg = Color(0xFFFFEBEE),
                                iconTint = Color(0xFFD32F2F),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Quick Actions Section
                        Text(
                            "Quick Actions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack,
                            modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1F000000)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                QuickActionItem(
                                    title = "Manage Dental Clinics",
                                    subtitle = "View, add or delete dental partner clinics",
                                    icon = Icons.Default.LocalHospital,
                                    iconBg = Color(0xFFE8F5E9),
                                    iconTint = Color(0xFF2E7D32),
                                    onClick = { onNavigate("Clinics") }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                } else if (errorMessage != null) {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        Card(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = primaryRed,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Something went wrong",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextBlack
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "We couldn't load the dashboard data. Please check your connection and try again.",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Button(
                                    onClick = onRetry,
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryRed),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Retry Connection")
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                TextButton(
                                    onClick = { showTechnicalDetails = !showTechnicalDetails }
                                ) {
                                    Text(
                                        if (showTechnicalDetails) "Hide Technical Details" else "View Technical Details",
                                        color = primaryRed.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                                
                                if (showTechnicalDetails) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = errorMessage,
                                            modifier = Modifier.padding(12.dp).verticalScroll(rememberScrollState()),
                                            fontSize = 11.sp,
                                            color = Color.DarkGray,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = iconBg
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                subtitle,
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 16.sp
            )
        }
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

@Composable
fun AdminBottomNavigationBar(currentScreen: String, onNavigate: (String) -> Unit) {
    val primaryRed = Color(0xFFD32F2F)
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf("Home", "Stock Requests", "Profile")
        val selectedIcons = mapOf(
            "Home" to Icons.Filled.Home,
            "Stock Requests" to Icons.Filled.Inventory,
            "Profile" to Icons.Filled.Person
        )
        val unselectedIcons = mapOf(
            "Home" to Icons.Outlined.Home,
            "Stock Requests" to Icons.Outlined.Inventory,
            "Profile" to Icons.Outlined.Person
        )

        items.forEach { screen ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = if (isSelected) selectedIcons[screen]!! else unselectedIcons[screen]!!, 
                        contentDescription = screen 
                    ) 
                },
                label = { 
                    Text(
                        text = screen, 
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    ) 
                },
                selected = isSelected,
                onClick = { onNavigate(screen) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryRed,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = primaryRed,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = primaryRed.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1A000000))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = iconBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = value, fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextBlack)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardPreview() {
    DigitalpdsTheme {
        AdminDashboardContent(
            stats = null,
            isLoading = false,
            errorMessage = null
        )
    }
}
