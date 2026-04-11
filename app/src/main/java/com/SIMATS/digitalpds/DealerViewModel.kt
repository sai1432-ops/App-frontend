package com.SIMATS.digitalpds

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.digitalpds.network.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class DealerViewModel : ViewModel() {
    var dashboardStats by mutableStateOf<DealerDashboardStats?>(null)
        private set

    var stockItems by mutableStateOf<List<StockItem>>(emptyList())
        private set

    var verifiedBeneficiary by mutableStateOf<VerifyBeneficiaryResponse?>(null)
        private set

    var dealerHousehold by mutableStateOf<DealerHouseholdResponse?>(null)
        private set

    var distributionHistory by mutableStateOf<List<DealerDistributionHistoryResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isStockLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var actionMessage by mutableStateOf<String?>(null)
        private set

    var stockRequestLoading by mutableStateOf(false)
        private set

    var stockRequestMessage by mutableStateOf<String?>(null)

    var stockRequestSuccess by mutableStateOf(false)
        private set

    var latestStockRequestId by mutableStateOf<String?>(null)
        private set

    var dealerProfile by mutableStateOf<DealerProfileResponse?>(null)
        private set

    fun fetchDealerProfile(dealerId: Int, token: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getDealerProfile("Bearer $token", dealerId)
                if (response.isSuccessful) {
                    dealerProfile = response.body()
                } else {
                    errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun updateDealerProfile(
        dealerId: Int,
        token: String,
        request: ProfileUpdateRequest,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.updateDealerProfile("Bearer $token", dealerId, request)
                if (response.isSuccessful) {
                    actionMessage = response.body()?.message ?: "Profile updated successfully"
                    fetchDealerProfile(dealerId, token)
                    onSuccess()
                } else {
                    errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchDashboardStats(dealerId: Int, token: String) {
        if (dealerId <= 0) {
            errorMessage = "Invalid Dealer ID"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getDealerDashboardStats("Bearer $token", dealerId)
                if (response.isSuccessful) {
                    dashboardStats = response.body()
                } else {
                    val errorText = response.errorBody()?.string()
                    errorMessage = parseErrorMessage(errorText, response.code())
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchStockList(dealerId: Int, token: String) {
        if (dealerId <= 0) return

        viewModelScope.launch {
            isStockLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.viewDealerStock("Bearer $token", dealerId)
                if (response.isSuccessful) {
                    stockItems = response.body() ?: emptyList()
                } else {
                    errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isStockLoading = false
            }
        }
    }

    fun verifyBeneficiary(dealerId: Int, token: String, pdsCardNo: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.verifyBeneficiary(
                    "Bearer $token",
                    VerifyBeneficiaryRequest(dealerId, pdsCardNo)
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.valid == true) {
                        verifiedBeneficiary = body
                        onSuccess()
                    } else {
                        errorMessage = body?.message ?: "Invalid PDS Card"
                    }
                } else {
                    errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchDealerHousehold(userId: Int, token: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getDealerHousehold("Bearer $token", userId)
                if (response.isSuccessful) {
                    dealerHousehold = response.body()
                } else {
                    errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchDistributionHistory(dealerId: Int, token: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getDealerDistributionHistory("Bearer $token", dealerId)
                if (response.isSuccessful) {
                    distributionHistory = response.body() ?: emptyList()
                } else {
                    errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun completeDistribution(
        dealerId: Int,
        token: String,
        beneficiaryId: Int,
        brushReceived: Int,
        pasteReceived: Int,
        iecReceived: Int,
        oldKitReturned: Boolean,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = DealerManualDistributionRequest(
                    dealerId = dealerId,
                    beneficiaryId = beneficiaryId,
                    brushReceived = brushReceived,
                    pasteReceived = pasteReceived,
                    iecReceived = iecReceived,
                    oldKitReturned = oldKitReturned
                )
                val response = RetrofitClient.apiService.dealerConfirmDistribution("Bearer $token", request)
                if (response.isSuccessful) {
                    actionMessage = response.body()?.message ?: "Distribution successful"
                    fetchDashboardStats(dealerId, token)
                    onSuccess()
                } else {
                    errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun registerHousehold(
        context: android.content.Context,
        token: String,
        dealerId: Int,
        name: String,
        email: String?,
        phone: String,
        age: String,
        gender: String,
        education: String,
        employment: String,
        address: String,
        pdsCardNo: String,
        members: List<com.SIMATS.digitalpds.network.FamilyMemberInput> = emptyList(),
        pdsFrontUri: android.net.Uri? = null,
        pdsBackUri: android.net.Uri? = null,
        onSuccess: (Int, String?) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            actionMessage = null
            try {
                val dealerIdPart = dealerId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPart = email?.toRequestBody("text/plain".toMediaTypeOrNull())
                val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())
                val agePart = age.toRequestBody("text/plain".toMediaTypeOrNull())
                val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())
                val educationPart = education.toRequestBody("text/plain".toMediaTypeOrNull())
                val employmentPart = employment.toRequestBody("text/plain".toMediaTypeOrNull())
                val addressPart = address.toRequestBody("text/plain".toMediaTypeOrNull())
                val pdsCardNoPart = pdsCardNo.toRequestBody("text/plain".toMediaTypeOrNull())
                val membersPart = com.google.gson.Gson().toJson(members).toRequestBody("text/plain".toMediaTypeOrNull())

                val frontPart = pdsFrontUri?.let { uri ->
                    uriToFile(context, uri, "front.jpg")?.let { file ->
                        MultipartBody.Part.createFormData("pds_front", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                    }
                }
                val backPart = pdsBackUri?.let { uri ->
                    uriToFile(context, uri, "back.jpg")?.let { file ->
                        MultipartBody.Part.createFormData("pds_back", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                    }
                }

                val response = RetrofitClient.apiService.registerDealerHousehold(
                    "Bearer $token", dealerIdPart, namePart, emailPart, phonePart, agePart, genderPart, educationPart, employmentPart, addressPart, pdsCardNoPart, membersPart, frontPart, backPart
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    actionMessage = body?.message ?: "Household registered successfully"
                    val createdUserId = body?.userId ?: 0
                    val receivedHouseholdId = body?.householdId
                    onSuccess(createdUserId, receivedHouseholdId)
                } else {
                    errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Something went wrong"
            } finally {
                isLoading = false
            }
        }
    }

    private fun uriToFile(context: android.content.Context, uri: android.net.Uri, fileName: String): java.io.File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = java.io.File(context.cacheDir, fileName)
            val outputStream = java.io.FileOutputStream(tempFile)
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

    fun submitStockRequest(
        dealerId: Int,
        token: String,
        totalKits: Int,
        urgency: String
    ) {
        if (dealerId <= 0) {
            stockRequestMessage = "Invalid dealer session"
            stockRequestSuccess = false
            return
        }

        viewModelScope.launch {
            stockRequestLoading = true
            stockRequestMessage = null
            stockRequestSuccess = false

            try {
                val response = RetrofitClient.apiService.requestStock(
                    "Bearer $token",
                    StockRequestBody(
                        dealerId = dealerId,
                        totalKits = totalKits,
                        urgency = urgency
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    stockRequestSuccess = true
                    stockRequestMessage = body?.message ?: "Stock request submitted successfully"
                    latestStockRequestId = body?.requestGroupId ?: ""
                } else {
                    stockRequestSuccess = false
                    stockRequestMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                stockRequestSuccess = false
                stockRequestMessage = e.message ?: "Something went wrong"
            } finally {
                stockRequestLoading = false
            }
        }
    }

    private fun parseErrorMessage(errorBody: String?, code: Int): String {
        return try {
            val json = JSONObject(errorBody ?: "{}")
            json.optString("message")
                .ifBlank { json.optString("error") }
                .ifBlank { "Error: $code" }
        } catch (e: Exception) {
            "Error: $code"
        }
    }

    fun resetStockRequestState() {
        stockRequestLoading = false
        stockRequestMessage = null
        stockRequestSuccess = false
        latestStockRequestId = null
    }

    fun clearStockRequestMessage() {
        stockRequestMessage = null
    }

    fun clearVerifiedBeneficiary() {
        verifiedBeneficiary = null
    }

    fun resetState() {
        dashboardStats = null
        stockItems = emptyList()
        verifiedBeneficiary = null
        dealerHousehold = null
        distributionHistory = emptyList()
        isLoading = false
        isStockLoading = false
        errorMessage = null
        actionMessage = null
        stockRequestLoading = false
        stockRequestMessage = null
        stockRequestSuccess = false
        latestStockRequestId = null
        dealerProfile = null
    }

    fun changePassword(token: String, request: ChangePasswordRequest, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.changeDealerPassword("Bearer $token", request)
                if (response.isSuccessful) {
                    onComplete(true, response.body()?.message ?: "Password changed successfully")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = try {
                        JSONObject(errorBody ?: "{}").getString("error")
                    } catch (e: Exception) {
                        "Failed to change password"
                    }
                    onComplete(false, message)
                }
            } catch (e: Exception) {
                onComplete(false, "Error: ${e.message}")
            }
        }
    }
}
