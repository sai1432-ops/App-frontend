package com.SIMATS.digitalpds

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.ui.theme.*
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.network.StockRequestBody

data class StockRequestData(
    val id: String = "",
    val totalKits: Int = 0,
    val urgency: String = "Normal"
)

@Composable
fun RequestStockNavigation(onBackToMain: () -> Unit) {
    var currentScreen by remember { mutableStateOf("request") }
    var lastRequest by remember { mutableStateOf<StockRequestData?>(null) }

    when (currentScreen) {
        "request" -> {
            RequestStockScreen(
                onBackClick = onBackToMain,
                onSubmitSuccess = { data ->
                    lastRequest = data
                    currentScreen = "success"
                }
            )
        }

        "success" -> {
            StockRequestSuccessScreen(
                requestData = lastRequest ?: StockRequestData(id = "ORD-98765"),
                onBackToInventory = {
                    currentScreen = "request"
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestStockScreen(
    onBackClick: () -> Unit,
    onSubmitSuccess: (StockRequestData) -> Unit,
    dealerViewModel: DealerViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val dealerId = sessionManager.getUserId()

    var totalKits by remember { mutableStateOf("0") }
    var urgency by remember { mutableStateOf("Normal") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(dealerViewModel.stockRequestMessage) {
        val message = dealerViewModel.stockRequestMessage
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            dealerViewModel.clearStockRequestMessage()
        }
    }

    LaunchedEffect(dealerViewModel.stockRequestSuccess) {
        if (dealerViewModel.stockRequestSuccess) {
            val kits = totalKits.toIntOrNull() ?: 0

            onSubmitSuccess(
                StockRequestData(
                    id = dealerViewModel.latestStockRequestId ?: "",
                    totalKits = kits,
                    urgency = urgency
                )
            )
            dealerViewModel.resetStockRequestState()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F7FB))) {
        // Gradient Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DealerGreen, Color(0xFF003322))
                    )
                )
        )

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Request New Stock",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Modern Inventory Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color(0x33000000)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Inventory Summary",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Current stock level requires topping up.",
                                fontSize = 13.sp,
                                color = TextGray
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = DealerGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = DealerGreen, modifier = Modifier.padding(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(56.dp))

                Text(
                    text = "Request Quantity",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Specify how many complete kits you need.",
                    fontSize = 13.sp,
                    color = TextGray
                )
                Spacer(modifier = Modifier.height(20.dp))

                RequestItemRow(
                    title = "Complete Kits",
                    subtitle = "All items included",
                    value = totalKits,
                    icon = Icons.Default.Inventory,
                    onValueChange = { totalKits = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Kit Composition Card (Explaining the 1:1:1 ratio)
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(6.dp, RoundedCornerShape(16.dp), spotColor = Color(0x1A000000)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Kit Composition (1:1:1)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DealerGreen,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            KitPart(icon = Icons.Default.Inventory, label = "1 Brush")
                            VerticalDivider(modifier = Modifier.height(24.dp), color = Color(0xFFCBD5E1))
                            KitPart(icon = Icons.Default.Inventory, label = "1 Paste")
                            VerticalDivider(modifier = Modifier.height(24.dp), color = Color(0xFFCBD5E1))
                            KitPart(icon = Icons.Default.Description, label = "1 Flyer")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = "Urgency Level",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        UrgencyChip(
                            label = "Routine",
                            isSelected = urgency == "Normal",
                            onClick = { urgency = "Normal" },
                            isUrgent = false
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        UrgencyChip(
                            label = "Critical",
                            isSelected = urgency == "Urgent",
                            onClick = { urgency = "Urgent" },
                            isUrgent = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                if (dealerViewModel.stockRequestLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = DealerGreen)
                    }
                } else {
                    Button(
                        onClick = {
                            val kits = totalKits.toIntOrNull() ?: 0
                            if (kits > 0) {
                                val token = sessionManager.getAccessToken() ?: ""
                                dealerViewModel.submitStockRequest(
                                    dealerId = dealerId,
                                    token = token,
                                    totalKits = kits,
                                    urgency = urgency
                                )
                            } else {
                                dealerViewModel.stockRequestMessage = "Please enter kit quantity"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = DealerGreen),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DealerGreen)
                    ) {
                        Text(
                            "Submit Stock Request",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun RequestItemRow(
    title: String,
    subtitle: String,
    value: String,
    icon: ImageVector,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x11000000)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFF3F4F6),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(icon, contentDescription = null, tint = DealerGreen, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Text(subtitle, fontSize = 12.sp, color = TextGray)
            }
            OutlinedTextField(
                value = value,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) onValueChange(it)
                },
                modifier = Modifier.width(80.dp),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DealerGreen,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
    }
}

@Composable
fun UrgencyChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isUrgent: Boolean
) {
    val activeColor = if (isUrgent) Color(0xFFD32F2F) else DealerGreen
    val activeBgColor = if (isUrgent) Color(0xFFFFEBEE) else DealerGreen.copy(alpha = 0.1f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) activeBgColor else Color(0xFFF1F4F8),
        border = if (isSelected) BorderStroke(1.dp, activeColor) else null
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 14.dp)) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) activeColor else TextGray
            )
        }
    }
}

@Composable
fun KitPart(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = DealerGreen.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextBlack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockRequestSuccessScreen(
    requestData: StockRequestData,
    onBackToInventory: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F7FB))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = DealerGreen.copy(alpha = 0.15f),
                modifier = Modifier.size(100.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = DealerGreen,
                    modifier = Modifier.padding(20.dp).fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Request Sent!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextBlack
            )
            
            Text(
                "Your stock request has been submitted to the admin.",
                fontSize = 16.sp,
                color = TextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("REQUEST SUMMARY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Kits Requested", fontSize = 16.sp, color = TextBlack)
                        Text("${requestData.totalKits}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DealerGreen)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Urgency", fontSize = 16.sp, color = TextBlack)
                        Text(requestData.urgency.uppercase(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (requestData.urgency == "Urgent") Color.Red else DealerGreen)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onBackToInventory,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DealerGreen)
            ) {
                Text("Back to Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuccessScreenPreview() {
    StockRequestSuccessScreen(StockRequestData(id = "ORD-98765", totalKits = 10, urgency = "Urgent"), {})
}
