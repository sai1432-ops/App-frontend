package com.SIMATS.digitalpds

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.network.AdminStockRequestDto
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.*
import kotlinx.coroutines.launch

data class StockRequest(
    val id: String,
    val totalUnits: String,
    val date: String,
    val status: String,
    val statusColor: Color,
    val progress: Float = 0f,
    val lastLocation: String? = null,
    val detailedItems: String? = null,
    val imagePlaceholder: Color = Color(0xFFF1F4F8)
)

class DealerStockViewModel : ViewModel() {
    var requestedItems by mutableStateOf<List<StockRequest>>(emptyList())
    var shippedItems by mutableStateOf<List<StockRequest>>(emptyList())
    var deliveredItems by mutableStateOf<List<StockRequest>>(emptyList())
    var totalRequested by mutableStateOf("0")
    var totalShipped by mutableStateOf("0")
    var totalDelivered by mutableStateOf("0")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchStockData(dealerId: Int) {
        if (dealerId <= 0) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getAdminStockRequests()
                if (response.isSuccessful) {
                    val allRequests = response.body() ?: emptyList()
                    val myRequests = allRequests.filter { it.dealerId == dealerId }

                    // Group by requestId to handle multiple items in one request
                    val grouped = myRequests.groupBy { it.requestId }

                    val mapped = grouped.map { (requestId, items) ->
                        val first = items.first()
                        val totalQty = items.sumOf { 
                            it.quantity.replace(" Units", "").trim().toIntOrNull() ?: 0 
                        }
                        val itemsDescription = items.joinToString(", ") { "${it.kitType} (${it.quantity})" }

                        val (statusText, statusColor, progress) = when (first.status.uppercase()) {
                            "PENDING" -> Triple("PENDING", Color(0xFFE65100), 0.1f)
                            "APPROVED" -> Triple("SHIPPED", Color(0xFF1976D2), 0.6f)
                            "DISPATCHED" -> Triple("DELIVERED", Color(0xFF4CAF50), 1.0f)
                            "REJECTED" -> Triple("REJECTED", Color(0xFFD32F2F), 0f)
                            else -> Triple(first.status, Color.Gray, 0f)
                        }

                        StockRequest(
                            id = requestId,
                            totalUnits = "$totalQty Total Units",
                            date = first.requestDate,
                            status = statusText,
                            statusColor = statusColor.copy(alpha = 0.2f),
                            progress = progress,
                            lastLocation = if (first.status == "DISPATCHED") "Delivered to Warehouse" else if (first.status == "APPROVED") "In Transit" else "Pending Review",
                            detailedItems = itemsDescription
                        )
                    }.sortedByDescending { it.date }

                    requestedItems = mapped.filter { it.status == "PENDING" || it.status == "REJECTED" }
                    shippedItems = mapped.filter { it.status == "SHIPPED" }
                    deliveredItems = mapped.filter { it.status == "DELIVERED" }

                    totalRequested = requestedItems.size.toString()
                    totalShipped = shippedItems.size.toString()
                    totalDelivered = deliveredItems.size.toString()

                } else {
                    errorMessage = "Failed to fetch stock data"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainStockManagementHubScreen(
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: DealerStockViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val dealerId = sessionManager.getUserId()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Requested", "Shipped", "Delivered")

    LaunchedEffect(Unit) {
        viewModel.fetchStockData(dealerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        when(selectedTabIndex) {
                            0 -> "Stock Requests Hub"
                            1 -> "Shipped Orders Tracking"
                            else -> "Delivered Records"
                        }, 
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        bottomBar = {
            AppBottomNavigationBar(currentScreen = "Stock", onNavigate = onNavigate)
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Stats Section
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(title = "Total Pending", value = viewModel.totalRequested, modifier = Modifier.weight(1f))
                    StatCard(title = "In Transit", value = viewModel.totalShipped, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                StatCard(title = "Successfully Delivered", value = viewModel.totalDelivered, modifier = Modifier.fillMaxWidth())
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = BackgroundWhite,
                contentColor = PrimaryBlue,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = PrimaryBlue
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { 
                            Text(
                                title, 
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) TextBlack else TextGray
                            ) 
                        }
                    )
                }
            }

            // List Content
            Box(modifier = Modifier.weight(1f)) {
                if (viewModel.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else {
                    val itemsToShow = when (selectedTabIndex) {
                        0 -> viewModel.requestedItems
                        1 -> viewModel.shippedItems
                        else -> viewModel.deliveredItems
                    }

                    if (itemsToShow.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No records found", color = TextGray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(itemsToShow) { item ->
                                StockItemCard(item, selectedTabIndex)
                                HorizontalDivider(modifier = Modifier.padding(top = 16.dp), color = Color(0xFFF1F4F8))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFE9F1F1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, color = TextBlack, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = TextBlack, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StockItemCard(item: StockRequest, tabIndex: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Request ID: ${item.id}", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.totalUnits, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Text(item.detailedItems ?: "", fontSize = 12.sp, color = TextGray)
                Text("Requested on: ${item.date}", fontSize = 12.sp, color = TextGray)
                Spacer(modifier = Modifier.height(8.dp))
                
                val displayStatusColor = when(item.status) {
                    "PENDING" -> Color(0xFFE65100)
                    "SHIPPED" -> Color(0xFF1976D2)
                    "DELIVERED" -> Color(0xFF4CAF50)
                    "REJECTED" -> Color(0xFFD32F2F)
                    else -> Color.Gray
                }

                Surface(
                    color = displayStatusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        item.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = displayStatusColor
                    )
                }
            }
            
            // Image Placeholder
            Box(
                modifier = Modifier
                    .size(100.dp, 80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F4F8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(item.status) {
                        "DELIVERED" -> Icons.Default.CheckCircle
                        "REJECTED" -> Icons.Default.Cancel
                        else -> Icons.Default.LocalShipping
                    },
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Gray.copy(alpha = 0.5f)
                )
            }
        }

        if (tabIndex == 1) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Delivery Progress", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Text("${(item.progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { item.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryBlue,
                trackColor = Color(0xFFE0E0E0),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.lastLocation ?: "", fontSize = 11.sp, color = TextGray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainStockManagementHubScreenPreview() {
    DigitalpdsTheme {
        MainStockManagementHubScreen(onBackClick = {}, onNavigate = {})
    }
}
