package com.SIMATS.digitalpds

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    userId: Int = 1,
    onBackClick: () -> Unit,
    onScanClick: () -> Unit = {},
    onLinkContinueClick: () -> Unit = {},
    scannedCardNo: String? = null
) {
    var cardNo by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Update cardNo if a result comes back from the scanner
    LaunchedEffect(scannedCardNo) {
        scannedCardNo?.let {
            cardNo = extractPdsId(it)
            if (cardNo.isNotEmpty()) {
                Toast.makeText(context, "Card scanned successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Invalid QR code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Launcher for camera permission request
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                onScanClick()
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Link Identity",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundWhite
                )
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Card Preview Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.gov),
                        contentDescription = "PDS Card Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.1f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Scan your PDS Family Card QR or enter your Card Number.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card Number Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "PDS Family Card Number",
                    color = Color(0xFF4A6572),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )
                TextField(
                    value = cardNo,
                    onValueChange = { cardNo = it },
                    placeholder = {
                        Text(
                            "Enter PDS Card ID",
                            color = Color(0xFF4A6572).copy(alpha = 0.6f),
                            fontSize = 15.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE9EEF3),
                        unfocusedContainerColor = Color(0xFFE9EEF3),
                        disabledContainerColor = Color(0xFFE9EEF3),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PrimaryBlue
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scan Card Button
            OutlinedButton(
                onClick = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                            onScanClick()
                        }
                        else -> {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan Card (QR)", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (cardNo.isBlank()) {
                        Toast.makeText(context, "Please enter or scan card number", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    isLoading = true
                    scope.launch {
                        try {
                            val request = LinkPdsRequest(userId = userId, pdsCardNo = cardNo)
                            val response = RetrofitClient.apiService.linkPdsCard(request)
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Identity Linked Successfully", Toast.LENGTH_SHORT).show()
                                onLinkContinueClick()
                            } else {
                                val errorMsg = response.errorBody()?.string() ?: response.message()
                                // Parse error JSON if possible
                                val message = try {
                                    JSONObject(errorMsg).getString("error")
                                } catch (e: Exception) {
                                    errorMsg
                                }
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
                    .height(54.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = !isLoading && cardNo.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Verifying PDS Card...", color = Color.White)
                    }
                } else {
                    Text(
                        "Link Identity and Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Extracts PDS Card Number from various QR formats.
 */
private fun extractPdsId(rawResult: String): String {
    return try {
        // Try parsing as JSON first
        val json = JSONObject(rawResult)
        when {
            json.has("card_no") -> json.getString("card_no")
            json.has("family_card_no") -> json.getString("family_card_no")
            json.has("pds_id") -> json.getString("pds_id")
            else -> rawResult.trim()
        }
    } catch (e: Exception) {
        // Not a JSON, treat as plain text and clean up
        rawResult.trim().filter { it.isLetterOrDigit() || it == '-' }
    }
}

@Preview(showBackground = true)
@Composable
fun LinkIdentityScreenPreview() {
    DigitalpdsTheme {
        LinkIdentityScreen(onBackClick = {})
    }
}
