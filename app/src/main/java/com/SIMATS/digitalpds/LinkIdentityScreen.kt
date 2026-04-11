package com.SIMATS.digitalpds

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.SIMATS.digitalpds.network.LinkPdsRequest
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.*
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkIdentityScreen(
    userId: Int,
    isVerified: Boolean = false,
    onBackClick: () -> Unit,
    onScanClick: () -> Unit = {},
    onLinkContinueClick: (String?) -> Unit = {},
    scannedCardNo: String? = null
) {
    var pdsCardNo by remember { mutableStateOf(scannedCardNo ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(scannedCardNo) {
        scannedCardNo?.let {
            val extracted = extractPdsId(it)
            if (extracted.isNotEmpty()) {
                pdsCardNo = extracted
                Toast.makeText(context, "Card scanned successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Invalid QR code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) onScanClick() else Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    )

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Back Button
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
                        if (isVerified) "Identity Verified" else "Link PDS Card",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        if (isVerified) "Your account is permanently locked to your PDS card" else "Scan your card QR to link your identity",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = (-30).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Realistic PDS Card Mockup
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFE3F2FD), Color.White),
                                        start = Offset(0f, 0f),
                                        end = Offset(1000f, 1000f)
                                    )
                                )
                        )
                        
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val path = Path().apply {
                                moveTo(0f, size.height * 0.7f)
                                quadraticBezierTo(size.width * 0.4f, size.height * 0.5f, size.width, size.height * 0.8f)
                                lineTo(size.width, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(path, Brush.verticalGradient(listOf(PrimaryBlue.copy(0.05f), Color.Transparent)))
                        }

                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("PDS IDENTITY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                    Text("Smart Card System", fontSize = 10.sp, color = TextGray)
                                }
                                Icon(Icons.Default.CreditCard, null, tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            if (pdsCardNo.isNotEmpty()) {
                                Text(
                                    text = pdsCardNo,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextBlack,
                                    letterSpacing = 2.sp
                                )
                                Text("Linked ID", fontSize = 10.sp, color = TextGray)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                        .background(Color.White, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Card ID will appear here", color = Color.LightGray, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Scanning Interaction Section
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(2.dp, PrimaryBlue.copy(0.2f), RoundedCornerShape(32.dp))
                        .clickable(enabled = !isVerified) {
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> onScanClick()
                                else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isVerified) {
                         Box(
                            modifier = Modifier
                                .size(100.dp)
                                .shadow(8.dp, CircleShape, spotColor = Color(0xFF2E7D32))
                                .background(Color(0xFF2E7D32), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Verified, null, tint = Color.White, modifier = Modifier.size(44.dp))
                        }
                    } else {
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 0.8f,
                            targetValue = 1.1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ), label = "pulse"
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .shadow(12.dp * scale, CircleShape, spotColor = PrimaryBlue)
                                .background(PrimaryBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QrCodeScanner, null, tint = Color.White, modifier = Modifier.size(40.dp))
                        }
                    }
                    
                    Text(
                        if (isVerified) "Verified & Locked" else "Tap to Scan",
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isVerified) Color(0xFF2E7D32) else PrimaryBlue
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Or enter manually", color = TextGray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = pdsCardNo,
                    onValueChange = { pdsCardNo = it },
                    placeholder = { Text("PDS-123456", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isVerified,
                    leadingIcon = { Icon(Icons.Default.Tag, null, tint = if (isVerified) Color.Gray else PrimaryBlue) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color(0xFFF1F5F9),
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.LightGray.copy(0.5f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (pdsCardNo.isBlank()) {
                            Toast.makeText(context, "Please enter or scan card number", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        isLoading = true
                        scope.launch {
                            try {
                                val request = LinkPdsRequest(
                                    userId = userId,
                                    identityCardNo = pdsCardNo
                                )
                                val response = RetrofitClient.apiService.linkPdsCard("Bearer ${SessionManager(context).getAccessToken() ?: ""}", request)
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "PDS linked successfully", Toast.LENGTH_SHORT).show()
                                    onLinkContinueClick(response.body()?.nextStep)
                                } else {
                                    val errorMsg = response.errorBody()?.string() ?: response.message()
                                    val message = try { JSONObject(errorMsg).getString("error") } catch (e: Exception) { errorMsg }
                                    Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Network Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(if (isVerified) 0.dp else 4.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVerified) Color(0xFFE2E8F0) else PrimaryBlue,
                        disabledContainerColor = if (isVerified) Color(0xFFE2E8F0) else PrimaryBlue.copy(alpha = 0.6f)
                    ),
                    enabled = !isLoading && !isVerified
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            if (isVerified) "Identity Connection Locked" else "Connect Identity",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isVerified) Color(0xFF94A3B8) else Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Extracts and validates PDS Card Number from scanned QR code.
 * Expected format: "PDS-XXXXXX" (e.g., PDS-123456) or a 10-12 digit numeric string.
 */
private fun extractPdsId(rawResult: String): String {
    val trimmedResult = rawResult.trim()
    
    // Pattern 1: Official PDS JSON format
    try {
        val json = JSONObject(trimmedResult)
        val extracted = when {
            json.has("pds_card_no") -> json.getString("pds_card_no")
            json.has("card_no") -> json.getString("card_no")
            json.has("pds_id") -> json.getString("pds_id")
            else -> ""
        }
        if (isValidPdsFormat(extracted)) return extracted
    } catch (e: Exception) {
        // Not JSON, continue to regex check
    }

    // Pattern 2: Plain text with "PDS-" prefix or pure digits
    if (isValidPdsFormat(trimmedResult)) {
        return trimmedResult
    }

    return "" // Invalid format
}

/**
 * Validates if a string matches the PDS Card Number format.
 * Matches: "PDS-123456" (case-insensitive prefix).
 */
private fun isValidPdsFormat(input: String): Boolean {
    val pdsPattern = Regex("^PDS-\\d+$", RegexOption.IGNORE_CASE)
    return input.matches(pdsPattern)
}

@Preview(showBackground = true)
@Composable
fun LinkIdentityScreenPreview() {
    DigitalpdsTheme {
        LinkIdentityScreen(userId = 1, onBackClick = {})
    }
}
