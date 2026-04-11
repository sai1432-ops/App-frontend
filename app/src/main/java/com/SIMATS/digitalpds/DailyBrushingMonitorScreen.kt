package com.SIMATS.digitalpds

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.BrushCountRequest
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyBrushingMonitorScreen(
    memberId: Int,
    currentBrushCount: Int,
    onBackClick: () -> Unit,
    onFinishSession: (Int) -> Unit,
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTime by remember { mutableStateOf("Morning") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Daily Brushing Monitor",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                    }
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
        bottomBar = {
            UserBottomNavigationBar(
                currentScreen = "Home",
                onHomeClick = onHomeClick,
                onKitsClick = onKitsClick,
                onLearnClick = onLearnClick,
                onConsultClick = onConsultClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BackgroundWhite
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

            // Time Toggle (Morning/Night)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF5F7F9)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    TimeToggleButton(
                        label = "Morning",
                        isSelected = selectedTime == "Morning",
                        onClick = { selectedTime = "Morning" },
                        modifier = Modifier.weight(1f)
                    )
                    TimeToggleButton(
                        label = "Night",
                        isSelected = selectedTime == "Night",
                        onClick = { selectedTime = "Night" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Timer display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimerBox(value = "02", label = "Minutes", modifier = Modifier.weight(1f))
                TimerBox(value = "00", label = "Seconds", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Video/Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.howp),
                    contentDescription = "Brushing Tutorial",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = Color.Black.copy(alpha = 0.5f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Current Session Progress
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Current Session",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { 0.5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PrimaryBlue,
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Preparation Tips
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Preparation Tips",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                TipItem(icon = Icons.Default.MedicalServices, label = "Use pea-sized toothpaste")
                TipItem(icon = Icons.Default.Timer, label = "Brush gently for 2 minutes")
                TipItem(icon = Icons.Default.Opacity, label = "Rinse with water")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Finish Session Button
            Button(
                onClick = {
                    isLoading = true
                    scope.launch {
                        try {
                            val newCount = currentBrushCount + 1
                            val request = BrushCountRequest(weeklyBrushCount = newCount)
                            val response = RetrofitClient.apiService.updateBrushCount(
                                "Bearer ${SessionManager(context).getAccessToken() ?: ""}",
                                memberId,
                                request
                            )
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Session updated!", Toast.LENGTH_SHORT).show()
                                onFinishSession(newCount)
                            } else {
                                Toast.makeText(context, "Failed to update count", Toast.LENGTH_SHORT).show()
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
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Finish Session", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TimeToggleButton(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.White else Color.Transparent,
            contentColor = TextBlack
        ),
        elevation = if (isSelected) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null,
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun TimerBox(value: String, label: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color(0xFFE9EEF3), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextBlack
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 14.sp, color = TextGray)
    }
}

@Composable
fun TipItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE9EEF3)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color(0xFF455A64))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextBlack)
    }
}

@Preview(showBackground = true)
@Composable
fun DailyBrushingMonitorScreenPreview() {
    DigitalpdsTheme {
        DailyBrushingMonitorScreen(
            memberId = 1,
            currentBrushCount = 5,
            onBackClick = {},
            onFinishSession = {}
        )
    }
}
