package com.SIMATS.digitalpds

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
fun HomeScreen(
    onFamilyProfileClick: () -> Unit = {},
    onCheckInClick: () -> Unit = {},
    onManageClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onMyProfileClick: () -> Unit = {},
    onUserHealthProfileClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onAnalysisOptionClick: () -> Unit = {},
    weeklyTargetProgress: Int = 9, // Example: 9 out of 14 sessions
    // completedSessions: Pair of (Morning, Evening) for each day (Mon-Sun)
    completedSessions: List<Pair<Boolean, Boolean>> = listOf(
        true to true,   // Mon
        true to true,   // Tue
        true to false,  // Wed
        true to true,   // Thu
        true to false,  // Fri
        false to false, // Sat
        false to false  // Sun
    ),
    isVerified: Boolean = false,
    userName: String = "User"
) {
    // Colors for modern look
    val softBlue = PrimaryBlue
    val lightGrey = Color(0xFFF8F9FA)
    val textGraySub = Color(0xFF757575)
    val morningColor = Color(0xFFFFB74D) // Warm Orange
    val eveningColor = Color(0xFF5C6BC0) // Deep Indigo

    val totalSessions = 14
    val morningCompleted = completedSessions.count { it.first }
    val eveningCompleted = completedSessions.count { it.second }
    val totalCompleted = morningCompleted + eveningCompleted
    val completionPercentage = (totalCompleted.toFloat() / totalSessions * 100).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Mukh Swasthya",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            UserBottomNavigationBar(
                currentScreen = "Home",
                onHomeClick = { },
                onKitsClick = onKitsClick,
                onLearnClick = onLearnClick,
                onConsultClick = onConsultClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = lightGrey
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Greeting Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hello, $userName",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                if (isVerified) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = if (isVerified) "Identity Linked • Monthly Kits Active" else "Track your family's daily brushing",
                fontSize = 15.sp,
                color = if (isVerified) Color(0xFF4CAF50) else textGraySub,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Weekly Progress Card - Redesigned for Dual Sessions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "$totalCompleted/$totalSessions sessions completed",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                            Text(
                                "Morning $morningCompleted/7 • Evening $eveningCompleted/7",
                                fontSize = 13.sp,
                                color = textGraySub,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$completionPercentage%",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Stacked Session Indicators Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val days = listOf("M", "T", "W", "T", "F", "S", "S")
                        
                        days.forEachIndexed { index, day ->
                            val (morningDone, eveningDone) = completedSessions.getOrElse(index) { false to false }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Morning Indicator
                                SessionIndicator(isDone = morningDone, activeColor = morningColor, icon = Icons.Default.WbSunny)
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Evening Indicator
                                SessionIndicator(isDone = eveningDone, activeColor = eveningColor, icon = Icons.Default.NightsStay)
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text(text = day, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = onCheckInClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = softBlue)
                    ) {
                        Text("Check-in Update", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Weekly Target Section - Updated for 14 sessions
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Weekly Target",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextBlack
                    )
                    Text(
                        "$totalCompleted/14",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = softBlue
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                val animatedProgress by animateFloatAsState(
                    targetValue = totalCompleted.toFloat() / 14f,
                    animationSpec = tween(durationMillis = 1000)
                )
                
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = softBlue,
                    trackColor = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Health Profiles",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextBlack
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ModernHomeCard(
                title = userName,
                subtitle = "Primary User",
                tag = "Healthy",
                tagColor = Color(0xFF4CAF50),
                onClick = onUserHealthProfileClick,
                isVerified = isVerified
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Quick Health Analysis Section
            Text(
                "AI Health Analysis",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onAnalysisOptionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = softBlue)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Start AI Scan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Replaced ActionCard with a centered single card for Family
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ActionCard(
                    title = "Family Members",
                    icon = Icons.Default.Groups,
                    modifier = Modifier.fillMaxWidth(0.6f),
                    onClick = onManageClick
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SessionIndicator(isDone: Boolean, activeColor: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDone) activeColor else Color(0xFFF0F2F5)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (isDone) Color.White else Color.LightGray
        )
    }
}

@Composable
fun ActionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextBlack)
        }
    }
}

@Composable
fun ModernHomeCard(
    title: String,
    subtitle: String,
    tag: String,
    tagColor: Color,
    onClick: () -> Unit,
    isVerified: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    if (isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF4CAF50))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        color = tagColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = tagColor
                        )
                    }
                }
                Text(subtitle, fontSize = 14.sp, color = Color(0xFF757575), modifier = Modifier.padding(top = 4.dp))
            }
            
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp),
                tint = Color.LightGray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DigitalpdsTheme {
        HomeScreen()
    }
}
