package com.SIMATS.digitalpds

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutProgramScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var feedbackText by remember { mutableStateOf("") }
    var selectedRating by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "About Program & Feedback",
                            fontSize = 18.sp,
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
                    selected = false,
                    onClick = onLearnClick,
                    icon = { Icon(Icons.Filled.School, contentDescription = "Learn", tint = Color.Black) },
                    label = { Text("Learn", color = Color.Black) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onConsultClick,
                    icon = { Icon(Icons.Filled.ChatBubble, contentDescription = "Consult", tint = Color.Black) },
                    label = { Text("Consult", color = Color.Black) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = onProfileClick,
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.Black) },
                    label = { Text("Profile", color = Color.Black) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
            }
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Emblem Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF1E3A3A)), // Dark professional green
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gov), // Using existing emblem
                    contentDescription = "Government Emblem",
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Mission Goals",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Our mission is to ensure every Indian has access to affordable oral healthcare and essential hygiene products. We aim to distribute subsidized kits and provide digital resources for improved oral health.",
                    fontSize = 14.sp,
                    color = TextGray,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Family Illustration
                Image(
                    painter = painterResource(id = R.drawable.fam),
                    contentDescription = "Happy Family",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Your Feedback",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "We value your input! Please rate your experience with the program and share any comments or suggestions.",
                    fontSize = 14.sp,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Rating Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) { index ->
                        val rating = index + 1
                        Surface(
                            modifier = Modifier
                                .size(60.dp, 40.dp)
                                .clickable { selectedRating = rating },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedRating == rating) PrimaryBlue.copy(alpha = 0.1f) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (selectedRating == rating) PrimaryBlue else Color(0xFFE2E8F0))
                        ) {
                            // Placeholder for emoji or number
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text("Write your feedback here...") },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { /* Submit logic */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Submit Feedback", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutProgramScreenPreview() {
    DigitalpdsTheme {
        AboutProgramScreen()
    }
}
