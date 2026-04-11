package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.network.Beneficiary
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeneficiaryListScreen(
    dealerId: Int = 0,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    onBeneficiaryClick: (Beneficiary) -> Unit,
    onAddNewClick: () -> Unit,
    viewModel: BeneficiaryViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val currentScreen = "Beneficiary"

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(dealerId) {
        if (dealerId > 0) {
            val token = com.SIMATS.digitalpds.SessionManager(context).getAccessToken() ?: ""
            viewModel.fetchBeneficiaries(token, dealerId)
        }
    }

    val beneficiaries = viewModel.beneficiaries
    val isLoading = viewModel.isLoading

    Box(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {
        // Top Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
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
                            "Beneficiary List",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                AppBottomNavigationBar(currentScreen, onNavigate)
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Search Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0D000000)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search by Name or Card ID", color = TextGray.copy(alpha = 0.5f)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = DealerGreen
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = SurfaceLight,
                                unfocusedContainerColor = SurfaceLight
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onAddNewClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DealerGreen),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "ADD NEW BENEFICIARY",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val errorMessage = viewModel.errorMessage
                val filteredBeneficiaries = beneficiaries.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.rationId.contains(searchQuery, ignoreCase = true)
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = DealerGreen)
                    }
                } else if (errorMessage != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = RedError, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(errorMessage, color = RedError, fontWeight = FontWeight.Medium)
                            Button(
                                onClick = { 
                                    val token = com.SIMATS.digitalpds.SessionManager(context).getAccessToken() ?: ""
                                    viewModel.fetchBeneficiaries(token, dealerId) 
                                },
                                modifier = Modifier.padding(top = 16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DealerGreen)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                } else if (filteredBeneficiaries.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextGray, modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(16.dp))
                            Text(
                                if (searchQuery.isEmpty()) "No beneficiaries registered yet" else "No matching beneficiaries found",
                                color = TextGray,
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(filteredBeneficiaries) { beneficiary ->
                            BeneficiaryRow(
                                beneficiary = beneficiary,
                                onClick = { onBeneficiaryClick(beneficiary) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BeneficiaryRow(beneficiary: Beneficiary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0x0D000000)),
        shape = RoundedCornerShape(16.dp),
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
                shape = CircleShape,
                color = DealerSecondary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = beneficiary.name.take(1).uppercase(),
                        fontWeight = FontWeight.Black,
                        color = DealerGreen,
                        fontSize = 18.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    beneficiary.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextBlack
                )
                Text(
                    beneficiary.rationId,
                    color = TextGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (beneficiary.isActive) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        "Verified",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = TextGray.copy(alpha = 0.3f))
        }
    }
}

@Composable
fun AppBottomNavigationBar(currentScreen: String, onNavigate: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 24.dp,
        tonalElevation = 8.dp,
        color = Color.White
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            modifier = Modifier.height(80.dp)
        ) {
            val items = listOf("Home", "Beneficiary", "Stock", "Profile")
            val icons = mapOf(
                "Home" to Icons.Default.Home,
                "Beneficiary" to Icons.Default.Groups,
                "Stock" to Icons.Default.Inventory,
                "Profile" to Icons.Default.Person
            )

            items.forEach { screen ->
                val isSelected = currentScreen == screen
                NavigationBarItem(
                    icon = { 
                        Icon(
                            icons[screen]!!, 
                            contentDescription = screen,
                            modifier = Modifier.size(if (isSelected) 26.dp else 24.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            screen,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            fontSize = 12.sp
                        ) 
                    },
                    selected = isSelected,
                    onClick = { onNavigate(screen) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DealerGreen,
                        unselectedIconColor = TextGray.copy(alpha = 0.6f),
                        selectedTextColor = DealerGreen,
                        unselectedTextColor = TextGray.copy(alpha = 0.6f),
                        indicatorColor = DealerGreen.copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}