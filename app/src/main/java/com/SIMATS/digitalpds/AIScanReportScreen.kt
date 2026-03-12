package com.SIMATS.digitalpds

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.SIMATS.digitalpds.network.AiDetection
import com.SIMATS.digitalpds.network.AiPredictionResponse
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIScanReportScreen(
    member: FamilyMember,
    imageUri: Uri? = null,
    analysisResult: AiPredictionResponse? = null,
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val detections = analysisResult?.detections?.sortedByDescending { it.confidence } ?: emptyList()
    val riskLevel = analysisResult?.riskLevel?.uppercase() ?: "LOW"
    val highestFindingRaw = detections.firstOrNull()?.detectedClass ?: "None Detected"
    val highestFinding = mapClassName(highestFindingRaw)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "AI Dental Analysis Report",
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
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Scan Successfully Completed",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your AI analysis is complete. Review your risk and findings below.",
                    fontSize = 14.sp,
                    color = TextGray,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .weight(1f)
                )
                if (analysisResult != null) {
                    Text(
                        text = "Report #${analysisResult.reportId}",
                        fontSize = 12.sp,
                        color = TextGray.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = member.imageResId),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE9EEF3)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = member.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    Text(
                        text = "Verified User",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Scanned Image",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUri),
                            contentDescription = "Analyzed Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ImageNotSupported,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "No image available", color = TextGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AiSummarySection(primaryFinding = highestFinding, riskLevel = riskLevel)

            Spacer(modifier = Modifier.height(24.dp))

            val (riskColor, riskHint) = when (riskLevel) {
                "HIGH" -> Color(0xFFE53935) to "Urgent attention recommended."
                "MEDIUM" -> Color(0xFFFB8C00) to "Some issues detected, consider a checkup."
                "LOW" -> Color(0xFF43A047) to "No major issues detected."
                else -> Color(0xFF43A047) to "No major issues detected."
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF8F9FA),
                border = BorderStroke(1.dp, Color(0xFFE9ECEF))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Risk Assessment",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )

                        Surface(
                            color = riskColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = riskLevel,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = riskColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = riskHint,
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Detailed Findings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (detections.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F8E9)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF43A047))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "No issues detected. Your teeth look healthy.",
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                detections.forEachIndexed { index, detection ->
                    DiseaseFindingCard(detection = detection, isPrimary = index == 0)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onDoneClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Done", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun AiSummarySection(primaryFinding: String, riskLevel: String) {
    val recommendation = when (riskLevel.uppercase()) {
        "LOW" -> "Maintain hygiene and regular checkups."
        "MEDIUM" -> "Schedule a dental consultation soon."
        "HIGH" -> "Consult a dentist urgently."
        else -> "Maintain hygiene and regular checkups."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
        border = BorderStroke(1.dp, Color(0xFFDCEDC8))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "AI Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(12.dp))

            SummaryItem(label = "Primary Finding", value = primaryFinding, icon = Icons.Default.AutoAwesome)
            Spacer(modifier = Modifier.height(8.dp))
            SummaryItem(label = "Overall Risk", value = riskLevel, icon = Icons.Default.Analytics)
            Spacer(modifier = Modifier.height(8.dp))
            SummaryItem(label = "Recommendation", value = recommendation, icon = Icons.Default.Lightbulb)
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp),
            tint = Color(0xFF2E7D32)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color(0xFF558B2F), fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 14.sp, color = TextBlack, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DiseaseFindingCard(detection: AiDetection, isPrimary: Boolean) {
    val mappedName = mapClassName(detection.detectedClass)
    val confidencePercent = (detection.confidence * 100)
    val formattedConfidence = String.format("%.1f", confidencePercent)

    val (confidenceLabel, confidenceColor) = when {
        detection.confidence < 0.40f -> "Low Confidence" to Color.Gray
        detection.confidence <= 0.70f -> "Moderate Confidence" to Color(0xFFFB8C00)
        else -> "High Confidence" to Color(0xFFE53935)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, if (isPrimary) PrimaryBlue.copy(alpha = 0.3f) else Color(0xFFF1F3F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (isPrimary) {
                        Surface(
                            color = PrimaryBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                "Primary Finding",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = PrimaryBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "Detected: $mappedName",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextBlack
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$formattedConfidence%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Text(
                        text = confidenceLabel,
                        fontSize = 10.sp,
                        color = confidenceColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = Color(0xFFF8F9FA),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp),
                        tint = TextGray.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Insight: This may indicate early signs of $mappedName. Consider a dental checkup if symptoms persist.",
                        fontSize = 12.sp,
                        color = TextGray,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

private fun mapClassName(name: String): String {
    return when (name) {
        "ToothDiscoloration" -> "Tooth Discoloration"
        "Caries" -> "Dental Caries (Cavities)"
        "Gingivitis" -> "Gingivitis (Gum Inflammation)"
        "Ulcer" -> "Mouth Ulcer"
        else -> name.replace(Regex("([a-z])([A-Z])"), "$1 $2")
    }
}

@Preview(showBackground = true)
@Composable
fun AIScanReportScreenPreview() {
    DigitalpdsTheme {
        AIScanReportScreen(
            member = FamilyMember(
                id = 1,
                name = "John Doe",
                oralHealthScore = 85,
                riskLevel = "Low",
                lastScan = "2024-05-20",
                imageResId = R.drawable.user
            ),
            analysisResult = AiPredictionResponse(
                message = "Analysis successful",
                reportId = 7,
                riskLevel = "MEDIUM",
                detections = listOf(
                    AiDetection(detectedClass = "Caries", confidence = 0.854f, bbox = listOf(0f, 0f, 0f, 0f)),
                    AiDetection(detectedClass = "ToothDiscoloration", confidence = 0.429f, bbox = listOf(0f, 0f, 0f, 0f)),
                    AiDetection(detectedClass = "Gingivitis", confidence = 0.35f, bbox = listOf(0f, 0f, 0f, 0f))
                )
            ),
            onBackClick = {},
            onDoneClick = {},
            onHomeClick = {},
            onKitsClick = {},
            onLearnClick = {},
            onConsultClick = {},
            onProfileClick = {}
        )
    }
}