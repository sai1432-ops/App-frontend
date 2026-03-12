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
    var statusText by remember { mutableStateOf("Uploading Image...") }

    LaunchedEffect(imageUri) {
        if (imageUri == null) {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            onBackClick()
            return@LaunchedEffect
        }

        try {
            Log.d("AIAnalysis", "URI = $imageUri")

            progress = 0.2f
            statusText = "Processing Image..."

            val file = getFileFromUri(context, imageUri)
            Log.d("AIAnalysis", "File path = ${file.absolutePath}, exists=${file.exists()}")

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val memberIdBody = memberId
                ?.takeIf { it > 0 }
                ?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

            progress = 0.5f
            statusText = "AI is analyzing your teeth..."

            val response = RetrofitClient.apiService.analyzeTeeth(
                body,
                userIdBody,
                memberIdBody
            )

            Log.d("AIAnalysis", "Response code = ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                progress = 1.0f
                statusText = "Analysis Complete!"
                delay(500)
                onAnalysisComplete(memberId, imageUri, response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string()
                Log.e("AIAnalysis", "Error body = $errorMsg")
                Toast.makeText(context, "Analysis failed: ${response.code()}", Toast.LENGTH_LONG).show()
                onBackClick()
            }
        } catch (e: Exception) {
            Log.e("AIAnalysis", "Crash in AIAnalysisScreen", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AI Analysis",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
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
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = statusText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PrimaryBlue,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Our AI is detecting dental issues like cavities, plaque, and gum disease to provide you with a detailed health report.",
                fontSize = 14.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getFileFromUri(context: Context, uri: Uri): File {
    return when (uri.scheme) {
        "file" -> File(requireNotNull(uri.path))
        "content" -> {
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            } ?: throw IllegalArgumentException("Cannot open content URI: $uri")
            file
        }
        else -> throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
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