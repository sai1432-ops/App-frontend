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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DentalRiskDetailsScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Dental Risk Details",
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
            NavigationBar(
                containerColor = BackgroundWhite,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.Black) },
                    label = { Text("Home", color = Color.Black) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Filled.ShoppingBag, contentDescription = "Kits", tint = Color.Black) },
                    label = { Text("Kits", color = Color.Black) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Filled.School, contentDescription = "Learn", tint = Color.Black) },
                    label = { Text("Learn", color = Color.Black) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Filled.ChatBubbleOutline, contentDescription = "Consult", tint = Color.Black) },
                    label = { Text("Consult", color = Color.Black) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.Black) },
                    label = { Text("Profile", color = Color.Black) }
                )
            }
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
            Spacer(modifier = Modifier.height(16.dp))

            // Dental Risk Level Card at top
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Dental Risk Level",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    Text(
                        "Moderate",
                        fontSize = 16.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE9EEF3)),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("View Details", color = TextBlack, fontSize = 12.sp)
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextBlack, modifier = Modifier.size(14.dp))
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD54F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.Black.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Risk Metrics
            RiskMetricItem("Cavity Risk", "60/100", 0.6f, "Moderate risk of cavities, maintain good oral hygiene")
            Spacer(modifier = Modifier.height(24.dp))
            RiskMetricItem("Gum Health", "50/100", 0.5f, "Moderate gum health, consider professional cleaning")
            Spacer(modifier = Modifier.height(24.dp))
            RiskMetricItem("Plaque Level", "40/100", 0.4f, "Moderate plaque buildup, improve brushing technique")

            Spacer(modifier = Modifier.height(32.dp))

            // Doctor's Note Section
            Text(
                "Doctor's Note",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Based on your assessment, your dental risk is moderate. This means there are some areas of concern that need attention to prevent further issues. Regular check-ups and improved oral hygiene practices are recommended.",
                fontSize = 15.sp,
                color = TextBlack,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Preventive Actions
            Text(
                "Preventive Actions",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            ActionItem(icon = Icons.Default.MedicalServices, label = "Use Fluoride Paste")
            ActionItem(icon = Icons.Default.MoneyOff, label = "Reduce Sugar Intake")
            ActionItem(icon = Icons.Default.Brush, label = "Brush Twice Daily")
            ActionItem(icon = Icons.Default.CleaningServices, label = "Floss Regularly")

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun RiskMetricItem(title: String, score: String, progress: Float, description: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Text(score, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color.Black,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(description, fontSize = 14.sp, color = TextBlack)
    }
}

@Composable
fun ActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
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
fun DentalRiskDetailsScreenPreview() {
    DigitalpdsTheme {
        DentalRiskDetailsScreen(onBackClick = {})
    }
}
