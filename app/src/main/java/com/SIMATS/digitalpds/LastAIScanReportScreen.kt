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
import com.SIMATS.digitalpds.FamilyMember
import com.SIMATS.digitalpds.network.AiDetection
import com.SIMATS.digitalpds.network.AiPredictionResponse
import com.SIMATS.digitalpds.DiseaseFindingCard
import com.SIMATS.digitalpds.InitialsAvatar
import com.SIMATS.digitalpds.UserBottomNavigationBar
import com.SIMATS.digitalpds.ui.theme.*
import com.SIMATS.digitalpds.ui.theme.textGraySub
import com.SIMATS.digitalpds.mapClassName

import androidx.compose.ui.graphics.Brush

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
    val detections = analysisResult?.detections
        ?.groupBy { it.detectedClass }
        ?.map { it.value.maxByOrNull { d -> d.confidence } ?: it.value.first() }
        ?.sortedByDescending { it.confidence } ?: emptyList()
    
    val rawRisk = analysisResult?.riskLevel?.trim()?.uppercase()
    val msg = analysisResult?.message?.lowercase() ?: ""
    val hasNoDetections = detections.isEmpty()
    
    val isAnalysisFaulty = msg.contains("invalid") || 
            msg.contains("no teeth") || 
            msg.contains("not recognized") || 
            msg.contains("poor quality") ||
            msg.contains("cannot analyze") ||
            msg.contains("try again")

    val riskLevel = when {
        isAnalysisFaulty || rawRisk == "INVALID" -> "INVALID"
        hasNoDetections && !msg.contains("teeth") && !msg.contains("dental") && !msg.contains("oral") -> "INVALID"
        rawRisk == null && hasNoDetections -> "INVALID" // Heuristic for historical bad scans
        else -> rawRisk ?: "LOW"
    }
    
    val highestFindingRaw = detections.firstOrNull()?.detectedClass ?: "None Detected"
    val highestFinding = mapClassName(highestFindingRaw)

    val softBlue = PrimaryBlue
    val cyanGradient = Color(0xFF00BCD4)

    Scaffold(
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            // Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
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
                            "Previous Analysis",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Viewing historical records for ${member.name}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Member & Date Info Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InitialsAvatar(
                            name = member.name,
                            modifier = Modifier.size(50.dp),
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = member.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                            Text(
                                text = "Assessment on $lastScanDate",
                                fontSize = 13.sp,
                                color = textGraySub
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Archived Scan Image",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(model = imageUri),
                                contentDescription = "Last Analyzed Image",
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

                // Summary Card
                val (riskColor, riskHint) = when (riskLevel) {
                    "HIGH" -> Color(0xFFC62828) to "Urgent attention recommended."
                    "MEDIUM" -> Color(0xFFEF6C00) to "Some issues detected, consider a dental checkup."
                    "LOW" -> Color(0xFF2E7D32) to "No major issues detected."
                    "INVALID" -> Color(0xFFC62828) to "This scan was identified as invalid or poor quality."
                    else -> Color(0xFF2E7D32) to "No major issues detected."
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AI Risk Summary",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )

                            Surface(
                                color = riskColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, riskColor.copy(alpha = 0.2f))
                            ) {
                                Text(
                                    text = riskLevel,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    color = riskColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Historical findings indicated early signs of $highestFinding. $riskHint",
                            fontSize = 14.sp,
                            color = textGraySub,
                            lineHeight = 20.sp
                        )
                        
                        if (analysisResult != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Report ID: #${analysisResult.reportId}",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Detailed clinical results",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (riskLevel == "INVALID") {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFF1F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Invalid Image: Analysis was not possible for this record.",
                                color = Color(0xFFC62828),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (detections.isEmpty()) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF1F8E9))
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "No issues were detected in this archived scan.",
                                color = Color(0xFF2E7D32),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
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
            }
        }
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