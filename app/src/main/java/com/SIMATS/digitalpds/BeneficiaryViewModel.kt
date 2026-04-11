package com.SIMATS.digitalpds

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.digitalpds.network.Beneficiary
import com.SIMATS.digitalpds.network.DealerQRConfirmRequest
import com.SIMATS.digitalpds.network.RetrofitClient
import kotlinx.coroutines.launch

class BeneficiaryViewModel : ViewModel() {
    var beneficiaries by mutableStateOf<List<Beneficiary>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var actionMessage by mutableStateOf<String?>(null)
        private set

    fun fetchBeneficiaries(token: String, dealerId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.apiService.getDealerBeneficiaries(formattedToken, dealerId)
                if (response.isSuccessful) {
                    beneficiaries = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Something went wrong"
            } finally {
                isLoading = false
            }
        }
    }

    fun confirmKitByDealerQR(
        token: String,
        dealerQrValue: String,
        beneficiaryId: Int,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            actionMessage = null
            try {
                val request = DealerQRConfirmRequest(
                    dealer_qr_value = dealerQrValue,
                    beneficiaryId = beneficiaryId
                )

                val response = RetrofitClient.apiService.confirmKitByDealerQR(token, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    actionMessage = body?.message ?: "Kit created successfully"
                    // If the response contains a dealerId, refresh the list
                    body?.dealerId?.let { fetchBeneficiaries(token, it) }
                    onSuccess()
                } else {
                    errorMessage = response.errorBody()?.string() ?: "Failed to confirm kit"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Something went wrong"
            } finally {
                isLoading = false
            }
        }
    }
}
