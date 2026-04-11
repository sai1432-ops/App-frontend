package com.SIMATS.digitalpds

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.AnimatedVisibility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmKitReceiptScreen(
    totalFamilyMembers: Int = 1,
    onBackClick: () -> Unit,
    onFinalizeClick: (
        oldKitReturned: Boolean,
        brushQuantity: Int,
        pasteQuantity: Int,
        iecQuantity: Int
    ) -> Unit = { _, _, _, _ -> },
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onManualConfirmClick: (
        qrValue: String,
        oldKitReturned: Boolean,
        brushQuantity: Int,
        pasteQuantity: Int,
        iecQuantity: Int
    ) -> Unit = { _, _, _, _, _ -> }
) {
    val brushQuantity = totalFamilyMembers 
    val pasteQuantity = totalFamilyMembers
    val iecQuantity = totalFamilyMembers
    
    var oldKitReturned by remember { mutableStateOf<Boolean?>(null) }
    
    val softBlue = PrimaryBlue
    val cyanGradient = Color(0xFF00BCD4)

    Scaffold(
        bottomBar = {
            UserBottomNavigationBar(
                currentScreen = "Kits",
                onHomeClick = onHomeClick,
                onKitsClick = onKitsClick,
                onLearnClick = onLearnClick,
                onConsultClick = onConsultClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.linearGradient(colors = listOf(softBlue, cyanGradient)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        
                        Text(
                            "Kit Confirmation",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Inventory2, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Standard Entitlement", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Calculated for $totalFamilyMembers family members",
                    fontSize = 14.sp,
                    color = textGraySub,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(20.dp))

                ModernEntitlementCard(
                    label = "Brushes",
                    value = brushQuantity.toString(),
                    icon = Icons.Default.Brush,
                    accent = softBlue
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ModernEntitlementCard(
                    label = "Toothpastes",
                    value = pasteQuantity.toString(),
                    icon = Icons.Default.Waves,
                    accent = Color(0xFF10B981)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ModernEntitlementCard(
                    label = "IEC Materials",
                    value = iecQuantity.toString(),
                    icon = Icons.Default.MenuBook,
                    accent = Color(0xFFF59E0B)
                )

                Spacer(modifier = Modifier.height(32.dp))
                Divider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(32.dp))

                // Verification Section
                Text(
                    "Verification",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Did you return the old kit?",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ModernReturnOption(
                                text = "Yes, Returned",
                                isSelected = oldKitReturned == true,
                                onClick = { oldKitReturned = true },
                                modifier = Modifier.weight(1f),
                                activeColor = Color(0xFF10B981)
                            )
                            ModernReturnOption(
                                text = "No, Not Yet",
                                isSelected = oldKitReturned == false,
                                onClick = { oldKitReturned = false },
                                modifier = Modifier.weight(1f),
                                activeColor = Color(0xFFEF4444)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                var manualDealerQrValue by remember { mutableStateOf("") }
                
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Alternative: Manual Entry",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = manualDealerQrValue,
                            onValueChange = { manualDealerQrValue = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter Dealer QR ID", fontSize = 14.sp) },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Numbers, null, tint = softBlue) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = softBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )
                        
                        AnimatedVisibility(visible = manualDealerQrValue.isNotBlank()) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        onManualConfirmClick(
                                            manualDealerQrValue,
                                            oldKitReturned ?: false,
                                            brushQuantity,
                                            pasteQuantity,
                                            iecQuantity
                                        )
                                    },
                                    enabled = oldKitReturned != null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF10B981),
                                        disabledContainerColor = Color(0xFFE2E8F0)
                                    )
                                ) {
                                    Text("CONFIRM WITH QR ID", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                    Text("  OR USE SCANNER  ", color = textGraySub, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Divider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        onFinalizeClick(
                            oldKitReturned ?: false,
                            brushQuantity,
                            pasteQuantity,
                            iecQuantity
                        )
                    },
                    enabled = oldKitReturned != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = softBlue,
                        disabledContainerColor = Color(0xFFE2E8F0)
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "SCAN QR TO FINALIZE",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ModernEntitlementCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accent: Color) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = accent.copy(alpha = 0.1f)
            ) {
                Icon(icon, null, modifier = Modifier.padding(12.dp), tint = accent)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 14.sp, color = textGraySub, fontWeight = FontWeight.Medium)
                Text("Standard Kit Item", fontSize = 11.sp, color = textGraySub.copy(alpha = 0.7f))
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextBlack)
                Spacer(modifier = Modifier.width(4.dp))
                Text("PCS", fontSize = 10.sp, color = textGraySub, modifier = Modifier.padding(bottom = 4.dp), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ModernReturnOption(text: String, isSelected: Boolean, onClick: () -> Unit, activeColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = if (isSelected) activeColor.copy(alpha = 0.1f) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isSelected) activeColor else Color(0xFFE2E8F0))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) activeColor else textGraySub
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmKitReceiptScreenPreview() {
    DigitalpdsTheme {
        ConfirmKitReceiptScreen(onBackClick = {})
    }
}
