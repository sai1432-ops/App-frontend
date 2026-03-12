package com.SIMATS.digitalpds

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.digitalpds.network.DealerDashboardStats
import com.SIMATS.digitalpds.network.DealerManualDistributionRequest
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.network.StockRequestBody
import kotlinx.coroutines.launch

class DealerViewModel : ViewModel() {
    var dashboardStats by mutableStateOf<DealerDashboardStats?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var actionMessage by mutableStateOf<String?>(null)
        private set

    var stockRequestLoading by mutableStateOf(false)
        private set

    var stockRequestMessage by mutableStateOf<String?>(null)
        private set

    var stockRequestSuccess by mutableStateOf(false)
        private set

    var latestStockRequestId by mutableStateOf<String?>(null)
        private set

    fun fetchDashboardStats(dealerId: Int) {
        if (dealerId <= 0) {
            errorMessage = "Invalid Dealer ID"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getDealerDashboardStats(dealerId)
                if (response.isSuccessful) {
                    val stats = response.body()
                    if (stats != null) {
                        dashboardStats = stats
                    } else {
                        errorMessage = "Empty response from server"
                    }
                } else {
                    errorMessage = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun confirmDealerDistribution(
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
                val request = DealerManualDistributionRequest(
                    dealerId = dealerId,
                    beneficiaryId = beneficiaryId,
                    oldKitReturned = oldKitReturned,
                    brushReceived = brushReceived,
                    pasteReceived = pasteReceived,
                    iecReceived = iecReceived
                )

                val response = RetrofitClient.apiService.dealerConfirmDistribution(request)
                if (response.isSuccessful) {
                    actionMessage =
                        response.body()?.message ?: "Distribution confirmed successfully"
                    fetchDashboardStats(dealerId)
                    onSuccess()
                } else {
                    errorMessage =
                        response.errorBody()?.string() ?: "Failed to confirm distribution"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Something went wrong"
            } finally {
                isLoading = false
            }
        }
    }

    fun submitStockRequest(
        dealerId: Int,
        adultBrushQty: Int,
        childBrushQty: Int,
        pasteQty: Int,
        iecQty: Int,
        urgency: String
    ) {
        if (dealerId <= 0) {
            stockRequestMessage = "Invalid dealer session"
            stockRequestSuccess = false
            return
        }

        if (adultBrushQty + childBrushQty + pasteQty + iecQty <= 0) {
            stockRequestMessage = "Please enter at least one quantity"
            stockRequestSuccess = false
            return
        }

        viewModelScope.launch {
            stockRequestLoading = true
            stockRequestMessage = null
            stockRequestSuccess = false
            latestStockRequestId = null

            try {
                val response = RetrofitClient.apiService.requestStock(
                    StockRequestBody(
                        dealerId = dealerId,
                        adultBrushQty = adultBrushQty,
                        childBrushQty = childBrushQty,
                        pasteQty = pasteQty,
                        iecQty = iecQty,
                        urgency = urgency
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    stockRequestSuccess = true
                    stockRequestMessage =
                        body?.message ?: "Stock request submitted successfully"
                    latestStockRequestId = body?.requestGroupId ?: ""
                } else {
                    stockRequestSuccess = false
                    stockRequestMessage =
                        response.errorBody()?.string() ?: "Failed to submit stock request"
                }
            } catch (e: Exception) {
                stockRequestSuccess = false
                stockRequestMessage = e.message ?: "Something went wrong"
            } finally {
                stockRequestLoading = false
            }
        }
    }

    fun clearStockRequestMessage() {
        stockRequestMessage = null
    }

    fun resetStockRequestState() {
        stockRequestLoading = false
        stockRequestMessage = null
        stockRequestSuccess = false
        latestStockRequestId = null
    }
}