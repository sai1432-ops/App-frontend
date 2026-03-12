package com.SIMATS.digitalpds

import androidx.compose.foundation.layout.*
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
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
                        Text(text = request.location, fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            DetailsSection(title = "Request Information") {
                DetailRow(icon = Icons.Default.Inventory, label = "Items", value = request.kitType)
                DetailRow(icon = Icons.Default.Dataset, label = "Quantity", value = request.quantity)
                DetailRow(icon = Icons.Default.CalendarToday, label = "Request Date", value = request.requestDate)
            }

            if (request.status != StockRequestStatus.PENDING) {
                DetailsSection(title = "Processing Details") {
                    when (request.status) {
                        StockRequestStatus.APPROVED, StockRequestStatus.DISPATCHED -> {
                            DetailRow(
                                icon = Icons.Default.CheckCircle,
                                label = "Approved At",
                                value = request.approvedAt ?: "N/A",
                                valueColor = Color(0xFF4CAF50)
                            )
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
                                Text("Reason:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray)
                                Text(request.adminNote, fontSize = 14.sp, color = TextBlack)
                            }
                        }
                        else -> {}
                    }
                }
            }

            if (request.status == StockRequestStatus.DISPATCHED) {
                DetailsSection(title = "Shipping Information") {
                    DetailRow(icon = Icons.Default.LocalShipping, label = "Courier", value = request.courierName ?: "N/A")
                    DetailRow(icon = Icons.Default.Numbers, label = "Tracking ID", value = request.trackingId ?: "N/A")
                    DetailRow(icon = Icons.Default.Schedule, label = "Dispatched At", value = request.dispatchedAt ?: "N/A")
                }
            }

            if (request.status == StockRequestStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.approveRequest(request, onDone = onBackClick) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Approve")
                    }

                    Button(
                        onClick = { viewModel.rejectRequest(request, "", onDone = onBackClick) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Reject")
                    }
                }
            }

            if (request.status == StockRequestStatus.APPROVED) {
                Button(
                    onClick = { viewModel.dispatchRequest(request, onDone = onBackClick) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B))
                ) {
                    Text("Dispatch")
                }
            }
        }
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
            kitType = "Standard Starter Kits",
            quantity = "50 Units",
            status = StockRequestStatus.PENDING,
            requestDate = "2024-02-20"
        )
    )
}
