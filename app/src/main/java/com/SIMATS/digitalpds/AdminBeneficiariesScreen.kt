package com.SIMATS.digitalpds

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.*
import kotlinx.coroutines.launch

data class AdminBeneficiary(
    val id: Int,
    val name: String,
    val pds_card_no: String?,
    val pds_verified: Boolean,
    val pds_linked_at: String?,
    val phone: String?,
    val email: String?,
    val dealer_name: String?,
    val location_name: String?
)

class BeneficiaryListViewModel : ViewModel() {
    var beneficiaries by mutableStateOf<List<AdminBeneficiary>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchBeneficiaries(token: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getAdminBeneficiaries("Bearer $token")
                if (response.isSuccessful) {
                    beneficiaries = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBeneficiariesScreen(
    onBackClick: () -> Unit = {},
    onBeneficiaryClick: (AdminBeneficiary) -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: BeneficiaryListViewModel = viewModel()
) {
    val context = LocalContext.current
    val token = remember { SessionManager(context).getAccessToken() ?: "" }
    var searchQuery by remember { mutableStateOf("") }
    val primaryRed = Color(0xFFD32F2F)

    androidx.activity.compose.BackHandler {
        onBackClick()
    }

    LaunchedEffect(Unit) {
        viewModel.fetchBeneficiaries(token)
    }

    val beneficiaries = viewModel.beneficiaries
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    val filteredList = beneficiaries.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.id.toString().contains(searchQuery, ignoreCase = true) ||
                (it.pds_card_no?.contains(searchQuery, ignoreCase = true) == true) ||
                (it.dealer_name?.contains(searchQuery, ignoreCase = true) == true) ||
                (it.location_name?.contains(searchQuery, ignoreCase = true) == true)
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
                            "Beneficiaries",
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
                        placeholder = { Text("Search by name, card, dealer or location...", color = Color.White.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
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
            AdminBottomNavigationBar(currentScreen = "Beneficiaries", onNavigate = onNavigate)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate("admin_add_beneficiary") },
                containerColor = primaryRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Beneficiary")
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = primaryRed
                )
            } else if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (filteredList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No beneficiaries found",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredList) { beneficiary ->
                        BeneficiaryCard(
                            beneficiary = beneficiary,
                            onClick = { onBeneficiaryClick(beneficiary) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BeneficiaryCard(
    beneficiary: AdminBeneficiary,
    onClick: () -> Unit
) {
    val firstLetter = beneficiary.name.take(1).uppercase()
    
    val avatarColors = listOf(
        Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8), 
        Color(0xFF9575CD), Color(0xFF7986CB), Color(0xFF64B5F6),
        Color(0xFF4FC3F7), Color(0xFF4DD0E1), Color(0xFF4DB6AC),
        Color(0xFF81C784), Color(0xFFAED581), Color(0xFFFFB74D)
    )
    val avatarBg = avatarColors[Math.abs(beneficiary.name.hashCode()) % avatarColors.size]

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
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = beneficiary.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Text(
                    text = "PDS: ${beneficiary.pds_card_no ?: "Not Linked"}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                if (!beneficiary.phone.isNullOrEmpty()) {
                    Text(
                        text = "Phone: ${beneficiary.phone}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                if (!beneficiary.email.isNullOrEmpty()) {
                    Text(
                        text = "Email: ${beneficiary.email}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                if (!beneficiary.dealer_name.isNullOrEmpty() && beneficiary.dealer_name != "N/A") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Storefront,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Dealer: ${beneficiary.dealer_name}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
                if (!beneficiary.location_name.isNullOrEmpty() && beneficiary.location_name != "N/A") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Location: ${beneficiary.location_name}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = beneficiary.pds_linked_at ?: "N/A",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val statusText = if (beneficiary.pds_verified) "Verified" else "Not Verified"
                val statusColor = if (beneficiary.pds_verified) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                val statusBg = if (beneficiary.pds_verified) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.LightGray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminBeneficiariesScreenPreview() {
    DigitalpdsTheme {
        AdminBeneficiariesScreen()
    }
}
