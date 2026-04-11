package com.SIMATS.digitalpds

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.network.ConfirmDeliveryRequest
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
    val imagePlaceholder: Color = Color(0xFFF1F4F8),
    val courierName: String? = null,
    val trackingId: String? = null,
    val dispatchedAt: String? = null,
    val deliveredAt: String? = null,
    val adminNote: String? = null
)

class DealerStockViewModel : ViewModel() {
    var requestedItems by mutableStateOf<List<StockRequest>>(emptyList())
    var inTransitItems by mutableStateOf<List<StockRequest>>(emptyList())
    var deliveredItems by mutableStateOf<List<StockRequest>>(emptyList())
    var totalRequested by mutableStateOf("0")
    var totalInTransit by mutableStateOf("0")
    var totalDelivered by mutableStateOf("0")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var confirmingDelivery by mutableStateOf<String?>(null) // requestId being confirmed

    fun fetchStockData(dealerId: Int, token: String) {
        if (dealerId <= 0) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getAdminStockRequests(token)
                if (response.isSuccessful) {
                    val allRequests = response.body() ?: emptyList()
                    val myRequests = allRequests.filter { it.dealerId == dealerId }

                    val mapped = myRequests.map { req ->
                        val (statusText, statusColor, progress) = when (req.status.uppercase()) {
                            "PENDING" -> Triple("PENDING", Color(0xFFE65100), 0.1f)
                            "APPROVED" -> Triple("APPROVED", Color(0xFF1976D2), 0.3f)
                            "DISPATCHED" -> Triple("IN TRANSIT", Color(0xFFFF8F00), 0.65f)
                            "DELIVERED" -> Triple("DELIVERED", Color(0xFF4CAF50), 1.0f)
                            "REJECTED" -> Triple("REJECTED", Color(0xFFD32F2F), 0f)
                            else -> Triple(req.status, Color.Gray, 0f)
                        }

                        StockRequest(
                            id = req.requestId,
                            totalUnits = "${req.totalKits} Kits",
                            date = req.requestDate,
                            status = statusText,
                            statusColor = statusColor.copy(alpha = 0.2f),
                            progress = progress,
                            lastLocation = when (req.status.uppercase()) {
                                "DELIVERED" -> "Delivered to Warehouse"
                                "DISPATCHED" -> "Shipped via ${req.courierName ?: "courier"}"
                                "APPROVED" -> "Awaiting Dispatch"
                                else -> "Pending Review"
                            },
                            detailedItems = "Full Oral Care Kits",
                            courierName = req.courierName,
                            trackingId = req.trackingId,
                            dispatchedAt = req.dispatchedAt,
                            deliveredAt = req.deliveredAt,
                            adminNote = req.adminNote
                        )
                    }.sortedByDescending { it.date }

                    requestedItems = mapped.filter { it.status == "PENDING" || it.status == "REJECTED" || it.status == "APPROVED" }
                    inTransitItems = mapped.filter { it.status == "IN TRANSIT" }
                    deliveredItems = mapped.filter { it.status == "DELIVERED" }

                    totalRequested = requestedItems.size.toString()
                    totalInTransit = inTransitItems.size.toString()
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

    fun confirmDelivery(token: String, requestGroupId: String, dealerId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            confirmingDelivery = requestGroupId
            try {
                val response = RetrofitClient.apiService.confirmDelivery(
                    token,
                    requestGroupId = requestGroupId,
                    request = mapOf("dealer_id" to dealerId.toString())
                )
                if (response.isSuccessful) {
                    fetchStockData(dealerId, token)
                    onSuccess()
                } else {
                    val error = response.errorBody()?.string() ?: "Failed to confirm delivery"
                    errorMessage = error
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                confirmingDelivery = null
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
    val tabs = listOf("Pending", "In Transit", "Delivered")

    val token = remember { sessionManager.getAccessToken() ?: "" }
    LaunchedEffect(Unit) {
        viewModel.fetchStockData(dealerId, "Bearer $token")
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {
        // Gradient Header background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
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
                            "Stock Hub", 
                            fontWeight = FontWeight.ExtraBold, 
                            color = Color.White,
                            fontSize = 22.sp
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
            bottomBar = {
                AppBottomNavigationBar(currentScreen = "Stock", onNavigate = onNavigate)
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HubStatCard(
                        label = "Pending",
                        value = viewModel.totalRequested,
                        icon = Icons.Default.PendingActions,
                        modifier = Modifier.weight(1f)
                    )
                    HubStatCard(
                        label = "In Transit",
                        value = viewModel.totalInTransit,
                        icon = Icons.Default.LocalShipping,
                        modifier = Modifier.weight(1f)
                    )
                    HubStatCard(
                        label = "Delivered",
                        value = viewModel.totalDelivered,
                        icon = Icons.Default.Inventory,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Custom Tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = DealerGreen,
                    divider = {},
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = DealerGreen,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { 
                                Text(
                                    title, 
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Black else FontWeight.Bold,
                                    color = if (selectedTabIndex == index) DealerGreen else TextGray,
                                    fontSize = 13.sp
                                ) 
                            }
                        )
                    }
                }

                // List Content
                Box(modifier = Modifier.weight(1f)) {
                    if (viewModel.isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = DealerGreen)
                        }
                    } else {
                        val itemsToShow = when (selectedTabIndex) {
                            0 -> viewModel.requestedItems
                            1 -> viewModel.inTransitItems
                            else -> viewModel.deliveredItems
                        }

                        if (itemsToShow.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextGray.copy(alpha = 0.3f))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("No records in this category", color = TextGray, fontWeight = FontWeight.Medium)
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(itemsToShow) { item ->
                                    StockItemCard(
                                        item = item,
                                        tabIndex = selectedTabIndex,
                                        isConfirming = viewModel.confirmingDelivery == item.id,
                                        onConfirmDelivery = {
                                            val token = sessionManager.getAccessToken() ?: ""
                                            viewModel.confirmDelivery("Bearer $token", item.id, dealerId) {
                                                android.widget.Toast.makeText(
                                                    context,
                                                    "Delivery confirmed! Stock updated.",
                                                    android.widget.Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
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

@Composable
fun HubStatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1A000000)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = DealerSecondary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = DealerGreen, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextBlack)
        }
    }
}

@Composable
fun StockItemCard(
    item: StockRequest,
    tabIndex: Int,
    isConfirming: Boolean = false,
    onConfirmDelivery: () -> Unit = {}
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0D000000)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = DealerSecondary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when(item.status) {
                                "DELIVERED" -> Icons.Default.Inventory
                                "IN TRANSIT" -> Icons.Default.LocalShipping
                                "REJECTED" -> Icons.Default.Cancel
                                "APPROVED" -> Icons.Default.CheckCircle
                                else -> Icons.Default.Inventory2
                            },
                            contentDescription = null,
                            tint = DealerGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Req ID: ${item.id}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = DealerGreen
                    )
                    Text(
                        text = item.totalUnits,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                }

                val displayStatusColor = when(item.status) {
                    "PENDING" -> Color(0xFFE65100)
                    "APPROVED" -> Color(0xFF1976D2)
                    "IN TRANSIT" -> Color(0xFFFF8F00)
                    "DELIVERED" -> Color(0xFF4CAF50)
                    "REJECTED" -> Color(0xFFD32F2F)
                    else -> Color.Gray
                }

                Surface(
                    color = displayStatusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        item.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = displayStatusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = item.detailedItems ?: "",
                fontSize = 13.sp,
                color = TextBlack.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp), tint = TextGray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Requested: ${item.date}", fontSize = 12.sp, color = TextGray)
            }

            if (!item.adminNote.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notes, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Admin Remarks", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.adminNote,
                            fontSize = 13.sp,
                            color = TextBlack,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // In Transit tab — show shipment details and confirm button
            if (tabIndex == 1 && item.status == "IN TRANSIT") {
                Spacer(modifier = Modifier.height(16.dp))

                // Shipment Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Shipment Details",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF8F00)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF8F00))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Courier: ${item.courierName ?: "N/A"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextBlack
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Numbers, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF8F00))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Tracking: ${item.trackingId ?: "N/A"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextBlack
                            )
                        }
                        if (item.dispatchedAt != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF8F00))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Dispatched: ${item.dispatchedAt}",
                                    fontSize = 12.sp,
                                    color = TextGray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress bar
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Delivery Progress", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Text("${(item.progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF8F00))
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFFFF8F00),
                    trackColor = Color(0xFFFFF3E0),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFFF8F00))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(item.lastLocation ?: "", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mark as Received Button
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    enabled = !isConfirming
                ) {
                    if (isConfirming) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirming...", fontWeight = FontWeight.Bold, color = Color.White)
                    } else {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mark as Received", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }
                }
            }

            // Delivered tab — show delivery confirmation info
            if (tabIndex == 2 && item.status == "DELIVERED") {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Delivery Confirmed", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        }
                        if (item.courierName != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Courier: ${item.courierName}", fontSize = 12.sp, color = TextGray)
                        }
                        if (item.trackingId != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Tracking: ${item.trackingId}", fontSize = 12.sp, color = TextGray)
                        }
                        if (item.deliveredAt != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Received: ${item.deliveredAt}", fontSize = 12.sp, color = TextGray)
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = { Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color(0xFF4CAF50)) },
            title = { Text("Confirm Receipt", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Have you received this shipment?")
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Request: ${item.id}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Items: ${item.totalUnits}", fontSize = 13.sp, color = TextGray)
                            if (item.courierName != null) {
                                Text("Courier: ${item.courierName}", fontSize = 13.sp, color = TextGray)
                            }
                            if (item.trackingId != null) {
                                Text("Tracking: ${item.trackingId}", fontSize = 13.sp, color = TextGray)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Your stock inventory will be updated with the received quantities.",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onConfirmDelivery()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Confirm Received", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainStockManagementHubScreenPreview() {
    DigitalpdsTheme {
        MainStockManagementHubScreen(onBackClick = {}, onNavigate = {})
    }
}
