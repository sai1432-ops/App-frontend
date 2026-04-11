package com.SIMATS.digitalpds

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.ui.theme.*
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStockRequestDetailsScreen(
    request: AdminStockRequest,
    onBackClick: () -> Unit = {},
    viewModel: StockViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.message) {
        viewModel.message?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    var showDispatchDialog by remember { mutableStateOf(false) }
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    
    var courierName by remember { mutableStateOf("") }
    var trackingId by remember { mutableStateOf("") }
    var dispatchAdminNote by remember { mutableStateOf("") }
    var approveAdminNote by remember { mutableStateOf("") }
    var rejectReason by remember { mutableStateOf("") }

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
                    title = { Text("Request Details", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        containerColor = Color(0xFFF8FBFF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = request.requestId,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        StatusBadgeDetails(status = request.status)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = request.dealerName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val locationText = buildString {
                            val addr = request.dealerAddress ?: ""
                            append(addr)
                            
                            val cityState = listOfNotNull(request.dispatchCity, request.dispatchState)
                                .filter { it.isNotBlank() }
                                .joinToString(", ")
                                
                            if (cityState.isNotBlank()) {
                                if (this.isNotEmpty()) append(", ")
                                append(cityState)
                            } else if (addr.isBlank() && request.location.isNotBlank()) {
                                append(request.location)
                            }
                        }
                        Text(
                            text = locationText,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            DetailsSection(title = "Request Information") {
                DetailRow(
                    icon = Icons.Default.Inventory,
                    label = "Total Kits Requested",
                    value = "${request.totalKits} Kits"
                )
                Text(
                    "Note: 1 Kit contains 1 Brush, 1 Paste, and 1 Flyer.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 32.dp, bottom = 8.dp)
                )
                DetailRow(icon = Icons.Default.CalendarToday, label = "Request Date", value = request.requestDate)
            }

            if (request.status != StockRequestStatus.PENDING) {
                DetailsSection(title = "Processing Details") {
                    when (request.status) {
                        StockRequestStatus.APPROVED, StockRequestStatus.DISPATCHED, StockRequestStatus.DELIVERED -> {
                            DetailRow(
                                icon = Icons.Default.CheckCircle,
                                label = "Approved At",
                                value = request.approvedAt ?: "N/A",
                                valueColor = Color(0xFF4CAF50)
                            )
                            if (!request.adminNote.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Admin Remarks:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray)
                                Text(request.adminNote, fontSize = 14.sp, color = TextBlack)
                            }
                        }
                        StockRequestStatus.REJECTED -> {
                            DetailRow(
                                icon = Icons.Default.Cancel,
                                label = "Rejected At",
                                value = request.rejectedAt ?: "N/A",
                                valueColor = Color.Red
                            )
                            if (!request.adminNote.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Reason / Remarks:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray)
                                Text(request.adminNote, fontSize = 14.sp, color = TextBlack)
                            }
                        }
                        else -> {}
                    }
                }
            }

            if (request.status == StockRequestStatus.DISPATCHED || request.status == StockRequestStatus.DELIVERED) {
                DetailsSection(title = "Shipping Information") {
                    DetailRow(icon = Icons.Default.LocalShipping, label = "Courier", value = request.courierName ?: "N/A")
                    DetailRow(icon = Icons.Default.Numbers, label = "Tracking ID", value = request.trackingId ?: "N/A")
                    DetailRow(icon = Icons.Default.Schedule, label = "Dispatched At", value = request.dispatchedAt ?: "N/A")
                    if (request.status == StockRequestStatus.DELIVERED) {
                        DetailRow(
                            icon = Icons.Default.CheckCircle,
                            label = "Received By Dealer",
                            value = request.deliveredAt ?: "N/A",
                            valueColor = Color(0xFF4CAF50)
                        )
                    }
                }
            }

            if (request.status == StockRequestStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showApproveDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Approve")
                    }

                    Button(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Reject")
                    }
                }
            }

            if (request.status == StockRequestStatus.APPROVED) {
                Button(
                    onClick = { showDispatchDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B))
                ) {
                    Text("Dispatch")
                }
            }
        }
    }

    if (showApproveDialog) {
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            title = { Text("Approve Request Group") },
            text = {
                Column {
                    Text("Proceed with approving this request?")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val token = com.SIMATS.digitalpds.SessionManager(context).getAccessToken() ?: ""
                        viewModel.approveRequest(token, request, "") {
                            showApproveDialog = false
                            onBackClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Approve")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApproveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Reject Request Group") },
            text = {
                Column {
                    Text("Please provide a reason for rejection:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        label = { Text("Rejection Reason") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rejectReason.isNotBlank()) {
                            val token = com.SIMATS.digitalpds.SessionManager(context).getAccessToken() ?: ""
                            viewModel.rejectRequest(token, request, rejectReason) {
                                showRejectDialog = false
                                onBackClick()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    enabled = rejectReason.isNotBlank()
                ) {
                    Text("Reject")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDispatchDialog) {
        AlertDialog(
            onDismissRequest = { showDispatchDialog = false },
            title = { Text("Dispatch Request Group") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("Enter shipping details for request ${request.requestId}:")
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
                            Text("Contact: ${request.contactPhone ?: "N/A"}", fontSize = 14.sp)
                            Text("Address: ${request.dealerAddress ?: "No address provided"}", fontSize = 14.sp)
                            
                            val cityState = listOfNotNull(request.dispatchCity, request.dispatchState)
                                .filter { it.isNotBlank() }
                                .joinToString(", ")
                            if (cityState.isNotBlank()) {
                                Text("City/State: $cityState", fontSize = 14.sp)
                            }
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
                        value = dispatchAdminNote,
                        onValueChange = { dispatchAdminNote = it },
                        label = { Text("Admin Note / Remarks (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (courierName.isNotBlank() && trackingId.isNotBlank()) {
                            val token = SessionManager(context).getAccessToken() ?: ""
                            viewModel.dispatchRequest(token, request, courierName, trackingId, dispatchAdminNote) {
                                showDispatchDialog = false
                                onBackClick()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B))
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDispatchDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = TextBlack
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = TextGray)
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
        }
    }
}

@Composable
private fun StatusBadgeDetails(status: StockRequestStatus) {
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
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminStockRequestDetailsScreenPreview() {
    AdminStockRequestDetailsScreen(
        request = AdminStockRequest(
            id = 1,
            itemIds = listOf(1),
            requestId = "REQ-001",
            dealerId = "1",
            dealerName = "John Doe",
            location = "New York, NY",
            totalKits = 50,
            status = StockRequestStatus.PENDING,
            requestDate = "2024-02-20"
        )
    )
}
