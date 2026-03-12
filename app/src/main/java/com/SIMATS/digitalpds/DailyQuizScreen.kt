package com.SIMATS.digitalpds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyQuizScreen(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var showResults by remember { mutableStateOf(false) }

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
                    IconButton(onClick = if (showResults) { { showResults = false } } else onBackClick) {
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
                    onClick = onHomeClick,
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.Black) },
                    label = { Text("Home", color = Color.Black) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onKitsClick,
                    icon = { Icon(Icons.Filled.ShoppingBag, contentDescription = "Kits", tint = Color.Black) },
                    label = { Text("Kits", color = Color.Black) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = onLearnClick,
                    icon = { Icon(Icons.Filled.School, contentDescription = "Learn", tint = Color.Black) },
                    label = { Text("Learn", color = Color.Black) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onConsultClick,
                    icon = { Icon(Icons.Filled.ChatBubbleOutline, contentDescription = "Consult", tint = Color.Black) },
                    label = { Text("Consult", color = Color.Black) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onProfileClick,
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.Black) },
                    label = { Text("Profile", color = Color.Black) }
                )
            }
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        if (showResults) {
            QuizResultContent(
                paddingValues = paddingValues,
                onReturnClick = onBackClick
            )
        } else {
            QuizQuestionsContent(
                paddingValues = paddingValues,
                onFinishClick = { showResults = true }
            )
        }
    }
}

@Composable
fun QuizQuestionsContent(
    paddingValues: PaddingValues,
    onFinishClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Premium Header
        Image(
            painter = painterResource(id = R.drawable.dental),
            contentDescription = "Quiz Header",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quiz Progress: 40%",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        LinearProgressIndicator(
            progress = { 0.4f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = PrimaryBlue,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        QuizQuestion(
            question = "1. What is the primary cause of tooth decay?",
            options = listOf(
                QuizOption("Excessive Sugar Intake", R.drawable.hobot),
                QuizOption("Poor Brushing Habits", R.drawable.brush),
                QuizOption("Both A and B", R.drawable.dental)
            )
        )

        QuizQuestion(
            question = "2. How often should you floss your teeth?",
            options = listOf(
                QuizOption("At least once a day", R.drawable.howp),
                QuizOption("Once a week", R.drawable.user),
                QuizOption("Only when food is stuck", R.drawable.img)
            )
        )

        QuizQuestion(
            question = "3. Which part of the tooth is the hardest?",
            options = listOf(
                QuizOption("Enamel", R.drawable.dental),
                QuizOption("Dentin", R.drawable.hobot),
                QuizOption("Pulp", R.drawable.howp)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onFinishClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(
                "Finish & Claim Points",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun QuizResultContent(
    paddingValues: PaddingValues,
    onReturnClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Trophy Image (Golden Trophy)
        Image(
            painter = painterResource(id = R.drawable.hobot), // Using hobot as placeholder for trophy
            contentDescription = "Success Trophy",
            modifier = Modifier
                .size(220.dp)
                .padding(16.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Score Gauge Image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4F8))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.howp), // Using howp as gauge placeholder
                    contentDescription = "Score Gauge",
                    modifier = Modifier.size(150.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "5 / 5 Correct",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "+50 Hygiene Points",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )

        Text(
            text = "You are now an Oral Health Expert!",
            fontSize = 16.sp,
            color = TextGray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* Share Logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Text(
                "Share Achievement",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onReturnClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(
                "Return to Education Hub",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

data class QuizOption(val text: String, val imageRes: Int)

@Composable
fun QuizQuestion(question: String, options: List<QuizOption>) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = question,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )
        Spacer(modifier = Modifier.height(16.dp))
        options.forEachIndexed { index, option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedOption == index) Color(0xFFE3F2FD) else Color(0xFFF1F4F8))
                    .border(
                        width = 2.dp,
                        color = if (selectedOption == index) PrimaryBlue else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedOption = index }
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = option.imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = option.text,
                        fontSize = 16.sp,
                        fontWeight = if (selectedOption == index) FontWeight.Bold else FontWeight.Medium,
                        color = TextBlack
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyQuizScreenPreview() {
    DigitalpdsTheme {
        DailyQuizScreen(onBackClick = {})
    }
}
