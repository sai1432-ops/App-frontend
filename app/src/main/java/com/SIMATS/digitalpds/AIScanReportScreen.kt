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

import androidx.compose.ui.graphics.Brush

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
        rawRisk == null && hasNoDetections -> "INVALID" // Catch spreadsheet-like cases
        else -> rawRisk ?: "LOW"
    }
    val highestFindingRaw = detections.firstOrNull()?.detectedClass ?: "None Detected"
    val highestFinding = mapClassName(highestFindingRaw)

    val softBlue = PrimaryBlue
    val cyanGradient = Color(0xFF00BCD4)

    Scaffold(
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
                    .height(220.dp)
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
                            "Scan Analysis Report",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "AI-powered comprehensive dental evaluation completed successfully.",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    if (analysisResult != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Report ID: #${analysisResult.reportId}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Verified Analysis",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Member Info Card
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
                                text = "Health Profile: ${member.riskLevel} Risk",
                                fontSize = 13.sp,
                                color = textGraySub
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Analyzed Image",
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
                    "HIGH" -> Color(0xFFC62828) to "Immediate professional attention is highly recommended."
                    "MEDIUM" -> Color(0xFFEF6C00) to "We detected potential issues. Consider scheduling a checkup."
                    "LOW" -> Color(0xFF2E7D32) to "Looking good! No major issues were detected in this scan area."
                    "INVALID" -> Color(0xFFC62828) to "The image does not appear to be a clear scan of teeth. Please try again."
                    else -> Color(0xFF2E7D32) to "Looking good! No major issues were detected in this scan area."
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
                                text = "Risk Assessment",
                                fontSize = 16.sp,
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
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = riskColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = riskHint,
                            fontSize = 14.sp,
                            color = textGraySub,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Clinical Findings",
                    fontSize = 20.sp,
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
                            modifier = Modifier.padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "Invalid Image: Could not perform clinical analysis.",
                                color = Color(0xFFC62828),
                                fontSize = 15.sp,
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
                            modifier = Modifier.padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "No clinical issues detected in this scan area.",
                                color = Color(0xFF2E7D32),
                                fontSize = 15.sp,
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

                Button(
                    onClick = onDoneClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Complete Review", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun AiSummarySection(primaryFinding: String, riskLevel: String) {
    val recommendation = when (riskLevel.uppercase()) {
        "LOW" -> "Maintain hygiene and regular checkups."
        "MEDIUM" -> "Schedule a dental consultation soon."
        "HIGH" -> "Consult a dentist urgently."
        "INVALID" -> "Please rescan with a clearer teeth image."
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