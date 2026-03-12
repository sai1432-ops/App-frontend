package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountAndSecurityScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    var biometricEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Account & Security",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack,
                            modifier = Modifier.padding(end = 48.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        bottomBar = {
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Save Security Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Password Management", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            
            Spacer(modifier = Modifier.height(24.dp))

            Text("Current Password", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                placeholder = "Enter currentpassword",
                visible = currentPasswordVisible,
                onToggleVisibility = { currentPasswordVisible = !currentPasswordVisible }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("New Password", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordField(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = "Enter newpassword",
                visible = newPasswordVisible,
                onToggleVisibility = { newPasswordVisible = !newPasswordVisible }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Confirm Password", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirm newpassword",
                visible = confirmPasswordVisible,
                onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("Account Security", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE9F1F1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = TextBlack)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Enable Biometric Logi...", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextBlack)
                }
                Switch(
                    checked = biometricEnabled,
                    onCheckedChange = { biometricEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Login Protection", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Last Active: 2024-01-20 10:30 AM", fontSize = 14.sp, color = TextBlack)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Sign out from all other devices",
                fontSize = 14.sp,
                color = TextBlack,
                modifier = Modifier.clickable { /* TODO */ }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visible: Boolean,
    onToggleVisibility: () -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF0F4F4),
            unfocusedContainerColor = Color(0xFFF0F4F4),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AccountAndSecurityScreenPreview() {
    DigitalpdsTheme {
        AccountAndSecurityScreen(onBackClick = {}, onSaveClick = {})
    }
}
