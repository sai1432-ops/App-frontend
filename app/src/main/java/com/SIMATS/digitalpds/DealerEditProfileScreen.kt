package com.SIMATS.digitalpds

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.ProfileUpdateRequest
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealerEditProfileScreen(
    viewModel: DealerViewModel,
    dealerId: Int,
    token: String,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val profile = viewModel.dealerProfile

    var name by remember { mutableStateOf(profile?.name ?: "") }
    var email by remember { mutableStateOf(profile?.email ?: "") }
    var phone by remember { mutableStateOf(profile?.phone ?: "") }
    var companyName by remember { mutableStateOf(profile?.companyName ?: "") }
    var address by remember { mutableStateOf(profile?.address ?: "") }
    var city by remember { mutableStateOf(profile?.city ?: "") }
    var state by remember { mutableStateOf(profile?.state ?: "") }

    LaunchedEffect(profile) {
        profile?.let {
            name = it.name
            email = it.email
            phone = it.phone
            companyName = it.companyName ?: ""
            address = it.address ?: ""
            city = it.city ?: ""
            state = it.state ?: ""
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(DealerGreen, Color(0xFF2E7D32))
                        )
                    )
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Edit Profile",
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
            }
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                "Personal Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            EditField(
                label = "Full Name",
                value = name,
                onValueChange = { name = it },
                icon = Icons.Default.Person
            )

            EditField(
                label = "Email Address",
                value = email,
                onValueChange = { email = it },
                icon = Icons.Default.Email,
                enabled = false // Usually email is primary identifier
            )

            EditField(
                label = "Phone Number",
                value = phone,
                onValueChange = { phone = it },
                icon = Icons.Default.Phone
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = SurfaceLight)
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Business Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            EditField(
                label = "Company Name",
                value = companyName,
                onValueChange = { companyName = it },
                icon = Icons.Default.Business
            )

            EditField(
                label = "Complete Address",
                value = address,
                onValueChange = { address = it },
                icon = Icons.Default.LocationOn,
                singleLine = false,
                minLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    EditField(
                        label = "City",
                        value = city,
                        onValueChange = { city = it },
                        icon = Icons.Default.LocationCity
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    EditField(
                        label = "State",
                        value = state,
                        onValueChange = { state = it },
                        icon = Icons.Default.Map
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    val request = ProfileUpdateRequest(
                        name = name,
                        phone = phone,
                        company_name = companyName,
                        address = address,
                        city = city,
                        state = state
                    )
                    viewModel.updateDealerProfile(dealerId, token, request, onSaveSuccess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DealerGreen),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(icon, contentDescription = null, tint = DealerGreen) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DealerGreen,
                unfocusedBorderColor = SurfaceLight,
                disabledBorderColor = SurfaceLight,
                disabledTextColor = TextGray,
                focusedContainerColor = if (enabled) Color.White else Color(0xFFF5F5F5),
                unfocusedContainerColor = if (enabled) Color.White else Color(0xFFF5F5F5),
                disabledContainerColor = if (enabled) Color.White else Color(0xFFF5F5F5)
            ),
            enabled = enabled,
            singleLine = singleLine,
            minLines = minLines
        )
    }
}
