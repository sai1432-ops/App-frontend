package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import com.SIMATS.digitalpds.ui.theme.PrimaryBlue
import com.SIMATS.digitalpds.ui.theme.TextBlack
import com.SIMATS.digitalpds.ui.theme.TextGray
import com.SIMATS.digitalpds.network.AiDetection
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.dp

@Composable
fun InitialsAvatar(
    name: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFE9EEF3),
    textColor: Color = PrimaryBlue,
    fontSize: TextUnit = 20.sp
) {
    val initial = name.firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial.toString(),
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
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

fun mapClassName(name: String): String {
    return when (name) {
        "ToothDiscoloration" -> "Tooth Discoloration"
        "Caries" -> "Dental Caries (Cavities)"
        "Gingivitis" -> "Gingivitis (Gum Inflammation)"
        "Ulcer" -> "Mouth Ulcer"
        else -> name.replace(Regex("([a-z])([A-Z])"), "$1 $2")
    }
}
