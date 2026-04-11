package com.SIMATS.digitalpds

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstantTeethAnalysisScreen(
    onCloseClick: () -> Unit,
    onProceedClick: (Uri?, Int) -> Unit,
    members: List<FamilyMember> = emptyList(),
    isLoading: Boolean = false
) {
    val context = LocalContext.current
    val softBlue = PrimaryBlue
    val cyanGradient = Color(0xFF00BCD4)

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val allMembers = remember(members) {
        val list = mutableListOf<FamilyMember>()
        list.add(FamilyMember(0, "Me", 90, "Low", "2024-05-20", R.drawable.user))
        list.addAll(members.filter { it.id != 0 })
        list
    }

    var selectedMember by remember { mutableStateOf<FamilyMember?>(allMembers.firstOrNull()) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            try {
                selectedBitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            selectedBitmap = it
            try {
                val file = File(context.cacheDir, "teeth_scan_${System.currentTimeMillis()}.jpg")
                val out = FileOutputStream(file)
                it.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                selectedImageUri = Uri.fromFile(file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) cameraLauncher.launch()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Gradient Backdrop
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(Brush.linearGradient(listOf(softBlue, cyanGradient)))
            ) {
                Column(modifier = Modifier.padding(24.dp).padding(top = 24.dp)) {
                    IconButton(
                        onClick = onCloseClick,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Instant Scan", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text("Powered by Bio-Cloud AI Engine", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }

            Column(
                modifier = Modifier.padding(24.dp).offset(y = (-40).dp)
            ) {
                // Member Selector Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Select Patient", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(allMembers) { member ->
                                ModernMemberChip(
                                    member = member,
                                    isSelected = selectedMember?.id == member.id,
                                    onSelect = { selectedMember = member },
                                    accent = softBlue
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Data Capture", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ModernActionBox(
                        title = "Camera",
                        icon = Icons.Default.CameraAlt,
                        modifier = Modifier.weight(1f),
                        accent = softBlue,
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                    )
                    ModernActionBox(
                        title = "Gallery",
                        icon = Icons.Default.PhotoLibrary,
                        modifier = Modifier.weight(1f),
                        accent = Color(0xFF10B981),
                        onClick = { galleryLauncher.launch("image/*") }
                    )
                }

                AnimatedVisibility(visible = selectedBitmap != null) {
                    Column(modifier = Modifier.padding(top = 32.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Image Preview", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = { selectedBitmap = null; selectedImageUri = null }) {
                                Text("Clear", color = Color.Red.copy(alpha = 0.6f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .border(2.dp, softBlue.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                                .shadow(8.dp, RoundedCornerShape(24.dp))
                        ) {
                            selectedBitmap?.let {
                                Image(bitmap = it.asImageBitmap(), null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        // Bottom CTAs
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            tonalElevation = 12.dp,
            color = Color.White
        ) {
            Button(
                onClick = {
                    if (selectedImageUri != null && selectedMember != null) {
                        onProceedClick(selectedImageUri, selectedMember!!.id)
                    }
                },
                modifier = Modifier.padding(24.dp).fillMaxWidth().height(60.dp).shadow(12.dp, RoundedCornerShape(16.dp)),
                enabled = selectedBitmap != null && selectedMember != null && !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = softBlue)
            ) {
                Text("INITIATE AI SCAN", fontSize = 14.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
fun ModernMemberChip(member: FamilyMember, isSelected: Boolean, onSelect: () -> Unit, accent: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onSelect() }.width(70.dp)) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = if (isSelected) accent.copy(alpha = 0.1f) else Color(0xFFF1F5F9),
            border = if (isSelected) BorderStroke(2.dp, accent) else null
        ) {
            InitialsAvatar(name = member.name, modifier = Modifier.padding(8.dp), fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(member.name, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) accent else TextBlack, maxLines = 1)
    }
}

@Composable
fun ModernActionBox(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accent: Color, modifier: Modifier, onClick: () -> Unit) {
    ElevatedCard(
        modifier = modifier.height(140.dp).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Surface(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(14.dp), color = accent.copy(alpha = 0.1f)) {
                Icon(icon, null, modifier = Modifier.padding(12.dp), tint = accent)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextBlack)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InstantTeethAnalysisScreenPreview() {
    DigitalpdsTheme {
        InstantTeethAnalysisScreen(
            onCloseClick = {},
            onProceedClick = { _, _ -> }
        )
    }
}
