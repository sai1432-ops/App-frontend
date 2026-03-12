package com.SIMATS.digitalpds

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.SIMATS.digitalpds.network.AiPredictionResponse
import com.SIMATS.digitalpds.network.Beneficiary
import com.SIMATS.digitalpds.network.BrushingSessionItem
import com.SIMATS.digitalpds.network.ClinicResponse
import com.SIMATS.digitalpds.network.DealerManualDistributionRequest
import com.SIMATS.digitalpds.network.DealerQRConfirmRequest
import com.SIMATS.digitalpds.network.FamilyMemberResponse
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.DealerGreen
import com.SIMATS.digitalpds.ui.theme.DigitalpdsTheme
import com.SIMATS.digitalpds.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val sessionManager = remember { SessionManager(context) }

            var selectedLanguage by remember { mutableStateOf("English (US)") }
            var isDarkMode by remember { mutableStateOf(false) }
            var notificationsEnabled by remember { mutableStateOf(true) }

            var adminProfileBitmap by remember { mutableStateOf<Bitmap?>(null) }

            DigitalpdsTheme(darkTheme = isDarkMode) {
                var currentScreen by remember { mutableStateOf("welcome") }

                var userRole by remember {
                    mutableStateOf(if (sessionManager.isLoggedIn()) sessionManager.getUserRole() else null)
                }
                var scannerSource by remember { mutableStateOf<String?>(null) }

                var loggedInUserId by remember { mutableIntStateOf(sessionManager.getUserId()) }
                var loggedInUserName by remember { mutableStateOf(sessionManager.getUserName() ?: "") }
                var loggedInUserEmail by remember { mutableStateOf(sessionManager.getUserEmail() ?: "") }
                var loggedInUserPhone by remember { mutableStateOf(sessionManager.getUserPhone() ?: "") }

                var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
                var aiAnalysisResult by remember { mutableStateOf<AiPredictionResponse?>(null) }

                var latestMemberReport by remember { mutableStateOf<MemberAiReport?>(null) }
                var latestReportLoading by remember { mutableStateOf(false) }

                val distributionRecords = remember { mutableStateListOf<DistributionRecord>() }

                var adultBrushesCount by remember { mutableIntStateOf(0) }
                var childBrushesCount by remember { mutableIntStateOf(0) }
                var fluoridePasteCount by remember { mutableIntStateOf(0) }
                var iecPamphletsCount by remember { mutableIntStateOf(0) }

                var selectedFamilyMember by remember { mutableStateOf<FamilyMember?>(null) }
                var editingFamilyMember by remember { mutableStateOf<FamilyMemberResponse?>(null) }
                var selectedClinic by remember { mutableStateOf<ClinicResponse?>(null) }
                var selectedAppointmentDate by remember { mutableStateOf("") }
                var selectedAppointmentTime by remember { mutableStateOf("") }
                var selectedHouseholdId by remember { mutableStateOf<String?>(null) }
                var selectedDealer by remember { mutableStateOf<DealerInfo?>(null) }
                var selectedStockRequest by remember { mutableStateOf<AdminStockRequest?>(null) }
                var selectedBeneficiary by remember { mutableStateOf<AdminBeneficiary?>(null) }
                var qrBeneficiaryId by remember { mutableStateOf("") }

                var scannedPdsId by remember { mutableStateOf<String?>(null) }
                var isPdsLinked by remember { mutableStateOf(sessionManager.isPdsVerified()) }

                val adminDealersList = remember { mutableStateListOf<DealerInfo>() }

                val familyMembersList = remember { mutableStateListOf<FamilyMemberResponse>() }
                var familyMembersLoading by remember { mutableStateOf(false) }
                val familyMembersScrollState = rememberLazyListState()

                val dealerBeneficiaries = remember { mutableStateListOf<Beneficiary>() }

                var brushesThisWeek by remember {
                    mutableIntStateOf(sessionManager.getWeeklyCompletedCount(loggedInUserId))
                }
                var completedSessions by remember {
                    mutableStateOf<List<Pair<Boolean, Boolean>>>(
                        sessionManager.getWeeklyCheckinStatus(loggedInUserId)
                    )
                }

                var monthlyUsageItems by remember { mutableStateOf<List<MonthlyUsageData>>(emptyList()) }
                var monthlyDailyRecords by remember { mutableStateOf<List<BrushingSessionItem>>(emptyList()) }
                var monthlyUsageLoading by remember { mutableStateOf(false) }

                var confirmOldKitReturned by remember { mutableStateOf(false) }
                var confirmBrushReceived by remember { mutableStateOf(false) }
                var confirmPasteReceived by remember { mutableStateOf(false) }
                var confirmIecReceived by remember { mutableStateOf(false) }

                val defaultMember = FamilyMember(0, "Me", 0, "Unknown", "Never", R.drawable.user)

                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()
                var showProfileSheet by remember { mutableStateOf(false) }

                fun deriveOralHealthScore(result: AiPredictionResponse?): Int {
                    val risk = result?.riskLevel?.trim()?.lowercase()
                    return when (risk) {
                        "low" -> 85
                        "medium" -> 60
                        "high" -> 35
                        else -> if ((result?.detections?.size ?: 0) == 0) 80 else 50
                    }
                }

                val fetchFamilyMembers: (Int) -> Unit = { userId ->
                    scope.launch {
                        familyMembersLoading = true
                        try {
                            val response = RetrofitClient.apiService.getFamilyMembers(userId)
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

                val fetchWeeklyProgressForUser: (Int) -> Unit = { userId ->
                    scope.launch {
                        try {
                            val response = RetrofitClient.apiService.getWeeklyProgress(userId)
                            if (response.isSuccessful) {
                                response.body()?.let { body ->
                                    brushesThisWeek = body.totalCompleted
                                    completedSessions = body.sessions.map { it.morning to it.evening }
                                    sessionManager.saveWeeklyCheckinStatus(userId, completedSessions)
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Error loading weekly progress: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                val fetchMonthlyUsageForFamily: (Int) -> Unit = { userId ->
                    scope.launch {
                        monthlyUsageLoading = true
                        try {
                            val calendar = java.util.Calendar.getInstance()
                            val year = calendar.get(java.util.Calendar.YEAR)
                            val month = calendar.get(java.util.Calendar.MONTH) + 1

                            val items = mutableListOf<MonthlyUsageData>()
                            val records = mutableListOf<BrushingSessionItem>()

                            val userResponse = RetrofitClient.apiService.getMonthlyUsage(
                                userId = userId,
                                memberId = null,
                                year = year,
                                month = month
                            )

                            if (userResponse.isSuccessful) {
                                userResponse.body()?.let { body ->
                                    items.add(
                                        MonthlyUsageData(
                                            name = loggedInUserName,
                                            days = "Month Progress: ${body.totalCompleted}/${body.totalPossible} sessions",
                                            score = if (body.totalPossible > 0) {
                                                (body.totalCompleted * 100 / body.totalPossible).coerceAtMost(100)
                                            } else 0,
                                            progress = if (body.totalPossible > 0) {
                                                (body.totalCompleted.toFloat() / body.totalPossible.toFloat()).coerceAtMost(1f)
                                            } else 0f,
                                            pasteConsumption = if (body.totalCompleted > (body.totalPossible / 2)) "Optimal" else "Low",
                                            brushCondition = "Good"
                                        )
                                    )

                                    records.addAll(body.sessions)
                                }
                            }

                            familyMembersList.forEach { member ->
                                val response = RetrofitClient.apiService.getMonthlyUsage(
                                    userId = userId,
                                    memberId = member.id,
                                    year = year,
                                    month = month
                                )

                                if (response.isSuccessful) {
                                    response.body()?.let { body ->
                                        items.add(
                                            MonthlyUsageData(
                                                name = member.memberName,
                                                days = "Month Progress: ${body.totalCompleted}/${body.totalPossible} sessions",
                                                score = if (body.totalPossible > 0) {
                                                    (body.totalCompleted * 100 / body.totalPossible).coerceAtMost(100)
                                                } else 0,
                                                progress = if (body.totalPossible > 0) {
                                                    (body.totalCompleted.toFloat() / body.totalPossible.toFloat()).coerceAtMost(1f)
                                                } else 0f,
                                                pasteConsumption = if (body.totalCompleted > (body.totalPossible / 2)) "Optimal" else "Low",
                                                brushCondition = "Good"
                                            )
                                        )
                                    }
                                }
                            }

                            monthlyUsageItems = items
                            monthlyDailyRecords = records.sortedBy { it.date }

                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Error loading monthly usage: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            monthlyUsageLoading = false
                        }
                    }
                }

                val fetchLatestReportForMember: (Int, (() -> Unit)?) -> Unit = { memberId, onDone ->
                    scope.launch {
                        latestReportLoading = true
                        try {
                            val response = RetrofitClient.apiService.getTeethReports(loggedInUserId)
                            if (response.isSuccessful) {
                                val reports = response.body().orEmpty()

                                val matchedReports = if (memberId == loggedInUserId) {
                                    reports.filter { it.memberId == null || it.memberId == 0 || it.userId == loggedInUserId }
                                } else {
                                    reports.filter { it.memberId == memberId }
                                }

                                val latestReport = matchedReports.maxByOrNull { it.createdAt ?: "" }

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

                                    latestMemberReport = MemberAiReport(
                                        memberId = memberId,
                                        imageUri = latestReport.imagePath,
                                        analysisResult = parsedResult,
                                        scanDate = latestReport.createdAt ?: "Unknown",
                                        oralHealthScore = deriveOralHealthScore(parsedResult),
                                        riskLevel = latestReport.riskLevel
                                    )
                                } else {
                                    latestMemberReport = null
                                }
                            } else {
                                latestMemberReport = null
                                Toast.makeText(context, "Failed to fetch latest AI report", Toast.LENGTH_SHORT).show()
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

                val hardResetForNewSignup = {
                    sessionManager.clearSession()

                    loggedInUserId = -1
                    loggedInUserName = ""
                    loggedInUserEmail = ""
                    loggedInUserPhone = ""
                    isPdsLinked = false
                    scannedPdsId = null
                    userRole = null

                    adminProfileBitmap = null
                    adminDealersList.clear()

                    familyMembersList.clear()
                    brushesThisWeek = 0
                    completedSessions = List(7) { false to false }
                    monthlyUsageItems = emptyList()
                    monthlyDailyRecords = emptyList()

                    selectedImageUri = null
                    aiAnalysisResult = null
                    latestMemberReport = null
                    selectedFamilyMember = null
                    editingFamilyMember = null
                    selectedClinic = null
                    selectedAppointmentDate = ""
                    selectedAppointmentTime = ""

                    distributionRecords.clear()
                    dealerBeneficiaries.clear()
                    selectedHouseholdId = null
                    selectedDealer = null
                    selectedStockRequest = null
                    selectedBeneficiary = null
                    qrBeneficiaryId = ""

                    confirmOldKitReturned = false
                    confirmBrushReceived = false
                    confirmPasteReceived = false
                    confirmIecReceived = false

                    currentScreen = "welcome"
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
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
                            onLoginSuccess = { userId, name, email, phone, pdsVerified ->
                                hardResetForNewSignup()
                                sessionManager.saveSession(userId, name, email, phone, "user", pdsVerified)
                                loggedInUserId = userId
                                loggedInUserName = name
                                loggedInUserEmail = email
                                loggedInUserPhone = phone
                                isPdsLinked = pdsVerified
                                userRole = "user"
                                fetchWeeklyProgressForUser(userId)
                                fetchFamilyMembers(userId)
                                currentScreen = "home"
                            },
                            onForgotPasswordClick = { currentScreen = "user_recovery" },
                            onSignUpClick = { currentScreen = "create_account" }
                        )

                        "dealer_login" -> DealerLoginScreen(
                            onLoginClick = { userId, name, email, phone ->
                                hardResetForNewSignup()
                                sessionManager.saveSession(userId, name, email, phone, "dealer", false)
                                loggedInUserId = userId
                                loggedInUserName = name
                                loggedInUserEmail = email
                                loggedInUserPhone = phone
                                userRole = "dealer"
                                currentScreen = "dealer_dashboard"
                            },
                            onBackClick = { currentScreen = "login" },
                            onForgotPasswordClick = { currentScreen = "dealer_recovery" }
                        )

                        "admin_login" -> AdminLoginScreen(
                            onLoginClick = { userId, name, email, phone ->
                                hardResetForNewSignup()
                                sessionManager.saveSession(userId, name, email, phone, "admin", false)
                                loggedInUserId = userId
                                loggedInUserName = name
                                loggedInUserEmail = email
                                loggedInUserPhone = phone
                                userRole = "admin"
                                currentScreen = "admin_dashboard"
                            },
                            onBackClick = { currentScreen = "login" }
                        )

                        "admin_dashboard" -> AdminDashboardScreen(
                            onNotificationClick = { currentScreen = "admin_notifications" },
                            onProfileClick = { currentScreen = "admin_profile" },
                            onNavigate = { screen ->
                                currentScreen = when (screen) {
                                    "Home" -> "admin_dashboard"
                                    "Stock Requests" -> "admin_stock_requests"
                                    "Profile" -> "admin_profile"
                                    "Dealers" -> "admin_dealer_list"
                                    "Beneficiaries" -> "admin_beneficiaries"
                                    else -> "admin_dashboard"
                                }
                            }
                        )

                        "admin_notifications" -> AdminNotificationsScreen(
                            onBackClick = { currentScreen = "admin_dashboard" },
                            onNotificationClick = { }
                        )

                        "admin_profile" -> AdminProfileScreen(
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
                            adminName = loggedInUserName,
                            adminEmail = loggedInUserEmail,
                            adminPhone = loggedInUserPhone,
                            adminId = loggedInUserId,
                            adminProfileBitmap = adminProfileBitmap
                        )

                        "admin_edit_profile" -> AdminEditProfileScreen(
                            initialName = loggedInUserName,
                            initialPhone = loggedInUserPhone,
                            initialLocation = "",
                            initialProfileBitmap = adminProfileBitmap,
                            onBackClick = { currentScreen = "admin_profile" },
                            onSaveSuccess = { newName, newPhone, newLocation, newBitmap ->
                                loggedInUserName = newName
                                loggedInUserPhone = newPhone
                                adminProfileBitmap = newBitmap
                                currentScreen = "admin_profile"
                            }
                        )

                        "admin_change_password" -> AdminChangePasswordScreen(
                            onBackClick = { currentScreen = "admin_profile" },
                            onChangeSuccess = { currentScreen = "admin_profile" }
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
                                    "Dealers" -> "admin_dealer_list"
                                    "Stock Requests" -> "admin_stock_requests"
                                    "Beneficiaries" -> "admin_beneficiaries"
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
                            onNavigate = { screen ->
                                currentScreen = when (screen) {
                                    "Home" -> "admin_dashboard"
                                    "Stock Requests" -> "admin_stock_requests"
                                    "Profile" -> "admin_profile"
                                    "Dealers" -> "admin_dealer_list"
                                    "Beneficiaries" -> "admin_beneficiaries"
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
                                    onBackClick = { currentScreen = "admin_beneficiaries" }
                                )
                            }
                        }

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
                                    onEditClick = { currentScreen = "admin_edit_dealer" }
                                )
                            }
                        }

                        "admin_edit_dealer" -> {
                            selectedDealer?.let { dealer ->
                                AdminEditDealerProfileScreen(
                                    dealer = dealer,
                                    onBackClick = { currentScreen = "admin_dealer_details" },
                                    onSaveClick = { updatedDealer ->
                                        val index = adminDealersList.indexOfFirst { it.handle == dealer.handle }
                                        if (index != -1) {
                                            adminDealersList[index] = updatedDealer
                                        }
                                        selectedDealer = updatedDealer
                                        currentScreen = "admin_dealer_details"
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
                                scannerSource = "dealer_dashboard"
                                currentScreen = "qr_scanner"
                            },
                            onProceedClick = { currentScreen = "verification_success" },
                            onPerformanceClick = { currentScreen = "distribution_activity" },
                            onGenerateQRClick = { id ->
                                qrBeneficiaryId = id.toString()
                                currentScreen = "dealer_generate_qr"
                            }
                        )

                        "dealer_generate_qr" -> {
                            DealerGenerateQrScreen(
                                dealerId = loggedInUserId,
                                onBackClick = { currentScreen = "dealer_dashboard" }
                            )
                        }

                        "dealer_profile" -> DealerProfileScreen(
                            dealerName = loggedInUserName,
                            dealerEmail = loggedInUserEmail,
                            dealerPhone = loggedInUserPhone,
                            dealerId = loggedInUserId,
                            onBackClick = { currentScreen = "dealer_dashboard" },
                            onNavigate = { screen ->
                                currentScreen = when (screen) {
                                    "Home" -> "dealer_dashboard"
                                    "Beneficiary" -> "beneficiary_list"
                                    "Stock" -> "main_stock_hub"
                                    "Profile" -> "dealer_profile"
                                    else -> "dealer_profile"
                                }
                            },
                            onLogoutClick = { hardResetForNewSignup() },
                            onAccountSecurityClick = { currentScreen = "account_security" },
                            onHelpSupportClick = { currentScreen = "dealer_help_support" }
                        )

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
                            dealerId = loggedInUserId,
                            onNavigate = { screen ->
                                currentScreen = when (screen) {
                                    "Home" -> "dealer_dashboard"
                                    "Beneficiary" -> "beneficiary_list"
                                    "Stock" -> "main_stock_hub"
                                    "Profile" -> "dealer_profile"
                                    else -> "beneficiary_list"
                                }
                            },
                            onBack = { currentScreen = "dealer_dashboard" },
                            onBeneficiaryClick = { id ->
                                selectedHouseholdId = id
                                currentScreen = "household_details"
                            },
                            onAddNewClick = { currentScreen = "new_household_registration" }
                        )

                        "new_household_registration" -> NewHouseholdRegistrationScreen(
                            onBackClick = { currentScreen = "beneficiary_list" },
                            onConfirmRegistration = { name: String, rationId: String ->
                                val newId = "HH-${(90000..99999).random()}"
                                dealerBeneficiaries.add(
                                    0,
                                    Beneficiary(
                                        name = name,
                                        rationId = "****${rationId.takeLast(4)}",
                                        householdId = newId,
                                        isActive = true
                                    )
                                )
                                selectedHouseholdId = newId
                                currentScreen = "registration_success"
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

                        "household_details" -> HouseholdDetailsScreen(
                            householdId = selectedHouseholdId ?: "",
                            onBack = { currentScreen = "beneficiary_list" }
                        )

                        "request_stock" -> RequestStockNavigation(
                            onBackToMain = { currentScreen = "dealer_dashboard" }
                        )

                        "distribution_activity" -> DistributionActivityScreen(
                            records = distributionRecords,
                            onBackClick = { currentScreen = "dealer_dashboard" },
                            onHomeClick = { currentScreen = "dealer_dashboard" },
                            onProfileClick = { currentScreen = "dealer_profile" }
                        )

                        "user_recovery" -> UserRecoveryScreen(
                            onBackClick = { currentScreen = "user_login" },
                            onResetPasswordClick = { currentScreen = "verification" }
                        )

                        "dealer_recovery" -> DealerRecoveryScreen(
                            onBackClick = { currentScreen = "dealer_login" },
                            onResetPasswordClick = { currentScreen = "verification" }
                        )

                        "verification" -> VerificationScreen(
                            onBackClick = {
                                when (userRole) {
                                    "dealer" -> currentScreen = "dealer_recovery"
                                    else -> currentScreen = "user_recovery"
                                }
                            },
                            onVerifyClick = { currentScreen = "reset_password" },
                            themeColor = when (userRole) {
                                "dealer" -> DealerGreen
                                "admin" -> Color(0xFFD32F2F)
                                else -> PrimaryBlue
                            }
                        )

                        "reset_password" -> ResetPasswordScreen(
                            onBackClick = { currentScreen = "verification" },
                            onSaveClick = {
                                when (userRole) {
                                    "dealer" -> currentScreen = "dealer_login"
                                    else -> currentScreen = "user_login"
                                }
                            },
                            themeColor = when (userRole) {
                                "dealer" -> DealerGreen
                                "admin" -> Color(0xFFD32F2F)
                                else -> PrimaryBlue
                            }
                        )

                        "create_account" -> CreateAccountScreen(
                            onBackClick = { currentScreen = "user_login" },
                            onRegistrationSuccess = { userId, name, email, phone ->
                                hardResetForNewSignup()
                                sessionManager.saveSession(userId, name, email, phone, "user", false)
                                loggedInUserId = userId
                                loggedInUserName = name
                                loggedInUserEmail = email
                                loggedInUserPhone = phone
                                userRole = "user"
                                fetchWeeklyProgressForUser(userId)
                                currentScreen = "link_identity"
                            }
                        )

                        "link_identity" -> LinkIdentityScreen(
                            userId = loggedInUserId,
                            onBackClick = { currentScreen = "user_login" },
                            onScanClick = {
                                scannerSource = "link_pds"
                                currentScreen = "qr_scanner"
                            },
                            onLinkContinueClick = {
                                scannedPdsId = null
                                isPdsLinked = true
                                sessionManager.setPdsVerified(true)
                                currentScreen = "home"
                            },
                            scannedCardNo = scannedPdsId
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
                            isVerified = isPdsLinked,
                            userName = loggedInUserName,
                            userEmail = loggedInUserEmail,
                            userPhone = loggedInUserPhone
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

                        "verification_success" -> VerificationSuccessScreen(
                            onContinueClick = {
                                if (userRole == "dealer") currentScreen = "household_eligibility"
                                else currentScreen = "home"
                            }
                        )

                        "household_eligibility" -> HouseholdEligibilityScreen(
                            familyMembers = familyMembersList,
                            onBackClick = { currentScreen = "dealer_dashboard" },
                            onProceedClick = { currentScreen = "final_distribution" }
                        )

                        "final_distribution" -> FinalDistributionHandoverScreen(
                            onBackClick = { currentScreen = "household_eligibility" },
                            onCompleteClick = { ab, cb, fp, iec ->

                                adultBrushesCount = ab
                                childBrushesCount = cb
                                fluoridePasteCount = fp
                                iecPamphletsCount = iec

                                scope.launch {
                                    try {
                                        val response = RetrofitClient.apiService.dealerConfirmDistribution(
                                            DealerManualDistributionRequest(
                                                dealerId = loggedInUserId,
                                                beneficiaryId = loggedInUserId,
                                                oldKitReturned = true,
                                                brushReceived = (ab + cb) > 0,
                                                pasteReceived = fp > 0,
                                                iecReceived = iec > 0
                                            )
                                        )

                                        if (response.isSuccessful) {
                                            val itemsSummary = mutableListOf<String>()
                                            if (ab > 0) itemsSummary.add("${ab}x Brush")
                                            if (cb > 0) itemsSummary.add("${cb}x Child Brush")
                                            if (fp > 0) itemsSummary.add("${fp}x Paste")
                                            if (iec > 0) itemsSummary.add("${iec}x Flyers")

                                            val currentDate = java.text.SimpleDateFormat(
                                                "yyyy-MM-dd",
                                                java.util.Locale.getDefault()
                                            ).format(java.util.Date())

                                            distributionRecords.add(
                                                0,
                                                DistributionRecord(
                                                    beneficiaryName = "Rajesh Kumar",
                                                    time = java.text.SimpleDateFormat(
                                                        "hh:mm a",
                                                        java.util.Locale.getDefault()
                                                    ).format(java.util.Date()),
                                                    date = currentDate,
                                                    category = "PHH",
                                                    itemsSummary = itemsSummary.joinToString(", ")
                                                )
                                            )

                                            currentScreen = "distribution_success"
                                        } else {
                                            Toast.makeText(
                                                context,
                                                response.errorBody()?.string() ?: "Dealer distribution failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            e.message ?: "Dealer distribution failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        )

                        "distribution_success" -> DistributionSuccessfulScreen(
                            adultBrushes = adultBrushesCount,
                            childBrushes = childBrushesCount,
                            fluoridePaste = fluoridePasteCount,
                            iecPamphlets = iecPamphletsCount,
                            onBackClick = { currentScreen = "final_distribution" },
                            onReturnToDashboard = { currentScreen = "dealer_dashboard" }
                        )

                        "qr_scanner" -> {
                            QRScannerScreen(
                                onCloseClick = {
                                    when (scannerSource) {
                                        "dealer_dashboard" -> currentScreen = "dealer_dashboard"
                                        "link_pds" -> currentScreen = "link_identity"
                                        else -> currentScreen = "confirm_kit_receipt"
                                    }
                                },
                                onResult = { result ->
                                    if (scannerSource == "link_pds") {
                                        scannedPdsId = result
                                        currentScreen = "link_identity"

                                    } else if (scannerSource == "dealer_dashboard") {
                                        currentScreen = "verification_success"

                                    } else if (scannerSource == "user_analysis") {
                                        selectedImageUri =
                                            Uri.parse("android.resource://${context.packageName}/drawable/howp")
                                        currentScreen = "ai_analysis"

                                    } else {
                                        scope.launch {
                                            try {
                                                val json = JSONObject(result)
                                                val scannedDealerId = json.optInt("dealer_id", -1)

                                                if (scannedDealerId <= 0) {
                                                    Toast.makeText(context, "Invalid dealer QR", Toast.LENGTH_SHORT).show()
                                                    return@launch
                                                }

                                                val response = RetrofitClient.apiService.confirmKitByDealerQR(
                                                    DealerQRConfirmRequest(
                                                        dealerId = scannedDealerId,
                                                        beneficiaryId = loggedInUserId,
                                                        oldKitReturned = confirmOldKitReturned,
                                                        brushReceived = confirmBrushReceived,
                                                        pasteReceived = confirmPasteReceived,
                                                        iecReceived = confirmIecReceived
                                                    )
                                                )

                                                if (response.isSuccessful) {
                                                    currentScreen = "kit_received"
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        response.errorBody()?.string() ?: "Kit confirmation failed",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    e.message ?: "QR processing failed",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
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
                                    id = loggedInUserId,
                                    name = loggedInUserName,
                                    oralHealthScore = 0,
                                    riskLevel = "Pending",
                                    lastScan = "Never",
                                    imageResId = R.drawable.user
                                )
                                fetchLatestReportForMember(loggedInUserId) {
                                    currentScreen = "user_health_profile"
                                }
                            },
                            onKitsClick = { currentScreen = "family_kit_hub" },
                            onLearnClick = { currentScreen = "education_hub" },
                            onConsultClick = { currentScreen = "consult" },
                            onAnalysisOptionClick = { currentScreen = "instant_analysis" },
                            weeklyTargetProgress = brushesThisWeek,
                            completedSessions = completedSessions,
                            isVerified = isPdsLinked,
                            userName = loggedInUserName
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
                                    onViewAppointmentDetails = { currentScreen = "consult" },
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
                            onAddMemberClick = { currentScreen = "add_family_member" },
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
                            familyMembers = familyMembersList,
                            isLoading = familyMembersLoading,
                            scrollState = familyMembersScrollState,
                            onRefresh = {
                                fetchFamilyMembers(loggedInUserId)
                            }
                        )

                        "add_family_member" -> AddFamilyMemberScreen(
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

                        "family_profiles" -> FamilyHealthProfilesScreen(
                            onBackClick = { currentScreen = "home" },
                            onViewProfileClick = { member ->
                                selectedFamilyMember = member
                                fetchLatestReportForMember(member.id) {
                                    currentScreen = "member_health_status"
                                }
                            }
                        )

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
                                    onViewAppointmentDetails = { currentScreen = "consult" },
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
                                }
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
                                latestMemberReport?.analysisResult
                            } else {
                                aiAnalysisResult
                            }

                            val reportImageUri = if (selectedFamilyMember?.id == latestMemberReport?.memberId) {
                                latestMemberReport?.imageUri?.let { Uri.parse(it) }
                            } else {
                                selectedImageUri
                            }

                            LastAIScanReportScreen(
                                member = selectedFamilyMember ?: defaultMember,
                                imageUri = reportImageUri,
                                analysisResult = analysisResult,
                                lastScanDate = latestMemberReport?.scanDate
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

                        "appointment_booking" -> {
                            selectedClinic?.let { clinic ->
                                AppointmentBookingScreen(
                                    clinic = clinic,
                                    onBackClick = { currentScreen = "consult" },
                                    onConfirmBooking = { date, time ->
                                        selectedAppointmentDate = date
                                        selectedAppointmentTime = time
                                        currentScreen = "appointment_details"
                                    }
                                )
                            }
                        }

                        "appointment_details" -> {
                            selectedClinic?.let { clinic ->
                                AppointmentDetailsScreen(
                                    userId = loggedInUserId,
                                    clinic = clinic,
                                    date = selectedAppointmentDate,
                                    time = selectedAppointmentTime,
                                    familyMembers = familyMembersList.toList(),
                                    onBackClick = { currentScreen = "appointment_booking" },
                                    onConfirmAppointment = {
                                        currentScreen = "appointment_confirmation"
                                    }
                                )
                            }
                        }

                        "appointment_confirmation" -> {
                            AppointmentConfirmationScreen(
                                clinicName = selectedClinic?.clinicName ?: "Clinic",
                                appointmentDate = selectedAppointmentDate,
                                appointmentTime = selectedAppointmentTime,
                                onBackClick = { currentScreen = "consult" },
                                onHomeClick = { currentScreen = "home" }
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
                                                    lastScan = latestMemberReport?.scanDate ?: "Just Now"
                                                )
                                            } else {
                                                FamilyMember(
                                                    id = returnedMemberId,
                                                    name = memberName,
                                                    oralHealthScore = generatedScore,
                                                    riskLevel = result.riskLevel ?: "Pending",
                                                    lastScan = latestMemberReport?.scanDate ?: "Just Now",
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
                                onBackClick = { currentScreen = "home" },
                                onBrushingTechniquesClick = { currentScreen = "brushing_techniques" },
                                onCommonDentalProblemsClick = { currentScreen = "dental_problems" },
                                onPrecautionsAndCareClick = { currentScreen = "precautions_care" },
                                onDailyQuizClick = { currentScreen = "daily_quiz" },
                                onHomeClick = { currentScreen = "home" },
                                onKitsClick = { currentScreen = "family_kit_hub" },
                                onLearnClick = { currentScreen = "education_hub" },
                                onConsultClick = { currentScreen = "consult" },
                                onProfileClick = { currentScreen = "user_profile" }
                            )
                        }

                        "brushing_techniques" -> {
                            BrushingTechniquesScreen(
                                onBackClick = { currentScreen = "education_hub" },
                                onVideoClick = { video ->
                                    if (video.videoUrl.isNotEmpty()) {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.videoUrl))
                                        context.startActivity(intent)
                                    }
                                }
                            )
                        }

                        "dental_problems" -> {
                            DentalProblemsScreen(
                                onBackClick = { currentScreen = "education_hub" }
                            )
                        }

                        "precautions_care" -> {
                            PrecautionsAndCareScreen(
                                onBackClick = { currentScreen = "education_hub" }
                            )
                        }

                        "daily_quiz" -> {
                            DailyQuizScreen(
                                onBackClick = { currentScreen = "education_hub" },
                                onHomeClick = { currentScreen = "home" },
                                onKitsClick = { currentScreen = "family_kit_hub" },
                                onLearnClick = { currentScreen = "education_hub" },
                                onConsultClick = { currentScreen = "consult" },
                                onProfileClick = { currentScreen = "user_profile" }
                            )
                        }

                        "family_kit_hub" -> {
                            FamilyKitHubScreen(
                                familyMembers = familyMembersList,
                                onBackClick = { currentScreen = "home" },
                                onHomeClick = { currentScreen = "home" },
                                onLogUsageClick = { currentScreen = "monthly_usage" },
                                onConfirmKitClick = { currentScreen = "confirm_kit_receipt" },
                                onLearnClick = { currentScreen = "education_hub" },
                                onConsultClick = { currentScreen = "consult" },
                                onProfileClick = { currentScreen = "user_profile" }
                            )
                        }

                        "monthly_usage" -> {
                            LaunchedEffect(loggedInUserId, familyMembersList.size) {
                                fetchMonthlyUsageForFamily(loggedInUserId)
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
                                    dailyRecords = monthlyDailyRecords,
                                    onBackClick = { currentScreen = "family_kit_hub" }
                                )
                            }
                        }

                        "confirm_kit_receipt" -> {
                            ConfirmKitReceiptScreen(
                                onBackClick = { currentScreen = "family_kit_hub" },
                                onFinalizeClick = { oldReturned, brushChecked, pasteChecked, iecChecked ->
                                    confirmOldKitReturned = oldReturned
                                    confirmBrushReceived = brushChecked
                                    confirmPasteReceived = pasteChecked
                                    confirmIecReceived = iecChecked
                                    scannerSource = "confirm_kit_receipt"
                                    currentScreen = "qr_scanner"
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
                                onBackClick = { currentScreen = "qr_scanner" },
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
                                onBookVisitClick = { clinic ->
                                    selectedClinic = clinic
                                    currentScreen = "appointment_booking"
                                }
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
                                userName = loggedInUserName,
                                userEmail = loggedInUserEmail,
                                userPhone = loggedInUserPhone
                            )
                        }
                    }
                }
            }
        }
    }
}
