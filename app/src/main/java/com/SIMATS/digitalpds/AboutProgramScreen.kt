package com.SIMATS.digitalpds

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
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

import androidx.compose.ui.graphics.Brush

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
            // Gradient Header with Emblem
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
                            "About Program",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.gov),
                        contentDescription = "Gov Emblem",
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Mission Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = softBlue, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Our Mission", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "To ensure every Indian has access to affordable oral healthcare and essential hygiene products through digital innovation and subsidized kits.",
                            fontSize = 14.sp,
                            color = textGraySub,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Your Feedback", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(modifier = Modifier.height(16.dp))
                
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Rate your experience", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textGraySub)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            val ratings = listOf("😞", "😐", "😊", "🤩")
                            ratings.forEachIndexed { index, emoji ->
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (selectedRating == index + 1) softBlue.copy(alpha = 0.1f) else Color(0xFFF8FAFC))
                                        .border(2.dp, if (selectedRating == index + 1) softBlue else Color.Transparent, RoundedCornerShape(16.dp))
                                        .clickable { selectedRating = index + 1 },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 28.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            placeholder = { Text("Share your thoughts...", color = Color.LightGray) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = softBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { /* Submit */ },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = softBlue)
                        ) {
                            Text("Submit Feedback", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
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
