package com.SIMATS.digitalpds

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.RegisterRequest
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddDealerScreen(
    onBackClick: () -> Unit,
    onAddDealerSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Dealer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = Color(0xFFF8FBFF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                "Dealer Personal Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(16.dp))

            AdminInputField(
                label = "Full Name",
                value = name,
                onValueChange = { name = it; nameError = null },
                placeholder = "Enter dealer full name",
                error = nameError
            )
            Spacer(modifier = Modifier.height(16.dp))

            AdminInputField(
                label = "Email Address",
                value = email,
                onValueChange = { email = it; emailError = null },
                placeholder = "dealer@example.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                error = emailError
            )
            Spacer(modifier = Modifier.height(16.dp))

            AdminInputField(
                label = "Phone Number",
                value = phone,
                onValueChange = { phone = it; phoneError = null },
                placeholder = "Enter phone number",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                error = phoneError
            )
            Spacer(modifier = Modifier.height(16.dp))

            AdminInputField(
                label = "Area / Location",
                value = location,
                onValueChange = { location = it },
                placeholder = "Enter city / area"
            )
            Spacer(modifier = Modifier.height(16.dp))

            AdminInputField(
                label = "Address (Optional)",
                value = address,
                onValueChange = { address = it },
                placeholder = "Enter full address",
                singleLine = false,
                modifier = Modifier.height(100.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Account Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(16.dp))

            AdminInputField(
                label = "Username",
                value = username,
                onValueChange = { username = it; usernameError = null },
                placeholder = "@dealer_username",
                error = usernameError
            )
            Spacer(modifier = Modifier.height(16.dp))

            AdminPasswordField(
                label = "Create Password",
                value = password,
                onValueChange = { password = it; passwordError = null },
                placeholder = "Create password",
                visible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible },
                error = passwordError
            )
            Spacer(modifier = Modifier.height(16.dp))

            AdminPasswordField(
                label = "Confirm Password",
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmPasswordError = null },
                placeholder = "Re-enter password",
                visible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible },
                error = confirmPasswordError
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    var isValid = true

                    if (name.isBlank()) {
                        nameError = "Name is required"
                        isValid = false
                    }

                    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

                    if (password.length < 6) {
                        passwordError = "Min 6 characters required"
                        isValid = false
                    }

                    if (password != confirmPassword) {
                        confirmPasswordError = "Passwords do not match"
                        isValid = false
                    }

                    if (isValid && !isLoading) {
                        isLoading = true
                        scope.launch {
                            try {
                                val registerRequest = RegisterRequest(
                                    name = name,
                                    email = email,
                                    password = password,
                                    phone = phone,
                                    companyName = location
                                )

                                val response = RetrofitClient.apiService.dealerRegister(registerRequest)

                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Dealer added successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onAddDealerSuccess()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to add dealer: ${response.message()}",
                                        Toast.LENGTH_SHORT
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
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        "Add Dealer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AdminInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextBlack,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = error != null,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            supportingText = {
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
fun AdminPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextBlack,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = error != null,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            singleLine = true,
            supportingText = {
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
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