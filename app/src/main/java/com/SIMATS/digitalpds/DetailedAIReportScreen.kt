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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedAIReportScreen(
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
                .verticalScroll(rememberScrollState())
        ) {
            // Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.linearGradient(colors = listOf(softBlue, cyanGradient)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        
                        Text(
                            "Detailed Health Report",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Analysis Verified", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                // Analysis Image Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                ) {
                    Box {
                        Image(
                            painter = painterResource(id = R.drawable.howp),
                            contentDescription = "Analyzed Scan",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))))
                        )
                        Text(
                            "Captured Scan View",
                            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Score Section
                Text("Health Metrics", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(modifier = Modifier.height(16.dp))
                
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
                            Text("Overall Score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textGraySub)
                            Text("92/100", fontSize = 24.sp, fontWeight = FontWeight.Black, color = softBlue)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { 0.92f },
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                            color = softBlue,
                            trackColor = Color(0xFFF1F5F9)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Divider(color = Color(0xFFF1F5F9))
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text("Findings Breakdown", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textGraySub)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        ModernFindingRow("Gum Health", "Excellent", Color(0xFF10B981))
                        ModernFindingRow("Cavity Detection", "None Found", Color(0xFF3B82F6))
                        ModernFindingRow("Enamel Quality", "Very Good", Color(0xFFF59E0B))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Advice Section
                Text("Personalized Advice", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(modifier = Modifier.height(16.dp))
                
                ModernAdviceCard(
                    title = "Optimal Brushing",
                    desc = "Maintain your current twice-daily routine with the provided fluoride paste.",
                    icon = Icons.Default.AutoAwesome
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ModernAdviceCard(
                    title = "Posterior Care",
                    desc = "Focus on the back molars during evening brushing to prevent plaque accumulation.",
                    icon = Icons.Default.HealthAndSafety
                )
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ModernFindingRow(label: String, value: String, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(accent, CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, fontSize = 14.sp, color = TextBlack)
        }
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = accent)
    }
}

@Composable
fun ModernAdviceCard(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = PrimaryBlue.copy(alpha = 0.1f)
            ) {
                Icon(icon, null, modifier = Modifier.padding(10.dp), tint = PrimaryBlue)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(modifier = Modifier.height(4.dp))
                Text(desc, fontSize = 14.sp, color = textGraySub, lineHeight = 20.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailedAIReportScreenPreview() {
    DigitalpdsTheme {
        DetailedAIReportScreen(onBackClick = {})
    }
}
