package com.SIMATS.digitalpds

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Scanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.AppointmentResponse
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberHealthStatusScreen(
    userId: Int,
    member: FamilyMember,
    latestReport: MemberAiReport? = null,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onViewAppointmentDetails: () -> Unit = {},
    onViewDentalRiskDetails: () -> Unit = {},
    onViewAIScanReport: () -> Unit = {},
    role: String = "Verified User"
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var appointments by remember { mutableStateOf<List<AppointmentResponse>>(emptyList()) }

    LaunchedEffect(userId, member.id) {
        scope.launch {
            try {
                val response = RetrofitClient.apiService.getAppointments(userId)
                if (response.isSuccessful) {
                    appointments = response.body().orEmpty()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load appointments", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val today = remember { LocalDate.now() }

    val upcomingAppointment = appointments
        .filter { appointment ->
            val isFutureOrToday = runCatching {
                !LocalDate.parse(appointment.appointmentDate).isBefore(today)
            }.getOrDefault(false)

            val matchesMember = if (member.id == userId) {
                appointment.memberId == null
            } else {
                appointment.memberId == member.id
            }

            isFutureOrToday && matchesMember && appointment.status.equals("BOOKED", true)
        }
        .sortedBy {
            runCatching { LocalDate.parse(it.appointmentDate) }.getOrNull()
        }
        .firstOrNull()

    val displayScore = latestReport?.oralHealthScore ?: member.oralHealthScore

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Member Health Status",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8F9FA))
            )
        },
        bottomBar = {
            UserBottomNavigationBar(
                currentScreen = "Profile",
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            androidx.compose.foundation.Image(
                painter = painterResource(id = member.imageResId),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE9EEF3)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = member.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Text(
                text = role,
                fontSize = 16.sp,
                color = TextGray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Oral Health Score",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Text(
                    text = if (displayScore > 0) "${displayScore}/100" else "--/100",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { if (displayScore > 0) displayScore.toFloat() / 100f else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PrimaryBlue,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (displayScore >= 80) "Excellent oral health, keep it up!"
                else if (displayScore >= 60) "Good oral health, maintain regular check-ups"
                else if (displayScore > 0) "Moderate health score, consider consulting a dentist"
                else "No health data available. Please perform an AI scan.",
                fontSize = 14.sp,
                color = TextBlack,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfessionalStatusCard(
                title = "Latest AI Scan Report",
                subtitle = latestReport?.scanDate ?: "No recent scans",
                buttonText = "View Report",
                iconColor = Color(0xFFE1F5FE),
                iconId = Icons.Default.Scanner,
                onButtonClick = onViewAIScanReport,
                isEnabled = latestReport != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfessionalStatusCard(
                title = "Dental Risk Level",
                subtitle = member.riskLevel,
                buttonText = "View Details",
                iconColor = Color(0xFFFFD54F),
                iconId = Icons.Default.Warning,
                onButtonClick = onViewDentalRiskDetails
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfessionalStatusCard(
                title = "Upcoming Appointment",
                subtitle = upcomingAppointment?.let {
                    "${it.clinicName ?: "Clinic"} • ${it.appointmentDate} • ${it.timeSlot}"
                } ?: "No upcoming appointment",
                buttonText = "View Details",
                iconColor = Color(0xFFFFAB91),
                iconId = Icons.Default.CalendarMonth,
                onButtonClick = onViewAppointmentDetails,
                isEnabled = upcomingAppointment != null
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "History",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Health Score Trend",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "75",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Last 6 Months", fontSize = 14.sp, color = TextGray)
                    Text(
                        text = "+5%",
                        fontSize = 14.sp,
                        color = Color(0xFF43A047),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            ProfessionalTrendGraph()
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfessionalStatusCard(
    title: String,
    subtitle: String,
    buttonText: String,
    iconColor: Color,
    iconId: androidx.compose.ui.graphics.vector.ImageVector,
    onButtonClick: () -> Unit = {},
    isEnabled: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Text(subtitle, fontSize = 14.sp, color = TextGray)
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    onClick = onButtonClick,
                    enabled = isEnabled,
                    color = Color(0xFFF1F3F5),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = buttonText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEnabled) TextBlack else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isEnabled) TextBlack else Color.Gray
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconId,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Black.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ProfessionalTrendGraph() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path()
                val points = listOf(0.3f, 0.7f, 0.6f, 0.4f, 0.65f, 0.35f, 0.5f, 0.55f, 0.2f, 0.15f, 0.75f, 0.5f, 0.4f, 0.8f)
                val width = size.width
                val height = size.height
                val stepX = width / (points.size - 1)

                path.moveTo(0f, height * (1 - points[0]))

                for (i in 0 until points.size - 1) {
                    val x1 = i * stepX
                    val y1 = height * (1 - points[i])
                    val x2 = (i + 1) * stepX
                    val y2 = height * (1 - points[i + 1])

                    path.cubicTo(
                        x1 + stepX / 2f, y1,
                        x1 + stepX / 2f, y2,
                        x2, y2
                    )
                }

                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun").forEach { month ->
                Text(text = month, fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Bold)
            }
        }
    }
}