package com.SIMATS.digitalpds

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealerDashboardScreen(
    dealerId: Int = 0,
    onHistoryClick: () -> Unit = {},
    onRequestStockClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onStockClick: () -> Unit = {},
    onBeneficiaryClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onProceedClick: (String) -> Unit = {},
    onPerformanceClick: () -> Unit = {},
    onGenerateQRClick: (Int) -> Unit = {},
    onTotalKitsClick: () -> Unit = {},
    dealerViewModel: DealerViewModel = viewModel()
) {
    val context = LocalContext.current
    val token = remember { SessionManager(context).getAccessToken() ?: "" }
    var beneficiaryIdInput by remember { mutableStateOf("") }
    var idError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(dealerId) {
        if (dealerId > 0) {
            dealerViewModel.fetchDashboardStats(dealerId, token)
        }
    }

    val stats = dealerViewModel.dashboardStats
    val isLoading = dealerViewModel.isLoading

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FBFF))) {
        // Top Background Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DealerGreen, Color(0xFF1E3333))
                    )
                )
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Mukh Swasthya",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = DealerGreen) },
                        label = { Text("Home", color = DealerGreen) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = DealerGreen.copy(alpha = 0.1f))
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onBeneficiaryClick,
                        icon = { Icon(Icons.Filled.People, contentDescription = "Beneficiary", tint = Color.Gray) },
                        label = { Text("Beneficiary", color = Color.Gray) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onStockClick,
                        icon = { Icon(Icons.Filled.Inventory, contentDescription = "Stock", tint = Color.Gray) },
                        label = { Text("Stock", color = Color.Gray) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onProfileClick,
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.Gray) },
                        label = { Text("Profile", color = Color.Gray) }
                    )
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DealerGreen)
            }
        } else if (stats != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Performance Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0x33000000))
                        .clickable { onPerformanceClick() },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DealerGreen)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(DealerGreen, Color(0xFF1E3333))
                        )
                    )) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Today's Performance",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "${stats.todayDistributions} Distributions",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Surface(
                                    modifier = Modifier.size(56.dp),
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.15f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Distribute Kit",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Distribute Kit Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0D000000)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Distribute Kit",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = beneficiaryIdInput,
                            onValueChange = {
                                beneficiaryIdInput = it
                                idError = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("PDS Card Number", color = TextGray.copy(alpha = 0.5f)) },
                            isError = idError != null,
                            supportingText = { if (idError != null) Text(idError!!) },
                            trailingIcon = {
                                IconButton(onClick = onScanClick) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = "Scan", tint = DealerGreen)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SurfaceLight,
                                unfocusedContainerColor = SurfaceLight,
                                focusedIndicatorColor = DealerGreen,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorContainerColor = Color(0xFFFFEBEE)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (beneficiaryIdInput.isBlank()) {
                                        idError = "PDS card number required"
                                    } else {
                                        onProceedClick(beneficiaryIdInput)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DealerGreen),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Text("PROCEED", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    idError = null
                                    onGenerateQRClick(dealerId)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DealerGreen),
                                border = BorderStroke(2.dp, DealerGreen)
                            ) {
                                Icon(Icons.Outlined.QrCode, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("QR CODE", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        title = "Total Kits",
                        value = stats.totalKits,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTotalKitsClick() }
                    )
                    StatCard(
                        title = "Distributed",
                        value = stats.distributedKits,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        title = "Remaining",
                        value = stats.remainingKits,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Returned",
                        value = stats.returnedKits,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Recent Transactions
                Text(
                    "Recent Transactions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0D000000)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        stats.recentTransactions.forEachIndexed { index, tx ->
                            TransactionItem(tx.name, tx.details, tx.quantity)
                            if (index < stats.recentTransactions.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    thickness = 0.5.dp,
                                    color = SurfaceLight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = onHistoryClick,
                        modifier = Modifier
                            .weight(0.4f)
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceLight,
                            contentColor = TextBlack
                        )
                    ) {
                        Text("History", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onRequestStockClick,
                        modifier = Modifier
                            .weight(0.6f)
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DealerGreen),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Text("REQUEST STOCK", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Spacer(modifier = Modifier.height(32.dp))
            }
        } else if (dealerViewModel.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = dealerViewModel.errorMessage!!, color = Color.Red)
            }
        }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1A000000)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Black, color = TextBlack)
        }
    }
}

@Composable
fun TransactionItem(name: String, details: String, quantity: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = DealerSecondary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = DealerGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Text(details, fontSize = 12.sp, color = TextGray)
            }
        }
        Text(
            quantity,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = DealerAccent
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DealerDashboardScreenPreview() {
    DigitalpdsTheme {
        DealerDashboardScreen()
    }
}
