package com.SIMATS.digitalpds

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import com.SIMATS.digitalpds.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddBeneficiaryScreen(
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    initialDealerId: Int = -1
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingDealers by remember { mutableStateOf(true) }
    var dealersList by remember { mutableStateOf<List<DealerInfo>>(emptyList()) }
    var dealersError by remember { mutableStateOf<String?>(null) }
    var selectedDealer by remember { mutableStateOf<DealerInfo?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val token = SessionManager(context).getAccessToken() ?: ""
    val addedMembers = remember { androidx.compose.runtime.mutableStateListOf<com.SIMATS.digitalpds.network.FamilyMemberInput>() }
    
    var pdsFrontUri by remember { mutableStateOf<Uri?>(null) }
    var pdsBackUri by remember { mutableStateOf<Uri?>(null) }

    val frontLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pdsFrontUri = uri
    }
    val backLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pdsBackUri = uri
    }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.apiService.getDealers("Bearer $token")
            if (response.isSuccessful) {
                val body = response.body() ?: emptyList()
                dealersList = body.map { networkDealer ->
                    DealerInfo(
                        id = networkDealer.id,
                        name = networkDealer.name,
                        email = networkDealer.email,
                        phone = networkDealer.phone,
                        companyName = networkDealer.companyName,
                        address = networkDealer.address,
                        city = networkDealer.city,
                        state = networkDealer.state,
                        handle = "@${networkDealer.name.lowercase().replace(" ", "")}",
                        location = networkDealer.location,
                        activeStatus = "Active"
                    )
                }
                if (initialDealerId != -1) {
                    selectedDealer = dealersList.find { it.id == initialDealerId }
                }
            } else {
                dealersError = "HTTP ${response.code()}"
            }
        } catch (e: Exception) {
            dealersError = e.message
        } finally {
            isLoadingDealers = false
        }
    }

    val PrimaryRed = Color(0xFFD32F2F)
    val gradientBrush = Brush.verticalGradient(colors = listOf(PrimaryRed, Color(0xFFE53935)))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Beneficiary", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                modifier = Modifier.background(gradientBrush),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).padding(paddingValues)) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(text = "Select Dealer", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryRed)

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedDealer?.let { "${it.name} - ${it.location ?: ""}" } ?: "Select a dealer",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(focusedBorderColor = PrimaryRed),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        if (isLoadingDealers) {
                            DropdownMenuItem(text = { Text("Loading...") }, onClick = {})
                        } else {
                            dealersList.forEach { dealer ->
                                DropdownMenuItem(
                                    text = { Text("${dealer.name} - ${dealer.location ?: ""}") },
                                    onClick = { selectedDealer = dealer; expanded = false }
                                )
                            }
                        }
                    }
                }

                BeneficiaryForm(
                    isLoading = isLoading,
                    submitButtonText = "Create Beneficiary",
                    buttonColor = PrimaryRed,
                    contentBeforeSubmit = {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "PDS Card Images", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryRed)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                ImagePickerSection("Front Side", pdsFrontUri) { frontLauncher.launch("image/*") }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                ImagePickerSection("Back Side", pdsBackUri) { backLauncher.launch("image/*") }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        HouseholdCompositionForm(addedMembers = addedMembers)
                    },
                    onSubmit = { bName, bEmail, bPhone, age, gender, edu, emp, addr, bCard ->
                        if (selectedDealer == null) {
                            Toast.makeText(context, "Please select a dealer", Toast.LENGTH_SHORT).show()
                        } else {
                            isLoading = true
                            scope.launch {
                                try {
                                    val namePart = bName.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val headPart = bName.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val emailPart = bEmail?.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val cardPart = bCard.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val addressPart = addr.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val phonePart = bPhone.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val agePart = age.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val eduPart = edu.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val empPart = emp.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val dIdPart = selectedDealer!!.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                                    
                                    val membersPart = Gson().toJson(addedMembers.toList()).toRequestBody("text/plain".toMediaTypeOrNull())

                                    val fImg = pdsFrontUri?.let { uriToFile(context, it, "front.jpg")?.let { f ->
                                        MultipartBody.Part.createFormData("pds_front", f.name, f.asRequestBody("image/*".toMediaTypeOrNull()))
                                    }}
                                    val bImg = pdsBackUri?.let { uriToFile(context, it, "back.jpg")?.let { f ->
                                        MultipartBody.Part.createFormData("pds_back", f.name, f.asRequestBody("image/*".toMediaTypeOrNull()))
                                    }}

                                    val response = RetrofitClient.apiService.adminCreateBeneficiary(
                                        "Bearer $token", namePart, headPart, emailPart, cardPart, addressPart, phonePart, agePart, genderPart, eduPart, empPart, dIdPart,
                                        membersPart, fImg, bImg
                                    )
                                    
                                    if (response.isSuccessful && response.body()?.error == null) {
                                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                                        onSuccess()
                                    } else {
                                        Toast.makeText(context, response.body()?.error ?: "Error", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

private fun uriToFile(context: android.content.Context, uri: Uri, fileName: String): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(tempFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        tempFile
    } catch (e: Exception) {
        null
    }
}
