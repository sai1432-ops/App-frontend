package com.SIMATS.digitalpds

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.AiPredictionResponse
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.BackgroundWhite
import com.SIMATS.digitalpds.ui.theme.DigitalpdsTheme
import com.SIMATS.digitalpds.ui.theme.PrimaryBlue
import com.SIMATS.digitalpds.ui.theme.TextBlack
import com.SIMATS.digitalpds.ui.theme.TextGray
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

import androidx.compose.ui.graphics.Brush
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.VerifiedUser
import com.SIMATS.digitalpds.ui.theme.textGraySub

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAnalysisScreen(
    userId: Int = 1,
    memberId: Int? = null,
    imageUri: Uri? = null,
    onAnalysisComplete: (Int?, Uri?, AiPredictionResponse) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var progress by remember { mutableFloatStateOf(0.1f) }
    var statusText by remember { mutableStateOf("Uploading Cloud Data...") }
    val softBlue = PrimaryBlue
    val cyanGradient = Color(0xFF00BCD4)

    // Animation for the pulsing icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(imageUri) {
        if (imageUri == null) {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            onBackClick()
            return@LaunchedEffect
        }

        try {
            Log.d("AIAnalysis", "URI = $imageUri")
            progress = 0.2f
            statusText = "Optimizing Image..."
            val file = getFileFromUri(context, imageUri)
            
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val memberIdBody = (memberId ?: 0).toString().toRequestBody("text/plain".toMediaTypeOrNull())

            progress = 0.5f
            statusText = "AI Engine Scanning..."

            val sessionManager = SessionManager(context)
            val token = "Bearer ${sessionManager.getAccessToken() ?: ""}"
            val response = RetrofitClient.apiService.analyzeTeeth(token, body, userIdBody, memberIdBody)

            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                val resultMessage = result.message.lowercase()
                val resultRisk = result.riskLevel?.trim()?.uppercase()
                val hasNoDetections = result.detections.isEmpty()

                val isInvalidMessage = resultMessage.contains("invalid") || 
                                     resultMessage.contains("not teeth") ||
                                     resultMessage.contains("no teeth") ||
                                     resultMessage.contains("not recognized") ||
                                     resultMessage.contains("poor quality") ||
                                     resultMessage.contains("cannot analyze") ||
                                     resultMessage.contains("try again")
                
                // ULTRA-STRICT: If no detections found, and message is generic (no 'teeth' or 'dental'), reject it.
                val isUnverifiedScan = hasNoDetections && 
                                       !resultMessage.contains("teeth") && 
                                       !resultMessage.contains("dental") &&
                                       !resultMessage.contains("oral")

                if (isInvalidMessage || resultRisk == "INVALID" || (resultRisk == null && hasNoDetections) || isUnverifiedScan) {
                    Toast.makeText(context, "Invalid Picture: No teeth detected in the scan.", Toast.LENGTH_LONG).show()
                    onBackClick()
                } else {
                    progress = 1.0f
                    statusText = "Analysis Finalized!"
                    delay(800)
                    onAnalysisComplete(memberId, imageUri, result)
                }
            } else {
                Toast.makeText(context, "Analysis encountered an issue", Toast.LENGTH_LONG).show()
                onBackClick()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            onBackClick()
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated AI Core
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale),
                    color = softBlue.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Brush.linearGradient(listOf(softBlue, cyanGradient)))
                ) {}
                
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.padding(24.dp).size(48.dp),
                        tint = softBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = statusText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = softBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Premium Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(softBlue, cyanGradient)))
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VerifiedUser, null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Clinical AI model detecting cavities, plaque, and gum health in real-time.",
                        fontSize = 13.sp,
                        color = textGraySub,
                        lineHeight = 18.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
            
            TextButton(onClick = onBackClick) {
                Text("Cancel Analysis", color = Color.Red.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun getFileFromUri(context: Context, uri: Uri): File {
    return when (uri.scheme) {

        "file" -> {
            File(uri.path!!)
        }

        "content" -> {
            val file = File(
                context.cacheDir,
                "temp_image_${System.currentTimeMillis()}.jpg"
            )

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Cannot open URI: $uri")

            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            file
        }

        else -> {
            throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AIAnalysisScreenPreview() {
    DigitalpdsTheme {
        AIAnalysisScreen(
            onAnalysisComplete = { _, _, _ -> },
            onBackClick = {}
        )
    }
}
