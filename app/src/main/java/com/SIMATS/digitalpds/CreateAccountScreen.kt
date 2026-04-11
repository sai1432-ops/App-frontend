package com.SIMATS.digitalpds

import android.net.Uri
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.SIMATS.digitalpds.network.RegistrationOtpRequest
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.network.VerifyRegistrationOtpRequest
import com.SIMATS.digitalpds.ui.theme.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onBackClick: () -> Unit,
    onRegistrationSuccess: (userId: Int, name: String, email: String, phone: String, token: String?, profileImage: String?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var education by remember { mutableStateOf("Undergraduate") }
    var employment by remember { mutableStateOf("Self-Employed") }
    var address by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val genderOptions = listOf("Male", "Female", "Other")
    val educationOptions = listOf("Undergraduate", "Postgraduate", "Secondary School", "Primary School", "No Formal Education")
    val employmentOptions = listOf("Self-Employed", "Private Sector", "Government Sector", "Unemployed", "Student", "Retired")

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Email Verification State
    var isOtpSent by remember { mutableStateOf(false) }
    var isEmailVerified by remember { mutableStateOf(false) }
    var registrationOtp by remember { mutableStateOf("") }
    var isVerifyingOtp by remember { mutableStateOf(false) }

    fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    fun isValidPhone(phone: String): Boolean = phone.trim().matches(Regex("^\\d{10}$"))

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = tempPhotoUri
        }
    }

    fun createImageFile(): File {
        val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        return File.createTempFile("profile_${System.currentTimeMillis()}", ".jpg", storageDir)
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Select Profile Picture") },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("Take Photo") },
                        leadingContent = { Icon(Icons.Default.PhotoCamera, null) },
                        modifier = Modifier.clickable {
                            val photoFile = createImageFile()
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                photoFile
                            )
                            tempPhotoUri = uri
                            cameraLauncher.launch(uri)
                            showImageSourceDialog = false
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Choose from Gallery") },
                        leadingContent = { Icon(Icons.Default.PhotoLibrary, null) },
                        modifier = Modifier.clickable {
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            showImageSourceDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showImageSourceDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.linearGradient(colors = listOf(PrimaryBlue, Color(0xFF00BCD4))))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = "Create Account",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Join the Digital PDS community today",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-30).dp)
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        // Profile Image Picker
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F3F5))
                                .clickable { showImageSourceDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AddAPhoto, null, tint = PrimaryBlue)
                                    Text("Photo", fontSize = 12.sp, color = PrimaryBlue)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Lock, null, tint = PrimaryBlue, modifier = Modifier.size(20.0.dp))
                            Spacer(modifier = Modifier.width(12.0.dp))
                            Text("Account Security", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        }
                        
                        Spacer(modifier = Modifier.height(24.0.dp))

                        // Full Name (Now first)
                        AccountTextField(
                            label = "Full Name",
                            value = name,
                            onValueChange = { name = it; nameError = null },
                            placeholder = "Enter your full name",
                            leadingIcon = Icons.Default.Person,
                            error = nameError
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Field (Always visible, but locked after verification)
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                            Box(modifier = Modifier.weight(1f)) {
                                AccountTextField(
                                    label = "Email Address",
                                    value = email,
                                    onValueChange = { if (!isOtpSent) { email = it; emailError = null } },
                                    placeholder = "name@example.com",
                                    error = emailError,
                                    leadingIcon = Icons.Default.Email
                                )
                            }
                            if (!isEmailVerified) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (!isValidEmail(email)) {
                                            emailError = "Invalid email"
                                            return@Button
                                        }
                                        isLoading = true
                                        scope.launch {
                                            try {
                                                val response = RetrofitClient.apiService.sendRegistrationOtp(RegistrationOtpRequest(email.trim()))
                                                if (response.isSuccessful) {
                                                    isOtpSent = true
                                                    Toast.makeText(context, response.body()?.message ?: "Code sent", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    val errorBody = response.errorBody()?.string() ?: ""
                                                    val errorMsg = if (errorBody.trim().startsWith("<!doctype", ignoreCase = true)) {
                                                        "Server Error: API not found (404) or Internal Error"
                                                    } else {
                                                        errorBody
                                                    }
                                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isLoading = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.height(56.dp).padding(bottom = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isLoading && email.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    if (isLoading && !isOtpSent) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text(if (isOtpSent) "Resend" else "Get Code", fontSize = 12.sp)
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.CheckCircle, "Verified", tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp).padding(bottom = 12.dp))
                            }
                        }

                        if (isOtpSent && !isEmailVerified) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                                Box(modifier = Modifier.weight(1f)) {
                                    AccountTextField(
                                        label = "Verification Code",
                                        value = registrationOtp,
                                        onValueChange = { registrationOtp = it },
                                        placeholder = "6-digit OTP",
                                        leadingIcon = Icons.Default.VpnKey,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (registrationOtp.length != 6) {
                                            Toast.makeText(context, "Enter 6-digit code", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        isVerifyingOtp = true
                                        scope.launch {
                                            try {
                                                val response = RetrofitClient.apiService.verifyRegistrationOtp(
                                                    VerifyRegistrationOtpRequest(email.trim(), registrationOtp)
                                                )
                                                if (response.isSuccessful) {
                                                    isEmailVerified = true
                                                    Toast.makeText(context, "Email verified successfully!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    val errorBody = response.errorBody()?.string() ?: ""
                                                    val errorMsg = if (errorBody.trim().startsWith("<!doctype", ignoreCase = true)) {
                                                        "Server Error: Verification failed (HTML response)"
                                                    } else {
                                                        errorBody
                                                    }
                                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isVerifyingOtp = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.height(56.dp).padding(bottom = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isVerifyingOtp && registrationOtp.length == 6,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) {
                                    if (isVerifyingOtp) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Verify", fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        AccountTextField(
                            label = "Phone Number",
                            value = phoneNumber,
                            onValueChange = { if (it.length <= 10) { phoneNumber = it; phoneError = null } },
                            placeholder = "10-digit mobile number",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = Icons.Default.Phone,
                            error = phoneError
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AccountPasswordField(
                            label = "Password",
                            value = password,
                            onValueChange = { password = it; passwordError = null },
                            placeholder = "Create a strong password",
                            visible = passwordVisible,
                            onToggleVisibility = { passwordVisible = !passwordVisible },
                            error = passwordError
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AccountPasswordField(
                            label = "Confirm Password",
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; confirmPasswordError = null },
                            placeholder = "Repeat your password",
                            visible = confirmPasswordVisible,
                            onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                            error = confirmPasswordError
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Personal Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        AccountTextField(
                            label = "Age",
                            value = age,
                            onValueChange = { if (it.length <= 3) age = it.filter { char -> char.isDigit() } },
                            placeholder = "Your age",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = Icons.Default.Cake
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        RegistrationDropdown(
                            label = "Gender",
                            selectedOption = gender,
                            options = genderOptions,
                            onOptionSelected = { gender = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        RegistrationDropdown(
                            label = "Education",
                            selectedOption = education,
                            options = educationOptions,
                            onOptionSelected = { education = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        RegistrationDropdown(
                            label = "Employment",
                            selectedOption = employment,
                            options = employmentOptions,
                            onOptionSelected = { employment = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AccountTextField(
                            label = "Address",
                            value = address,
                            onValueChange = { address = it },
                            placeholder = "Plot No, Street, City",
                            leadingIcon = Icons.Default.Home
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (!isEmailVerified) {
                            Toast.makeText(context, "Please verify your email first", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        var isValid = true
                            if (name.isBlank()) { nameError = "Required"; isValid = false }
                            if (!isValidPhone(phoneNumber)) { phoneError = "Invalid phone"; isValid = false }
                            if (!isValidEmail(email)) { emailError = "Invalid email"; isValid = false }
                            if (!isValidPassword(password)) { passwordError = "Weak password"; isValid = false }
                            if (password != confirmPassword) { confirmPasswordError = "Mismatch"; isValid = false }

                            if (!isValid) return@Button

                            isLoading = true
                            scope.launch {
                                try {
                                    val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val emailPart = email.trim().toRequestBody("text/plain".toMediaTypeOrNull())
                                    val passwordPart = password.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val phonePart = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val agePart = age.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val educationPart = education.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val employmentPart = employment.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val addressPart = address.toRequestBody("text/plain".toMediaTypeOrNull())

                                    val imagePart = selectedImageUri?.let { uri ->
                                        uriToFile(context, uri, "profile.jpg")?.let { file ->
                                            MultipartBody.Part.createFormData(
                                                "profile_image",
                                                file.name,
                                                file.asRequestBody("image/*".toMediaTypeOrNull())
                                            )
                                        }
                                    }

                                    val response = RetrofitClient.apiService.userRegister(
                                        namePart, emailPart, passwordPart, phonePart,
                                        agePart, genderPart, educationPart, employmentPart, addressPart,
                                        imagePart
                                    )

                                    val body = response.body()

                                    if (response.isSuccessful && body != null && body.userId != null) {
                                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                                        onRegistrationSuccess(
                                            body.userId,
                                            name,
                                            email,
                                            phoneNumber,
                                            body.token,
                                            body.profileImage
                                        )
                                    } else {
                                        val errorMsg = response.errorBody()?.string() ?: "Registration failed: missing userId"
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(8.dp, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Complete Registration", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                Spacer(modifier = Modifier.height(24.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already part of Digital PDS?", color = TextGray, fontSize = 14.sp)
                    TextButton(onClick = onBackClick) {
                        Text("Sign In", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun AccountTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    error: String? = null,
    leadingIcon: ImageVector? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color(0xFF4A6572),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.6f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = if (leadingIcon != null) {
                { Icon(imageVector = leadingIcon, contentDescription = null, tint = PrimaryBlue) }
            } else null,
            isError = error != null,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F5F9),
                unfocusedContainerColor = Color(0xFFF1F5F9),
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = Color.Red
            ),
            singleLine = true
        )
        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun AccountPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = if (error != null) Color(0xFFD32F2F) else Color(0xFF4A6572),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = Color(0xFF4A6572).copy(alpha = 0.6f),
                    fontSize = 15.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = error != null,
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (visible) "Hide password" else "Show password",
                        tint = Color(0xFF455A64)
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE9EEF3),
                unfocusedContainerColor = Color(0xFFE9EEF3),
                disabledContainerColor = Color(0xFFE9EEF3),
                errorContainerColor = Color(0xFFFFEBEE),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                cursorColor = PrimaryBlue
            ),
            singleLine = true
        )
        if (error != null) {
            Text(
                text = error,
                color = Color(0xFFD32F2F),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationDropdown(
    label: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color(0xFF4A6572),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .menuAnchor(),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE9EEF3),
                    unfocusedContainerColor = Color(0xFFE9EEF3),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            onOptionSelected(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CreateAccountScreenPreview() {
    DigitalpdsTheme(dynamicColor = false) { // Disable dynamic color for better preview stability
        Surface(color = BackgroundWhite) {
            CreateAccountScreen(
                onBackClick = {},
                onRegistrationSuccess = { userId, name, email, phone, token, profileImage -> 
                    // Empty handler for preview
                }
            )
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
        e.printStackTrace()
        null
    }
}
