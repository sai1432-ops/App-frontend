package com.SIMATS.digitalpds

import android.util.Patterns
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.network.RegisterRequest
import com.SIMATS.digitalpds.ui.theme.DigitalpdsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddDealerScreen(
    onBackClick: () -> Unit = {},
    onAddDealerSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    var isOtpSent by remember { mutableStateOf(false) }
    var registrationOtp by remember { mutableStateOf("") }
    var isEmailVerified by remember { mutableStateOf(false) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }

    val primaryRed = Color(0xFFD32F2F)

    fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Dealer", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                    ModernEditField(label = "Full Name", value = name, onValueChange = { name = it; nameError = null }, icon = Icons.Default.Person, error = nameError)
                    ModernEditField(label = "Phone Number", value = phone, onValueChange = { phone = it; phoneError = null }, icon = Icons.Default.Phone, keyboardType = KeyboardType.Phone, error = phoneError)
                    
                    // Email Field with Get Code
                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                        Text(text = "Email Address", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    emailError = null
                                    isEmailVerified = false
                                    isOtpSent = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                isError = emailError != null,
                                enabled = !isEmailVerified,
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = primaryRed, modifier = Modifier.size(20.dp)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryRed,
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedContainerColor = Color(0xFFFFF9F9)
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                label = { Text("Email Address") }
                            )
                            
                            if (!isEmailVerified) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                            emailError = "Valid email is required"
                                            return@Button
                                        }
                                        isSendingOtp = true
                                        scope.launch {
                                            try {
                                                val response = RetrofitClient.apiService.sendDealerRegistrationOtp(
                                                    com.SIMATS.digitalpds.network.RegistrationOtpRequest(email.trim().lowercase())
                                                )
                                                if (response.isSuccessful) {
                                                    isOtpSent = true
                                                    Toast.makeText(context, response.body()?.message ?: "Code sent successfully", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    val errorBody = response.errorBody()?.string() ?: "Failed to send code"
                                                    Toast.makeText(context, errorBody, Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isSendingOtp = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isSendingOtp && email.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryRed)
                                ) {
                                    if (isSendingOtp) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text(if (isOtpSent) "Resend" else "Get Code", fontSize = 12.sp)
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.CheckCircle, "Verified", tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                            }
                        }
                        if (emailError != null) {
                            Text(emailError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                        }
                    }

                    // OTP Verification Field
                    if (isOtpSent && !isEmailVerified) {
                        Column(modifier = Modifier.padding(bottom = 12.dp)) {
                            Text(text = "Verification Code", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = registrationOtp,
                                    onValueChange = { registrationOtp = it; otpError = null },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    isError = otpError != null,
                                    placeholder = { Text("6-digit OTP") },
                                    leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = primaryRed, modifier = Modifier.size(20.dp)) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = primaryRed,
                                        unfocusedBorderColor = Color(0xFFE0E0E0),
                                        focusedContainerColor = Color(0xFFFFF9F9)
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (registrationOtp.length != 6) {
                                            otpError = "Enter 6-digit code"
                                            return@Button
                                        }
                                        isVerifyingOtp = true
                                        scope.launch {
                                            try {
                                                val response = RetrofitClient.apiService.verifyDealerRegistrationOtp(
                                                    com.SIMATS.digitalpds.network.VerifyRegistrationOtpRequest(email.trim().lowercase(), registrationOtp)
                                                )
                                                if (response.isSuccessful) {
                                                    isEmailVerified = true
                                                    Toast.makeText(context, "Email verified successfully!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    val errorBody = response.errorBody()?.string() ?: "Invalid code"
                                                    Toast.makeText(context, errorBody, Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isVerifyingOtp = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isVerifyingOtp && registrationOtp.length == 6,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) {
                                    if (isVerifyingOtp) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Verify", fontSize = 12.sp)
                                    }
                                }
                            }
                            if (otpError != null) {
                                Text(otpError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                EditSection(title = "Business / Shop Details") {
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

                Spacer(modifier = Modifier.height(16.dp))

                EditSection(title = "Account Details") {
                    ModernEditField(label = "Username", value = username, onValueChange = { username = it; usernameError = null }, icon = Icons.Default.AccountCircle, error = usernameError)
                    ModernPasswordField(
                        label = "Create Password", 
                        value = password, 
                        onValueChange = { password = it; passwordError = null }, 
                        icon = Icons.Default.Lock, 
                        visible = passwordVisible, 
                        onToggleVisibility = { passwordVisible = !passwordVisible }, 
                        error = passwordError
                    )
                    ModernPasswordField(
                        label = "Confirm Password", 
                        value = confirmPassword, 
                        onValueChange = { confirmPassword = it; confirmPasswordError = null }, 
                        icon = Icons.Default.LockReset, 
                        visible = passwordVisible, 
                        onToggleVisibility = { passwordVisible = !passwordVisible }, 
                        error = confirmPasswordError
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        var isValid = true

                        if (name.isBlank()) {
                            nameError = "Name is required"
                            isValid = false
                        }

                        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailError = "Valid email is required"
                            isValid = false
                        }

                        if (phone.length != 10) {
                            phoneError = "Invalid phone number"
                            isValid = false
                        }

                        if (username.isBlank()) {
                            usernameError = "Username is required"
                            isValid = false
                        }

                        if (!isValidPassword(password)) {
                            passwordError = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character"
                            isValid = false
                        }

                        if (password != confirmPassword) {
                            confirmPasswordError = "Passwords do not match"
                            isValid = false
                        }

                        if (isValid && !isLoading) {
                            if (!isEmailVerified) {
                                emailError = "Please verify the dealer email"
                                return@Button
                            }
                            isLoading = true
                            scope.launch {
                                try {
                                    val response = RetrofitClient.apiService.dealerRegister(
                                        RegisterRequest(
                                            name = name,
                                            email = email,
                                            password = password,
                                            phone = phone,
                                            companyName = companyName,
                                            address = address,
                                            city = city,
                                            state = state,
                                            username = username
                                        )
                                    )

                                    if (response.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Dealer added successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onAddDealerSuccess()
                                    } else {
                                        val errorBody = response.errorBody()?.string()
                                        Toast.makeText(
                                            context,
                                            "Failed: ${errorBody ?: response.message()}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Error: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading && isEmailVerified,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryRed,
                        disabledContainerColor = primaryRed.copy(alpha = 0.5f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Create Dealer Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
    singleLine: Boolean = true,
    error: String? = null
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = error != null,
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD32F2F),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color(0xFFFFF9F9)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            supportingText = {
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }
}

@Composable
private fun ModernPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = error != null,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD32F2F),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color(0xFFFFF9F9)
            ),
            singleLine = true,
            supportingText = {
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminAddDealerScreenPreview() {
    DigitalpdsTheme {
        AdminAddDealerScreen(
            onBackClick = {},
            onAddDealerSuccess = {}
        )
    }
}
