package com.SIMATS.digitalpds

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.network.AiPredictionResponse
import com.SIMATS.digitalpds.network.Beneficiary
import com.SIMATS.digitalpds.network.BrushingSessionItem
import com.SIMATS.digitalpds.network.ClinicResponse
import com.SIMATS.digitalpds.network.DealerConfirmDistributionResponse
import com.SIMATS.digitalpds.network.DealerManualDistributionRequest
import com.SIMATS.digitalpds.network.DealerQRConfirmRequest
import com.SIMATS.digitalpds.network.FamilyMemberResponse
import com.SIMATS.digitalpds.network.KitConfirmRequest
import com.SIMATS.digitalpds.network.KitReceivedResponse
import com.SIMATS.digitalpds.network.MonthlyProgressResponse
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.network.TeethReportResponse
import com.SIMATS.digitalpds.network.ProfileUpdateRequest
import com.SIMATS.digitalpds.notification.CheckInPrefs
import com.SIMATS.digitalpds.notification.NotificationHelper
import com.SIMATS.digitalpds.notification.NotificationScheduler
import com.SIMATS.digitalpds.ui.theme.DealerGreen
import com.SIMATS.digitalpds.ui.theme.DigitalpdsTheme
import com.SIMATS.digitalpds.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            NotificationScheduler.scheduleAll(this)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Notifications
        CheckInPrefs.resetIfNewDay(this)
        NotificationHelper.createChannel(this)
        requestNotificationPermission()
        NotificationScheduler.scheduleAll(this)

        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val sessionManager = remember { SessionManager(context) }
            
            // Session logic: No longer clearing on every startup to prevent accidental logout


            val dealerViewModel: DealerViewModel = viewModel()
            val familyHealthViewModel: FamilyHealthViewModel = viewModel()
            val adminProfileViewModel: AdminProfileViewModel = viewModel()

            var selectedLanguage by remember { mutableStateOf("English (US)") }
            var isDarkMode by remember { mutableStateOf(false) }
            var notificationsEnabled by remember { mutableStateOf(true) }


            // Toasts for DealerViewModel actions
            LaunchedEffect(dealerViewModel.errorMessage) {
                dealerViewModel.errorMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            }
            LaunchedEffect(dealerViewModel.actionMessage) {
                dealerViewModel.actionMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }

                var userRole by remember { mutableStateOf<String?>(sessionManager.getUserRole()) }
                var currentScreen by remember { mutableStateOf("splash") }
                var recoveryEmail by remember { mutableStateOf("") }
                var recoveryCode by remember { mutableStateOf("") }
                var scannerSource by remember { mutableStateOf<String?>(null) }
                var loggedInUserId by remember { mutableIntStateOf(sessionManager.getUserId()) }
                var loggedInUserName by remember { mutableStateOf(sessionManager.getUserName() ?: "") }
                var loggedInUserEmail by remember { mutableStateOf(sessionManager.getUserEmail() ?: "") }
                var loggedInUserPhone by remember { mutableStateOf(sessionManager.getUserPhone() ?: "") }
                var loggedInUserProfileImage by remember { mutableStateOf<String?>(sessionManager.getProfileImage()) }
                var isPdsLinked by remember { mutableStateOf(sessionManager.isPdsVerified()) }
                var scannedPdsId by remember { mutableStateOf<String?>(null) }

            // Sync User Profile on Startup / Login
            LaunchedEffect(loggedInUserId) {
                if (loggedInUserId != -1 && userRole == "user") {
                    try {
                        val token = "Bearer ${sessionManager.getAccessToken() ?: ""}"
                        val response = RetrofitClient.apiService.getUserProfile(token, loggedInUserId)
                        if (response.isSuccessful) {
                            response.body()?.let { profile ->
                                // Update local state
                                isPdsLinked = profile.pds_verified
                                loggedInUserName = profile.name
                                loggedInUserEmail = profile.email
                                loggedInUserPhone = profile.phone
                                loggedInUserProfileImage = profile.profile_image
                                
                                // Update session persistence
                                sessionManager.saveSession(
                                    userId = profile.id,
                                    name = profile.name,
                                    email = profile.email,
                                    phone = profile.phone,
                                    role = "user",
                                    pdsVerified = profile.pds_verified,
                                    token = sessionManager.getAccessToken(),
                                    profileImage = profile.profile_image
                                )
                            }
                        }
                    } catch (e: Exception) {
                        // Silent fail for background sync
                        android.util.Log.e("MainActivity", "Profile sync failed: ${e.message}")
                    }
                }
            }

            DigitalpdsTheme(darkTheme = isDarkMode) {
                var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
                var aiAnalysisResult by remember { mutableStateOf<AiPredictionResponse?>(null) }

                var latestMemberReport by remember { mutableStateOf<MemberAiReport?>(null) }
                var latestReportLoading by remember { mutableStateOf(false) }

                val distributionRecords = remember { mutableStateListOf<DistributionRecord>() }

                var brushesCount by remember { mutableIntStateOf(0) }
                var fluoridePasteCount by remember { mutableIntStateOf(0) }
                var iecPamphletsCount by remember { mutableIntStateOf(0) }

                var selectedFamilyMember by remember { mutableStateOf<FamilyMember?>(null) }
                var editingFamilyMember by remember { mutableStateOf<FamilyMemberResponse?>(null) }
    var selectedClinic by remember { mutableStateOf<ClinicResponse?>(null) }
                var selectedHouseholdId by remember { mutableStateOf<String?>(null) }
                var selectedBeneficiaryId by remember { mutableIntStateOf(0) }
                var selectedDealer by remember { mutableStateOf<DealerInfo?>(null) }
                var selectedStockRequest by remember { mutableStateOf<AdminStockRequest?>(null) }
                var selectedBeneficiary by remember { mutableStateOf<AdminBeneficiary?>(null) }
                var qrBeneficiaryId by remember { mutableStateOf("") }
                var adminViewDealerId by remember { mutableIntStateOf(-1) }


                val adminDealersList = remember { mutableStateListOf<DealerInfo>() }

                val familyMembersList = remember { mutableStateListOf<FamilyMemberResponse>() }
                var familyMembersLoading by remember { mutableStateOf(false) }
                val familyMembersScrollState = rememberLazyListState()

                val dealerBeneficiaries = remember { mutableStateListOf<Beneficiary>() }

                var brushesThisWeek by remember { mutableIntStateOf(0) }
                var completedSessions by remember {
                    mutableStateOf<List<Pair<Boolean, Boolean>>>(List(7) { false to false })
                }

                var monthlyUsageItems by remember { mutableStateOf<List<MonthlyUsageData>>(emptyList()) }
                var monthlyDailyRecords by remember { mutableStateOf<List<BrushingSessionItem>>(emptyList()) }
                var monthlyUsageLoading by remember { mutableStateOf(false) }

                var confirmOldKitReturned by remember { mutableStateOf(false) }

                var currentKitUniqueId by remember { mutableStateOf<String?>(null) }
                var latestKitReceivedData by remember { mutableStateOf<KitReceivedResponse?>(null) }

                val defaultMember = FamilyMember(0, "Me", 0, "Unknown", "Never", R.drawable.user)

                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()
                var showProfileSheet by remember { mutableStateOf(false) }

                fun deriveOralHealthScore(result: AiPredictionResponse?): Int {
                    val risk = result?.riskLevel?.trim()?.uppercase()
                    val detectionsCount = result?.detections?.size ?: 0
                    val msg = result?.message?.lowercase() ?: ""
                    
                    val isUnverified = detectionsCount == 0 && 
                                       !msg.contains("teeth") && 
                                       !msg.contains("dental") &&
                                       !msg.contains("oral")

                    if (risk == "INVALID" || (risk == null && detectionsCount == 0) || isUnverified) return 0
                    
                    return when (risk) {
                        "LOW" -> 85
                        "MEDIUM" -> 60
                        "HIGH" -> 35
                        else -> if (detectionsCount == 0) 0 else 50
                    }
                }

                val fetchFamilyMembers: (Int) -> Unit = { userId ->
                    scope.launch {
                        familyMembersLoading = true
                        try {
                            val token = "Bearer ${sessionManager.getAccessToken() ?: ""}"
                            val response = RetrofitClient.apiService.getFamilyMembers(token, userId)
                            if (response.isSuccessful) {
                                familyMembersList.clear()
                                response.body()?.let { members ->
                                    familyMembersList.addAll(members)
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error fetching family: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            familyMembersLoading = false
                        }
                    }
                }
                
                val deleteFamilyMember: (Int) -> Unit = { memberId ->
                    scope.launch {
                        try {
                            val token = "Bearer ${sessionManager.getAccessToken() ?: ""}"
                            val response = RetrofitClient.apiService.deleteFamilyMember(token, memberId, loggedInUserId)
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Member deleted successfully", Toast.LENGTH_SHORT).show()
                                fetchFamilyMembers(loggedInUserId)
                            } else {
                                val errorMsg = response.errorBody()?.string() ?: "Failed to delete"
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error deleting member: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                val fetchWeeklyProgressForUser: (Int) -> Unit = { userId ->
                    scope.launch {
                        try {
                            val token = "Bearer ${sessionManager.getAccessToken() ?: ""}"
                            val response = RetrofitClient.apiService.getWeeklyProgress(token, userId)
                            if (response.isSuccessful) {
                                response.body()?.let { body ->
                                    brushesThisWeek = body.totalCompleted
                                    completedSessions = body.sessions.map { it.morning to it.evening }
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error loading weekly progress: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                val processDealerQRConfirmation: (String) -> Unit = { qrValue ->
                    scope.launch {
                        try {
                            val scannedDealerQrValue = qrValue.trim()

                            if (scannedDealerQrValue.isBlank()) {
                                Toast.makeText(context, "Invalid dealer QR", Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            // STEP 1: Create/get pending distribution using dealer QR
                            val qrToken = "Bearer ${sessionManager.getAccessToken() ?: ""}"
                            val qrResponse = RetrofitClient.apiService.confirmKitByDealerQR(
                                qrToken,
                                DealerQRConfirmRequest(
                                    dealer_qr_value = scannedDealerQrValue,
                                    beneficiaryId = loggedInUserId
                                )
                            )

                            if (!qrResponse.isSuccessful) {
                                val errorText = qrResponse.errorBody()?.string()
                                val errorMessage = try {
                                    JSONObject(errorText ?: "{}").optString("message")
                                        .ifBlank { JSONObject(errorText ?: "{}").optString("error") }
                                        .ifBlank { "QR verification failed" }
                                } catch (e: Exception) {
                                    errorText ?: "QR verification failed"
                                }

                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                return@launch
                            }

                            val qrBody = qrResponse.body()
                            val kitId = qrBody?.kit_unique_id

                            if (kitId.isNullOrBlank()) {
                                Toast.makeText(context, "Kit ID not returned from server", Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            currentKitUniqueId = kitId

                            // STEP 2: Confirm kit with quantities
                            val confirmResponse = RetrofitClient.apiService.confirmKit(
                                qrToken,
                                KitConfirmRequest(
                                    kit_unique_id = kitId,
                                    brushReceived = brushesCount,
                                    pasteReceived = fluoridePasteCount,
                                    iecReceived = iecPamphletsCount,
                                    oldKitReturned = confirmOldKitReturned
                                )
                            )

                            if (!confirmResponse.isSuccessful) {
                                val errorText = confirmResponse.errorBody()?.string()
                                val errorMessage = try {
                                    JSONObject(errorText ?: "{}").optString("error")
                                        .ifBlank { "Kit confirmation failed" }
                                } catch (e: Exception) {
                                    errorText ?: "Kit confirmation failed"
                                }

                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                return@launch
                            }

                            // STEP 3: Fetch saved kit received details
                            val receivedResponse = RetrofitClient.apiService.getKitReceived(qrToken, kitId)
                            latestKitReceivedData = if (receivedResponse.isSuccessful) {
                                receivedResponse.body()
                            } else {
                                null
                            }

                            Toast.makeText(
                                context,
                                confirmResponse.body()?.message ?: "Kit confirmed successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Explicitly trigger a refresh of the distribution history list
                            val tokenValue = sessionManager.getAccessToken() ?: ""
                            familyHealthViewModel.fetchDistributionHistory("Bearer $tokenValue", loggedInUserId)

                            currentScreen = "kit_received"

                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                e.message ?: "QR processing failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                val fetchLatestReportForMember: (Int, (() -> Unit)?) -> Unit = { memberId, onDone ->
                    val requestUserId = loggedInUserId // Use the state variable directly
                    scope.launch {
                        latestReportLoading = true
                        try {
                            val token = "Bearer ${sessionManager.getAccessToken() ?: ""}"
                            val response = RetrofitClient.apiService.getLatestMemberReport(token, memberId, requestUserId)
                            if (response.isSuccessful) {
                                val latestReport = response.body()

                                if (latestReport != null) {
                                    val parsedResult = try {
                                        com.google.gson.Gson().fromJson(
                                            latestReport.aiResult,
                                            AiPredictionResponse::class.java
                                        )
                                    } catch (e: Exception) {
                                        AiPredictionResponse(
                                            message = latestReport.aiResult,
                                            riskLevel = latestReport.riskLevel,
                                            detections = emptyList()
                                        )
                                    }

                                    val fullImagePath = if (latestReport.imagePath?.startsWith("http") == true) {
                                        latestReport.imagePath
                                    } else if (!latestReport.imagePath.isNullOrBlank()) {
                                        "${RetrofitClient.BASE_URL}/${latestReport.imagePath}"
                                    } else {
                                        null
                                    }
                                    val report = MemberAiReport(
                                        memberId = memberId,
                                        imagePath = fullImagePath,
                                        aiResult = parsedResult,
                                        createdAt = latestReport.createdAt ?: "Unknown",
                                        riskLevel = latestReport.riskLevel ?: "Unknown"
                                    )
                                    latestMemberReport = report
                                    // Update ViewModel so FamilyHealthProfilesScreen gets the update
                                    familyHealthViewModel.saveOrUpdateMemberReport(memberId, null, parsedResult)
                                } else {
                                    latestMemberReport = null
                                }
                            } else {
                                latestMemberReport = null
                                if (response.code() != 404) {
                                    Toast.makeText(context, "Failed to fetch latest AI report", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            latestMemberReport = null
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            latestReportLoading = false
                            onDone?.invoke()
                        }
                    }
                }

                val fetchMonthlyUsageLeaderboard: (Int) -> Unit = { userId ->
                    scope.launch {
                        monthlyUsageLoading = true
                        val leaderboardItems = mutableListOf<MonthlyUsageData>()

                        try {
                            // 1. Fetch Head's Data (memberId = null)
                            val token = "Bearer ${sessionManager.getAccessToken() ?: ""}"
                            val headResponse = RetrofitClient.apiService.getMonthlyUsage(token, userId, null, null, null)
                            if (headResponse.isSuccessful) {
                                headResponse.body()?.let { body ->
                                    val adherence = if (body.totalPossible > 0) (body.totalCompleted * 100 / body.totalPossible) else 0
                                    val pasteStatus = when {
                                        adherence >= 80 -> "Optimal"
                                        adherence >= 50 -> "Moderate"
                                        else -> "Low Usage"
                                    }
                                    val brushStatus = when {
                                        adherence >= 90 -> "Heavy Use"
                                        adherence >= 40 -> "Good"
                                        else -> "Little Used"
                                    }
                                    leaderboardItems.add(
                                        MonthlyUsageData(
                                            name = loggedInUserName,
                                            days = "${body.totalCompleted}/${body.totalPossible} Brushes",
                                            score = adherence,
                                            progress = if (body.totalPossible > 0) body.totalCompleted.toFloat() / body.totalPossible else 0f,
                                            pasteConsumption = pasteStatus,
                                            brushCondition = brushStatus
                                        )
                                    )
                                }
                            }

                            // 2. Fetch each family member's data
                            for (member in familyMembersList) {
                                val memberResponse = RetrofitClient.apiService.getMonthlyUsage(token, userId, member.id, null, null)
                                if (memberResponse.isSuccessful) {
                                    memberResponse.body()?.let { body ->
                                        val mAdherence = if (body.totalPossible > 0) (body.totalCompleted * 100 / body.totalPossible) else 0
                                        val mPasteStatus = when {
                                            mAdherence >= 80 -> "Optimal"
                                            mAdherence >= 50 -> "Moderate"
                                            else -> "Low Usage"
                                        }
                                        val mBrushStatus = when {
                                            mAdherence >= 90 -> "Heavy Use"
                                            mAdherence >= 40 -> "Good"
                                            else -> "Little Used"
                                        }
                                        leaderboardItems.add(
                                            MonthlyUsageData(
                                                name = member.memberName,
                                                days = "${body.totalCompleted}/${body.totalPossible} Brushes",
                                                score = mAdherence,
                                                progress = if (body.totalPossible > 0) body.totalCompleted.toFloat() / body.totalPossible else 0f,
                                                pasteConsumption = mPasteStatus,
                                                brushCondition = mBrushStatus
                                            )
                                        )
                                    }
                                }
                            }

                            // Update states
                            monthlyUsageItems = leaderboardItems.sortedByDescending { it.score }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            monthlyUsageLoading = false
                        }
                    }
                }

                val hardResetForNewSignup = {
                    sessionManager.clearSession()
                    dealerViewModel.resetState()
                    loggedInUserId = -1
                    loggedInUserName = ""
                    loggedInUserEmail = ""
                    loggedInUserPhone = ""
                    isPdsLinked = false
                    userRole = null
                    currentScreen = "login"
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        "splash" -> SplashScreen(
                            onTimeout = { currentScreen = "welcome" }
                        )

                        "welcome" -> WelcomeScreen(
                            onGetStartedClick = { currentScreen = "info" }
                        )

                        "info" -> InfoScreen(
                            onNextClick = { currentScreen = "brushing" }
                        )

                        "brushing" -> BrushingScreen(
                            onNextClick = { currentScreen = "login" }
                        )

                        "login" -> LoginScreen(
                            onUserLoginClick = {
                                userRole = "user"
                                currentScreen = "user_login"
                            },
                            onDealerLoginClick = {
                                userRole = "dealer"
                                currentScreen = "dealer_login"
                            },
                            onAdminLoginClick = {
                                userRole = "admin"
                                currentScreen = "admin_login"
                            }
                        )

                        "user_login" -> UserLoginScreen(
                            onBackClick = { currentScreen = "login" },
                            onLoginSuccess = { userId, name, email, phone, pdsVerified, token, profileImage ->
                                sessionManager.saveSession(userId, name, email, phone, "user", pdsVerified, token, profileImage)
                                loggedInUserId = userId
                                loggedInUserName = name
                                loggedInUserEmail = email
                                loggedInUserPhone = phone
                                loggedInUserProfileImage = profileImage
                                isPdsLinked = pdsVerified
                                userRole = "user"
                                fetchWeeklyProgressForUser(userId)
                                fetchFamilyMembers(userId)
                                currentScreen = "home"
                            },
                             onForgotPasswordClick = { 
                                 userRole = "user"
                                 currentScreen = "forgot_password" 
                             },
                            onSignUpClick = { currentScreen = "create_account" }
                        )

                        "dealer_login" -> DealerLoginScreen(
                            onLoginClick = { userId, name, email, phone, token, profileImage, qrValue ->
                                sessionManager.saveSession(userId, name, email, phone, "dealer", false, token, profileImage, qrValue)
                                loggedInUserId = userId
                                loggedInUserName = name
                                loggedInUserEmail = email
                                loggedInUserPhone = phone
                                loggedInUserProfileImage = profileImage
                                userRole = "dealer"
                                currentScreen = "dealer_dashboard"
                            },
                            onBackClick = { currentScreen = "login" },
                             onForgotPasswordClick = { 
                                 userRole = "dealer"
                                 currentScreen = "forgot_password" 
                             }
                        )

                        "admin_login" -> AdminLoginScreen(
                            onLoginClick = { userId, name, email, phone, token, profileImage ->
                                sessionManager.saveSession(userId, name, email, phone, "admin", false, token, profileImage)
                                loggedInUserId = userId
                                loggedInUserName = name
                                loggedInUserEmail = email
                                loggedInUserPhone = phone
                                loggedInUserProfileImage = profileImage
                                userRole = "admin"
                                adminProfileViewModel.fetchAdminProfile(token ?: "")
                                currentScreen = "admin_dashboard"
                            },
                            onBackClick = { currentScreen = "login" }
                        )

                        "forgot_password" -> ForgotPasswordScreen(
                            isDealer = userRole == "dealer",
                            onBackClick = { currentScreen = "login" },
                            onCodeSent = { email -> 
                                recoveryEmail = email
                                currentScreen = "verify_code" 
                            }
                        )

                        "verify_code" -> VerifyCodeScreen(
                            email = recoveryEmail,
                            isDealer = userRole == "dealer",
                            onBackClick = { currentScreen = "forgot_password" },
                            onVerifyClick = { code ->
                                recoveryCode = code
                                currentScreen = "reset_password"
                            },
                            onResendClick = {
                                // Logic to resend (handled by navigating back or calling API)
                                Toast.makeText(applicationContext, "Resending code...", Toast.LENGTH_SHORT).show()
                            }
                        )

                        "reset_password" -> ResetPasswordScreen(
                            email = recoveryEmail,
                            code = recoveryCode,
                            isDealer = userRole == "dealer",
                            onBackClick = { currentScreen = "verify_code" },
                            onResetSuccess = { 
                                currentScreen = if (userRole == "dealer") "dealer_login" else "user_login" 
                            }
                        )

                        "admin_dashboard" -> AdminDashboardScreen(
                            onNotificationClick = { currentScreen = "admin_notifications" },
                            onProfileClick = { currentScreen = "admin_profile" },
                            onNavigate = { screen ->
                                currentScreen = when (screen) {
                                    "Home" -> "admin_dashboard"
                                    "Stock Requests" -> "admin_stock_requests"
                                    "Profile" -> "admin_profile"
                                    "Dealers" -> {
                                        adminViewDealerId = -1
                                        "admin_dealer_list"
                                    }
                                    "Beneficiaries" -> {
                                        adminViewDealerId = -1
                                        "admin_beneficiaries"
                                    }
                                    "Add Location" -> "admin_add_location"
                                    "Clinics" -> "admin_clinics_list"
                                    "Add Clinic" -> "admin_add_clinic"
                                    "Distributions" -> "admin_distributions"
                                    else -> "admin_dashboard"
                                }
                            }
                        )


                        "admin_add_clinic" -> AdminAddClinicScreen(
                            onBackClick = { currentScreen = "admin_clinics_list" }
                        )

                        "admin_clinics_list" -> AdminClinicsScreen(
                            onBackClick = { currentScreen = "admin_dashboard" },
                            onAddClinicClick = { currentScreen = "admin_add_clinic" },
                            onNavigate = { screen ->
                                currentScreen = when (screen) {
                                    "Home" -> "admin_dashboard"
                                    "Stock Requests" -> "admin_stock_requests"
                                    "Profile" -> "admin_profile"
                                    else -> "admin_dashboard"
                                }
                            }
                        )

                        "admin_distributions" -> AdminDistributionsScreen(
                            onBackClick = { currentScreen = "admin_dashboard" },
                            adminViewModel = viewModel()
                        )

                        "admin_notifications" -> AdminNotificationsScreen(
                            onBackClick = { currentScreen = "admin_dashboard" },
                            onNotificationClick = { }
                        )

                        "admin_profile" -> {
                            val prof = adminProfileViewModel.adminProfile
                            AdminProfileScreen(
                                onBackClick = { currentScreen = "admin_dashboard" },
                                onLogoutClick = { hardResetForNewSignup() },
                                onEditProfileClick = { currentScreen = "admin_edit_profile" },
                                onChangePasswordClick = { currentScreen = "admin_change_password" },
                                onPrivacyPolicyClick = { currentScreen = "privacy_policy" },
                                onTermsConditionsClick = { currentScreen = "terms_conditions" },
                                onHelpSupportClick = { currentScreen = "help_support" },
                                onNavigate = { screen ->
                                    currentScreen = when (screen) {
                                        "Home" -> "admin_dashboard"
                                        "Stock Requests" -> "admin_stock_requests"
                                        "Profile" -> "admin_profile"
                                        else -> "admin_profile"
                                    }
                                },
                                notificationsEnabled = notificationsEnabled,
                                onNotificationsToggle = { notificationsEnabled = it },
                                adminName = prof?.name ?: loggedInUserName,
                                adminEmail = prof?.email ?: loggedInUserEmail,
                                adminPhone = prof?.phone ?: loggedInUserPhone,
                                adminLocation = prof?.officeLocation ?: "Central Headquarters",
                                adminId = loggedInUserId
                            )
                        }

                        "admin_edit_profile" -> {
                            val prof = adminProfileViewModel.adminProfile
                            AdminEditProfileScreen(
                                initialName = prof?.name ?: loggedInUserName,
                                initialPhone = prof?.phone ?: loggedInUserPhone,
                                initialLocation = prof?.officeLocation ?: "Central Headquarters",
                                onBackClick = { currentScreen = "admin_profile" },
                                onSaveSuccess = { newName, newPhone, newLocation ->
                                    scope.launch {
                                        val token = sessionManager.getAccessToken()
                                        adminProfileViewModel.updateAdminProfile(
                                            token = token ?: "",
                                            name = newName,
                                            phone = newPhone,
                                            location = newLocation,
                                            onSuccess = {
                                                loggedInUserName = newName
                                                loggedInUserPhone = newPhone
                                                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                                                currentScreen = "admin_profile"
                                            },
                                            onError = { errorMsg ->
                                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            )
                        }

                        "admin_change_password" -> AdminChangePasswordScreen(
                            onBackClick = { currentScreen = "admin_profile" },
                            onChangePassword = { currentPassword, newPassword ->
                                scope.launch {
                                    val token = sessionManager.getAccessToken()
                                    val request = com.SIMATS.digitalpds.network.ChangePasswordRequest(
                                        currentPassword = currentPassword,
                                        newPassword = newPassword
                                    )
                                    adminProfileViewModel.changeAdminPassword(
                                        token = token ?: "",
                                        request = request,
                                        onSuccess = {
                                            Toast.makeText(context, "Password changed", Toast.LENGTH_SHORT).show()
                                            currentScreen = "admin_profile"
                                        },
                                        onError = { errorMsg ->
                                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        )

                        "admin_language" -> AdminLanguageScreen(
                            currentLanguage = selectedLanguage,
                            onBackClick = { currentScreen = "admin_profile" },
                            onLanguageSelect = {
                                selectedLanguage = it
                                currentScreen = "admin_profile"
                            }
                        )

                        "privacy_policy" -> PrivacyPolicyScreen(
                            onBackClick = { currentScreen = "admin_profile" }
                        )

                        "terms_conditions" -> TermsConditionsScreen(
                            onBackClick = { currentScreen = "admin_profile" }
                        )

                        "admin_dealer_list" -> AdminDealerListScreen(
                            onBackClick = { currentScreen = "admin_dashboard" },
                            onAddDealerClick = { currentScreen = "admin_add_dealer" },
                            onDealerClick = { dealer ->
                                selectedDealer = dealer
                                currentScreen = "admin_dealer_details"
                            },
                            onNavigate = { screen ->
                                currentScreen = when (screen) {
                                    "Home" -> "admin_dashboard"
                                    "Dealers" -> {
                                        adminViewDealerId = -1
                                        "admin_dealer_list"
                                    }
                                    "Stock Requests" -> "admin_stock_requests"
                                    "Beneficiaries" -> {
                                        adminViewDealerId = -1
                                        "admin_beneficiaries"
                                    }
                                    else -> "admin_dealer_list"
                                }
                            }
                        )

                        "admin_stock_requests" -> AdminStockRequestsScreen(
                            onNavigate = { screen ->
                                currentScreen = when (screen) {
                                    "Home" -> "admin_dashboard"
                                    "Dealers" -> "admin_dealer_list"
                                    "Stock Requests" -> "admin_stock_requests"
                                    "Beneficiaries" -> "admin_beneficiaries"
                                    "Profile" -> "admin_profile"
                                    else -> "admin_stock_requests"
                                }
                            },
                            onDetailsClick = { request ->
                                selectedStockRequest = request
                                currentScreen = "admin_stock_request_details"
                            }
                        )

                        "admin_stock_request_details" -> {
                            selectedStockRequest?.let { request ->
                                AdminStockRequestDetailsScreen(
                                    request = request,
                                    onBackClick = { currentScreen = "admin_stock_requests" }
                                )
                            }
                        }

                        "admin_beneficiaries" -> AdminBeneficiariesScreen(
                            onBackClick = { currentScreen = "admin_dashboard" },
                            onNavigate = { screen ->
                                currentScreen = when (screen) {
                                    "Home" -> "admin_dashboard"
                                    "Stock Requests" -> "admin_stock_requests"
                                    "Profile" -> "admin_profile"
                                    "Dealers" -> {
                                        adminViewDealerId = -1
                                        "admin_dealer_list"
                                    }
                                    "Beneficiaries" -> {
                                        adminViewDealerId = -1
                                        "admin_beneficiaries"
                                    }
                                    "admin_add_beneficiary" -> "admin_add_beneficiary"
                                    else -> "admin_beneficiaries"
                                }
                            },
                            onBeneficiaryClick = { beneficiary ->
                                selectedBeneficiary = beneficiary
                                currentScreen = "admin_beneficiary_details"
                            }
                        )

                        "admin_beneficiary_details" -> {
                            selectedBeneficiary?.let { beneficiary ->
                                AdminBeneficiaryDetailsScreen(
                                    beneficiaryId = beneficiary.id,
                                    onBackClick = { currentScreen = "admin_beneficiaries" },
                                    onAddFamilyMemberClick = { id ->
                                        selectedBeneficiaryId = id
                                        currentScreen = "add_family_member"
                                    }
                                )
                            }
                        }

                        "admin_add_beneficiary" -> AdminAddBeneficiaryScreen(
                            onNavigateBack = { 
                                currentScreen = if (adminViewDealerId != -1) "beneficiary_list" else "admin_beneficiaries" 
                            },
                            onSuccess = { 
                                currentScreen = if (adminViewDealerId != -1) "beneficiary_list" else "admin_beneficiaries" 
                            },
                            initialDealerId = adminViewDealerId
                        )

                        "admin_add_dealer" -> AdminAddDealerScreen(
                            onBackClick = { currentScreen = "admin_dealer_list" },
                            onAddDealerSuccess = {
                                currentScreen = "admin_dealer_list"
                            }
                        )

                        "admin_dealer_details" -> {
                            selectedDealer?.let { dealer ->
                                AdminDealerDetailsScreen(
                                    dealer = dealer,
                                    onBackClick = { currentScreen = "admin_dealer_list" },
                                    onEditClick = { currentScreen = "admin_edit_dealer" },
                                    onDeleteClick = {
                                        scope.launch {
                                            try {
                                                val token = sessionManager.getAccessToken()
                                                val response = RetrofitClient.apiService.adminDeleteDealer(
                                                    token = "Bearer ${token ?: ""}",
                                                    dealerId = dealer.id
                                                )
                                                if (response.isSuccessful) {
                                                    Toast.makeText(context, "Dealer deleted successfully", Toast.LENGTH_SHORT).show()
                                                    currentScreen = "admin_dealer_list"
                                                } else {
                                                    val errorMsg = response.errorBody()?.string() ?: "Delete failed"
                                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        "admin_edit_dealer" -> {
                            selectedDealer?.let { dealer ->
                                AdminEditDealerProfileScreen(
                                    dealer = dealer,
                                    onBackClick = { currentScreen = "admin_dealer_details" },
                                    onSaveClick = { updatedDealer ->
                                        scope.launch {
                                            try {
                                                val token = sessionManager.getAccessToken()
                                                val response = RetrofitClient.apiService.adminUpdateDealer(
                                                    token = "Bearer ${token ?: ""}",
                                                    dealerId = dealer.id,
                                                    request = ProfileUpdateRequest(
                                                        name = updatedDealer.name,
                                                        phone = updatedDealer.phone,
                                                        email = updatedDealer.email,
                                                        company_name = updatedDealer.companyName,
                                                        address = updatedDealer.address,
                                                        city = updatedDealer.city,
                                                        state = updatedDealer.state,

                                                        username = updatedDealer.username
                                                    )
                                                )

                                                if (response.isSuccessful) {
                                                    val index = adminDealersList.indexOfFirst { it.handle == dealer.handle }
                                                    if (index != -1) {
                                                        adminDealersList[index] = updatedDealer
                                                    }
                                                    selectedDealer = updatedDealer
                                                    currentScreen = "admin_dealer_details"
                                                } else {
                                                    val errorMsg = response.errorBody()?.string() ?: "Update failed"
                                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        "dealer_dashboard" -> DealerDashboardScreen(
                            dealerId = loggedInUserId,
                            onHistoryClick = { currentScreen = "distribution_activity" },
                            onRequestStockClick = { currentScreen = "request_stock" },
                            onProfileClick = { currentScreen = "dealer_profile" },
                            onStockClick = { currentScreen = "main_stock_hub" },
                            onBeneficiaryClick = { currentScreen = "beneficiary_list" },
                            onScanClick = {
                                scannerSource = "dealer_pds_scan"
                                currentScreen = "qr_scanner"
                            },
                            onProceedClick = { pdsNo ->
                                val token = sessionManager.getAccessToken() ?: ""
                                dealerViewModel.verifyBeneficiary(loggedInUserId, token, pdsNo) {
                                    val userId = dealerViewModel.verifiedBeneficiary?.userId
                                    if (userId != null) {
                                        dealerViewModel.fetchDealerHousehold(userId, token)
                                        currentScreen = "household_eligibility"
                                    }
                                }
                            },
                            onPerformanceClick = { currentScreen = "distribution_activity" },
                            onGenerateQRClick = { id ->
                                qrBeneficiaryId = id.toString()
                                currentScreen = "dealer_generate_qr"
                            },
                            onTotalKitsClick = { 
                                val token = sessionManager.getAccessToken() ?: ""
                                dealerViewModel.fetchStockList(loggedInUserId, token)
                                currentScreen = "dealer_stock_list" 
                            }
                        )

                        "dealer_stock_list" -> {
                            DealerStockListScreen(
                                isLoading = dealerViewModel.isStockLoading,
                                stockItems = dealerViewModel.stockItems,
                                onBackClick = { currentScreen = "dealer_dashboard" }
                            )
                        }

                        "dealer_generate_qr" -> {
                            DealerGenerateQrScreen(
                                dealerId = loggedInUserId,
                                qrValue = sessionManager.getDealerQrValue(),
                                onBackClick = { currentScreen = "dealer_dashboard" }
                            )
                        }

                        "dealer_profile" -> {
                            LaunchedEffect(loggedInUserId) {
                                val token = sessionManager.getAccessToken() ?: ""
                                dealerViewModel.fetchDealerProfile(loggedInUserId, token)
                            }
                            
                            val profile = dealerViewModel.dealerProfile
                            
                            // Profile image handling removed

                            DealerProfileScreen(
                                dealerName = profile?.name ?: loggedInUserName,
                                dealerEmail = profile?.email ?: loggedInUserEmail,
                                dealerPhone = profile?.phone ?: loggedInUserPhone,
                                dealerId = loggedInUserId,
                                companyName = profile?.companyName ?: "",
                                address = profile?.address ?: "",
                                city = profile?.city ?: "",
                                state = profile?.state ?: "",
                                pincode = profile?.pincode ?: "",
                                onBackClick = { currentScreen = "dealer_dashboard" },
                                onNavigate = { screen ->
                                    currentScreen = when (screen) {
                                        "Home" -> "dealer_dashboard"
                                        "Beneficiary" -> "beneficiary_list"
                                        "Stock" -> "main_stock_hub"
                                        "Profile" -> "dealer_profile"
                                        "dealer_edit_profile" -> "dealer_edit_profile"
                                        else -> "dealer_profile"
                                    }
                                },
                                onLogoutClick = { hardResetForNewSignup() },
                                onChangePasswordClick = { currentScreen = "dealer_change_password" }
                            )
                        }

                        "dealer_change_password" -> {
                            val token = sessionManager.getAccessToken() ?: ""
                            DealerChangePasswordScreen(
                                onBackClick = { currentScreen = "dealer_profile" },
                                onChangePassword = { current, new ->
                                    val request = com.SIMATS.digitalpds.network.ChangePasswordRequest(current, new)
                                    dealerViewModel.changePassword(token, request) { success, message ->
                                        if (success) {
                                            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                                            currentScreen = "dealer_profile"
                                        } else {
                                            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        }

                        "dealer_edit_profile" -> {
                            LaunchedEffect(loggedInUserId) {
                                val token = sessionManager.getAccessToken() ?: ""
                                dealerViewModel.fetchDealerProfile(loggedInUserId, token)
                            }

                            DealerEditProfileScreen(
                                viewModel = dealerViewModel,
                                dealerId = loggedInUserId,
                                token = sessionManager.getAccessToken() ?: "",
                                onBackClick = { currentScreen = "dealer_profile" },
                                onSaveSuccess = {
                                    currentScreen = "dealer_profile"
                                }
                            )
                        }

                        "dealer_help_support" -> DealerHelpSupportScreen(
                            onBackClick = { currentScreen = "dealer_profile" }
                        )

                        "account_security" -> AccountAndSecurityScreen(
                            onBackClick = { currentScreen = "dealer_profile" },
                            onSaveClick = { currentScreen = "dealer_profile" }
                        )

                        "main_stock_hub" -> MainStockManagementHubScreen(
                            onBackClick = { currentScreen = "dealer_dashboard" },
                            onNavigate = { screen ->
                                currentScreen = when (screen) {
                                    "Home" -> "dealer_dashboard"
                                    "Beneficiary" -> "beneficiary_list"
                                    "Stock" -> "main_stock_hub"
                                    "Profile" -> "dealer_profile"
                                    else -> "main_stock_hub"
                                }
                            }
                        )

                        "beneficiary_list" -> BeneficiaryListScreen(
                            dealerId = if (userRole == "dealer") loggedInUserId else adminViewDealerId,
                            onNavigate = { screen ->
                                currentScreen = when (screen) {
                                    "Home" -> if (userRole == "dealer") "dealer_dashboard" else "admin_dashboard"
                                    "Beneficiary" -> "beneficiary_list"
                                    "Stock" -> if (userRole == "dealer") "main_stock_hub" else "admin_stock_requests"
                                    "Profile" -> if (userRole == "dealer") "dealer_profile" else "admin_profile"
                                    else -> "beneficiary_list"
                                }
                            },
                            onBack = { 
                                currentScreen = if (userRole == "dealer") "dealer_dashboard" else "admin_dealer_details" 
                            },
                            onBeneficiaryClick = { beneficiary ->
                                selectedBeneficiaryId = beneficiary.id ?: 0
                                selectedHouseholdId = beneficiary.householdId ?: ""
                                currentScreen = "household_details"
                            },
                            onAddNewClick = { 
                                if (userRole == "dealer") {
                                    currentScreen = "new_household_registration"
                                } else {
                                    currentScreen = "admin_add_beneficiary"
                                }
                            }
                        )

                        "new_household_registration" -> NewHouseholdRegistrationScreen(
                            onBackClick = { currentScreen = "beneficiary_list" },
                            isLoading = dealerViewModel.isLoading,
                            onConfirmRegistration = { fullName, email, phone, age, gender, education, employment, address, pdsCard, members, frontUri, backUri ->
                                val token = sessionManager.getAccessToken() ?: ""
                                dealerViewModel.registerHousehold(
                                    context = context,
                                    token = token,
                                    dealerId = loggedInUserId,
                                    name = fullName,
                                    email = email,
                                    phone = phone,
                                    age = age,
                                    gender = gender,
                                    education = education,
                                    employment = employment,
                                    address = address,
                                    pdsCardNo = pdsCard,
                                    members = members,
                                    pdsFrontUri = frontUri,
                                    pdsBackUri = backUri
                                ) { createdUserId, receivedHouseholdId ->
                                    selectedBeneficiaryId = createdUserId
                                    selectedHouseholdId = receivedHouseholdId ?: "HH-$createdUserId"
                                    currentScreen = "registration_success"
                                }
                            }
                        )

                        "registration_success" -> RegistrationSuccessScreen(
                            householdId = selectedHouseholdId ?: "#HH-98210",
                            onBackClick = { currentScreen = "beneficiary_list" },
                            onViewDashboardClick = { currentScreen = "household_details" },
                            onRegisterAnotherClick = { currentScreen = "new_household_registration" },
                            onHomeClick = { currentScreen = "dealer_dashboard" },
                            onBeneficiariesClick = { currentScreen = "beneficiary_list" },
                            onStockClick = { currentScreen = "main_stock_hub" },
                            onProfileClick = { currentScreen = "dealer_profile" }
                        )

                        "household_details" -> AdminBeneficiaryDetailsScreen(
                            beneficiaryId = selectedBeneficiaryId,
                            userRole = "dealer",
                            onBackClick = { currentScreen = "beneficiary_list" },
                            onAddFamilyMemberClick = { id ->
                                selectedBeneficiaryId = id
                                currentScreen = "add_family_member"
                            }
                        )

                        "add_family_member" -> AddFamilyMemberScreen(
                            userId = selectedBeneficiaryId,
                            onBackClick = { currentScreen = if (userRole == "admin") "admin_beneficiary_details" else "household_details" },
                            onSaveSuccess = { currentScreen = if (userRole == "admin") "admin_beneficiary_details" else "household_details" }
                        )

                        "request_stock" -> RequestStockNavigation(
                            onBackToMain = { currentScreen = "dealer_dashboard" }
                        )

                        "distribution_activity" -> DistributionActivityScreen(
                            dealerId = loggedInUserId,
                            records = distributionRecords,
                            onBackClick = { currentScreen = "dealer_dashboard" },
                            onHomeClick = { currentScreen = "dealer_dashboard" },
                            onNavigate = { screen ->
                                currentScreen = when (screen) {
                                    "Home" -> "dealer_dashboard"
                                    "Beneficiary" -> "beneficiary_list"
                                    "Stock" -> "main_stock_hub"
                                    "Profile" -> "dealer_profile"
                                    else -> "distribution_activity"
                                }
                            }
                        )


                        "create_account" -> CreateAccountScreen(
                            onBackClick = { currentScreen = "user_login" },
                            onRegistrationSuccess = { userId, name, email, phone, token, profileImage ->
                                hardResetForNewSignup()
                                sessionManager.saveSession(userId, name, email, phone, "user", false, token, profileImage)
                                loggedInUserId = userId
                                loggedInUserName = name
                                loggedInUserEmail = email
                                loggedInUserPhone = phone
                                loggedInUserProfileImage = profileImage
                                userRole = "user"
                                fetchWeeklyProgressForUser(userId)
                                currentScreen = "link_identity"
                            }
                        )

                        "link_identity" -> LinkIdentityScreen(
                            userId = loggedInUserId,
                            isVerified = isPdsLinked,
                            onBackClick = { currentScreen = "user_login" },
                            onScanClick = {
                                scannerSource = "link_pds"
                                currentScreen = "qr_scanner"
                            },
                            onLinkContinueClick = { nextStep ->
                                scannedPdsId = null
                                isPdsLinked = true
                                sessionManager.setPdsVerified(true)
                                if (nextStep == "SELECT_LOCATION" || nextStep == "SELECT_DEALER") {
                                    currentScreen = "select_dealer"
                                } else {
                                    currentScreen = "home"
                                }
                            },
                            scannedCardNo = scannedPdsId
                        )

                        "select_dealer" -> SelectDealerScreen(
                            userId = loggedInUserId,
                            onBackClick = { currentScreen = "link_identity" },
                            onDealerSelected = {
                                isPdsLinked = true
                                currentScreen = "home"
                            }
                        )

                        "user_profile" -> UserProfileScreen(
                            onBackClick = {
                                if (userRole == "dealer") currentScreen = "dealer_dashboard"
                                else currentScreen = "home"
                            },
                            onHomeClick = {
                                if (userRole == "dealer") currentScreen = "dealer_dashboard"
                                else currentScreen = "home"
                            },
                            onKitsClick = { currentScreen = "family_kit_hub" },
                            onLearnClick = { currentScreen = "education_hub" },
                            onConsultClick = { currentScreen = "consult" },
                            onAboutProgramClick = { currentScreen = "about_program" },
                            onHelpSupportClick = { currentScreen = "help_support" },
                            onLogoutClick = { hardResetForNewSignup() },
                            onLinkIdentityClick = { currentScreen = "link_identity" },
                            isVerified = isPdsLinked,
                            userName = loggedInUserName,
                            userEmail = loggedInUserEmail,
                            userPhone = loggedInUserPhone,
                            profileImage = loggedInUserProfileImage,
                            onProfilePictureUpdated = { newUrl ->
                                loggedInUserProfileImage = newUrl
                                sessionManager.saveSession(
                                    loggedInUserId,
                                    loggedInUserName,
                                    loggedInUserEmail,
                                    loggedInUserPhone,
                                    userRole ?: "user",
                                    isPdsLinked,
                                    sessionManager.getAccessToken(),
                                    newUrl
                                )
                            },
                            onProfileUpdated = { newName, newEmail, newPhone ->
                                loggedInUserName = newName
                                loggedInUserEmail = newEmail
                                loggedInUserPhone = newPhone
                                sessionManager.saveSession(
                                    loggedInUserId,
                                    newName,
                                    newEmail,
                                    newPhone,
                                    userRole ?: "user",
                                    isPdsLinked,
                                    sessionManager.getAccessToken(),
                                    loggedInUserProfileImage
                                )
                            },
                            onSelectDealerClick = { currentScreen = "select_dealer" }

                        )

                        "about_program" -> AboutProgramScreen(
                            onBackClick = { currentScreen = "user_profile" },
                            onHomeClick = {
                                if (userRole == "dealer") currentScreen = "dealer_dashboard"
                                else currentScreen = "home"
                            },
                            onKitsClick = { currentScreen = "family_kit_hub" },
                            onLearnClick = { currentScreen = "education_hub" },
                            onConsultClick = { currentScreen = "consult" },
                            onProfileClick = { currentScreen = "user_profile" }
                        )

                        "help_support" -> HelpSupportScreen(
                            onBackClick = {
                                if (userRole == "admin") currentScreen = "admin_profile"
                                else currentScreen = "user_profile"
                            },
                            onHomeClick = {
                                currentScreen = when (userRole) {
                                    "dealer" -> "dealer_dashboard"
                                    "admin" -> "admin_dashboard"
                                    else -> "home"
                                }
                            },
                            onKitsClick = { currentScreen = "family_kit_hub" },
                            onLearnClick = { currentScreen = "education_hub" },
                            onConsultClick = { currentScreen = "consult" },
                            onProfileClick = {
                                if (userRole == "admin") currentScreen = "admin_profile"
                                else currentScreen = "user_profile"
                            }
                        )

                        "household_eligibility" -> {
                            val household = dealerViewModel.dealerHousehold
                            HouseholdEligibilityScreen(
                                householdId = household?.householdId ?: "",
                                headName = household?.headName ?: "",
                                category = household?.category ?: "PHH",
                                familyMembers = household?.members ?: emptyList(),
                                onBackClick = { currentScreen = "dealer_dashboard" },
                                onProceedClick = { returned ->
                                    confirmOldKitReturned = returned
                                    currentScreen = "final_distribution"
                                }
                            )
                        }

                        "final_distribution" -> {
                            val household = dealerViewModel.dealerHousehold
                            val familyMemberCount = (household?.members?.size ?: 0) + 1 // +1 for the Family Head
                            FinalDistributionHandoverScreen(
                                oldKitReturned = confirmOldKitReturned,
                                familyMemberCount = familyMemberCount,
                                householdId = household?.householdId ?: "",
                                headName = household?.headName ?: "",
                                category = household?.category ?: "PHH",
                                onBackClick = { currentScreen = "household_eligibility" },
                                onCompleteClick = { b, fp, iec ->
                                    val beneficiaryId = dealerViewModel.verifiedBeneficiary?.beneficiaryId ?: 0
                                    if (beneficiaryId <= 0) {
                                        Toast.makeText(context, "Invalid beneficiary selected", Toast.LENGTH_SHORT).show()
                                        return@FinalDistributionHandoverScreen
                                    }

                                    brushesCount = b
                                    fluoridePasteCount = fp
                                    iecPamphletsCount = iec

                                    val token = sessionManager.getAccessToken() ?: ""
                                    dealerViewModel.completeDistribution(
                                        dealerId = loggedInUserId,
                                        token = token,
                                        beneficiaryId = beneficiaryId,
                                        brushReceived = b,
                                        pasteReceived = fp,
                                        iecReceived = iec,
                                        oldKitReturned = confirmOldKitReturned
                                    ) {
                                        val itemsSummaryList = mutableListOf<String>()
                                        if (b > 0) itemsSummaryList.add("${b}x Brush")
                                        if (fp > 0) itemsSummaryList.add("${fp}x Paste")
                                        if (iec > 0) itemsSummaryList.add("${iec}x Flyers")

                                        val currentDate = java.text.SimpleDateFormat(
                                            "yyyy-MM-01", // Match month start for history
                                            java.util.Locale.getDefault()
                                        ).format(java.util.Date())

                                        distributionRecords.add(
                                            0,
                                            DistributionRecord(
                                                beneficiary_name = household?.headName ?: "Beneficiary",
                                                time = java.text.SimpleDateFormat(
                                                    "hh:mm a",
                                                    java.util.Locale.getDefault()
                                                ).format(java.util.Date()),
                                                date = currentDate,
                                                category = household?.category ?: "PHH",
                                                items_summary = itemsSummaryList.joinToString(", "),
                                                oldKitReturned = confirmOldKitReturned
                                            )
                                        )

                                        currentScreen = "distribution_success"
                                    }
                                }
                            )
                        }

                        "distribution_success" -> DistributionSuccessfulScreen(
                            brushes = brushesCount,
                            fluoridePaste = fluoridePasteCount,
                            iecPamphlets = iecPamphletsCount,
                            onBackClick = { currentScreen = "final_distribution" },
                            onReturnToDashboard = { currentScreen = "dealer_dashboard" }
                        )

                        "qr_scanner" -> {
                            QRScannerScreen(
                                scanModeLabel = when (scannerSource) {
                                    "dealer_pds_scan" -> "Scan PDS Card"
                                    "confirm_kit_receipt" -> "Scan Dealer QR"
                                    "link_pds" -> "Scan PDS Card"
                                    else -> "Scan QR Code"
                                },
                                onCloseClick = {
                                    when (scannerSource) {
                                        "dealer_pds_scan" -> currentScreen = "dealer_dashboard"
                                        "link_pds" -> currentScreen = "link_identity"
                                        else -> currentScreen = "confirm_kit_receipt"
                                    }
                                },
                                onResult = { result ->
                                    if (scannerSource == "link_pds") {
                                        scannedPdsId = result
                                        currentScreen = "link_identity"

                                    } else if (scannerSource == "dealer_pds_scan") {
                                        val token = sessionManager.getAccessToken() ?: ""
                                        dealerViewModel.verifyBeneficiary(loggedInUserId, token, result) {
                                            val userId = dealerViewModel.verifiedBeneficiary?.userId
                                            if (userId != null) {
                                                dealerViewModel.fetchDealerHousehold(userId, token)
                                                currentScreen = "household_eligibility"
                                            }
                                        }

                                    } else if (scannerSource == "user_analysis") {
                                        selectedImageUri =
                                            Uri.parse("android.resource://${context.packageName}/drawable/howp")
                                        currentScreen = "ai_analysis"

                                    } else {
                                        processDealerQRConfirmation(result)
                                    }
                                },
                                themeColor = if (userRole == "dealer") DealerGreen else PrimaryBlue
                            )
                        }

                        "home" -> HomeScreen(
                            onFamilyProfileClick = { currentScreen = "family_members" },
                            onCheckInClick = { currentScreen = "daily_check_in" },
                            onManageClick = { currentScreen = "family_members" },
                            onProfileClick = { currentScreen = "user_profile" },
                            onMyProfileClick = { currentScreen = "user_profile" },
                            onUserHealthProfileClick = {
                                selectedFamilyMember = FamilyMember(
                                    id = 0, // Using 0 for primary user to match AI report storage
                                    name = loggedInUserName,
                                    oralHealthScore = 0,
                                    riskLevel = "Pending",
                                    lastScan = "Never",
                                    imageResId = R.drawable.user
                                )
                                fetchLatestReportForMember(0) {
                                    currentScreen = "user_health_profile"
                                }
                            },
                            onKitsClick = { currentScreen = "family_kit_hub" },
                            onLearnClick = { currentScreen = "education_hub" },
                            onConsultClick = { currentScreen = "consult" },
                            onAnalysisOptionClick = { currentScreen = "instant_analysis" },
                            onNotificationClick = { currentScreen = "user_notifications" },
                            weeklyTargetProgress = brushesThisWeek,
                            completedSessions = completedSessions,
                            isVerified = isPdsLinked,
                            userName = loggedInUserName,
                            profileImage = loggedInUserProfileImage
                        )

                        "user_notifications" -> UserNotificationsScreen(
                            onBackClick = { currentScreen = "home" },
                            morningCheckedIn = CheckInPrefs.isMorningCheckedIn(context),
                            eveningCheckedIn = CheckInPrefs.isEveningCheckedIn(context)
                        )

                        "user_health_profile" -> {
                            selectedFamilyMember?.let { member ->
                                MemberHealthStatusScreen(
                                    userId = loggedInUserId,
                                    member = member,
                                    latestReport = latestMemberReport,
                                    onBackClick = { currentScreen = "home" },
                                    onHomeClick = { currentScreen = "home" },
                                    onKitsClick = { currentScreen = "family_kit_hub" },
                                    onLearnClick = { currentScreen = "education_hub" },
                                    onConsultClick = { currentScreen = "consult" },
                                    onProfileClick = { currentScreen = "user_profile" },
                                    onViewAIScanReport = {
                                        fetchLatestReportForMember(member.id) {
                                            currentScreen = "last_ai_scan_report"
                                        }
                                    },
                                    onViewDentalRiskDetails = { currentScreen = "dental_risk_details" },
                                    role = "Verified User"
                                )
                            }
                        }

                        "instant_analysis" -> {
                            val members = familyMembersList.map { resp ->
                                FamilyMember(
                                    id = resp.id,
                                    name = resp.memberName,
                                    oralHealthScore = 0,
                                    riskLevel = "Pending",
                                    lastScan = "Never",
                                    imageResId = R.drawable.user
                                )
                            }

                            InstantTeethAnalysisScreen(
                                onCloseClick = { currentScreen = "home" },
                                members = members,
                                onProceedClick = { uri, memberId ->
                                    selectedImageUri = uri
                                    selectedFamilyMember = if (memberId == 0) {
                                        defaultMember
                                    } else {
                                        members.find { it.id == memberId } ?: defaultMember
                                    }
                                    currentScreen = "ai_analysis"
                                }
                            )
                        }

                        "family_members" -> FamilyMembersScreen(
                            userId = loggedInUserId,
                            onBackClick = { currentScreen = "home" },
                            onAddMemberClick = { currentScreen = "user_add_family_member" },
                            onEditMemberClick = { member ->
                                editingFamilyMember = member
                                currentScreen = "edit_family_member"
                            },
                            onViewProfileClick = { member ->
                                selectedFamilyMember = FamilyMember(
                                    id = member.id,
                                    name = member.memberName,
                                    oralHealthScore = 0,
                                    riskLevel = "Pending",
                                    lastScan = "Never",
                                    imageResId = R.drawable.user
                                )
                                fetchLatestReportForMember(member.id) {
                                    currentScreen = "member_health_status"
                                }
                            },
                            onDeleteMemberClick = { member ->
                                deleteFamilyMember(member.id)
                            },
                            familyMembers = familyMembersList,
                            isLoading = familyMembersLoading,
                            scrollState = familyMembersScrollState,
                            onRefresh = {
                                fetchFamilyMembers(loggedInUserId)
                            }
                        )

                        "user_add_family_member" -> AddFamilyMemberScreen(
                            userId = loggedInUserId,
                            onBackClick = { currentScreen = "family_members" },
                            onSaveSuccess = {
                                fetchFamilyMembers(loggedInUserId)
                                currentScreen = "family_members"
                            }
                        )

                        "edit_family_member" -> {
                            editingFamilyMember?.let { member ->
                                EditFamilyMemberScreen(
                                    userId = loggedInUserId,
                                    member = member,
                                    onBackClick = { currentScreen = "family_members" },
                                    onSaveSuccess = {
                                        fetchFamilyMembers(loggedInUserId)
                                        currentScreen = "family_members"
                                    },
                                    onRemoveSuccess = {
                                        fetchFamilyMembers(loggedInUserId)
                                        currentScreen = "family_members"
                                    }
                                )
                            }
                        }

                        "family_profiles" -> {
                            val allMembers = listOf(
                                FamilyMember(0, "Me", 0, "Unknown", "Never", R.drawable.user)
                            ) + familyMembersList.map {
                                FamilyMember(
                                    id = it.id,
                                    name = it.memberName,
                                    oralHealthScore = 0,
                                    riskLevel = "Pending",
                                    lastScan = "Never",
                                    imageResId = R.drawable.user
                                )
                            }
                            FamilyHealthProfilesScreen(
                                familyMembers = allMembers,
                                onBackClick = { currentScreen = "home" },
                                onViewProfileClick = { member ->
                                    selectedFamilyMember = member
                                    fetchLatestReportForMember(member.id) {
                                        currentScreen = "member_health_status"
                                    }
                                },
                                viewModel = familyHealthViewModel
                            )
                        }

                        "member_health_status" -> {
                            selectedFamilyMember?.let { member ->
                                MemberHealthStatusScreen(
                                    userId = loggedInUserId,
                                    member = member,
                                    latestReport = latestMemberReport,
                                    onBackClick = { currentScreen = "family_members" },
                                    onHomeClick = { currentScreen = "home" },
                                    onKitsClick = { currentScreen = "family_kit_hub" },
                                    onLearnClick = { currentScreen = "education_hub" },
                                    onConsultClick = { currentScreen = "consult" },
                                    onProfileClick = { currentScreen = "user_profile" },
                                    onViewAIScanReport = {
                                        fetchLatestReportForMember(member.id) {
                                            currentScreen = "last_ai_scan_report"
                                        }
                                    },
                                    onViewDentalRiskDetails = { currentScreen = "dental_risk_details" }
                                )
                            }
                        }

                        "dental_risk_details" -> {
                            DentalRiskDetailsScreen(
                                onBackClick = {
                                    currentScreen =
                                        if (selectedFamilyMember?.id == loggedInUserId) "user_health_profile"
                                        else "member_health_status"
                                },
                                onHomeClick = { currentScreen = "home" },
                                onKitsClick = { currentScreen = "family_kit_hub" },
                                onLearnClick = { currentScreen = "education_hub" },
                                onConsultClick = { currentScreen = "consult" },
                                onProfileClick = { currentScreen = "user_profile" }
                            )
                        }

                        "ai_scan_report" -> {
                            AIScanReportScreen(
                                member = selectedFamilyMember ?: defaultMember,
                                imageUri = selectedImageUri,
                                analysisResult = aiAnalysisResult,
                                onBackClick = { currentScreen = "home" },
                                onDoneClick = { currentScreen = "home" },
                                onHomeClick = { currentScreen = "home" },
                                onKitsClick = { currentScreen = "family_kit_hub" },
                                onLearnClick = { currentScreen = "education_hub" },
                                onConsultClick = { currentScreen = "consult" },
                                onProfileClick = { currentScreen = "user_profile" }
                            )
                        }

                        "last_ai_scan_report" -> {
                            val analysisResult = if (selectedFamilyMember?.id == latestMemberReport?.memberId) {
                                latestMemberReport?.aiResult
                            } else {
                                aiAnalysisResult
                            }

                            val reportImageUri = if (selectedFamilyMember?.id == latestMemberReport?.memberId) {
                                latestMemberReport?.imagePath?.let { Uri.parse(it) }
                            } else {
                                selectedImageUri
                            }

                            LastAIScanReportScreen(
                                member = selectedFamilyMember ?: defaultMember,
                                imageUri = reportImageUri,
                                analysisResult = analysisResult,
                                lastScanDate = latestMemberReport?.createdAt
                                    ?: (selectedFamilyMember ?: defaultMember).lastScan,
                                onBackClick = {
                                    currentScreen =
                                        if (selectedFamilyMember?.id == loggedInUserId) "user_health_profile"
                                        else "member_health_status"
                                },
                                onHomeClick = { currentScreen = "home" },
                                onKitsClick = { currentScreen = "family_kit_hub" },
                                onLearnClick = { currentScreen = "education_hub" },
                                onConsultClick = { currentScreen = "consult" },
                                onProfileClick = { currentScreen = "user_profile" }
                            )
                        }

                        "detailed_report" -> {
                            DetailedAIReportScreen(
                                onBackClick = { currentScreen = "home" }
                            )
                        }


                        "daily_check_in" -> {
                            DailyCheckInScreen(
                                userId = loggedInUserId,
                                userName = loggedInUserName,
                                familyMembers = familyMembersList,
                                currentCompletedSessions = completedSessions,
                                onBackClick = { currentScreen = "home" },
                                onCheckInSuccess = {
                                    fetchWeeklyProgressForUser(loggedInUserId)
                                    currentScreen = "home"
                                }
                            )
                        }

                        "ai_analysis" -> {
                            AIAnalysisScreen(
                                userId = loggedInUserId,
                                memberId = selectedFamilyMember?.id,
                                imageUri = selectedImageUri,
                                onAnalysisComplete = { returnedMemberId, returnedImageUri, result ->
                                    aiAnalysisResult = result
                                    selectedImageUri = returnedImageUri

                                    if (returnedMemberId != null) {
                                        // Update shared viewmodel
                                        familyHealthViewModel.saveOrUpdateMemberReport(
                                            memberId = returnedMemberId,
                                            imageUri = returnedImageUri,
                                            result = result
                                        )

                                        fetchLatestReportForMember(returnedMemberId) {
                                            val memberName = if (returnedMemberId == 0) {
                                                loggedInUserName
                                            } else {
                                                familyMembersList.find { it.id == returnedMemberId }?.memberName ?: "Unknown"
                                            }

                                            val generatedScore = deriveOralHealthScore(result)

                                            selectedFamilyMember = if (returnedMemberId == 0) {
                                                defaultMember.copy(
                                                    name = loggedInUserName,
                                                    oralHealthScore = generatedScore,
                                                    riskLevel = result.riskLevel ?: "Pending",
                                                    lastScan = latestMemberReport?.createdAt ?: "Just Now"
                                                )
                                            } else {
                                                FamilyMember(
                                                    id = returnedMemberId,
                                                    name = memberName,
                                                    oralHealthScore = generatedScore,
                                                    riskLevel = result.riskLevel ?: "Pending",
                                                    lastScan = latestMemberReport?.createdAt ?: "Just Now",
                                                    imageResId = R.drawable.user
                                                )
                                            }
                                            currentScreen = "ai_scan_report"
                                        }
                                    } else {
                                        currentScreen = "ai_scan_report"
                                    }
                                },
                                onBackClick = { currentScreen = "instant_analysis" }
                            )
                        }

                        "education_hub" -> {
                            EducationHubScreen(
                                onHomeClick = { currentScreen = "home" },
                                onKitsClick = { currentScreen = "family_kit_hub" },
                                onLearnClick = { currentScreen = "education_hub" },
                                onConsultClick = { currentScreen = "consult" },
                                onProfileClick = { currentScreen = "user_profile" }
                            )
                        }

                        "family_kit_hub" -> {
                            FamilyKitHubScreen(
                                userId = loggedInUserId,
                                familyMembers = familyMembersList,
                                isLoading = familyMembersLoading,
                                onBackClick = { currentScreen = "home" },
                                onHomeClick = { currentScreen = "home" },
                                onLogUsageClick = { currentScreen = "monthly_usage" },
                                onConfirmKitClick = { currentScreen = "confirm_kit_receipt" },
                                onLearnClick = { currentScreen = "education_hub" },
                                onConsultClick = { currentScreen = "consult" },
                                onProfileClick = { currentScreen = "user_profile" },
                                viewModel = familyHealthViewModel
                            )
                        }

                        "monthly_usage" -> {
                            LaunchedEffect(loggedInUserId, familyMembersList.size) {
                                fetchMonthlyUsageLeaderboard(loggedInUserId)
                            }

                            if (monthlyUsageLoading) {
                                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                                    androidx.compose.foundation.layout.Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            } else {
                                MonthlyUsageScreen(
                                    monthYear = java.text.SimpleDateFormat(
                                        "MMMM yyyy",
                                        java.util.Locale.getDefault()
                                    ).format(java.util.Date()),
                                    usageItems = monthlyUsageItems,
                                    onBackClick = { currentScreen = "family_kit_hub" },
                                    onHomeClick = { currentScreen = "home" },
                                    onKitsClick = { currentScreen = "family_kit_hub" },
                                    onLearnClick = { currentScreen = "education_hub" },
                                    onConsultClick = { currentScreen = "consult" },
                                    onProfileClick = { currentScreen = "user_profile" }
                                )
                            }
                        }

                        "confirm_kit_receipt" -> {
                            ConfirmKitReceiptScreen(
                                totalFamilyMembers = familyMembersList.size + 1,
                                onBackClick = { currentScreen = "family_kit_hub" },
                                onFinalizeClick = { oldReturned, brushQty, pasteQty, iecQty ->
                                    confirmOldKitReturned = oldReturned
                                    brushesCount = brushQty
                                    fluoridePasteCount = pasteQty
                                    iecPamphletsCount = iecQty
                                    scannerSource = "confirm_kit_receipt"
                                    currentScreen = "qr_scanner"
                                },
                                onManualConfirmClick = { qrValue, oldReturned, brushQty, pasteQty, iecQty ->
                                    confirmOldKitReturned = oldReturned
                                    brushesCount = brushQty
                                    fluoridePasteCount = pasteQty
                                    iecPamphletsCount = iecQty
                                    processDealerQRConfirmation(qrValue)
                                },
                                onHomeClick = { currentScreen = "home" },
                                onKitsClick = { currentScreen = "family_kit_hub" },
                                onLearnClick = { currentScreen = "education_hub" },
                                onConsultClick = { currentScreen = "consult" },
                                onProfileClick = {
                                    userRole = "user"
                                    currentScreen = "user_profile"
                                }
                            )
                        }

                        "kit_received" -> {
                            KitReceivedScreen(
                                kitData = latestKitReceivedData,
                                onBackClick = { currentScreen = "family_kit_hub" },
                                onDashboardClick = { currentScreen = "home" },
                                onHomeClick = { currentScreen = "home" },
                                onKitsClick = { currentScreen = "family_kit_hub" },
                                onProfileClick = { currentScreen = "user_profile" }
                            )
                        }

                        "consult" -> {
                            ConsultScreen(
                                userId = loggedInUserId,
                                onBackClick = { currentScreen = "home" },
                                onHomeClick = { currentScreen = "home" },
                                onKitsClick = { currentScreen = "family_kit_hub" },
                                onLearnClick = { currentScreen = "education_hub" },
                                onProfileClick = { currentScreen = "user_profile" },
                                onNearbyClinicsClick = { }
                            )
                        }

                    }

                    if (showProfileSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showProfileSheet = false },
                            sheetState = sheetState
                        ) {
                            UserProfileScreen(
                                onBackClick = {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) showProfileSheet = false
                                    }
                                },
                                onHomeClick = {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showProfileSheet = false
                                            if (userRole == "dealer") {
                                                currentScreen = "dealer_dashboard"
                                            } else {
                                                currentScreen = "home"
                                            }
                                        }
                                    }
                                },
                                onAboutProgramClick = {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showProfileSheet = false
                                            currentScreen = "about_program"
                                        }
                                    }
                                },
                                onHelpSupportClick = {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showProfileSheet = false
                                            currentScreen = "help_support"
                                        }
                                    }
                                },
                                onLogoutClick = {
                                    hardResetForNewSignup()
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) showProfileSheet = false
                                    }
                                },
                                onLinkIdentityClick = {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showProfileSheet = false
                                            currentScreen = "link_identity"
                                        }
                                    }
                                },
                                userName = loggedInUserName,
                                userEmail = loggedInUserEmail,
                                userPhone = loggedInUserPhone,
                                profileImage = loggedInUserProfileImage,
                                isVerified = isPdsLinked,
                                onProfilePictureUpdated = { newUrl ->
                                    loggedInUserProfileImage = newUrl
                                    // Also update session manager just in case
                                    sessionManager.saveSession(
                                        loggedInUserId,
                                        loggedInUserName,
                                        loggedInUserEmail,
                                        loggedInUserPhone,
                                        userRole ?: "user",
                                        isPdsLinked,
                                        sessionManager.getAccessToken(),
                                        newUrl
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchLatestReportForMember(memberId: Int, userId: Int): MemberAiReport? {
        return try {
            val token = SessionManager(this).getAccessToken() ?: ""
            val response = RetrofitClient.apiService.getLatestMemberReport("Bearer $token", memberId, userId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                val parsedResult = try {
                    com.google.gson.Gson().fromJson(
                        body.aiResult,
                        AiPredictionResponse::class.java
                    )
                } catch (e: Exception) {
                    AiPredictionResponse(
                        message = body.aiResult,
                        riskLevel = body.riskLevel,
                        detections = emptyList()
                    )
                }

                MemberAiReport(
                    memberId = memberId,
                    riskLevel = body.riskLevel ?: "Unknown",
                    createdAt = body.createdAt ?: "Unknown",
                    aiResult = parsedResult,
                    imagePath = if (body.imagePath?.startsWith("http") == true) {
                        body.imagePath
                    } else if (!body.imagePath.isNullOrBlank()) {
                        "${RetrofitClient.BASE_URL}/${body.imagePath}"
                    } else {
                        null
                    }
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchMonthlyUsageForFamily(
        userId: Int,
        memberId: Int?
    ): MonthlyProgressResponse? {
        return try {
            val token = SessionManager(this).getAccessToken() ?: ""
            val response = RetrofitClient.apiService.getMonthlyUsage(
                token = "Bearer $token",
                userId = userId,
                memberId = memberId
            )

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
