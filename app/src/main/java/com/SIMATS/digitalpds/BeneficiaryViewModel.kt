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

    fun fetchBeneficiaries(dealerId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getDealerBeneficiaries(dealerId)
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
        dealerId: Int,
        beneficiaryId: Int,
        oldKitReturned: Boolean,
        brushReceived: Boolean,
        pasteReceived: Boolean,
        iecReceived: Boolean,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            actionMessage = null
            try {
                val request = DealerQRConfirmRequest(
                    dealerId = dealerId,
                    beneficiaryId = beneficiaryId,
                    oldKitReturned = oldKitReturned,
                    brushReceived = brushReceived,
                    pasteReceived = pasteReceived,
                    iecReceived = iecReceived
                )

                val response = RetrofitClient.apiService.confirmKitByDealerQR(request)
                if (response.isSuccessful) {
                    actionMessage = response.body()?.message ?: "Kit confirmed successfully"
                    fetchBeneficiaries(dealerId)
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