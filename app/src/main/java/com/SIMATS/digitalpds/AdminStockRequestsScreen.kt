package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.SIMATS.digitalpds.network.RejectStockRequestBody
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.*
import kotlinx.coroutines.launch

enum class StockRequestStatus {
    PENDING, APPROVED, DISPATCHED, REJECTED
}

data class AdminStockRequest(
    val id: Int,
    val itemIds: List<Int>, // List of individual database IDs for bulk actions
    val requestId: String,
    val dealerId: String,
    val dealerName: String,
    val location: String,
    val kitType: String,
    val quantity: String,
    val status: StockRequestStatus,
    val requestDate: String,
    val approvedAt: String? = null,
    val rejectedAt: String? = null,
    val dispatchedAt: String? = null,
    val adminNote: String? = null,
    val courierName: String? = null,
    val trackingId: String? = null
)

class StockViewModel : ViewModel() {
    var requests by mutableStateOf<List<AdminStockRequest>>(emptyList())
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf<String?>(null)

    fun fetchRequests() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.apiService.getAdminStockRequests()
                if (response.isSuccessful) {
                    val rawList = response.body() ?: emptyList()
                    
                    // Group requests by requestId to avoid showing multiple cards for one order
                    val grouped = rawList.groupBy { it.requestId }
                    
                    requests = grouped.map { (gid, dtos) ->
                        val first = dtos.first()
                        
                        // Aggregate items summary
                        val itemsSummary = dtos.joinToString(", ") { "${it.kitType} (${it.quantity})" }
                        
                        AdminStockRequest(
                            id = first.id,
                            itemIds = dtos.map { it.id },
                            requestId = gid,
                            dealerId = first.dealerId.toString(),
                            dealerName = first.dealerName,
                            location = first.location,
                            kitType = itemsSummary,
                            quantity = "${dtos.size} Item Types",
                            status = when (first.status.uppercase()) {
                                "APPROVED" -> StockRequestStatus.APPROVED
                                "DISPATCHED" -> StockRequestStatus.DISPATCHED
                                "REJECTED" -> StockRequestStatus.REJECTED
                                else -> StockRequestStatus.PENDING
                            },
                            requestDate = first.requestDate,
                            approvedAt = first.approvedAt,
                            rejectedAt = first.rejectedAt,
                            dispatchedAt = first.dispatchedAt,
                            adminNote = first.adminNote,
                            courierName = first.courierName,
                            trackingId = first.trackingId
                        )
                    }.sortedByDescending { it.requestDate }
                } else {
                    message = "Failed to load stock requests"
                }
            } catch (e: Exception) {
                message = e.message ?: "Something went wrong"
            } finally {
                isLoading = false
            }
        }
    }

    fun approveRequest(group: AdminStockRequest, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            var allSuccess = true
            for (id in group.itemIds) {
                try {
                    val response = RetrofitClient.apiService.approveStockRequest(id)
                    if (!response.isSuccessful) allSuccess = false
                } catch (e: Exception) {
                    allSuccess = false
                }
            }
            
            if (allSuccess) {
                message = "Request group approved"
                fetchRequests()
                onDone()
            } else {
                message = "Failed to approve some items in the request"
            }
        }
    }

    fun dispatchRequest(group: AdminStockRequest, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            var allSuccess = true
            for (id in group.itemIds) {
                try {
                    val response = RetrofitClient.apiService.dispatchStockRequest(id)
                    if (!response.isSuccessful) allSuccess = false
                } catch (e: Exception) {
                    allSuccess = false
                }
            }
            
            if (allSuccess) {
                message = "Request group dispatched"
                fetchRequests()
                onDone()
            } else {
                message = "Failed to dispatch some items"
            }
        }
    }

    fun rejectRequest(group: AdminStockRequest, reason: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            var allSuccess = true
            for (id in group.itemIds) {
                try {
                    val response = RetrofitClient.apiService.rejectStockRequest(
                        id,
                        RejectStockRequestBody(reason.ifBlank { null })
                    )
                    if (!response.isSuccessful) allSuccess = false
                } catch (e: Exception) {
                    allSuccess = false
                }
            }
            
            if (allSuccess) {
                message = "Request group rejected"
                fetchRequests()
                onDone()
            } else {
                message = "Failed to reject some items"
            }
        }
    }

    fun clearMessage() {
        message = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStockRequestsScreen(
    onNavigate: (String) -> Unit = {},
    onDetailsClick: (AdminStockRequest) -> Unit = {},
    viewModel: StockViewModel = viewModel()
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        viewModel.fetchRequests()
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

    val pendingCount = requests.count { it.status == StockRequestStatus.PENDING }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White).padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Stock Requests", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        Text("Manage dealer inventory", fontSize = 14.sp, color = TextGray)
                    }
                    Surface(
                        color = Color(0xFF1A1C1E),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Pending: $pendingCount",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by dealer or request ID...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf("All", "Pending", "Approved", "Dispatched", "Rejected")
                    items(filters) { filter ->
                        FilterTab(
                            label = filter,
                            isSelected = selectedFilter == filter,
                            onClick = { selectedFilter = filter }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
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
                                "Are you sure you want to dispatch all items in this stock request (${request.requestId})?"
                            else -> ""
                        }
                    )

                    if (targetStatus == StockRequestStatus.REJECTED) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = rejectionReason,
                            onValueChange = { rejectionReason = it },
                            label = { Text("Reason (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (targetStatus) {
                            StockRequestStatus.APPROVED -> {
                                viewModel.approveRequest(request)
                            }
                            StockRequestStatus.REJECTED -> {
                                viewModel.rejectRequest(request, rejectionReason)
                            }
                            StockRequestStatus.DISPATCHED -> {
                                viewModel.dispatchRequest(request)
                            }
                            else -> {}
                        }
                        showConfirmDialog = null
                        rejectionReason = ""
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
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun FilterTab(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color(0xFF5C6BC0) else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = label,
                color = if (isSelected) Color.White else Color.Gray,
                fontSize = 14.sp,
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(request.requestId, fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    Text(request.dealerName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(request.location, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                StatusBadge(status = request.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("ITEMS SUMMARY", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(request.kitType, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack)
                    Text(request.quantity, fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("DATE", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(request.requestDate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (request.status == StockRequestStatus.PENDING) {
                        Surface(
                            onClick = onApprove,
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Check, contentDescription = "Approve All", tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                            }
                        }
                        Surface(
                            onClick = onReject,
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Close, contentDescription = "Reject All", tint = Color.Red, modifier = Modifier.size(20.dp))
                            }
                        }
                    } else if (request.status == StockRequestStatus.APPROVED) {
                        Button(
                            onClick = onDispatch,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                            modifier = Modifier.height(40.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("DISPATCH ALL", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                IconButton(onClick = onDetails) {
                    Icon(Icons.Default.Visibility, contentDescription = "View Details", tint = Color.Gray)
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