package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationHubScreen(
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bluetooth, // Using Bluetooth as a placeholder for the tooth icon
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Dental Care Guide",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                            Text(
                                "Learn how to keep your teeth healthy.",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { /* Search */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextBlack)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        bottomBar = {
            UserBottomNavigationBar(
                currentScreen = "Learn",
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Brushing Techniques Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Brushing Techniques",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE3F2FD)
                ) {
                    Text(
                        "STEP-BY-STEP",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid for Brushing Techniques
            Row(modifier = Modifier.fillMaxWidth()) {
                BrushingTechniqueCard(
                    modifier = Modifier.weight(1f),
                    step = "STEP 1",
                    title = "Circular brushing",
                    description = "Gently move in small circles.",
                    icon = Icons.Default.Refresh,
                    iconColor = PrimaryBlue
                )
                Spacer(modifier = Modifier.width(16.dp))
                BrushingTechniqueCard(
                    modifier = Modifier.weight(1f),
                    step = "STEP 2",
                    title = "Vertical brushing",
                    description = "Short strokes up & down.",
                    icon = Icons.Default.SwapVert,
                    iconColor = PrimaryBlue
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                BrushingTechniqueCard(
                    modifier = Modifier.weight(1f),
                    step = "STEP 3",
                    title = "Gumline cleaning",
                    description = "Angle at 45 degrees.",
                    icon = Icons.Default.ChangeHistory,
                    iconColor = PrimaryBlue
                )
                Spacer(modifier = Modifier.width(16.dp))
                BrushingTechniqueCard(
                    modifier = Modifier.weight(1f),
                    step = "STEP 4",
                    title = "Tongue cleaning",
                    description = "Scrape gently front to back.",
                    icon = Icons.Default.CleaningServices,
                    iconColor = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Teeth Problems Section
            Text(
                "Teeth Problems",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            TeethProblemItem(
                title = "Cavities",
                description = "Tooth decay causing small holes.",
                icon = Icons.Default.AddCircle,
                iconColor = Color(0xFFEF5350),
                backgroundColor = Color(0xFFFFEBEE)
            )
            TeethProblemItem(
                title = "Gingivitis",
                description = "Inflammation of the gum tissues.",
                icon = Icons.Default.Settings,
                iconColor = Color(0xFFFFA726),
                backgroundColor = Color(0xFFFFF3E0)
            )
            TeethProblemItem(
                title = "Discoloration",
                description = "Yellowing from coffee, tea, or food.",
                icon = Icons.Default.Brush,
                iconColor = Color(0xFFFFD54F),
                backgroundColor = Color(0xFFFFFDE7)
            )
            TeethProblemItem(
                title = "Mouth Ulcers",
                description = "Small painful sores in the mouth.",
                icon = Icons.Default.BrightnessLow,
                iconColor = Color(0xFFAB47BC),
                backgroundColor = Color(0xFFF3E5F5)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Precautions Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color(0xFF00796B),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Precautions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    PrecautionCheckItem("Brush twice daily for 2 minutes")
                    PrecautionCheckItem("Avoid sugary drinks and snacks")
                    PrecautionCheckItem("Use fluoride-based toothpaste")
                    PrecautionCheckItem("Drink water after every meal")
                    PrecautionCheckItem("Replace toothbrush every 3 months")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun BrushingTechniqueCard(
    modifier: Modifier = Modifier,
    step: String,
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FBFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                step,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Text(
                description,
                fontSize = 11.sp,
                color = TextGray,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun TeethProblemItem(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Text(
                    description,
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
        }
    }
}

@Composable
fun PrecautionCheckItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF00796B),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = TextBlack
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EducationHubScreenPreview() {
    DigitalpdsTheme {
        EducationHubScreen()
    }
}
