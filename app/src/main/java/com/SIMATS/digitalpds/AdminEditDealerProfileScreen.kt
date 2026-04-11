package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.SIMATS.digitalpds.network.ProfileUpdateRequest
import com.SIMATS.digitalpds.ui.theme.*
import com.SIMATS.digitalpds.ui.theme.DigitalpdsTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Save

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditDealerProfileScreen(
    dealer: DealerInfo,
    onBackClick: () -> Unit,
    onSaveClick: (DealerInfo) -> Unit
) {
    var name by remember { mutableStateOf(dealer.name ?: "") }
    var phone by remember { mutableStateOf(dealer.phone ?: "") }
    var email by remember { mutableStateOf(dealer.email ?: "") }
    var companyName by remember { mutableStateOf(dealer.companyName ?: "") }
    var address by remember { mutableStateOf(dealer.address ?: "") }
    var city by remember { mutableStateOf(dealer.city ?: "") }
    var state by remember { mutableStateOf(dealer.state ?: "") }
    var username by remember { mutableStateOf(dealer.username ?: "") }
    val context = LocalContext.current

    val primaryRed = Color(0xFFD32F2F)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Dealer Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryRed)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(primaryRed, Color(0xFFB71C1C))
                        )
                    )
                    .padding(bottom = 60.dp, top = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        InitialsAvatar(
                            name = name.ifEmpty { "D" },
                            modifier = Modifier.size(96.dp),
                            fontSize = 36.sp
                        )
                    }
            }

            Column(
                modifier = Modifier
                    .offset(y = (-20).dp)
                    .padding(horizontal = 16.dp)
            ) {
                EditSection(title = "Personal Details") {
                    ModernEditField(label = "Dealer Name", value = name, onValueChange = { name = it }, icon = Icons.Default.Person)
                    ModernEditField(label = "Phone Number", value = phone, onValueChange = { phone = it }, icon = Icons.Default.Phone, keyboardType = KeyboardType.Phone)
                    ModernEditField(label = "Email Address", value = email, onValueChange = { email = it }, icon = Icons.Default.Email, keyboardType = KeyboardType.Email)
                    ModernEditField(label = "Username", value = username, onValueChange = { username = it }, icon = Icons.Default.AccountCircle)
                }

                Spacer(modifier = Modifier.height(16.dp))

                EditSection(title = "Business Details") {
                    ModernEditField(label = "Agency/Shop Name", value = companyName, onValueChange = { companyName = it }, icon = Icons.Default.Storefront)
                    ModernEditField(label = "Address", value = address, onValueChange = { address = it }, icon = Icons.Default.Home, singleLine = false)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            ModernEditField(label = "City", value = city, onValueChange = { city = it }, icon = Icons.Default.LocationCity)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ModernEditField(label = "State", value = state, onValueChange = { state = it }, icon = Icons.Default.Map)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val updatedDealer = dealer.copy(
                            name = name,
                            phone = phone,
                            email = email,
                            companyName = companyName,
                            address = address,
                            city = city,
                            state = state,
                            username = username
                        )
                        onSaveClick(updatedDealer)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryRed)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Dealer Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun EditSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
private fun ModernEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(text = label, fontSize = 12.sp, color = TextGray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD32F2F),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color(0xFFFFF9F9)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminEditDealerProfileScreenPreview() {
    DigitalpdsTheme {
        AdminEditDealerProfileScreen(
            dealer = DealerInfo(
                id = 1,
                name = "John Doe",
                phone = "1234567890",
                email = "john.doe@example.com",
                companyName = "Doe Enterprises",
                address = "123 Main St",
                city = "Chennai",
                state = "Tamil Nadu"
            ),
            onBackClick = {},
            onSaveClick = { }
        )
    }
}
