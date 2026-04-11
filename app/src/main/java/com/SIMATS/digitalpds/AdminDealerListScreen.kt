package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDealerListScreen(
    onBackClick: () -> Unit = {},
    onAddDealerClick: () -> Unit = {},
    onDealerClick: (DealerInfo) -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: AdminDealerViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val token = SessionManager(context).getAccessToken() ?: ""

    AdminDealerListContent(
        dealers = viewModel.dealers,
        isLoading = viewModel.isLoading,
        errorMessage = viewModel.errorMessage,
        onBackClick = onBackClick,
        onAddDealerClick = onAddDealerClick,
        onDealerClick = onDealerClick,
        onNavigate = onNavigate,
        fetchDealers = { viewModel.fetchDealers(token) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDealerListContent(
    dealers: List<DealerInfo>,
    isLoading: Boolean,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onAddDealerClick: () -> Unit,
    onDealerClick: (DealerInfo) -> Unit,
    onNavigate: (String) -> Unit,
    fetchDealers: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val primaryRed = Color(0xFFD32F2F)

    LaunchedEffect(Unit) {
        fetchDealers()
    }

    val filteredDealers = remember(searchQuery, dealers) {
        dealers.filter {
            (it.name ?: "").contains(searchQuery, ignoreCase = true) ||
                    (it.handle ?: "").contains(searchQuery, ignoreCase = true) ||
                    (it.location ?: "").contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(primaryRed, primaryRed.copy(alpha = 0.8f))
                        )
                    )
                    .statusBarsPadding()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Dealers Management",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onAddDealerClick) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Add Dealer", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search dealers...", color = Color.White.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search", tint = Color.White)
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        bottomBar = {
            AdminBottomNavigationBar(
                currentScreen = "Dealers",
                onNavigate = onNavigate
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = primaryRed
                    )
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = primaryRed, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage, color = Color.Red, fontSize = 14.sp)
                        Button(onClick = fetchDealers, colors = ButtonDefaults.buttonColors(containerColor = primaryRed)) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (filteredDealers.isEmpty()) {
                            item {
                                EmptyDealerState(searchQuery.isNotEmpty())
                            }
                        } else {
                            items(filteredDealers) { dealer ->
                                ModernDealerCard(
                                    dealer = dealer,
                                    onClick = { onDealerClick(dealer) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernDealerCard(dealer: DealerInfo, onClick: () -> Unit) {
    val primaryRed = Color(0xFFD32F2F)
    val firstLetter = dealer.name?.firstOrNull()?.uppercase() ?: "?"
    
    // Background colors for avatars based on name
    val avatarColors = listOf(
        Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8), 
        Color(0xFF9575CD), Color(0xFF7986CB), Color(0xFF64B5F6),
        Color(0xFF4FC3F7), Color(0xFF4DD0E1), Color(0xFF4DB6AC),
        Color(0xFF81C784), Color(0xFFAED581), Color(0xFFFFB74D)
    )
    val avatarBg = avatarColors[Math.abs(dealer.name.hashCode()) % avatarColors.size]
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(56.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(avatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = firstLetter,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (dealer.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                            .border(2.dp, Color.White, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dealer.name ?: "Unknown Dealer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    if (dealer.emailVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    val addressText = when {
                        !dealer.location.isNullOrEmpty() -> dealer.location
                        !dealer.city.isNullOrEmpty() && !dealer.state.isNullOrEmpty() -> "${dealer.city}, ${dealer.state}"
                        else -> dealer.address ?: "No Address Provided"
                    }
                    
                    Text(
                        text = addressText,
                        fontSize = 13.sp,
                        color = TextGray
                    )
                }
            }

            Surface(
                color = if (dealer.activeStatus == "Active") Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = dealer.activeStatus ?: "Offline",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (dealer.activeStatus == "Active") Color(0xFF2E7D32) else Color(0xFF757575)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}

@Composable
fun EmptyDealerState(isSearching: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSearching) Icons.Default.SearchOff else Icons.Default.Group,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isSearching) "No matching dealers found" else "No dealers registered yet",
            fontSize = 16.sp,
            color = TextGray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDealerListScreenPreview() {
    DigitalpdsTheme {
        val sampleDealers = listOf(
            DealerInfo(
                id = 1,
                name = "Chennai North Agency",
                handle = "@chennainorth",
                location = "Madhavaram, Chennai",
                phone = "9876543210",
                activeStatus = "Active",
                isOnline = true
            ),
            DealerInfo(
                id = 2,
                name = "Salem Distribution Point",
                handle = "@salemdist",
                location = "Fairlands, Salem",
                phone = "8765432109",
                activeStatus = "Active",
                isOnline = false
            )
        )
        AdminDealerListContent(
            dealers = sampleDealers,
            isLoading = false,
            errorMessage = null,
            onBackClick = {},
            onAddDealerClick = {},
            onDealerClick = {},
            onNavigate = {},
            fetchDealers = {}
        )
    }
}