package com.SIMATS.digitalpds

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    onProceedClick: () -> Unit = {},
    onPerformanceClick: () -> Unit = {},
    onGenerateQRClick: (Int) -> Unit = {},
    dealerViewModel: DealerViewModel = viewModel()
) {
    var beneficiaryIdInput by remember { mutableStateOf("") }
    var idError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(dealerId) {
        if (dealerId > 0) {
            dealerViewModel.fetchDashboardStats(dealerId)
        }
    }

    val stats = dealerViewModel.dashboardStats
    val isLoading = dealerViewModel.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Mukh Swasthya",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
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
        containerColor = Color(0xFFF8FBFF)
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

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPerformanceClick() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DealerGreen)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "${stats.todayDistributions} Distributions",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "TODAY'S PERFORMANCE",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "${stats.performancePercentage}% of target",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
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

                OutlinedTextField(
                    value = beneficiaryIdInput,
                    onValueChange = {
                        beneficiaryIdInput = it
                        idError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("BENEFICIARY ID") },
                    isError = idError != null,
                    supportingText = { if (idError != null) Text(idError!!) },
                    trailingIcon = {
                        IconButton(onClick = onScanClick) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Scan", tint = Color.Gray)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE9EEF3),
                        unfocusedContainerColor = Color(0xFFE9EEF3),
                        focusedIndicatorColor = DealerGreen,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (beneficiaryIdInput.isBlank()) {
                                idError = "Beneficiary ID required"
                            } else {
                                onProceedClick()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DealerGreen)
                    ) {
                        Text("PROCEED", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            idError = null
                            onGenerateQRClick(dealerId)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DealerGreen),
                        border = BorderStroke(1.dp, DealerGreen)
                    ) {
                        Icon(Icons.Outlined.QrCode, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GENERATE QR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        title = "Total Kits",
                        value = stats.totalKits,
                        change = stats.totalKitsChange,
                        isPositive = stats.isTotalKitsPositive,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Distributed",
                        value = stats.distributedKits,
                        change = stats.distributedKitsChange,
                        isPositive = stats.isDistributedPositive,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        title = "Remaining",
                        value = stats.remainingKits,
                        change = stats.remainingKitsChange,
                        isPositive = stats.isRemainingPositive,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Returned",
                        value = stats.returnedKits,
                        change = stats.returnedKitsChange,
                        isPositive = stats.isReturnedPositive,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Live Item Count", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(modifier = Modifier.height(16.dp))
                stats.itemCounts.forEach { item ->
                    ItemCountRow(formatItemName(item.name), "${item.count} Units", Color(0xFFF5F5F5))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Recent Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(modifier = Modifier.height(16.dp))
                stats.recentTransactions.forEach { tx ->
                    TransactionItem(tx.name, tx.details, tx.quantity)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = onHistoryClick,
                        modifier = Modifier
                            .weight(0.4f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE9EEF3),
                            contentColor = TextBlack
                        )
                    ) {
                        Text("History")
                    }
                    Button(
                        onClick = onRequestStockClick,
                        modifier = Modifier
                            .weight(0.6f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DealerGreen)
                    ) {
                        Text("REQUEST STOCK", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        } else if (dealerViewModel.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = dealerViewModel.errorMessage!!, color = Color.Red)
            }
        }
    }
}

private fun formatItemName(name: String): String {
    val upperName = name.trim().uppercase(Locale.ROOT)
    return when {
        upperName.contains("ADULT_BRUSH") || upperName.contains("ADULT BRUSH") -> "ADULT BRUSH"
        upperName.contains("CHILD_BRUSH") || upperName.contains("CHILD BRUSH") -> "CHILD BRUSH"
        upperName.contains("TOOTHPASTE") -> "TOOTHPASTE"
        upperName.contains("FLYER") || upperName.contains("IEC") -> "FLYER"
        upperName.isBlank() -> "ITEM"
        else -> upperName.replace("_", " ")
    }
}

@Composable
private fun StatCard(title: String, value: String, change: String, isPositive: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 14.sp, color = Color.Gray)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Text(
                change,
                fontSize = 12.sp,
                color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFE53935),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ItemCountRow(name: String, count: String, iconBg: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(name, fontSize = 16.sp, color = TextBlack, fontWeight = FontWeight.Medium)
        }
        Text(count, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
    }
}

@Composable
private fun TransactionItem(name: String, details: String, quantity: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Text(details, fontSize = 12.sp, color = Color.Gray)
        }
        Text(quantity, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack)
    }
}

@Preview(showBackground = true)
@Composable
fun DealerDashboardScreenPreview() {
    DigitalpdsTheme {
        DealerDashboardScreen()
    }
}
