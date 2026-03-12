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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Info
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
fun LastAIScanReportScreen(
    member: FamilyMember,
    imageUri: Uri? = null,
    analysisResult: AiPredictionResponse? = null,
    lastScanDate: String = "Unknown",
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val detections = analysisResult?.detections?.sortedByDescending { it.confidence } ?: emptyList()
    val riskLevel = analysisResult?.riskLevel?.uppercase() ?: "LOW"
    val highestFindingRaw = detections.firstOrNull()?.detectedClass ?: "None Detected"
    val highestFinding = mapLastScanClassName(highestFindingRaw)

    val (riskColor, riskHint) = when (riskLevel) {
        "HIGH" -> Color(0xFFE53935) to "Urgent attention recommended."
        "MEDIUM" -> Color(0xFFFB8C00) to "Some issues detected, consider a dental checkup."
        "LOW" -> Color(0xFF43A047) to "No major issues detected."
        else -> Color(0xFF43A047) to "No major issues detected."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Last AI Scan Report",
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
                currentScreen = "Profile",
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = member.imageResId),
                    contentDescription = "Member",
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
                        text = "Scanned on $lastScanDate",
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
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUri),
                            contentDescription = "Scanned Teeth Image",
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
                            Text("No image available", color = TextGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                border = BorderStroke(1.dp, Color(0xFFDCEDC8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Report Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Primary Finding: $highestFinding", color = TextBlack, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Risk Level: $riskLevel", color = riskColor, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Note: $riskHint", color = TextGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (analysisResult != null) {
                        Text("Report ID: ${analysisResult.reportId}", color = TextGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Detailed Findings",
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
                            "No issues detected. Teeth look healthy.",
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                detections.forEachIndexed { index, detection ->
                    LastScanDiseaseCard(detection = detection, isPrimary = index == 0)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun LastScanDiseaseCard(detection: AiDetection, isPrimary: Boolean) {
    val mappedName = mapLastScanClassName(detection.detectedClass)
    val confidencePercent = detection.confidence * 100
    val formattedConfidence = String.format("%.1f", confidencePercent)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            1.dp,
            if (isPrimary) PrimaryBlue.copy(alpha = 0.3f) else Color(0xFFF1F3F5)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isPrimary) {
                Surface(
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Primary Finding",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = PrimaryBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = mappedName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Text(
                    text = "$formattedConfidence%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TextGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "This finding may indicate early signs of $mappedName.",
                    fontSize = 12.sp,
                    color = TextGray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private fun mapLastScanClassName(name: String): String {
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
fun LastAIScanReportScreenPreview() {
    DigitalpdsTheme {
        val sampleMember = FamilyMember(
            id = 1,
            name = "John Doe",
            oralHealthScore = 85,
            riskLevel = "Low",
            lastScan = "12 Feb 2026",
            imageResId = R.drawable.user
        )
        val sampleAiResponse = AiPredictionResponse(
            message = "Success",
            reportId = 1234,
            riskLevel = "LOW",
            detections = listOf(
                AiDetection(detectedClass = "Caries", confidence = 0.85f, bbox = listOf(0.1f, 0.2f, 0.3f, 0.4f)),
                AiDetection(detectedClass = "Gingivitis", confidence = 0.65f, bbox = listOf(0.5f, 0.6f, 0.7f, 0.8f))
            )
        )
        LastAIScanReportScreen(
            member = sampleMember,
            analysisResult = sampleAiResponse,
            lastScanDate = "12 Feb 2026",
            onBackClick = {}
        )
    }
}