package com.SIMATS.digitalpds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.KitReceivedResponse
import com.SIMATS.digitalpds.ui.theme.*
import com.SIMATS.digitalpds.ui.theme.textGraySub
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border

import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitReceivedScreen(
    kitData: KitReceivedResponse?,
    onBackClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val softBlue = PrimaryBlue
    val cyanGradient = Color(0xFF00BCD4)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                    .background(Brush.linearGradient(listOf(softBlue, cyanGradient))),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(120.dp).padding(10.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Icon(Icons.Default.Inventory, null, modifier = Modifier.padding(24.dp), tint = Color.White)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Kit Received!", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text("Inventory update successful", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .offset(y = (-40).dp)
            ) {
                // Receipt Card
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(24.dp))
                        .border(1.dp, PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Household Receipt", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        ReceiptRow("Brushes", "${kitData?.brushReceived ?: 0} Units", Icons.Default.Brush)
                        ReceiptRow("Pastes", "${kitData?.pasteReceived ?: 0} Units", Icons.Default.Waves)
                        ReceiptRow("IEC Guides", "${kitData?.iecReceived ?: 0} Sets", Icons.Default.MenuBook)
                        ReceiptRow("Return Verified", if (kitData?.oldKitReturned == true) "Yes" else "No", Icons.Default.Verified)
                    }
                }

                if (kitData?.show_red_alert == true) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(kitData.red_alert_message ?: "Old kit pending return", color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Points Area
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF1F5F9).copy(alpha = 0.5f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color.White) {
                            Icon(Icons.Default.Stars, null, modifier = Modifier.padding(10.dp), tint = Color(0xFFF59E0B))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Hygiene Points", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                            Text("Earned for timely receipt", fontSize = 12.sp, color = textGraySub)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text("+50", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onDashboardClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = softBlue)
                ) {
                    Text("BACK TO DASHBOARD", fontSize = 14.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = textGraySub)
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, fontSize = 14.sp, color = textGraySub, fontWeight = FontWeight.Medium)
        }
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextBlack)
    }
}

@Composable
fun ReceiptItem(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextBlack)
        Text(description, fontSize = 14.sp, color = TextGray)
    }
}

@Preview(showBackground = true)
@Composable
fun KitReceivedScreenPreview() {
    DigitalpdsTheme {
        KitReceivedScreen(
            kitData = null,
            onBackClick = {},
            onDashboardClick = {}
        )
    }
}
