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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalDistributionHandoverScreen(
    oldKitReturned: Boolean,
    familyMemberCount: Int,
    householdId: String,
    headName: String,
    category: String = "PHH",
    onBackClick: () -> Unit,
    onCompleteClick: (Int, Int, Int) -> Unit
) {
    val brushes = familyMemberCount
    val fluoridePaste = familyMemberCount
    val iecPamphlets = familyMemberCount

    val isButtonEnabled = brushes > 0 || fluoridePaste > 0 || iecPamphlets > 0

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
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Final Handover",
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

                Card(
                    modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color(0x33000000)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Household: $householdId",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Head: $headName",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Old Kit Returned Badge
                        val statusText = if (oldKitReturned) "Kit Return: Verified" else "Kit Not Returned"
                        val statusColor = if (oldKitReturned) DealerGreen else Color(0xFFD32F2F)
                        val statusIcon = if (oldKitReturned) Icons.Default.CheckCircle else Icons.Default.Cancel

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = statusColor.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = statusIcon,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = statusText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = "Distribute Kits",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(16.dp))

                DistributionItemRow(
                    name = "Brushes",
                    count = brushes
                )
                Spacer(modifier = Modifier.height(12.dp))
                DistributionItemRow(
                    name = "Fluoride Paste",
                    count = fluoridePaste
                )
                Spacer(modifier = Modifier.height(12.dp))
                DistributionItemRow(
                    name = "IEC Pamphlets",
                    count = iecPamphlets
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = { onCompleteClick(brushes, fluoridePaste, iecPamphlets) },
                    enabled = isButtonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(if (isButtonEnabled) 8.dp else 0.dp, RoundedCornerShape(12.dp), spotColor = DealerGreen),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DealerGreen,
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text(
                        "Complete Distribution",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun DistributionItemRow(
    name: String,
    count: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x11000000)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Auto-calculated based on members",
                    fontSize = 13.sp,
                    color = DealerGreen
                )
            }
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = DealerGreen.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = count.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DealerGreen
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinalDistributionHandoverScreenPreview() {
    DigitalpdsTheme {
        FinalDistributionHandoverScreen(
            oldKitReturned = true,
            familyMemberCount = 4,
            householdId = "HH-5612",
            headName = "Rajesh Kumar",
            onBackClick = {}, 
            onCompleteClick = { _, _, _ -> }
        )
    }
}
