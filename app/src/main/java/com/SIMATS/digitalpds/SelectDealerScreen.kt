package com.SIMATS.digitalpds

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.DealerNetworkInfo
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.network.SelectDealerRequest
import com.SIMATS.digitalpds.ui.theme.PrimaryBlue
import com.SIMATS.digitalpds.ui.theme.TextBlack
import com.SIMATS.digitalpds.ui.theme.TextGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDealerScreen(
    userId: Int,
    onBackClick: () -> Unit,
    onDealerSelected: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var dealers by remember { mutableStateOf<List<DealerNetworkInfo>>(emptyList()) }
    var selectedDealer by remember { mutableStateOf<DealerNetworkInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Load dealers
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.apiService.getUserDealers()
            if (response.isSuccessful) {
                dealers = response.body() ?: emptyList()
            } else {
                Toast.makeText(context, "Failed to load dealers", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.verticalGradient(listOf(PrimaryBlue, Color(0xFF1976D2))))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        "Select Your Dealer",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        "Choose your preferred PDS dealer for kit collection",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .offset(y = (-30).dp)
            ) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading dealers...", color = TextGray, fontSize = 14.sp)
                        }
                    }
                } else if (dealers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(Color(0xFFF1F5F9), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Storefront, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                "No Dealers Available",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "There are no active PDS dealers currently available. Please contact support.",
                                color = TextGray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, null, tint = PrimaryBlue.copy(0.7f), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "${dealers.size} active dealers found",
                                    fontSize = 12.sp,
                                    color = TextGray
                                )
                            }
                        }
                        
                        items(dealers) { d ->
                            DealerItem(
                                dealer = d,
                                isSelected = selectedDealer?.id == d.id,
                                onClick = { selectedDealer = d }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        // Fixed bottom button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    if (selectedDealer == null) {
                        Toast.makeText(context, "Please select a dealer", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isSubmitting = true
                    scope.launch {
                        try {
                            val token = com.SIMATS.digitalpds.SessionManager(context).getAccessToken() ?: ""
                            val response = RetrofitClient.apiService.selectDealer(
                                "Bearer $token",
                                SelectDealerRequest(userId = userId, dealer_id = selectedDealer!!.id)
                            )
                             if (response.isSuccessful) {
                                 val sessionManager = com.SIMATS.digitalpds.SessionManager(context)
                                 sessionManager.setPdsVerified(true)
                                 
                                 // Persist dealer metadata locally for immediate UI update
                                 selectedDealer?.let { dealer ->
                                     sessionManager.setAssignedDealerId(dealer.id)
                                     sessionManager.setAssignedDealerName(dealer.name)
                                     sessionManager.setAssignedDealerLocation(dealer.location ?: "")
                                 }
                                 
                                 Toast.makeText(context, "Dealer selected successfully!", Toast.LENGTH_SHORT).show()
                                 onDealerSelected()
                             } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Toast.makeText(context, "Failed: $errorBody", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(if (selectedDealer != null) 8.dp else 0.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                enabled = !isSubmitting && selectedDealer != null && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = Color(0xFFE2E8F0)
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Confirm Selection", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun DealerItem(
    dealer: DealerNetworkInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) PrimaryBlue else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) Color.White else Color(0xFFF1F5F9)
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isSelected) PrimaryBlue else Color.White,
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Storefront,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dealer.name,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextBlack,
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Area: ${dealer.location ?: "Main Center"}",
                    color = TextGray,
                    fontSize = 13.sp
                )
                if (!dealer.companyName.isNullOrBlank()) {
                    Text(
                        text = dealer.companyName,
                        color = PrimaryBlue.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
