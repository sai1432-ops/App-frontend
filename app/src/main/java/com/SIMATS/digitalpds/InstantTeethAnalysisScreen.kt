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
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    val softBlue = Color(0xFF2E7DFF)
    val lightGrey = Color(0xFFF8F9FA)
    val textGraySub = Color(0xFF757575)

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
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 120.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Instant Teeth Analysis",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = softBlue
                )
                Text(
                    text = "Powered by Advanced AI",
                    fontSize = 16.sp,
                    color = textGraySub,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Select Family Member",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Text(
                    text = "Choose who this analysis is for",
                    fontSize = 14.sp,
                    color = textGraySub
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(allMembers) { member ->
                        FamilyMemberChip(
                            member = member,
                            isSelected = selectedMember?.id == member.id,
                            onSelect = { selectedMember = member }
                        )
                    }
                }

                if (selectedMember == null) {
                    Text(
                        text = "Please select a family member to continue.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnalysisActionCard(
                    title = "Take Photo",
                    subtitle = "Capture a live image of your teeth using your camera.",
                    icon = Icons.Default.CameraAlt,
                    iconTint = softBlue,
                    iconBg = softBlue.copy(alpha = 0.1f),
                    enabled = selectedMember != null,
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                AnalysisActionCard(
                    title = "Upload from Gallery",
                    subtitle = "Choose an existing high-resolution photo from your device.",
                    icon = Icons.Default.PhotoLibrary,
                    iconTint = Color(0xFF009688),
                    iconBg = Color(0xFF009688).copy(alpha = 0.1f),
                    enabled = selectedMember != null,
                    onClick = {
                        galleryLauncher.launch("image/*")
                    }
                )

                AnimatedVisibility(
                    visible = selectedBitmap != null,
                    enter = fadeIn(animationSpec = tween(500)) + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 40.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Image Preview",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                            IconButton(onClick = {
                                selectedBitmap = null
                                selectedImageUri = null
                            }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(lightGrey)
                                .shadow(1.dp, RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            selectedBitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Selected Teeth Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                tonalElevation = 8.dp,
                color = Color.White
            ) {
                Button(
                    onClick = {
                        if (selectedImageUri != null && selectedMember != null) {
                            onProceedClick(selectedImageUri, selectedMember!!.id)
                        } else {
                            Toast.makeText(context, "Please select member and image", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                        .height(60.dp),
                    enabled = selectedBitmap != null && selectedMember != null && !isLoading,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = softBlue,
                        disabledContainerColor = softBlue.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = if (selectedMember != null) {
                            "Analyze for ${selectedMember?.name}"
                        } else {
                            "Analyze Teeth"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(enabled = false) { },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = softBlue)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Analyzing for ${selectedMember?.name}...",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = TextBlack
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FamilyMemberChip(
    member: FamilyMember,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onSelect)
            .width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF2E7DFF).copy(alpha = 0.1f) else Color(0xFFF8F9FA))
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) Color(0xFF2E7DFF) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = member.imageResId),
                contentDescription = member.name,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = member.name,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF2E7DFF) else Color.Black,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AnalysisActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .shadow(elevation = if (enabled) 2.dp else 0.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color.White else Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (enabled) iconBg else Color.LightGray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) iconTint else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) TextBlack else Color.Gray
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = if (enabled) Color(0xFF757575) else Color.Gray.copy(alpha = 0.7f),
                    lineHeight = 20.sp
                )
            }
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
