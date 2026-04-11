package com.SIMATS.digitalpds

import android.net.Uri
import android.widget.Toast
import org.json.JSONObject
import androidx.activity.compose.rememberLauncherForActivityResult

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.SIMATS.digitalpds.ui.theme.textGraySub
import com.SIMATS.digitalpds.UserBottomNavigationBar
import com.SIMATS.digitalpds.network.UserProfileResponse
import com.SIMATS.digitalpds.network.DealerProfileResponse
import com.SIMATS.digitalpds.network.ProfileUpdateRequest
import com.SIMATS.digitalpds.network.RetrofitClient



import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.SIMATS.digitalpds.ui.theme.*
import androidx.compose.ui.graphics.Brush
import java.io.File
import java.io.FileOutputStream
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.core.content.FileProvider
import java.util.*

enum class ProfileState {
    MENU, DISPLAY, EDIT, CHANGE_PASSWORD
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun UserProfileScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAboutProgramClick: () -> Unit = {},
    onHelpSupportClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onLinkIdentityClick: () -> Unit = {},
    onProfilePictureUpdated: (String) -> Unit = {},
    onProfileUpdated: (String, String, String) -> Unit = { _, _, _ -> },
    isVerified: Boolean = false,
    userName: String = "User",
    userEmail: String = "",
    userPhone: String = "",
    profileImage: String? = null,
    onSelectDealerClick: () -> Unit = {}
) {

    var profileState by remember { mutableStateOf(ProfileState.MENU) }
    var name by remember { mutableStateOf(userName) }
    var role by remember { mutableStateOf(if (isVerified) "Verified Household User" else "Primary Household User") }
    var phone by remember { mutableStateOf(userPhone) }
    var email by remember { mutableStateOf(userEmail) }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var employment by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var isUploadingPhoto by remember { mutableStateOf(false) }
    var profileImageUrl by remember { mutableStateOf<String?>(profileImage) }
    var canEditProfilePicture by remember { mutableStateOf(true) } // Updated from backend profile data

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isChangingPassword by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")
    val educationOptions = listOf("No Formal Education", "Primary School", "High School", "Diploma", "Bachelor's Degree", "Master's Degree", "Doctorate")
    val employmentOptions = listOf("Employed", "Self-Employed", "Unemployed", "Student", "Retired", "Homemaker")

    var profileData by remember { mutableStateOf<UserProfileResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val sessionManager = remember { SessionManager(context) }
    var dealerName by remember { mutableStateOf(sessionManager.getAssignedDealerName()) }
    var dealerLocation by remember { mutableStateOf(sessionManager.getAssignedDealerLocation()) }
    var dealerId by remember { mutableIntStateOf(sessionManager.getAssignedDealerId()) }

    val userId = sessionManager.getUserId() ?: 0

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isUploadingPhoto = true
                try {
                    val token = SessionManager(context).getAccessToken() ?: ""
                    val inputStream = context.contentResolver.openInputStream(it)
                    val tempFile = File(context.cacheDir, "profile_upload_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(tempFile).use { out -> inputStream?.copyTo(out) }
                    val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("profile_image", tempFile.name, requestBody)
                    val response = RetrofitClient.apiService.uploadUserProfilePicture("Bearer $token", userId, part)
                    if (response.isSuccessful) {
                        val profileResp = RetrofitClient.apiService.getUserProfile("Bearer $token", userId)
                        if (profileResp.isSuccessful) {
                            profileData = profileResp.body()
                            val newUrl = profileResp.body()?.profile_image
                            profileImageUrl = newUrl
                            if (newUrl != null) onProfilePictureUpdated(newUrl)
                        }
                        Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Upload failed"
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    isUploadingPhoto = false
                }
            }
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let { uri ->
                scope.launch {
                    isUploadingPhoto = true
                    try {
                        val token = SessionManager(context).getAccessToken() ?: ""
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val tempFile = File(context.cacheDir, "profile_camera_${System.currentTimeMillis()}.jpg")
                        FileOutputStream(tempFile).use { out -> inputStream?.copyTo(out) }
                        
                        val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData("profile_image", tempFile.name, requestBody)
                        val response = RetrofitClient.apiService.uploadUserProfilePicture("Bearer $token", userId, part)
                        
                        if (response.isSuccessful) {
                            val profileResp = RetrofitClient.apiService.getUserProfile("Bearer $token", userId)
                            if (profileResp.isSuccessful) {
                                profileData = profileResp.body()
                                val newUrl = profileResp.body()?.profile_image
                                profileImageUrl = newUrl
                                if (newUrl != null) onProfilePictureUpdated(newUrl)
                            }
                            Toast.makeText(context, "Profile picture updated from camera!", Toast.LENGTH_SHORT).show()
                        } else {
                            val errorMsg = response.errorBody()?.string() ?: "Upload failed"
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Camera Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isUploadingPhoto = false
                    }
                }
            }
        }
    }

    val softBlue = PrimaryBlue
    val cyanGradient = Color(0xFF00BCD4)
    val premiumGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF3F51B5), // Deep dark indigo at top
            Color(0xFF1E88E5)  // Vibrant blue at bottom
        )
    )

    LaunchedEffect(userName, userEmail, userPhone, isVerified) {
        if (profileData == null) {
            name = userName
            email = userEmail
            phone = userPhone
            role = if (isVerified) "Verified Household User" else "Primary Household User"
        }
    }

    LaunchedEffect(profileState) {
        if (profileState == ProfileState.DISPLAY || profileState == ProfileState.MENU) {
            isLoading = true
            try {
                val sessionManager = SessionManager(context)
                val token = sessionManager.getAccessToken() ?: ""
                val currentRole = sessionManager.getUserRole() ?: "user"
                
                val response = if (currentRole == "dealer") {
                    RetrofitClient.apiService.getDealerProfile("Bearer $token", userId)
                } else {
                    RetrofitClient.apiService.getUserProfile("Bearer $token", userId)
                }

                if (response.isSuccessful) {
                    val body = response.body()
                    if (currentRole == "dealer" && body is DealerProfileResponse) {
                        profileData = UserProfileResponse(
                            id = body.id,
                            name = body.name,
                            email = body.email,
                            phone = body.phone,
                            age = null,
                            gender = null,
                            education = null,
                            employment = null,
                            address = body.address,
                            pds_card_no = null,
                            pds_verified = true,
                            pds_linked_at = null,
                            profile_image = body.profileImage
                        )
                        name = body.name
                        email = body.email
                        phone = body.phone
                        address = body.address ?: ""
                        role = "Authorized Dealer"
                    } else if (body is UserProfileResponse) {
                        profileData = body
                        canEditProfilePicture = body.createdByType == "SELF"
                        name = body.name
                        email = body.email
                        phone = body.phone
                        age = body.age?.toString() ?: ""
                        gender = body.gender ?: ""
                        education = body.education ?: ""
                        employment = body.employment ?: ""
                        address = body.address ?: ""
                        role = if (body.pds_verified) "Verified Household User" else "Primary Household User"
                        
                        // Sync dealer details to session and local state
                        if (body.dealer_id != null && body.dealer_id != 0) {
                            dealerId = body.dealer_id
                            dealerName = body.dealer_name
                            dealerLocation = body.dealer_location
                            sessionManager.setAssignedDealerId(body.dealer_id)
                            body.dealer_name?.let { sessionManager.setAssignedDealerName(it) }
                            body.dealer_location?.let { sessionManager.setAssignedDealerLocation(it) }
                        }
                    }

                    val newUrl = if (currentRole == "dealer") (body as? DealerProfileResponse)?.profileImage else (body as? UserProfileResponse)?.profile_image
                    if (newUrl != null && profileImageUrl != newUrl) {
                        profileImageUrl = newUrl
                        onProfilePictureUpdated(newUrl)
                    }
                }
            } catch (e: Exception) { 
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }


    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = profileState == ProfileState.MENU,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                UserBottomNavigationBar(
                    currentScreen = "Profile",
                    onHomeClick = onHomeClick,
                    onKitsClick = onKitsClick,
                    onLearnClick = onLearnClick,
                    onConsultClick = onConsultClick,
                    onProfileClick = { }
                )
            }
        },
        containerColor = Color(0xFFF4F6F9) // Slightly cooler premium light grey
    ) { paddingValues ->
        if (showPhotoOptions) {
            ModalBottomSheet(
                onDismissRequest = { showPhotoOptions = false },
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp, top = 16.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Change Profile Picture",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PhotoSourceItem(
                            icon = Icons.Default.CameraAlt,
                            label = "Camera",
                            color = PrimaryBlue
                        ) {
                            showPhotoOptions = false
                            val tempFile = File(context.cacheDir, "camera_capture_${System.currentTimeMillis()}.jpg")
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                tempFile
                            )
                            cameraImageUri = uri
                            cameraLauncher.launch(uri)
                        }
                        
                        PhotoSourceItem(
                            icon = Icons.Default.PhotoLibrary,
                            label = "Gallery",
                            color = Color(0xFFE91E63)
                        ) {
                            showPhotoOptions = false
                            imagePickerLauncher.launch("image/*")
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Refined Header Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // Adjusted height
                    .background(premiumGradient)
            ) {
                // Top control bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (profileState != ProfileState.MENU) {
                        IconButton(
                            onClick = {
                                profileState = when (profileState) {
                                    ProfileState.MENU -> { onBackClick(); ProfileState.MENU }
                                    ProfileState.DISPLAY -> ProfileState.MENU
                                    ProfileState.EDIT -> ProfileState.DISPLAY
                                    ProfileState.CHANGE_PASSWORD -> ProfileState.MENU
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = if (profileState == ProfileState.EDIT || profileState == ProfileState.CHANGE_PASSWORD) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }

                    if (profileState == ProfileState.MENU) {
                        Spacer(modifier = Modifier.width(40.dp))
                    } else if (profileState == ProfileState.DISPLAY) {
                        Text("Personal Info", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        if (canEditProfilePicture) {
                            IconButton(
                                onClick = { profileState = ProfileState.EDIT },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(40.dp))
                        }
                    } else if (profileState == ProfileState.EDIT) {
                        Text("Edit Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(40.dp))
                    } else if (profileState == ProfileState.CHANGE_PASSWORD) {
                        Text("Change Password", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(40.dp))
                    }
                }

                // Centered Profile Avatar Block
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 104.dp), // Adjust relative position
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedContent(
                        targetState = profileState,
                        label = "Avatar Transition"
                    ) { state ->
                        val isMenu = state == ProfileState.MENU
                        val isEdit = state == ProfileState.EDIT
                        val avatarSize = if (isMenu) 100.dp else 80.dp // Scale down Avatar smoothly if not in menu
                        Box(
                            modifier = Modifier.size(avatarSize + 4.dp), // Extra padding for the badge
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            if (!profileImageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(if (profileImageUrl?.startsWith("http") == true) profileImageUrl else "${RetrofitClient.BASE_URL}/$profileImageUrl")
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(avatarSize)
                                        .clip(CircleShape)
                                        .border(4.dp, Color.White, CircleShape)
                                        .shadow(16.dp, CircleShape)
                                        .clickable(enabled = (profileState == ProfileState.EDIT) && canEditProfilePicture) {
                                            showPhotoOptions = true
                                        }
                                )
                            } else {
                                InitialsAvatar(
                                    name = name,
                                    modifier = Modifier
                                        .size(avatarSize)
                                        .clip(CircleShape)
                                        .border(4.dp, Color.White, CircleShape)
                                        .shadow(16.dp, CircleShape)
                                        .clickable(enabled = (profileState == ProfileState.EDIT) && canEditProfilePicture) {
                                            showPhotoOptions = true
                                        },
                                    fontSize = if (isMenu) 36.sp else 28.sp
                                )
                            }

                            if (isEdit && canEditProfilePicture) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .offset(x = 2.dp, y = 2.dp) // Slight offset
                                        .clip(CircleShape)
                                        .background(if (isUploadingPhoto) Color.Gray else Color.White)
                                        .border(2.dp, Color(0xFF1E88E5), CircleShape)
                                        .clickable(enabled = !isUploadingPhoto) {
                                            showPhotoOptions = true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isUploadingPhoto) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color(0xFF1E88E5),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.CameraAlt,
                                            null,
                                            tint = Color(0xFF1E88E5),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    AnimatedVisibility(visible = profileState == ProfileState.MENU) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(name, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(role, fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            AnimatedContent(
                targetState = profileState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { 50 }) with
                    fadeOut(animationSpec = tween(300)) + slideOutVertically(targetOffsetY = { -50 })
                },
                modifier = Modifier.offset(y = (-20).dp), // Connect overlapping with Header
                label = "Profile Content Transition"
            ) { targetState ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    when (targetState) {
                        ProfileState.MENU -> {
                            if (isLoading) {
                                Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = softBlue)
                                }
                            } else {
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color.LightGray, spotColor = Color.LightGray),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = if (profileData?.pds_verified == true || isVerified) Color(0xFFF4FDF8) else Color(0xFFFFF9F5)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(20.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier.size(48.dp).clip(CircleShape).background(
                                                if (profileData?.pds_verified == true || isVerified) Color(0xFF22C55E).copy(alpha=0.15f) else Color(0xFFF97316).copy(alpha=0.15f)
                                            ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (profileData?.pds_verified == true || isVerified) Icons.Default.VerifiedUser else Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = if (profileData?.pds_verified == true || isVerified) Color(0xFF16A34A) else Color(0xFFEA580C),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(20.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                if (profileData?.pds_verified == true || isVerified) "Identity Verified" else "Identity Not Linked",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = TextBlack
                                            )
                                            if (profileData?.pds_verified == true && !profileData?.pds_card_no.isNullOrBlank()) {
                                                Text(
                                                    "Card: ${profileData?.pds_card_no}",
                                                    fontSize = 13.sp,
                                                    color = textGraySub,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            } else {
                                                Text(
                                                    "Enhance your PDS tracking",
                                                    fontSize = 12.sp,
                                                    color = textGraySub,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                        }
                                        if (profileData?.pds_verified != true && !isVerified) {
                                            Button(
                                                onClick = { onLinkIdentityClick() },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                                modifier = Modifier.height(36.dp)
                                            ) {
                                                Text("Link Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Dealer Information Card
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color.LightGray, spotColor = Color.LightGray),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = if (profileData?.dealer_id != null && profileData?.dealer_id != 0) Color(0xFFF0F7FF) else Color(0xFFFFF9F5)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(20.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val hasDealer = (profileData?.dealer_id != null && profileData?.dealer_id != 0) || (dealerId != -1)
                                        Box(
                                            modifier = Modifier.size(48.dp).clip(CircleShape).background(
                                                if (profileData?.dealer_id != null && profileData?.dealer_id != 0) Color(0xFF1E88E5).copy(alpha=0.1f) else Color(0xFFF97316).copy(alpha=0.1f)
                                            ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (profileData?.dealer_id != null && profileData?.dealer_id != 0) Icons.Default.Storefront else Icons.Default.HelpOutline,
                                                contentDescription = null,
                                                tint = if (profileData?.dealer_id != null && profileData?.dealer_id != 0) Color(0xFF1E88E5) else Color(0xFFEA580C),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(20.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                if (hasDealer) "Assigned Dealer" else "No Dealer Selected",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = TextBlack
                                            )
                                            if (hasDealer) {
                                                Text(
                                                    profileData?.dealer_name ?: dealerName ?: "Dealer Name N/A",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = TextBlack,
                                                    modifier = Modifier.padding(top = 2.dp)
                                                )
                                                Text(
                                                    profileData?.dealer_location ?: dealerLocation ?: "Location N/A",
                                                    fontSize = 12.sp,
                                                    color = textGraySub,
                                                    modifier = Modifier.padding(top = 1.dp)
                                                )
                                            } else {
                                                Text(
                                                    "Select a dealer to receive your PDS kits",
                                                    fontSize = 12.sp,
                                                    color = textGraySub,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                        }
                                        if (!hasDealer) {
                                            Button(
                                                onClick = { onSelectDealerClick() },
                                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                                modifier = Modifier.height(36.dp)
                                            ) {
                                                Text("Select Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            Text("Account Settings", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextBlack, modifier = Modifier.padding(start = 4.dp))
                            Spacer(modifier = Modifier.height(16.dp))

                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(28.dp), ambientColor = Color.LightGray, spotColor = Color.LightGray),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)) {
                                    ModernProfileOption(Icons.Default.Person, "Personal Information") { profileState = ProfileState.DISPLAY }
                                    Divider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFF1F5F9))
                                    ModernProfileOption(Icons.Default.Lock, "Change Password") { profileState = ProfileState.CHANGE_PASSWORD }
                                    Divider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFF1F5F9))
                                    ModernProfileOption(Icons.Default.Description, "About Program") { onAboutProgramClick() }
                                    Divider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFF1F5F9))
                                    ModernProfileOption(
                                        Icons.Default.DeleteForever,
                                        "Delete Account",
                                        color = Color(0xFFDC2626)
                                    ) { showDeleteConfirmation = true }
                                    Divider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFF1F5F9))
                                    ModernProfileOption(
                                        Icons.AutoMirrored.Outlined.Logout,
                                        "Logout",
                                        color = Color(0xFFDC2626)
                                    ) { onLogoutClick() }
                                }
                            }

                            Spacer(modifier = Modifier.height(48.dp))
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(
                                    "Digital PDS v1.0.4",
                                    fontSize = 13.sp,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        ProfileState.DISPLAY -> {
                            if (isLoading) {
                                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = softBlue)
                                }
                            } else {
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(28.dp), ambientColor = Color.LightGray, spotColor = Color.LightGray),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(vertical = 24.dp, horizontal = 8.dp)) {
                                        DisplayInfoRow("Full Name", profileData?.name ?: name, Icons.Default.Person)
                                        Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp), color = Color(0xFFF1F5F9))
                                        DisplayInfoRow("Phone Number", profileData?.phone ?: phone, Icons.Default.Phone)
                                        Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp), color = Color(0xFFF1F5F9))
                                        DisplayInfoRow("Email Address", profileData?.email ?: email, Icons.Default.Email)
                                        Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp), color = Color(0xFFF1F5F9))

                                        DisplayInfoRow("Age", profileData?.age?.toString() ?: "Not provided", Icons.Default.Cake)
                                        Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp), color = Color(0xFFF1F5F9))
                                        DisplayInfoRow("Gender", profileData?.gender ?: "Not provided", Icons.Default.Wc)
                                        Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp), color = Color(0xFFF1F5F9))
                                        DisplayInfoRow("Education", profileData?.education ?: "Not provided", Icons.Default.School)
                                        Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp), color = Color(0xFFF1F5F9))
                                        DisplayInfoRow("Employment", profileData?.employment ?: "Not provided", Icons.Default.Work)
                                        Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp), color = Color(0xFFF1F5F9))
                                        DisplayInfoRow("Address", profileData?.address ?: "Not provided", Icons.Default.Home)
                                    }
                                }
                                
                                if (canEditProfilePicture) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(
                                        onClick = { profileState = ProfileState.EDIT },
                                        modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(16.dp), spotColor = softBlue),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = softBlue)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Edit Information", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(48.dp))
                            }
                        }
                        ProfileState.EDIT -> {
                            // Account Section
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(28.dp), ambientColor = Color.LightGray, spotColor = Color.LightGray),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Lock, null, tint = softBlue, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("Account Security", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    ModernEditField("Full Name", name, Icons.Default.Person) { name = it }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    ModernEditField("Email Address", email, Icons.Default.Email) { email = it }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    ModernEditField("Phone Number", phone, Icons.Default.Phone) { phone = it }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Personal Profile Section
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(28.dp), ambientColor = Color.LightGray, spotColor = Color.LightGray),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Person, null, tint = softBlue, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("Personal Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Age
                                    Column {
                                        Text("Age", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textGraySub, modifier = Modifier.padding(start = 6.dp, bottom = 8.dp))
                                        OutlinedTextField(
                                            value = age,
                                            onValueChange = { if (it.length <= 3) age = it.filter { c -> c.isDigit() } },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(16.dp),
                                            leadingIcon = { Icon(Icons.Default.Cake, null, tint = softBlue, modifier = Modifier.size(20.dp)) },
                                            placeholder = { Text("Your age", color = Color(0xFFCBD5E1)) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = softBlue,
                                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                                focusedContainerColor = Color(0xFFF8FAFC),
                                                unfocusedContainerColor = Color(0xFFF8FAFC)
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    ProfileEditDropdown(
                                        label = "Gender",
                                        selectedOption = gender,
                                        options = genderOptions,
                                        icon = Icons.Default.Wc,
                                        onOptionSelected = { gender = it }
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    ProfileEditDropdown(
                                        label = "Education",
                                        selectedOption = education,
                                        options = educationOptions,
                                        icon = Icons.Default.School,
                                        onOptionSelected = { education = it }
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    ProfileEditDropdown(
                                        label = "Employment",
                                        selectedOption = employment,
                                        options = employmentOptions,
                                        icon = Icons.Default.Work,
                                        onOptionSelected = { employment = it }
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    ModernEditField("Address", address, Icons.Default.Home) { address = it }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = {
                                    scope.launch {
                                        isSaving = true
                                        try {
                                            val sessionManager = SessionManager(context)
                                            val token = sessionManager.getAccessToken() ?: ""
                                            val role = sessionManager.getUserRole() ?: "user"
                                            
                                            val request = ProfileUpdateRequest(
                                                name = name,
                                                email = email,
                                                phone = phone,
                                                age = age.toIntOrNull(),
                                                gender = gender.ifBlank { null },
                                                education = education.ifBlank { null },
                                                employment = employment.ifBlank { null },
                                                address = address.ifBlank { null }
                                            )
                                            
                                            val response = if (role == "dealer") {
                                                RetrofitClient.apiService.updateDealerProfile("Bearer $token", userId, request)
                                            } else {
                                                RetrofitClient.apiService.updateUserProfile("Bearer $token", userId, request)
                                            }

                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                                onProfileUpdated(name, email, phone)
                                                profileState = ProfileState.DISPLAY
                                            } else {

                                                val errorBody = response.errorBody()?.string()
                                                val errorMessage = try {
                                                    JSONObject(errorBody ?: "{}").optString("message", "Update failed. Please try again.")
                                                } catch (e: Exception) {
                                                    "Update failed. Please try again."
                                                }
                                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isSaving = false
                                        }
                                    }
                                },

                                modifier = Modifier.fillMaxWidth().height(60.dp).shadow(12.dp, RoundedCornerShape(16.dp), spotColor = softBlue),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = softBlue),
                                enabled = !isSaving
                            ) {
                                if (isSaving) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                                } else {
                                    Text("Save Changes", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(48.dp))
                        }
                        ProfileState.CHANGE_PASSWORD -> {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(12.dp, RoundedCornerShape(28.dp), ambientColor = Color.LightGray, spotColor = Color.LightGray),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Lock, null, tint = softBlue, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("Security Update", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))

                                    ModernEditField(
                                        label = "Current Password",
                                        value = currentPassword,
                                        icon = Icons.Default.LockOpen,
                                        onValueChange = { currentPassword = it },
                                        isPassword = true
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    ModernEditField(
                                        label = "New Password",
                                        value = newPassword,
                                        icon = Icons.Default.Lock,
                                        onValueChange = { newPassword = it },
                                        isPassword = true
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    ModernEditField(
                                        label = "Confirm New Password",
                                        value = confirmPassword,
                                        icon = Icons.Default.CheckCircle,
                                        onValueChange = { confirmPassword = it },
                                        isPassword = true
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = {
                                    if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (newPassword != confirmPassword) {
                                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    scope.launch {
                                        isChangingPassword = true
                                        try {
                                            val token = SessionManager(context).getAccessToken() ?: ""
                                            val request = com.SIMATS.digitalpds.network.ChangePasswordRequest(
                                                currentPassword = currentPassword,
                                                newPassword = newPassword
                                            )
                                            val response = RetrofitClient.apiService.changeUserPassword("Bearer $token", request)
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                                                currentPassword = ""
                                                newPassword = ""
                                                confirmPassword = ""
                                                profileState = ProfileState.MENU
                                            } else {
                                                val errorBody = response.errorBody()?.string()
                                                val errorMessage = if (errorBody?.contains("Incorrect current password") == true) {
                                                    "Incorrect current password"
                                                } else {
                                                    "Failed to change password"
                                                }
                                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isChangingPassword = false
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = softBlue),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = softBlue),
                                enabled = !isChangingPassword
                            ) {
                                if (isChangingPassword) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                                } else {
                                    Text("Update Password", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(48.dp))
                        }
                    }
                }
            }
        }

        DeleteConfirmationDialog(
            show = showDeleteConfirmation,
            isDeleting = isDeleting,
            onDismissRequest = { showDeleteConfirmation = false },
            onConfirm = {
                scope.launch {
                    isDeleting = true
                    try {
                        val token = SessionManager(context).getAccessToken() ?: ""
                        val response = RetrofitClient.apiService.deleteUserAccount("Bearer $token", userId)
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_LONG).show()
                            SessionManager(context).clearSession()
                            onLogoutClick()
                        } else {
                            val errorMsg = response.errorBody()?.string() ?: "Failed to delete account"
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        isDeleting = false
                        showDeleteConfirmation = false
                    }
                }
            }
        )
    }
}

@Composable
fun PhotoSourceItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack)
    }
}

@Composable
fun DisplayInfoRow(label: String, value: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(PrimaryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(22.dp), tint = PrimaryBlue)
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(label, fontSize = 13.sp, color = textGraySub, fontWeight = FontWeight.Medium)
            Text(if (value.isEmpty()) "Not provided" else value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack, modifier = Modifier.padding(top=2.dp))
        }
    }
}

@Composable
fun ModernEditField(
    label: String,
    value: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textGraySub, modifier = Modifier.padding(start = 6.dp, bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp)) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedContainerColor = Color(0xFFF8FAFC)
            )
        )
    }
}

@Composable
fun ModernProfileOption(icon: ImageVector, title: String, color: Color = TextBlack, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (color == TextBlack) PrimaryBlue.copy(alpha = 0.1f) else color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = if (color == TextBlack) PrimaryBlue else color)
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = null,
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditDropdown(
    label: String,
    selectedOption: String,
    options: List<String>,
    icon: ImageVector,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = if (selectedOption.isBlank()) "Select $label" else selectedOption

    Column {
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textGraySub,
            modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = displayValue,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 14.sp) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                        leadingIcon = if (option == selectedOption) {
                            { Icon(Icons.Default.Check, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmationDialog(
    show: Boolean,
    isDeleting: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) onDismissRequest() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Delete Account?", fontWeight = FontWeight.Bold, color = TextBlack)
                }
            },
            text = {
                Text(
                    "This will permanently delete your account and all associated data including family members, brushing history, kit distributions, and reports. This action cannot be undone.",
                    fontSize = 14.sp,
                    color = textGraySub
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    enabled = !isDeleting,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Delete Permanently", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest,
                    enabled = !isDeleting
                ) {
                    Text("Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
