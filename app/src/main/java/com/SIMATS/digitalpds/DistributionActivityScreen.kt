package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class DistributionRecord(
    val beneficiary_name: String,
    val time: String,
    val date: String,
    val category: String,
    val items_summary: String,
    val oldKitReturned: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistributionActivityScreen(
    dealerId: Int = 0,
    records: List<DistributionRecord>,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    dealerViewModel: DealerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateString by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(dealerId) {
        if (dealerId > 0) {
            val token = com.SIMATS.digitalpds.SessionManager(context).getAccessToken() ?: ""
            dealerViewModel.fetchDistributionHistory(dealerId, token)
        }
    }

    val historyRecords = dealerViewModel.distributionHistory.map {
        DistributionRecord(
            beneficiary_name = it.beneficiary_name,
            time = it.time,
            date = it.date,
            category = it.category,
            items_summary = it.items_summary,
            oldKitReturned = it.oldKitReturned
        )
    }

    val allRecords = (records + historyRecords).distinctBy { "${it.beneficiary_name}-${it.date}-${it.time}" }
    val filteredRecords = allRecords.filter { it.date == selectedDateString }
    
    val displayDate = remember(selectedDateString) {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDateString)
            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date!!)
        } catch (e: Exception) {
            selectedDateString
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDateString =
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = DealerGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {
        // Premium Gradient Header background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DealerGreen, BackgroundWhite)
                    )
                )
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Distribution Activity", 
                            fontWeight = FontWeight.ExtraBold, 
                            color = Color.White,
                            fontSize = 20.sp
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Filter", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                AppBottomNavigationBar(currentScreen = "Home", onNavigate = onNavigate)
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Statistics Summary Row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DistributionStatCard(
                            label = "Total Kits",
                            value = filteredRecords.size.toString(),
                            icon = Icons.Default.Inventory2,
                            modifier = Modifier.weight(1f)
                        )
                        DistributionStatCard(
                            label = "Old Kits",
                            value = filteredRecords.count { it.oldKitReturned }.toString(),
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                        Text(
                            text = "Activity Log – $displayDate",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextBlack
                        )
                        Text(
                            text = "Detailed list of kits handed over today",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                if (filteredRecords.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    modifier = Modifier.size(80.dp),
                                    shape = CircleShape,
                                    color = SurfaceLight
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.History, 
                                            contentDescription = null, 
                                            modifier = Modifier.size(40.dp),
                                            tint = DealerGreen.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No activity found for this date",
                                    color = TextGray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else {
                    items(filteredRecords) { record ->
                        ActivityCard(record)
                    }
                }
            }
        }
    }
}

@Composable
fun DistributionStatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x1A000000)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DealerSecondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = DealerGreen, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(label, fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextBlack)
        }
    }
}

@Composable
fun ActivityCard(record: DistributionRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0D000000)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = DealerSecondary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = record.beneficiary_name.take(1).uppercase(),
                        fontWeight = FontWeight.Black,
                        color = DealerGreen,
                        fontSize = 20.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.beneficiary_name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, size = 12.dp, tint = TextGray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = record.time, fontSize = 12.sp, color = TextGray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = record.items_summary,
                    fontSize = 13.sp,
                    color = Color(0xFF455A64),
                    lineHeight = 18.sp
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                if (record.oldKitReturned) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle, 
                            contentDescription = "Returned", 
                            modifier = Modifier.padding(4.dp).size(16.dp), 
                            tint = Color(0xFF2E7D32)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFEBEE)
                    ) {
                        Icon(
                            Icons.Default.Cancel, 
                            contentDescription = "Not Returned", 
                            modifier = Modifier.padding(4.dp).size(16.dp), 
                            tint = Color(0xFFD32F2F)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp, tint: Color) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier.size(size),
        tint = tint
    )
}

@Preview(showBackground = true)
@Composable
fun DistributionActivityScreenPreview() {
    DigitalpdsTheme {
        DistributionActivityScreen(records = emptyList())
    }
}
