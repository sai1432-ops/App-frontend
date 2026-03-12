package com.SIMATS.digitalpds

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.BookAppointmentRequest
import com.SIMATS.digitalpds.network.ClinicResponse
import com.SIMATS.digitalpds.network.FamilyMemberResponse
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.*
import kotlinx.coroutines.launch
import java.io.Serializable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AppointmentDetailsScreen(
    userId: Int,
    clinic: ClinicResponse,
    date: String,
    time: String,
    familyMembers: List<FamilyMemberResponse>,
    onBackClick: () -> Unit,
    onConfirmAppointment: () -> Unit
) {
    var selectedPatientType by remember { mutableStateOf("Me") }
    var selectedFamilyMemberId by remember { mutableStateOf<Int?>(null) }
    var selectedReason by remember { mutableStateOf("Routine Checkup") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Confirm Patient Details",
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Selected Date & Time",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(date, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextBlack)
                    Text(time, fontSize = 14.sp, color = TextGray)
                    Text(clinic.clinicName, fontSize = 13.sp, color = PrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Who is this for?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PatientSelectionChip("Me", selectedPatientType == "Me") {
                    selectedPatientType = "Me"
                    selectedFamilyMemberId = null
                }
                PatientSelectionChip("Family", selectedPatientType == "Family") {
                    selectedPatientType = "Family"
                }
            }

            if (selectedPatientType == "Family") {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Select Family Member",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (familyMembers.isEmpty()) {
                    Text(
                        "No family members found.",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                } else {
                    familyMembers.forEach { member ->
                        FamilyMemberSelectionItem(
                            name = member.memberName,
                            relation = member.relation,
                            selected = selectedFamilyMemberId == member.id,
                            onClick = { selectedFamilyMemberId = member.id }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Reason for Visit",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("Routine Checkup", "Tooth Pain", "Cleaning", "Cavity", "Gum Issue").forEach { reason ->
                    ReasonSelectionChip(
                        text = reason,
                        isSelected = selectedReason == reason,
                        onClick = { selectedReason = reason }
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    if (selectedPatientType == "Family" && selectedFamilyMemberId == null) {
                        Toast.makeText(context, "Please select a family member", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            val safeClinicId = when (val id = clinic.id) {
                                is Int -> id
                                is Number -> id.toInt()
                                is String -> id.toIntOrNull() ?: 0
                                is Serializable -> (id as? Int) ?: 0
                                else -> 0
                            }

                            val request = BookAppointmentRequest(
                                userId = userId,
                                memberId = if (selectedPatientType == "Family") selectedFamilyMemberId else null,
                                clinicId = safeClinicId,
                                appointmentDate = date,
                                timeSlot = time
                            )

                            val response = RetrofitClient.apiService.bookAppointment(request)
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Appointment Booked Successfully", Toast.LENGTH_SHORT).show()
                                onConfirmAppointment()
                            } else {
                                Toast.makeText(
                                    context,
                                    response.errorBody()?.string() ?: "Failed to book appointment",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text(
                        "Confirm Appointment",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PatientSelectionChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF1F4F8),
        border = if (isSelected) BorderStroke(1.dp, Color(0xFF4CAF50)) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (isSelected) Color(0xFF2E7D32) else Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color(0xFF2E7D32) else TextBlack
            )
        }
    }
}

@Composable
fun FamilyMemberSelectionItem(
    name: String,
    relation: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFFE8F5E9) else Color(0xFFF8FAFC),
        border = if (selected) BorderStroke(1.dp, Color(0xFF4CAF50)) else null
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Text(relation, fontSize = 13.sp, color = TextGray)
        }
    }
}

@Composable
fun ReasonSelectionChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFE8F5E9) else Color.White,
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF4CAF50) else Color(0xFFE2E8F0))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color(0xFF2E7D32) else TextBlack
        )
    }
}