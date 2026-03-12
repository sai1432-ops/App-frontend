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
import com.SIMATS.digitalpds.ui.theme.*
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Daily Brushing Check-in",
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Session Mode",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedSession == "Morning",
                    onClick = { selectedSession = "Morning" },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                )
                Text("Morning", modifier = Modifier.clickable { selectedSession = "Morning" })

                Spacer(modifier = Modifier.width(24.dp))

                RadioButton(
                    selected = selectedSession == "Evening",
                    onClick = { selectedSession = "Evening" },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                )
                Text("Evening", modifier = Modifier.clickable { selectedSession = "Evening" })
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Mark who brushed today",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BrushingMemberCard(
                    name = "$userName (Me)",
                    isBrushed = isPrimaryUserChecked,
                    onToggle = { isPrimaryUserChecked = !isPrimaryUserChecked }
                )

                familyMembers.forEach { member ->
                    BrushingMemberCard(
                        name = member.memberName,
                        isBrushed = brushedFamilyMemberIds.contains(member.id),
                        onToggle = {
                            brushedFamilyMemberIds =
                                if (brushedFamilyMemberIds.contains(member.id)) {
                                    brushedFamilyMemberIds - member.id
                                } else {
                                    brushedFamilyMemberIds + member.id
                                }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isSubmitting = true
                    scope.launch {
                        try {
                            val sessionValue = selectedSession.uppercase()
                            var anySuccess = false

                            if (isPrimaryUserChecked) {
                                val response = RetrofitClient.apiService.brushingCheckIn(
                                    CheckinRequest(userId, null, sessionValue)
                                )
                                if (response.isSuccessful) anySuccess = true
                            }

                            brushedFamilyMemberIds.forEach { memberId ->
                                val response = RetrofitClient.apiService.brushingCheckIn(
                                    CheckinRequest(userId, memberId, sessionValue)
                                )
                                if (response.isSuccessful) anySuccess = true
                            }

                            if (anySuccess) {
                                Toast.makeText(
                                    context,
                                    "Check-in updated successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = (isPrimaryUserChecked || brushedFamilyMemberIds.isNotEmpty()) && !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Updating...", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("Confirm Check-in", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BrushingMemberCard(
    name: String,
    isBrushed: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isBrushed) BorderStroke(2.dp, PrimaryBlue) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isBrushed) PrimaryBlue.copy(alpha = 0.1f)
                            else Color(0xFFF0F2F5)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (isBrushed) PrimaryBlue else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextBlack
                )
            }

            Checkbox(
                checked = isBrushed,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryBlue,
                    uncheckedColor = Color.LightGray
                )
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