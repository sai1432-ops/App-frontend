package com.SIMATS.digitalpds

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
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
import com.SIMATS.digitalpds.network.FamilyMemberResponse
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdEligibilityScreen(
    householdId: String = "HH-5612",
    headName: String = "Rajesh Kumar",
    category: String = "PHH",
    familyMembers: List<FamilyMemberResponse> = emptyList(),
    onBackClick: () -> Unit,
    onProceedClick: (Boolean) -> Unit
) {
    var isKitReturned by remember { mutableStateOf<Boolean?>(null) }
    
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
                            "Household Eligibility",
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

                // Modern Head Profile Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color(0x33000000)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F4F8),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.padding(16.dp),
                                tint = Color.Gray.copy(alpha = 0.6f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Household: $householdId",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = headName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DealerGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Category: $category",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DealerGreen,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = "Family Members List",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Family Members List
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (familyMembers.isEmpty()) {
                        Text(
                            "No family members registered.",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                    } else {
                        familyMembers.forEach { member ->
                            val kitType = if (member.age < 12) "Child Kit" else "Adult Kit"
                            FamilyEligibilityItem(
                                name = "${member.memberName} - ${member.relation}",
                                kit = "1x $kitType: Brush, Paste, IEC"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Old Kit Return Status",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Confirm if the previous distribution kit has been securely returned for replacement.",
                    fontSize = 14.sp,
                    color = TextGray,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = { isKitReturned = true },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isKitReturned == true) DealerGreen.copy(alpha = 0.1f) else Color.White,
                            contentColor = if (isKitReturned == true) DealerGreen else TextBlack
                        ),
                        border = BorderStroke(1.dp, if (isKitReturned == true) DealerGreen else Color.LightGray)
                    ) {
                        Text("Yes, Returned", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { isKitReturned = false },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isKitReturned == false) Color.Red.copy(alpha = 0.1f) else Color.White,
                            contentColor = if (isKitReturned == false) Color.Red else TextBlack
                        ),
                        border = BorderStroke(1.dp, if (isKitReturned == false) Color.Red else Color.LightGray)
                    ) {
                        Text("No, Not Returned", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        if (isKitReturned != null) {
                            onProceedClick(isKitReturned!!)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = if (isKitReturned != null) DealerGreen else Color.Transparent),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isKitReturned != null) DealerGreen else Color.LightGray
                    ),
                    enabled = isKitReturned != null
                ) {
                    Text(
                        "Proceed to Handover",
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
fun FamilyEligibilityItem(name: String, kit: String) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x11000000)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F4F8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = DealerGreen
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = kit,
                    fontSize = 13.sp,
                    color = TextGray
                )
            }
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Eligible",
                tint = DealerGreen,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HouseholdEligibilityScreenPreview() {
    DigitalpdsTheme {
        HouseholdEligibilityScreen(onBackClick = {}, onProceedClick = {})
    }
}
