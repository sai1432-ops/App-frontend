package com.SIMATS.digitalpds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationHubScreen(
    onBackClick: () -> Unit,
    onBrushingTechniquesClick: () -> Unit = {},
    onCommonDentalProblemsClick: () -> Unit = {},
    onPrecautionsAndCareClick: () -> Unit = {},
    onDailyQuizClick: () -> Unit = {},
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
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Education Hub",
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Pro-Brushing Masterclass
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.howp),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Pro-Brushing Masterclass",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Text(
                            "Learn advanced brushing techniques from dental experts to achieve a healthier smile. Video",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Core Learning Modules",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            EducationModuleItem(
                title = "Brushing Techniques",
                description = "Step-by-step illustrations and guidance on effective brushing methods for all ages.",
                imageRes = R.drawable.brush,
                onClick = onBrushingTechniquesClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            EducationModuleItem(
                title = "Common Dental Problems",
                description = "Identify symptoms and understand common dental issues, from cavities to gum disease.",
                imageRes = R.drawable.hobot,
                onClick = onCommonDentalProblemsClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            EducationModuleItem(
                title = "Precautions & Care",
                description = "Discover preventive habits and care tips to maintain optimal oral health and avoid future problems.",
                imageRes = R.drawable.user,
                onClick = onPrecautionsAndCareClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Interactive Zone",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Dental Quiz
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDailyQuizClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EFE6))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Graphic placeholder for quiz
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("+50", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                            Text("points", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Daily Dental Quiz",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    Text(
                        "Test your knowledge with fun quizzes and earn +50 points for each completed quiz.",
                        fontSize = 14.sp,
                        color = TextGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun EducationModuleItem(title: String, description: String, imageRes: Int, onClick: () -> Unit = {}) {
    Column(modifier = Modifier.clickable { onClick() }) {
        Image(
            painter = painterResource(id = R.drawable.brush),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
        Text(description, fontSize = 14.sp, color = TextGray)
    }
}

@Preview(showBackground = true)
@Composable
fun EducationHubScreenPreview() {
    DigitalpdsTheme {
        EducationHubScreen(onBackClick = {})
    }
}
