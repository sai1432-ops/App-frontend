package com.SIMATS.digitalpds

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

data class StockRequestData(
    val id: String = "",
    val adultQty: Int = 0,
    val childQty: Int = 0,
    val pasteQty: Int = 0,
    val iecQty: Int = 0,
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

    var adultBrushQty by remember { mutableStateOf("0") }
    var childBrushQty by remember { mutableStateOf("0") }
    var pasteQty by remember { mutableStateOf("0") }
    var iecQty by remember { mutableStateOf("0") }
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
            val a = adultBrushQty.toIntOrNull() ?: 0
            val c = childBrushQty.toIntOrNull() ?: 0
            val p = pasteQty.toIntOrNull() ?: 0
            val i = iecQty.toIntOrNull() ?: 0

            onSubmitSuccess(
                StockRequestData(
                    id = dealerViewModel.latestStockRequestId ?: "",
                    adultQty = a,
                    childQty = c,
                    pasteQty = p,
                    iecQty = i,
                    urgency = urgency
                )
            )
            dealerViewModel.resetStockRequestState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Request New Stock",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
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
                        Text(
                            text = "Available Units: 1200",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.howp),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Requested Items",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            RequestItemRow("Adult Brush", adultBrushQty) { adultBrushQty = it }
            RequestItemRow("Child Brush", childBrushQty) { childBrushQty = it }
            RequestItemRow("Paste", pasteQty) { pasteQty = it }
            RequestItemRow("IEC Materials", iecQty) { iecQty = it }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Urgency Level",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                UrgencyChip(
                    label = "Normal",
                    isSelected = urgency == "Normal",
                    onClick = { urgency = "Normal" }
                )
                UrgencyChip(
                    label = "Urgent (Stock Critical)",
                    isSelected = urgency == "Urgent",
                    onClick = { urgency = "Urgent" }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (dealerViewModel.stockRequestLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                Button(
                    onClick = {
                        val a = adultBrushQty.toIntOrNull() ?: 0
                        val c = childBrushQty.toIntOrNull() ?: 0
                        val p = pasteQty.toIntOrNull() ?: 0
                        val i = iecQty.toIntOrNull() ?: 0

                        dealerViewModel.submitStockRequest(
                            dealerId = dealerId,
                            adultBrushQty = a,
                            childBrushQty = c,
                            pasteQty = p,
                            iecQty = i,
                            urgency = urgency
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockRequestSuccessScreen(
    requestData: StockRequestData,
    onBackToInventory: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Request New Stock",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundWhite
                )
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.38f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF003322), Color(0xFF004D40))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(160.dp),
                    tint = Color(0xFF4CAF50)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.62f)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Stock Request Submitted!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Request ID: #${requestData.id.ifBlank { "REQ-${System.currentTimeMillis()}" }}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Adult Brushes, Child Brushes,\nPaste, IEC Materials",
                            fontSize = 14.sp,
                            color = TextGray,
                            lineHeight = 18.sp
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.howp),
                        contentDescription = null,
                        modifier = Modifier
                            .size(90.dp, 65.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Your request is being reviewed by the District Warehouse.",
                    textAlign = TextAlign.Center,
                    color = TextGray,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onBackToInventory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(
                        "Back to Inventory",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RequestItemRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Quantity",
                fontSize = 12.sp,
                color = TextGray
            )
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) onValueChange(it)
            },
            modifier = Modifier.width(90.dp),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DealerGreen,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}

@Composable
fun UrgencyChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) DealerGreen.copy(alpha = 0.1f) else Color(0xFFF1F4F8),
        border = if (isSelected) BorderStroke(1.dp, DealerGreen) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) DealerGreen else TextBlack
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SuccessScreenPreview() {
    DigitalpdsTheme {
        StockRequestSuccessScreen(StockRequestData(id = "ORD-98765"), {})
    }
}