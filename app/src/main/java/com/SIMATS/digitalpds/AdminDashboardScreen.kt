package com.SIMATS.digitalpds

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.network.AdminDashboardStats
import com.SIMATS.digitalpds.ui.theme.*

@Composable
fun AdminDashboardScreen(
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    adminViewModel: AdminViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        adminViewModel.fetchDashboardStats()
    }

    AdminDashboardContent(
        stats = adminViewModel.dashboardStats,
        isLoading = adminViewModel.isLoading,
        errorMessage = adminViewModel.errorMessage,
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onNavigate = onNavigate
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    stats: AdminDashboardStats?,
    isLoading: Boolean,
    errorMessage: String?,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val currentTab = "Home"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Admin Dashboard",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        bottomBar = {
            AdminBottomNavigationBar(currentScreen = currentTab, onNavigate = onNavigate)
        },
        containerColor = Color(0xFFF8FBFF)
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFD32F2F))
            }
        } else if (stats != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Stats Cards Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatCard(
                        title = "Total Dealers",
                        value = stats.totalDealers,
                        change = stats.totalDealersChange,
                        isPositive = stats.isDealersPositive,
                        icon = Icons.Default.Groups,
                        iconBg = Color(0xFFE3F2FD),
                        iconTint = Color(0xFF1976D2),
                        onClick = { onNavigate("Dealers") },
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Active Beneficiaries",
                        value = stats.activeBeneficiaries,
                        change = stats.activeBeneficiariesChange,
                        isPositive = stats.isBeneficiariesPositive,
                        icon = Icons.Default.Inventory2,
                        iconBg = Color(0xFFE8F5E9),
                        iconTint = Color(0xFF2E7D32),
                        onClick = { onNavigate("Beneficiaries") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatCard(
                        title = "Total Distributions",
                        value = stats.totalDistributions,
                        change = stats.totalDistributionsChange,
                        isPositive = stats.isDistributionsPositive,
                        icon = Icons.Default.BarChart,
                        iconBg = Color(0xFFF3E5F5),
                        iconTint = Color(0xFF7B1FA2),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Return Rate",
                        value = stats.returnRate,
                        change = stats.returnRateChange,
                        isPositive = stats.isReturnRatePositive,
                        icon = Icons.Default.Warning,
                        iconBg = Color(0xFFFFF3E0),
                        iconTint = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Distribution Trends Chart Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Distribution Trends",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ChartLegendItem(color = Color(0xFF00BFA5), label = "Kits")
                                Spacer(modifier = Modifier.width(12.dp))
                                ChartLegendItem(color = Color(0xFF2979FF), label = "Dealers")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Simple Custom Line Chart
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            DistributionTrendsChart()
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // X-Axis Labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul").forEach { month ->
                                Text(text = month, fontSize = 10.sp, color = TextGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Kit Status Overview Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Kit Status Overview",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Text(
                            "Current status of all distributed kits",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        StatusProgressItem(label = "Given", percentage = stats.kitGivenPercentage, color = Color(0xFF00BFA5))
                        Spacer(modifier = Modifier.height(16.dp))
                        StatusProgressItem(label = "Returned", percentage = stats.kitReturnedPercentage, color = Color(0xFFF44336))
                        Spacer(modifier = Modifier.height(16.dp))
                        StatusProgressItem(label = "Pending", percentage = stats.kitPendingPercentage, color = Color(0xFF90A4AE))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = errorMessage, color = Color.Red)
            }
        }
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
    change: String,
    isPositive: Boolean,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = change,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Icon(
                        imageVector = if (isPositive) Icons.Default.NorthEast else Icons.Default.SouthEast,
                        contentDescription = null,
                        tint = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextBlack)
            Text(text = title, fontSize = 12.sp, color = TextGray)
        }
    }
}

@Composable
fun ChartLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 12.sp, color = TextGray)
    }
}

@Composable
fun StatusProgressItem(label: String, percentage: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack)
            }
            Text(text = "$percentage%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextBlack)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { percentage.toFloat() / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFF1F4F4)
        )
    }
}

@Composable
fun DistributionTrendsChart() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Kits line (Teal) - Wave pattern
        val kitPoints = listOf(
            Offset(0f, height * 0.8f),
            Offset(width * 0.2f, height * 0.7f),
            Offset(width * 0.4f, height * 0.4f),
            Offset(width * 0.5f, height * 0.1f),
            Offset(width * 0.6f, height * 0.9f),
            Offset(width * 0.8f, height * 0.7f),
            Offset(width, height * 0.5f)
        )
        
        val kitPath = Path().apply {
            moveTo(kitPoints[0].x, kitPoints[0].y)
            for (i in 1 until kitPoints.size) {
                cubicTo(
                    (kitPoints[i-1].x + kitPoints[i].x) / 2, kitPoints[i-1].y,
                    (kitPoints[i-1].x + kitPoints[i].x) / 2, kitPoints[i].y,
                    kitPoints[i].x, kitPoints[i].y
                )
            }
        }
        
        // Draw Fill for Kits
        val fillPath = Path().apply {
            addPath(kitPath)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF00BFA5).copy(alpha = 0.1f), Color.Transparent)
            )
        )
        
        // Draw Line for Kits
        drawPath(
            path = kitPath,
            color = Color(0xFF00BFA5),
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Dealers line (Blue) - Flat/Slightly rising line
        drawLine(
            color = Color(0xFF2979FF),
            start = Offset(0f, height * 0.9f),
            end = Offset(width, height * 0.88f),
            strokeWidth = 3.dp.toPx()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardPreview() {
    DigitalpdsTheme {
        AdminDashboardContent(
            stats = AdminDashboardStats(
                totalDealers = "45",
                totalDealersChange = "12%",
                isDealersPositive = true,
                activeBeneficiaries = "1,240",
                activeBeneficiariesChange = "5%",
                isBeneficiariesPositive = true,
                totalDistributions = "8,450",
                totalDistributionsChange = "2%",
                isDistributionsPositive = true,
                returnRate = "1.2%",
                returnRateChange = "0.5%",
                isReturnRatePositive = false,
                kitGivenPercentage = 75,
                kitReturnedPercentage = 15,
                kitPendingPercentage = 10
            ),
            isLoading = false,
            errorMessage = null
        )
    }
}
