package com.SIMATS.digitalpds

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.SIMATS.digitalpds.ui.theme.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.network.AdminBeneficiaryDetailResponse
import com.SIMATS.digitalpds.network.AdminUpdateBeneficiaryRequest
import com.SIMATS.digitalpds.network.KitHistoryItemResponse
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class AdminBeneficiaryDetailsViewModel : ViewModel() {
    var beneficiary by mutableStateOf<AdminBeneficiaryDetailResponse?>(null)
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchDetails(token: String, userId: Int, userRole: String = "admin") {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = if (userRole == "admin") {
                    RetrofitClient.apiService.getAdminBeneficiaryDetails("Bearer $token", userId)
                } else {
                    RetrofitClient.apiService.getDealerBeneficiaryDetails("Bearer $token", userId)
                }
                
                if (response.isSuccessful && response.body() != null) {
                    beneficiary = response.body()
                } else {
                    errorMessage = "Failed to load details: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    fun updateBeneficiary(
        token: String,
        userId: Int,
        name: String,
        phone: String,
        pdsCardNo: String,
        email: String? = null,
        age: Int? = null,
        gender: String? = null,
        education: String? = null,
        employment: String? = null,
        address: String? = null,
        status: String? = null,
        category: String? = null,
        adminNote: String? = null
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = AdminUpdateBeneficiaryRequest(
                    name = name, 
                    phone = phone, 
                    email = email,
                    age = age,
                    gender = gender,
                    education = education,
                    employment = employment,
                    address = address,
                    pds_card_no = pdsCardNo, 
                    status = status, 
                    category = category,
                    adminNote = adminNote
                )
                val response = RetrofitClient.apiService.adminUpdateBeneficiary("Bearer $token", userId, request)
                if (response.isSuccessful) {
                    fetchDetails(token, userId)
                } else {
                    errorMessage = "Failed to update beneficiary: ${response.message()}"
                    isLoading = false
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
                isLoading = false
            }
        }
    }

    fun deleteBeneficiary(token: String, userId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.adminDeleteBeneficiary("Bearer $token", userId)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage = "Failed to delete beneficiary: ${response.message()}"
                    isLoading = false
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
                isLoading = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBeneficiaryDetailsScreen(
    beneficiaryId: Int,
    userRole: String = "admin",
    onBackClick: () -> Unit = {},
    onAddFamilyMemberClick: ((Int) -> Unit)? = null,
    viewModel: AdminBeneficiaryDetailsViewModel = viewModel()
) {
    val context = LocalContext.current
    val token = remember { SessionManager(context).getAccessToken() ?: "" }

    LaunchedEffect(beneficiaryId) {
        viewModel.fetchDetails(token, beneficiaryId, userRole)
    }

    val beneficiary = viewModel.beneficiary
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    var selectedStatus by remember { mutableStateOf("GIVEN") }
    var adminNote by remember { mutableStateOf("") }
    var showStatusDropdown by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var isUploadingPhoto by remember { mutableStateOf(false) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editPdsCard by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editAge by remember { mutableStateOf("") }
    var editGender by remember { mutableStateOf("") }
    var editEducation by remember { mutableStateOf("") }
    var editEmployment by remember { mutableStateOf("") }
    var editAddress by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("") }

    LaunchedEffect(beneficiary) {
        beneficiary?.let { 
            selectedStatus = it.status 
            profileImageUrl = it.profileImage
        }
    }

    // Image picker launcher for beneficiary profile picture
    val scope = rememberCoroutineScope()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isUploadingPhoto = true
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val tempFile = File(context.cacheDir, "bene_profile_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(tempFile).use { out -> inputStream?.copyTo(out) }
                    val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("profile_image", tempFile.name, requestBody)
                    val response = RetrofitClient.apiService.uploadUserProfilePicture("Bearer $token", beneficiaryId, part)
                    if (response.isSuccessful) {
                        // Re-fetch profile to get new image URL
                        val profileResp = RetrofitClient.apiService.getUserProfile("Bearer $token", beneficiaryId)
                        if (profileResp.isSuccessful) {
                            profileImageUrl = profileResp.body()?.profile_image
                        }
                        Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Upload failed. Try again.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    isUploadingPhoto = false
                }
            }
        }
    }

    val PrimaryBlue = if (userRole == "dealer") DealerGreen else com.SIMATS.digitalpds.ui.theme.PrimaryBlue
    val themeGradientEnd = if (userRole == "dealer") Color(0xFF1B5E20) else Color(0xFF1E3A8A)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FBFF))) {
        // Top Background Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryBlue, themeGradientEnd)
                    )
                )
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Beneficiary Profile", fontWeight = FontWeight.SemiBold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        if (beneficiary != null) {
                            IconButton(onClick = {
                                editName = beneficiary.name
                                editPhone = beneficiary.phone.takeIf { it != "N/A" } ?: ""
                                editPdsCard = beneficiary.pdsCardNo.takeIf { it != "Not Linked" } ?: ""
                                editEmail = beneficiary.email ?: ""
                                editAge = beneficiary.age?.toString() ?: ""
                                editGender = beneficiary.gender ?: "Male"
                                editEducation = beneficiary.education ?: ""
                                editEmployment = beneficiary.employment ?: ""
                                editAddress = beneficiary.address.takeIf { it != "N/A" } ?: ""
                                editCategory = beneficiary.category ?: ""
                                showEditDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                            }
                            if (userRole == "admin") {
                                IconButton(onClick = { showDeleteConfirm = true }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (!errorMessage.isNullOrEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Text(errorMessage ?: "Error", color = Color.Red, modifier = Modifier.padding(16.dp))
                    }
                }
            } else if (beneficiary != null) {
                val b = beneficiary

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500), initialOffsetY = { 100 })
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))

                        // Profile Card Overlapping
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0x33000000)),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                // Avatar with photo upload
                                Box(
                                    modifier = Modifier.size(80.dp),
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
                                                .size(78.dp)
                                                .clip(CircleShape)
                                                .border(2.dp, PrimaryBlue, CircleShape)
                                        )
                                    } else {
                                        Surface(
                                            modifier = Modifier.size(78.dp),
                                            shape = CircleShape,
                                            color = Color(0xFFF3F4F6),
                                            border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryBlue)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = b.name.take(1).uppercase(),
                                                    color = PrimaryBlue,
                                                    fontSize = 28.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                        }
                                    }
                                    // Camera button overlay
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(if (isUploadingPhoto) Color.Gray else Color.White)
                                            .border(1.5.dp, PrimaryBlue, CircleShape)
                                            .clickable(enabled = !isUploadingPhoto) {
                                                imagePickerLauncher.launch("image/*")
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isUploadingPhoto) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                color = PrimaryBlue,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Icon(
                                                Icons.Default.CameraAlt,
                                                null,
                                                tint = PrimaryBlue,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                    BeneficiaryStatusBadge(status = selectedStatus)
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(b.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                                Text("Card No: ${b.pdsCardNo}", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)

                                Spacer(modifier = Modifier.height(20.dp))
                                HorizontalDivider(color = Color(0xFFF0F0F0))
                                Spacer(modifier = Modifier.height(20.dp))

                                val details = listOf(
                                    Icons.Default.Phone to ("Phone" to b.phone),
                                    Icons.Default.Email to ("Email" to (b.email ?: "N/A")),
                                    Icons.Default.Home to ("Household ID" to (b.householdId ?: "N/A")),
                                    Icons.Default.Category to ("Category" to (b.category ?: "N/A")),
                                    Icons.Default.Person to ("Age" to (b.age?.toString() ?: "N/A")),
                                    Icons.Default.Person to ("Gender" to (b.gender ?: "N/A")),
                                    Icons.Default.Info to ("Education" to (b.education ?: "N/A")),
                                    Icons.Default.Info to ("Employment" to (b.employment ?: "N/A")),
                                    Icons.Default.LocationOn to ("Location" to b.location),
                                    Icons.Default.Home to ("Address" to b.address),
                                    Icons.Default.CalendarToday to ("Joined" to b.createdAt)
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    details.chunked(2).forEach { rowItems ->
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            rowItems.forEach { (icon, texts) ->
                                                val (label, value) = texts
                                                Row(
                                                    modifier = Modifier.weight(1f),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = Color(0xFFF0F4FF),
                                                        modifier = Modifier.size(36.dp)
                                                    ) {
                                                        Icon(icon, contentDescription = null, modifier = Modifier.padding(8.dp), tint = PrimaryBlue)
                                                    }
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column {
                                                        Text(label, fontSize = 11.sp, color = Color.Gray)
                                                        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextBlack, maxLines = 1)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Surface(
                                    color = Color(0xFFFAFAFA),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = Color(0xFFE3F2FD),
                                            shape = CircleShape,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                if (b.createdByRole == "Dealer") Icons.Default.Storefront else Icons.Default.Person,
                                                contentDescription = null,
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("Registered By", fontSize = 12.sp, color = Color.Gray)
                                            Text(
                                                text = "${b.createdByName} (${b.createdByRole})",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextBlack
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // PDS Card Images Section
                        if (b.pdsCardFront != null || b.pdsCardBack != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x22000000)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "PDS Card Images",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextBlack
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        b.pdsCardFront?.let { url ->
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Front Side", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                                Spacer(modifier = Modifier.height(6.dp))
                                                AsyncImage(
                                                    model = if (url.startsWith("http")) url else "${RetrofitClient.BASE_URL}/$url",
                                                    contentDescription = "PDS Front",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(120.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(Color(0xFFF3F4F6))
                                                )
                                            }
                                        }
                                        b.pdsCardBack?.let { url ->
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Back Side", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                                Spacer(modifier = Modifier.height(6.dp))
                                                AsyncImage(
                                                    model = if (url.startsWith("http")) url else "${RetrofitClient.BASE_URL}/$url",
                                                    contentDescription = "PDS Back",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(120.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(Color(0xFFF3F4F6))
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Status Management Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Update Status", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                                Spacer(modifier = Modifier.height(16.dp))

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedButton(
                                        onClick = { showStatusDropdown = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(selectedStatus, fontWeight = FontWeight.Bold, color = TextBlack)
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextBlack)
                                        }
                                    }
                                    DropdownMenu(
                                        expanded = showStatusDropdown,
                                        onDismissRequest = { showStatusDropdown = false },
                                        modifier = Modifier.fillMaxWidth(0.85f).background(Color.White)
                                    ) {
                                        listOf("GIVEN", "PENDING", "RETURNED").forEach { status ->
                                            DropdownMenuItem(
                                                text = { Text(status, fontWeight = FontWeight.Medium) },
                                                onClick = {
                                                    selectedStatus = status
                                                    showStatusDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = adminNote,
                                    onValueChange = { adminNote = it },
                                    label = { Text("Admin Note") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    minLines = 2,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryBlue,
                                        unfocusedBorderColor = Color.LightGray
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { 
                                        beneficiary?.let { b ->
                                            viewModel.updateBeneficiary(
                                                token = token,
                                                userId = beneficiaryId,
                                                name = b.name,
                                                phone = b.phone,
                                                pdsCardNo = b.pdsCardNo,
                                                status = selectedStatus,
                                                category = b.category,
                                                adminNote = adminNote
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Publish Changes", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Family Members
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Family Members",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                            if (onAddFamilyMemberClick != null) {
                                IconButton(onClick = { onAddFamilyMemberClick.invoke(beneficiaryId) }) {
                                    Icon(Icons.Default.AddCircle, contentDescription = "Add Member", tint = PrimaryBlue)
                                }
                            }
                        }

                        if (b.familyMembers.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No family members found.", color = Color.Gray, fontSize = 14.sp)
                            }
                        } else {
                            b.familyMembers.forEach { member ->
                                FamilyMemberCard(member)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        // Kit History
                        Text(
                            "Kit Distribution History",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack,
                            modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                        )

                        if (b.history.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No kits distributed yet.", color = Color.Gray, fontSize = 14.sp)
                            }
                        } else {
                            b.history.forEach { item ->
                                HistoryCard(item, PrimaryBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }

            // Edit Dialog
            if (showEditDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Edit Beneficiary Profile", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = { Text("Name") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = editPhone,
                                onValueChange = { editPhone = it },
                                label = { Text("Phone") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
                            )
                            OutlinedTextField(
                                value = editEmail,
                                onValueChange = { editEmail = it },
                                label = { Text("Email (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email)
                            )
                            OutlinedTextField(
                                value = editAge,
                                onValueChange = { editAge = it },
                                label = { Text("Age") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = editGender,
                                onValueChange = { editGender = it },
                                label = { Text("Gender (e.g. Male/Female)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = editEducation,
                                onValueChange = { editEducation = it },
                                label = { Text("Education") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = editEmployment,
                                onValueChange = { editEmployment = it },
                                label = { Text("Employment Status") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = editAddress,
                                onValueChange = { editAddress = it },
                                label = { Text("Street Address") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = editPdsCard,
                                onValueChange = { editPdsCard = it },
                                label = { Text("PDS Card Number") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = editCategory,
                                onValueChange = { editCategory = it },
                                label = { Text("Ration Category (e.g. AAY/PHH)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.updateBeneficiary(
                                    token = token,
                                    userId = beneficiaryId,
                                    name = editName,
                                    phone = editPhone,
                                    pdsCardNo = editPdsCard,
                                    email = editEmail.trim().ifBlank { null },
                                    age = editAge.trim().toIntOrNull(),
                                    gender = editGender.trim().ifBlank { null },
                                    education = editEducation.trim().ifBlank { null },
                                    employment = editEmployment.trim().ifBlank { null },
                                    address = editAddress.trim().ifBlank { null },
                                    category = editCategory.trim().ifBlank { null }
                                )
                                showEditDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    },
                    containerColor = Color.White
                )
            }

            // Delete Confirm Dialog
            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = { Text("Delete Beneficiary?", fontWeight = FontWeight.Bold, color = Color.Red) },
                    text = { Text("Are you sure you want to permanently delete this beneficiary and all associated records? This action cannot be undone.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteBeneficiary(token, beneficiaryId, onSuccess = onBackClick)
                                showDeleteConfirm = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Delete Permanently", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    },
                    containerColor = Color.White
                )
            }
        }
    }
}

@Composable
fun HistoryCard(item: KitHistoryItemResponse, primaryBlue: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(item.kitName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Text(item.kitType, fontSize = 12.sp, color = Color.Gray)
                }
                BeneficiaryStatusBadge(status = item.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color(0xFFF9FAFB),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Items", fontSize = 11.sp, color = Color.Gray)
                        Text(item.quantity, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Date", fontSize = 11.sp, color = Color.Gray)
                        Text(item.date, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(14.dp), tint = primaryBlue)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Distributed by: ", fontSize = 12.sp, color = Color.Gray)
                Text(item.givenBy ?: "Unknown", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
            }

            if (item.status.uppercase() == "RETURNED") {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Red)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Returned on ${item.returnDate ?: "Unknown"}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        }
                        if (!item.returnReason.isNullOrEmpty()) {
                            Text("Reason: ${item.returnReason}", fontSize = 12.sp, color = Color(0xFF991B1B), modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }

            if (!item.notes.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Note: ${item.notes}", fontSize = 12.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        }
    }
}

@Composable
fun BeneficiaryStatusBadge(status: String) {
    val (color, bgColor) = when (status.uppercase()) {
        "GIVEN", "CONFIRMED" -> Color(0xFF166534) to Color(0xFFDCFCE7)
        "PENDING" -> Color(0xFF9A3412) to Color(0xFFFEF08A)
        "RETURNED" -> Color(0xFF991B1B) to Color(0xFFFEE2E2)
        else -> Color.Gray to Color(0xFFF3F4F6)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status.uppercase(),
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun FamilyMemberCard(member: com.SIMATS.digitalpds.network.FamilyMemberResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFE8F5E9),
                shape = CircleShape,
                modifier = Modifier.size(46.dp)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(member.memberName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Text("Age: ${member.age} • ${member.relation}", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminBeneficiaryDetailsScreenPreview() {
    AdminBeneficiaryDetailsScreen(beneficiaryId = 1)
}
