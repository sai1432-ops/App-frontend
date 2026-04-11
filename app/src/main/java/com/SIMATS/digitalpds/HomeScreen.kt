package com.SIMATS.digitalpds

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*
import com.SIMATS.digitalpds.ui.theme.textGraySub
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.SIMATS.digitalpds.network.RetrofitClient

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
    onNotificationClick: () -> Unit = {},
    weeklyTargetProgress: Int = 9,
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
    userName: String = "User",
    profileImage: String? = null
) {
    // Colors for modern look
    val softBlue = PrimaryBlue
    val lightGrey = Color(0xFFF8F9FA)
    val textGraySub = Color(0xFF757575)
    val morningColor = Color(0xFFFFB74D) // Warm Orange
    val eveningColor = Color(0xFF3F51B5) // Deep Indigo
    val cyanGradient = Color(0xFF00BCD4)

    val totalSessions = 14
    val morningCompleted = completedSessions.count { it.first }
    val eveningCompleted = completedSessions.count { it.second }
    val totalCompleted = morningCompleted + eveningCompleted
    val completionPercentage = (totalCompleted.toFloat() / totalSessions * 100).toInt()

    Scaffold(
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
        ) {
            // Premium Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(softBlue, cyanGradient)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 48.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Mukh Swasthya",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Hello, $userName",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (isVerified) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = onNotificationClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = if (isVerified) "Identity Linked • Monthly Kits Active" else "Track your family's daily brushing",
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Offset the first card to overlap with the header
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = (-40).dp)
            ) {
                // Weekly Progress Card - Redesigned for Premium Look
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "$totalCompleted/$totalSessions sessions complete",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextBlack
                                )
                                Text(
                                    "Morning $morningCompleted/7 • Evening $eveningCompleted/7",
                                    fontSize = 13.sp,
                                    color = textGraySub,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$completionPercentage%",
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
                                val (morningDone, eveningDone) =
                                    completedSessions.getOrElse(index) { false to false }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    SessionIndicator(
                                        isDone = morningDone,
                                        activeColor = morningColor,
                                        icon = Icons.Default.WbSunny
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    SessionIndicator(
                                        isDone = eveningDone,
                                        activeColor = eveningColor,
                                        icon = Icons.Default.NightsStay
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = day,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextBlack
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onCheckInClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = softBlue)
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Update Brushing Log", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Weekly Target Progress
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Weekly Target",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Text(
                            "$totalCompleted/14",
                            fontSize = 14.sp,
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
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = softBlue,
                        trackColor = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))



                // AI Health Analysis - Prominent Gradient CTA
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(colors = listOf(softBlue, Color(0xFF3F51B5))))
                            .clickable { onAnalysisOptionClick() }
                            .padding(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "AI Teeth Scan",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Check for dental issues in seconds",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Family Health profiles",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(16.dp))

                ModernHomeCard(
                    title = userName,
                    subtitle = "Main Profile",
                    tag = if (isVerified) "Verified" else "Guest",
                    tagColor = if (isVerified) Color(0xFF2E7D32) else Color(0xFFFBC02D),
                    onClick = onUserHealthProfileClick,
                    isVerified = isVerified,
                    profileImage = profileImage
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Add Family Quick Link
                Surface(
                    onClick = onManageClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = softBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Manage Family Members",
                            color = softBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
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
    isVerified: Boolean = false,
    profileImage: String? = null
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
            
            if (profileImage != null) {
                AsyncImage(
                    model = if (profileImage.startsWith("http")) profileImage else "${RetrofitClient.BASE_URL}/$profileImage",
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                InitialsAvatar(
                    name = title,
                    modifier = Modifier.size(48.dp),
                    fontSize = 18.sp
                )
            }
            
            Icon(
                Icons.Filled.ArrowForward,
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
