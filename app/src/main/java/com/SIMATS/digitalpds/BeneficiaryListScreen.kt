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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    onBeneficiaryClick: (String) -> Unit,
    onAddNewClick: () -> Unit,
    viewModel: BeneficiaryViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val currentScreen = "Beneficiary"

    LaunchedEffect(dealerId) {
        if (dealerId > 0) {
            viewModel.fetchBeneficiaries(dealerId)
        }
    }

    val beneficiaries = viewModel.beneficiaries
    val isLoading = viewModel.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Beneficiary List",
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextBlack
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        bottomBar = {
            AppBottomNavigationBar(currentScreen, onNavigate)
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by Name or Ration Card ID") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color(0xFFF1F4F8),
                    unfocusedContainerColor = Color(0xFFF1F4F8)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddNewClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Add New Beneficiary",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        beneficiaries.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                                    it.rationId.contains(searchQuery, ignoreCase = true)
                        }
                    ) { beneficiary ->
                        BeneficiaryRow(
                            beneficiary = beneficiary,
                            onClick = { onBeneficiaryClick(beneficiary.householdId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BeneficiaryRow(beneficiary: Beneficiary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                fontSize = 14.sp
            )
        }

        if (beneficiary.isActive) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32))
            )
        }
    }
}

@Composable
fun AppBottomNavigationBar(currentScreen: String, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = BackgroundWhite,
        tonalElevation = 8.dp
    ) {
        val items = listOf("Home", "Beneficiary", "Stock", "Profile")
        val icons = mapOf(
            "Home" to Icons.Default.Home,
            "Beneficiary" to Icons.Default.Groups,
            "Stock" to Icons.Default.Inventory,
            "Profile" to Icons.Default.Person
        )

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(icons[screen]!!, contentDescription = screen) },
                label = { Text(screen) },
                selected = currentScreen == screen,
                onClick = { onNavigate(screen) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    unselectedIconColor = TextGray,
                    selectedTextColor = PrimaryBlue,
                    unselectedTextColor = TextGray,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                )
            )
        }
    }
}