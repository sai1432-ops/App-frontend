package com.SIMATS.digitalpds

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.digitalpds.network.AdminStockRequestDto
import com.SIMATS.digitalpds.network.DispatchStockRequestBody
import com.SIMATS.digitalpds.network.RejectStockRequestBody
import com.SIMATS.digitalpds.network.RetrofitClient
import kotlinx.coroutines.launch

enum class StockRequestStatus {
    PENDING, APPROVED, DISPATCHED, DELIVERED, REJECTED
}

data class AdminStockRequest(
    val id: Int,
    val itemIds: List<Int>,
    val requestId: String,
    val dealerId: String,
    val dealerName: String,
    val location: String,
    val totalKits: Int,
    val status: StockRequestStatus,
    val requestDate: String,
    val approvedAt: String? = null,
    val rejectedAt: String? = null,
    val dispatchedAt: String? = null,
    val adminNote: String? = null,
    val courierName: String? = null,
    val trackingId: String? = null,
    val dealerAddress: String? = null,
    val dispatchCity: String? = null,
    val dispatchState: String? = null,
    val contactPhone: String? = null,
    val deliveredAt: String? = null
)

class StockViewModel : ViewModel() {
    var requests by mutableStateOf<List<AdminStockRequest>>(emptyList())
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf<String?>(null)

    fun fetchRequests(token: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.apiService.getAdminStockRequests("Bearer $token")
                if (response.isSuccessful) {
                    val rawList = response.body() ?: emptyList()
                    
                    requests = rawList.map { dto ->
                        AdminStockRequest(
                            id = dto.id,
                            itemIds = listOf(dto.id),
                            requestId = dto.requestId,
                            dealerId = dto.dealerId.toString(),
                            dealerName = dto.dealerName,
                            location = dto.location,
                            totalKits = dto.totalKits,
                            status = when (dto.status.uppercase()) {
                                "APPROVED" -> StockRequestStatus.APPROVED
                                "DISPATCHED" -> StockRequestStatus.DISPATCHED
                                "DELIVERED" -> StockRequestStatus.DELIVERED
                                "REJECTED" -> StockRequestStatus.REJECTED
                                else -> StockRequestStatus.PENDING
                            },
                            requestDate = dto.requestDate,
                            approvedAt = dto.approvedAt,
                            rejectedAt = dto.rejectedAt,
                            dispatchedAt = dto.dispatchedAt,
                            adminNote = dto.adminNote,
                            courierName = dto.courierName,
                            trackingId = dto.trackingId,
                            dealerAddress = dto.dealerAddress,
                            dispatchCity = dto.dispatchCity,
                            dispatchState = dto.dispatchState,
                            contactPhone = dto.contactPhone,
                            deliveredAt = dto.deliveredAt
                        )
                    }.sortedByDescending { it.requestDate }
                } else {
                    message = "Failed to load stock requests"
                }
            } catch (e: Exception) {
                message = e.message ?: "Something went wrong"
            } finally {
                isLoading = false
            }
        }
    }

    fun approveRequest(token: String, group: AdminStockRequest, adminNote: String? = null, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            var allSuccess = true
            val body = mapOf("admin_note" to (adminNote ?: ""))
            for (id in group.itemIds) {
                try {
                    val response = RetrofitClient.apiService.approveStockRequest("Bearer $token", id, body)
                    if (!response.isSuccessful) allSuccess = false
                } catch (e: Exception) {
                    allSuccess = false
                }
            }
            
            if (allSuccess) {
                message = "Request group approved"
                fetchRequests(token)
                onDone()
            } else {
                message = "Failed to approve some items in the request"
            }
        }
    }

    fun dispatchRequest(
        token: String,
        group: AdminStockRequest,
        courierName: String,
        trackingId: String,
        adminNote: String? = null,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            var allSuccess = true

            val body = DispatchStockRequestBody(
                courierName = courierName,
                trackingId = trackingId,
                adminNote = adminNote
            )

            for (id in group.itemIds) {
                try {
                    val response = RetrofitClient.apiService.dispatchStockRequest("Bearer $token", id, body)
                    if (!response.isSuccessful) allSuccess = false
                } catch (e: Exception) {
                    allSuccess = false
                }
            }

            if (allSuccess) {
                message = "Request group dispatched"
                fetchRequests(token)
                onDone()
            } else {
                message = "Failed to dispatch some items"
            }
        }
    }

    fun rejectRequest(token: String, group: AdminStockRequest, reason: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            var allSuccess = true
            for (id in group.itemIds) {
                try {
                    val response = RetrofitClient.apiService.rejectStockRequest(
                        "Bearer $token",
                        id,
                        RejectStockRequestBody(reason.ifBlank { null })
                    )
                    if (!response.isSuccessful) allSuccess = false
                } catch (e: Exception) {
                    allSuccess = false
                }
            }
            
            if (allSuccess) {
                message = "Request group rejected"
                fetchRequests(token)
                onDone()
            } else {
                message = "Failed to reject some items"
            }
        }
    }

    fun clearMessage() {
        message = null
    }
}
