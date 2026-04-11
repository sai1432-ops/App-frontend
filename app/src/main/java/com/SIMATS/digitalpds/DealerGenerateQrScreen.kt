package com.SIMATS.digitalpds

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.SIMATS.digitalpds.ui.theme.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealerGenerateQrScreen(
    dealerId: Int,
    qrValue: String? = null,
    onBackClick: () -> Unit
) {
    val qrPayload = remember(dealerId, qrValue) {
        if (!qrValue.isNullOrBlank()) {
            qrValue
        } else {
            """
            {
              "type": "DEALER",
              "dealer_id": $dealerId
            }
            """.trimIndent()
        }
    }

    val qrBitmap = remember(qrPayload) {
        generateQrBitmap(qrPayload)
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {
        // Top Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DealerGreen, BackgroundWhite)
                    )
                )
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Kit Distribution QR", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0x0D000000)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Scan to Confirm Receipt",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    Text(
                        text = "Dealer ID: $dealerId",
                        fontSize = 14.sp,
                        color = TextGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (qrBitmap != null) {
                        Box(
                            modifier = Modifier
                                .size(260.dp)
                                .background(Color(0xFFF1F4F4), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Distribution QR Code",
                                modifier = Modifier.size(220.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        
                        if (!qrValue.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                color = Color(0xFFF1F8E9),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, DealerGreen.copy(alpha = 0.2f))
                            ) {
                                Text(
                                    text = qrValue,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DealerGreen,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    letterSpacing = 2.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "This is your standard dealer QR. The user scans this to confirm receipt.",
                        fontSize = 13.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Static Dealer QR",
                fontSize = 12.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
}

private fun generateQrBitmap(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

@Preview(showBackground = true)
@Composable
fun DealerGenerateQrScreenPreview() {
    DigitalpdsTheme {
        DealerGenerateQrScreen(
            dealerId = 12345,
            onBackClick = {}
        )
    }
}