package com.SIMATS.digitalpds

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.SIMATS.digitalpds.ui.theme.*
import com.SIMATS.digitalpds.network.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealerLoginScreen(
    onLoginClick: (Int, String, String, String, String?, String?, String?) -> Unit,
    onBackClick: () -> Unit,
    onForgotPasswordClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val primaryGreen = Color(0xFF1B5E20)
    val accentGreen = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF1F8E9)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DealerGreen, Color.White)
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
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Dealer Login",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Access your dealer dashboard",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(28.dp), spotColor = Color(0x20000000)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
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
                        label = { Text("Dealer Email") },
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
                            focusedBorderColor = DealerGreen,
                            unfocusedBorderColor = DealerGreen.copy(alpha = 0.3f),
                            focusedLabelColor = DealerGreen
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
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError != null,
                        supportingText = {
                            if (passwordError != null) {
                                Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = DealerGreen)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DealerGreen,
                            unfocusedBorderColor = DealerGreen.copy(alpha = 0.3f),
                            focusedLabelColor = DealerGreen
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(onClick = onForgotPasswordClick) {
                            Text("Forgot Password?", color = DealerGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }

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
                                        val response = RetrofitClient.apiService.dealerLogin(
                                            LoginRequest(email, password)
                                        )

                                        val body = response.body()

                                        if (response.isSuccessful && body != null) {
                                            val dealerId = body.dealerId
                                            val name = body.name ?: "Dealer"
                                            val respEmail = body.email ?: email
                                            val phone = body.phone ?: ""
                                            val token = body.token
                                            val profileImage = body.profileImage
                                            val qrValue = body.dealerQrValue

                                            if (dealerId != null && dealerId > 0) {
                                                Toast.makeText(
                                                    context,
                                                    body.message ?: "Dealer Login Successful",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                onLoginClick(dealerId, name, respEmail, phone, token, profileImage, qrValue)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Dealer ID not received from server",
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
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DealerGreen)
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
                    color = DealerGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}