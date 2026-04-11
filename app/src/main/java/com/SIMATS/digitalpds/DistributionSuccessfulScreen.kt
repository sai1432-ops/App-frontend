package com.SIMATS.digitalpds

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistributionSuccessfulScreen(
    brushes: Int = 2,
    fluoridePaste: Int = 3,
    iecPamphlets: Int = 3,
    onBackClick: () -> Unit = {},
    onReturnToDashboard: () -> Unit = {}
) {
    val receiptId = remember {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val randomCode = (1000..9999).random().toString()
        "REC-$today-5612-$randomCode"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF4F7FB)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Immersive Header Graphic
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(DealerGreen, Color(0xFF003322))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(130.dp).shadow(24.dp, CircleShape, spotColor = Color(0x66000000)),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        tint = DealerGreen
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(220.dp))

                // Receipt Ticket Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color(0x22000000)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Distribution Successful!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextBlack,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Kits have been successfully allocated to the household.",
                            fontSize = 14.sp,
                            color = TextGray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(24.dp))

                        // Receipt Number
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Receipt ID", color = TextGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(receiptId, fontWeight = FontWeight.Bold, color = DealerGreen, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Summary
                        if (brushes > 0) ReceiptItemRow(name = "Brushes", count = brushes)
                        if (fluoridePaste > 0) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ReceiptItemRow(name = "Fluoride Paste", count = fluoridePaste)
                        }
                        if (iecPamphlets > 0) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ReceiptItemRow(name = "IEC Pamphlets", count = iecPamphlets)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))

                Button(
                    onClick = onReturnToDashboard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = DealerGreen),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DealerGreen)
                ) {
                    Text("Return to Dashboard", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ReceiptItemRow(name: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = Color(0xFFF1F4F8)
            ) {
                Text(
                    text = "${count}x",
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = DealerGreen,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = name, fontSize = 16.sp, color = TextBlack, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DistributionSuccessfulScreenPreview() {
    DigitalpdsTheme {
        DistributionSuccessfulScreen()
    }
}
