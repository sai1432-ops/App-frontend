package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import com.SIMATS.digitalpds.UserBottomNavigationBar
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.FamilyMemberResponse
import com.SIMATS.digitalpds.network.KitReceivedSummary
import com.SIMATS.digitalpds.ui.theme.*
import com.SIMATS.digitalpds.ui.theme.textGraySub
import androidx.compose.ui.draw.shadow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyKitHubScreen(
    userId: Int = 0,
    familyMembers: List<FamilyMemberResponse> = emptyList(),
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onLogUsageClick: () -> Unit = {},
    onConfirmKitClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: FamilyHealthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val distributionHistory by viewModel.distributionHistory.collectAsState()
    val isLoadingHistory by viewModel.isLoadingHistory.collectAsState()
    
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        if (userId > 0) {
            viewModel.fetchDistributionHistory("Bearer ${SessionManager(context).getAccessToken() ?: ""}", userId)
        }
    }

    val softBlue = PrimaryBlue
    val cyanGradient = Color(0xFF00BCD4)

    Scaffold(
        bottomBar = {
            UserBottomNavigationBar(
                currentScreen = "Kits",
                onHomeClick = onHomeClick,
                onKitsClick = { },
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
                        Text(
                            "Family Kit Hub",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Manage your monthly dental kits and track your family's usage.",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Status Banner
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Active Enrollment", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), fontSize = 15.sp)
                            Text("Your family is eligible for current month's kit.", fontSize = 12.sp, color = Color(0xFF2E7D32).copy(alpha = 0.7f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Eligibility Overview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isLoading) {
                        items(2) { EligibilityMemberItem(name = "Family Member", status = "Checking...") }
                    } else if (familyMembers.isEmpty()) {
                        item { 
                            EligibilityMemberItem(name = "None", status = "No family members")
                        }
                    } else {
                        items(familyMembers) { member ->
                            EligibilityMemberItem(name = member.memberName, status = "Eligible")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Service Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(16.dp))

                KitActionCard(
                    title = "Confirm Receipt",
                    subtitle = "Verify and scan your new dental kit",
                    icon = Icons.Default.Inventory,
                    indicatorColor = PrimaryBlue,
                    onClick = onConfirmKitClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                KitActionCard(
                    title = "Log Monthly Usage",
                    subtitle = "Record brushing habits for all members",
                    icon = Icons.Default.EditCalendar,
                    indicatorColor = Color(0xFFEF6C00),
                    onClick = onLogUsageClick
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Distribution History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoadingHistory) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else if (distributionHistory.isEmpty()) {
                    EmptyHistoryPlaceholder()
                } else {
                    distributionHistory.forEach { record ->
                        val itemsSummary = buildString {
                            if (record.brushReceived > 0) append("${record.brushReceived} Brushes ")
                            if (record.pasteReceived > 0) append("${record.pasteReceived} Paste ")
                            if (record.iecReceived > 0) append("${record.iecReceived} IEC ")
                        }.trim()

                        HistoryItem(
                            title = if (itemsSummary.isNotEmpty()) itemsSummary else "Complete Kit",
                            id = record.kit_unique_id,
                            date = record.confirmed_at ?: "Processing",
                            isDelivered = record.status == "Delivered" || record.status == "CONFIRMED",
                            isAlert = record.show_red_alert
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun KitActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    indicatorColor: Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(indicatorColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = indicatorColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = TextBlack, fontSize = 16.sp)
                Text(subtitle, color = textGraySub, fontSize = 13.sp)
            }
            Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun EligibilityMemberItem(name: String, status: String) {
    ElevatedCard(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            InitialsAvatar(
                name = name,
                modifier = Modifier.size(64.dp),
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextBlack, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (status == "Eligible") Color(0xFFE8F5E9) else Color(0xFFF5F5F5)
            ) {
                Text(
                    status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (status == "Eligible") Color(0xFF2E7D32) else Color.Gray
                )
            }
        }
    }
}

@Composable
fun HistoryItem(
    title: String,
    id: String,
    date: String,
    isDelivered: Boolean,
    isAlert: Boolean = false
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isAlert) Color(0xFFFFEBEE) else Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isAlert) Icons.Default.ErrorOutline else Icons.Default.Inventory,
                    contentDescription = null,
                    tint = if (isAlert) Color(0xFFD32F2F) else PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isAlert) Color(0xFFD32F2F) else TextBlack
                )
                Text("Received: $date • ID: ${id.take(8).uppercase()}", fontSize = 11.sp, color = textGraySub)
                if (isAlert) {
                    Text("Previous kit return pending", fontSize = 10.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            }
            if (isDelivered && !isAlert) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF43A047), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun EmptyHistoryPlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(12.dp))
        Text("No kit distributions recorded yet.", color = textGraySub, fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun FamilyKitHubScreenPreview() {
    DigitalpdsTheme {
        FamilyKitHubScreen(onBackClick = {}, onHomeClick = {})
    }
}
