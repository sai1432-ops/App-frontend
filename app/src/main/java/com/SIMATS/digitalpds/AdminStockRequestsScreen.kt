package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.ui.theme.*
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStockRequestsScreen(
    onNavigate: (String) -> Unit = {},
    onDetailsClick: (AdminStockRequest) -> Unit = {},
    viewModel: StockViewModel = viewModel()
) {
    val context = LocalContext.current
    val token = remember { SessionManager(context).getAccessToken() ?: "" }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        viewModel.fetchRequests(token)
    }

    LaunchedEffect(viewModel.message) {
        viewModel.message?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    val requests = viewModel.requests

    var showConfirmDialog by remember { mutableStateOf<Pair<AdminStockRequest, StockRequestStatus>?>(null) }
    var rejectionReason by remember { mutableStateOf("") }
    var courierName by remember { mutableStateOf("") }
    var trackingId by remember { mutableStateOf("") }
    var adminNote by remember { mutableStateOf("") }

    val pendingCount = requests.count { it.status == StockRequestStatus.PENDING }
    val primaryRed = Color(0xFFD32F2F)

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(primaryRed, primaryRed.copy(alpha = 0.8f))
                        )
                    )
                    .statusBarsPadding()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Stock Requests",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    actions = {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text(
                                "Pending: $pendingCount",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by dealer or ID...", color = Color.White.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    val filters = listOf("All", "Pending", "Approved", "Dispatched", "Delivered", "Rejected")
                    items(filters) { filter ->
                        FilterTabPremium(
                            label = filter,
                            isSelected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            selectedColor = Color.White,
                            unselectedColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        },
        bottomBar = {
            AdminBottomNavigationBar(currentScreen = "Stock Requests", onNavigate = onNavigate)
        },
        containerColor = Color(0xFFF8FBFF)
    ) { paddingValues ->
        val filteredList = requests.filter {
            val matchesSearch = it.dealerName.contains(searchQuery, ignoreCase = true) ||
                    it.requestId.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Pending" -> it.status == StockRequestStatus.PENDING
                "Approved" -> it.status == StockRequestStatus.APPROVED
                "Dispatched" -> it.status == StockRequestStatus.DISPATCHED
                "Delivered" -> it.status == StockRequestStatus.DELIVERED
                "Rejected" -> it.status == StockRequestStatus.REJECTED
                else -> true
            }
            matchesSearch && matchesFilter
        }

        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            filteredList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No stock requests found", color = TextGray)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredList) { request ->
                        StockRequestCard(
                            request = request,
                            onApprove = { showConfirmDialog = request to StockRequestStatus.APPROVED },
                            onReject = { showConfirmDialog = request to StockRequestStatus.REJECTED },
                            onDispatch = { showConfirmDialog = request to StockRequestStatus.DISPATCHED },
                            onDetails = { onDetailsClick(request) }
                        )
                    }
                }
            }
        }
    }

    showConfirmDialog?.let { (request, targetStatus) ->
        AlertDialog(
            onDismissRequest = { showConfirmDialog = null },
            title = {
                Text(
                    when (targetStatus) {
                        StockRequestStatus.APPROVED -> "Approve Request Group"
                        StockRequestStatus.REJECTED -> "Reject Request Group"
                        StockRequestStatus.DISPATCHED -> "Dispatch Request Group"
                        else -> "Update Request"
                    }
                )
            },
            text = {
                Column {
                    Text(
                        when (targetStatus) {
                            StockRequestStatus.APPROVED ->
                                "Are you sure you want to approve all items in this stock request (${request.requestId})?"
                            StockRequestStatus.REJECTED ->
                                "Are you sure you want to reject all items in this stock request (${request.requestId})?"
                            StockRequestStatus.DISPATCHED ->
                                "Enter shipping details for request ${request.requestId}:"
                            else -> ""
                        }
                    )



                    if (targetStatus == StockRequestStatus.REJECTED) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = rejectionReason,
                            onValueChange = { rejectionReason = it },
                            label = { Text("Reason (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    if (targetStatus == StockRequestStatus.DISPATCHED) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Shipping To:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Dealer: ${request.dealerName}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("Address: ${request.dealerAddress ?: "No address provided"}", fontSize = 14.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = courierName,
                            onValueChange = { courierName = it },
                            label = { Text("Courier Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = trackingId,
                            onValueChange = { trackingId = it },
                            label = { Text("Tracking ID") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = adminNote,
                            onValueChange = { adminNote = it },
                            label = { Text("Admin Note / Remarks (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (targetStatus) {
                            StockRequestStatus.APPROVED -> {
                                viewModel.approveRequest(token, request, "")
                            }
                            StockRequestStatus.REJECTED -> {
                                viewModel.rejectRequest(token, request, rejectionReason)
                            }
                            StockRequestStatus.DISPATCHED -> {
                                if (courierName.isNotBlank() && trackingId.isNotBlank()) {
                                    viewModel.dispatchRequest(token, request, courierName, trackingId, adminNote)
                                } else {
                                    return@Button
                                }
                            }
                            else -> {}
                        }
                        showConfirmDialog = null
                        rejectionReason = ""
                        courierName = ""
                        trackingId = ""
                        adminNote = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (targetStatus) {
                            StockRequestStatus.APPROVED -> Color(0xFF4CAF50)
                            StockRequestStatus.REJECTED -> Color.Red
                            StockRequestStatus.DISPATCHED -> Color(0xFF00897B)
                            else -> PrimaryBlue
                        }
                    )
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmDialog = null
                    rejectionReason = ""
                    courierName = ""
                    trackingId = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun FilterTabPremium(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color = Color.White,
    unselectedColor: Color = Color.White.copy(alpha = 0.3f)
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) selectedColor else unselectedColor,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = label,
                color = if (isSelected) Color(0xFFD32F2F) else Color.White,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StockRequestCard(
    request: AdminStockRequest,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDispatch: () -> Unit,
    onDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onDetails),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                request.dealerName.take(1).uppercase(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB71C1C)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(request.dealerName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        Text("ID: ${request.requestId}", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(request.location, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
                StatusBadge(status = request.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color(0xFFF9FAFB),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("REQUESTED KITS", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("${request.totalKits} Full Kits", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("1 Kit = 1 Brush + 1 Paste + 1 Flyer", fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("DATE", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(request.requestDate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    }
                }
            }

            val hasActions = request.status == StockRequestStatus.PENDING || request.status == StockRequestStatus.APPROVED

            if (hasActions) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (request.status == StockRequestStatus.PENDING) {
                            Button(
                                onClick = onApprove,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9), contentColor = Color(0xFF2E7D32)),
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("APPROVE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onReject,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color(0xFFD32F2F)),
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("REJECT", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (request.status == StockRequestStatus.APPROVED) {
                            Button(
                                onClick = onDispatch,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F2F1), contentColor = Color(0xFF00695C)),
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("DISPATCH", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    IconButton(onClick = onDetails) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "View Details", tint = Color.LightGray)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View Details",
                        tint = Color.LightGray,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 4.dp, end = 4.dp)
                            .size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: StockRequestStatus) {
    val (color, bgColor, text) = when (status) {
        StockRequestStatus.PENDING -> Triple(Color(0xFFE65100), Color(0xFFFFF3E0), "PENDING")
        StockRequestStatus.APPROVED -> Triple(Color(0xFF1976D2), Color(0xFFE3F2FD), "APPROVED")
        StockRequestStatus.DISPATCHED -> Triple(Color(0xFF7B1FA2), Color(0xFFF3E5F5), "DISPATCHED")
        StockRequestStatus.DELIVERED -> Triple(Color(0xFF2E7D32), Color(0xFFE8F5E9), "DELIVERED")
        StockRequestStatus.REJECTED -> Triple(Color(0xFFD32F2F), Color(0xFFFFEBEE), "REJECTED")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            val icon = when (status) {
                StockRequestStatus.PENDING -> Icons.Default.History
                StockRequestStatus.APPROVED -> Icons.Default.CheckCircle
                StockRequestStatus.DISPATCHED -> Icons.Default.LocalShipping
                StockRequestStatus.DELIVERED -> Icons.Default.Inventory
                StockRequestStatus.REJECTED -> Icons.Default.Cancel
            }
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = color)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminStockRequestsScreenPreview() {
    AdminStockRequestsScreen()
}
