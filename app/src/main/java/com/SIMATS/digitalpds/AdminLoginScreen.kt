package com.SIMATS.digitalpds

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.LoginRequest
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.TextGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    onLoginClick: (Int, String, String, String, String, String?) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val primaryRed = Color(0xFFD32F2F)
    val accentRed = Color(0xFFC62828)
    val lightRedBg = Color(0xFFFFEBEE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, lightRedBg)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(20.dp),
                color = primaryRed.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = primaryRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Admin Login",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = primaryRed
            )
            Text(
                text = "Oversee system operations",
                fontSize = 15.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = { Text("Admin Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = emailError != null,
                        supportingText = {
                            if (emailError != null) {
                                Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryRed,
                            unfocusedBorderColor = accentRed.copy(alpha = 0.3f),
                            focusedLabelColor = primaryRed
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        isError = passwordError != null,
                        supportingText = {
                            if (passwordError != null) {
                                Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        trailingIcon = {
                            val image = if (passwordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            }
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = null,
                                    tint = accentRed
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryRed,
                            unfocusedBorderColor = accentRed.copy(alpha = 0.3f),
                            focusedLabelColor = primaryRed
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            var hasError = false

                            if (email.isBlank()) {
                                emailError = "Email is required"
                                hasError = true
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                                emailError = "Enter a valid email address"
                                hasError = true
                            }

                            if (password.isBlank()) {
                                passwordError = "Password is required"
                                hasError = true
                            } else if (password.length < 8) {
                                passwordError = "Password must be at least 8 characters"
                                hasError = true
                            }

                            if (!hasError) {

                                isLoading = true
                                scope.launch {
                                    try {
                                        val response = RetrofitClient.apiService.adminLogin(
                                            LoginRequest(email, password)
                                        )

                                        val body = response.body()

                                        if (response.isSuccessful && body != null) {
                                            val adminId = body.adminId
                                            val name = body.name ?: "Admin"
                                            val respEmail = body.email ?: email
                                            val phone = body.phone ?: ""

                                            if (adminId != null && adminId > 0) {
                                                Toast.makeText(
                                                    context,
                                                    body.message ?: "Admin Login Successful",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                onLoginClick(adminId, name, respEmail, phone, body.token ?: "", body.profileImage)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Admin ID not received from server",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            val errorText = body?.error ?: response.message()
                                            Toast.makeText(
                                                context,
                                                "Login Failed: $errorText",
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
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryRed)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                "Login",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = onBackClick) {
                Text(
                    "Back to Role Selection",
                    color = primaryRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}