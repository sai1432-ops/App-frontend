package com.SIMATS.digitalpds

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    familyMembers: List<FamilyMemberResponse> = emptyList(),
    onBackClick: () -> Unit,
    onProceedClick: () -> Unit
) {
    var isKitReturned by remember { mutableStateOf<Boolean?>(null) }
    
    val headMember = familyMembers.find { it.relation.equals("Head", ignoreCase = true) }
    val headName = headMember?.memberName ?: familyMembers.firstOrNull()?.memberName ?: "Rajesh Kumar"

    Scaffold(
        topBar = {
            @Suppress("OPT_IN_USAGE")
            TopAppBar(
                title = {
                    Text(
                        "Household Eligibility",
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

            // Head Profile Placeholder (Replaced Image)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF1F4F8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color.Gray.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Household Header Info
            Text(
                text = "Household ID: ****5612",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Text(
                text = "Head: $headName",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextBlack,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Category: PHH (Primary Household)",
                fontSize = 16.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Family Members List
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
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
                            kit = "1x $kitType: Brush, Paste, IEC materials"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Old Kit Return Status
            Text(
                text = "Old Kit Return Status",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Confirm if the previous kit has been returned for replacement.",
                fontSize = 15.sp,
                color = TextBlack,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { isKitReturned = true },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isKitReturned == true) DealerGreen.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (isKitReturned == true) DealerGreen else TextBlack
                    ),
                    border = BorderStroke(1.dp, if (isKitReturned == true) DealerGreen else Color.LightGray)
                ) {
                    Text("Yes", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { isKitReturned = false },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isKitReturned == false) Color.Red.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (isKitReturned == false) Color.Red else TextBlack
                    ),
                    border = BorderStroke(1.dp, if (isKitReturned == false) Color.Red else Color.LightGray)
                ) {
                    Text("No", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onProceedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    "Proceed to Distribution",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FamilyEligibilityItem(name: String, kit: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F4F8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.6f)
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
            Text(
                text = kit,
                fontSize = 14.sp,
                color = TextGray
            )
        }
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HouseholdEligibilityScreenPreview() {
    DigitalpdsTheme {
        HouseholdEligibilityScreen(onBackClick = {}, onProceedClick = {})
    }
}
