package com.SIMATS.digitalpds

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*
import com.SIMATS.digitalpds.ui.theme.textGraySub

import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DentalRiskDetailsScreen(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
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
                .verticalScroll(rememberScrollState())
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
                            "Dental Risk Analysis",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Detailed breakdown of your oral health assessment and risk factors.",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Risk Overview Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Overall Assessment",
                                fontSize = 14.sp,
                                color = textGraySub,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Moderate Risk",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = Color(0xFFFFB300).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Requires Attention",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    color = Color(0xFFFFB300),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB300).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFFFFB300)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Risk Metrics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Risk Metrics List
                RiskMetricItem("Cavity Potential", "60/100", 0.6f, "Early signs detected, needs focus.", Color(0xFFEF6C00))
                Spacer(modifier = Modifier.height(20.dp))
                RiskMetricItem("Gum Health", "50/100", 0.5f, "Marginal inflammation noted.", Color(0xFFFFB300))
                Spacer(modifier = Modifier.height(20.dp))
                RiskMetricItem("Plaque Accumulation", "40/100", 0.4f, "Improve interdental cleaning.", Color(0xFF2E7D32))

                Spacer(modifier = Modifier.height(32.dp))

                // Clinician's Summary
                Text(
                    "Clinician's Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(12.dp))
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF1F8E9))
                ) {
                    Text(
                        text = "Your current assessment indicates a moderate overall risk. Specifically, we've identified early-stage caries risk and mild gingival markers. Consistent adherence to the recommended preventive protocol is essential to reverse these findings.",
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32),
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Preventive Protocol
                Text(
                    "Preventive Protocol",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                ActionItem(icon = Icons.Default.MedicalServices, label = "Biological Fluoride Application", desc = "Apply twice a day after meals.")
                ActionItem(icon = Icons.Default.Fastfood, label = "Sugar-Controlled Diet", desc = "Limit fermentable carbohydrates.")
                ActionItem(icon = Icons.Default.Brush, label = "Standardized Brushing", desc = "Use soft-bristled brush, 2 mins.")
                ActionItem(icon = Icons.Default.CleaningServices, label = "Interdental Care", desc = "Floss or use interdental brushes daily.")

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun RiskMetricItem(title: String, score: String, progress: Float, description: String, indicatorColor: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Text(score, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = indicatorColor)
        }
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = indicatorColor,
            trackColor = indicatorColor.copy(alpha = 0.1f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(description, fontSize = 13.sp, color = textGraySub)
    }
}

@Composable
fun ActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, desc: String) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = PrimaryBlue)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Text(text = desc, fontSize = 12.sp, color = textGraySub)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DentalRiskDetailsScreenPreview() {
    DigitalpdsTheme {
        DentalRiskDetailsScreen(onBackClick = {})
    }
}
