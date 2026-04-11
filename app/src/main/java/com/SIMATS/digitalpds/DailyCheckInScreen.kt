package com.SIMATS.digitalpds

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.CheckinRequest
import com.SIMATS.digitalpds.network.FamilyMemberResponse
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.notification.CheckInPrefs
import com.SIMATS.digitalpds.ui.theme.*
import com.SIMATS.digitalpds.ui.theme.textGraySub
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.border
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckInScreen(
    userId: Int,
    userName: String,
    familyMembers: List<FamilyMemberResponse>,
    currentCompletedSessions: List<Pair<Boolean, Boolean>>,
    onBackClick: () -> Unit,
    onCheckInSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedSession by remember { mutableStateOf("Morning") }
    var isPrimaryUserChecked by remember { mutableStateOf(false) }
    var brushedFamilyMemberIds by remember { mutableStateOf(setOf<Int>()) }
    var isSubmitting by remember { mutableStateOf(false) }

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
                            "Daily Habits",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Session Toggle
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            SessionChip(
                                label = "Morning",
                                icon = Icons.Default.LightMode,
                                isSelected = selectedSession == "Morning",
                                onClick = { selectedSession = "Morning" }
                            )
                            SessionChip(
                                label = "Evening",
                                icon = Icons.Default.DarkMode,
                                isSelected = selectedSession == "Evening",
                                onClick = { selectedSession = "Evening" }
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    "Brushing Tracker",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Text(
                    "Mark who brushed in this session",
                    fontSize = 14.sp,
                    color = textGraySub
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Primary User Card
                ModernBrushingMemberCard(
                    name = "$userName (Me)",
                    isBrushed = isPrimaryUserChecked,
                    onToggle = { isPrimaryUserChecked = !isPrimaryUserChecked },
                    accent = softBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Family Members
                familyMembers.forEach { member ->
                    ModernBrushingMemberCard(
                        name = member.memberName,
                        isBrushed = brushedFamilyMemberIds.contains(member.id),
                        onToggle = {
                            brushedFamilyMemberIds =
                                if (brushedFamilyMemberIds.contains(member.id)) {
                                    brushedFamilyMemberIds - member.id
                                } else {
                                    brushedFamilyMemberIds + member.id
                                }
                        },
                        accent = softBlue
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Action
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Button(
                    onClick = {
                        isSubmitting = true
                        scope.launch {
                            try {
                                val sessionValue = selectedSession.uppercase()
                                var anySuccess = false

                                if (isPrimaryUserChecked) {
                                    val response = RetrofitClient.apiService.brushingCheckIn(
                                        "Bearer ${SessionManager(context).getAccessToken() ?: ""}",
                                        CheckinRequest(userId, null, sessionValue)
                                    )
                                    if (response.isSuccessful) {
                                        anySuccess = true
                                        if (sessionValue == "MORNING") CheckInPrefs.setMorningCheckedIn(context, true)
                                        else CheckInPrefs.setEveningCheckedIn(context, true)
                                    }
                                }

                                brushedFamilyMemberIds.forEach { memberId ->
                                    val response = RetrofitClient.apiService.brushingCheckIn(
                                        "Bearer ${SessionManager(context).getAccessToken() ?: ""}",
                                        CheckinRequest(userId, memberId, sessionValue)
                                    )
                                    if (response.isSuccessful) anySuccess = true
                                }

                                if (anySuccess) {
                                    Toast.makeText(context, "Check-in successful!", Toast.LENGTH_SHORT).show()
                                    onCheckInSuccess()
                                } else {
                                    Toast.makeText(context, "Check-in failed", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isSubmitting = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    enabled = (isPrimaryUserChecked || brushedFamilyMemberIds.isNotEmpty()) && !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = softBlue)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Confirm Check-in", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SessionChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = if (isSelected) Color.White else Color.Transparent,
        contentColor = if (isSelected) PrimaryBlue else Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ModernBrushingMemberCard(name: String, isBrushed: Boolean, onToggle: () -> Unit, accent: Color) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .then(if (isBrushed) Modifier.border(1.dp, accent, RoundedCornerShape(20.dp)) else Modifier),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = if (isBrushed) accent.copy(alpha = 0.05f) else Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isBrushed) accent else Color(0xFFF1F5F9)
            ) {
                Icon(
                    Icons.Default.Person,
                    null,
                    modifier = Modifier.padding(12.dp),
                    tint = if (isBrushed) Color.White else Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(name, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            
            Checkbox(
                checked = isBrushed,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = accent, uncheckedColor = Color(0xFFE2E8F0))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyCheckInScreenPreview() {
    DigitalpdsTheme {
        DailyCheckInScreen(
            userId = 1,
            userName = "Hari",
            familyMembers = emptyList(),
            currentCompletedSessions = List(7) { false to false },
            onBackClick = {},
            onCheckInSuccess = {}
        )
    }
}
