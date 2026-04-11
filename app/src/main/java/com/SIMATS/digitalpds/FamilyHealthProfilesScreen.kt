package com.SIMATS.digitalpds

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.ui.theme.*
import com.SIMATS.digitalpds.ui.theme.textGraySub
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background

data class FamilyMember(
    val id: Int,
    val name: String,
    val oralHealthScore: Int,
    val riskLevel: String,
    val lastScan: String,
    val imageResId: Int
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyHealthProfilesScreen(
    familyMembers: List<FamilyMember> = emptyList(),
    onBackClick: () -> Unit,
    onViewProfileClick: (FamilyMember) -> Unit = {},
    viewModel: FamilyHealthViewModel = viewModel()
) {
    val memberReports by viewModel.memberReports.collectAsState()
    val softBlue = PrimaryBlue
    val cyanGradient = Color(0xFF00BCD4)

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                        .padding(horizontal = 24.dp, vertical = 40.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Family Health Hub",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Monitor the oral health status of your loved ones",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (familyMembers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No family profiles found.", color = TextGray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(familyMembers) { member ->
                        val report = memberReports[member.id]
                        val displayMember = if (report != null) {
                            member.copy(
                                riskLevel = report.riskLevel,
                                lastScan = report.createdAt
                            )
                        } else {
                            member
                        }
                        ModernFamilyHealthCard(displayMember, onViewProfileClick)
                    }
                }
            }
        }
    }
}

@Composable
fun ModernFamilyHealthCard(
    member: FamilyMember,
    onViewProfileClick: (FamilyMember) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InitialsAvatar(
                    name = member.name,
                    modifier = Modifier.size(60.dp),
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Last Assessment: ${member.lastScan}",
                        fontSize = 12.sp,
                        color = textGraySub
                    )
                }

                val riskColor = when (member.riskLevel.uppercase()) {
                    "LOW" -> Color(0xFF2E7D32)
                    "MEDIUM" -> Color(0xFFEF6C00)
                    "HIGH" -> Color(0xFFC62828)
                    else -> Color.Gray
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = riskColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, riskColor.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = member.riskLevel,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = riskColor
                        )
                    }
                    Text(
                        text = "Risk Level",
                        fontSize = 10.sp,
                        color = textGraySub,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onViewProfileClick(member) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("View Detailed Health Status", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FamilyHealthProfilesScreenPreview() {
    DigitalpdsTheme {
        FamilyHealthProfilesScreen(onBackClick = {})
    }
}
