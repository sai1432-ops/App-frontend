package com.SIMATS.digitalpds

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.digitalpds.network.AdminDashboardStats
import com.SIMATS.digitalpds.network.AdminDistributionDto
import com.SIMATS.digitalpds.network.AdminNotification
import com.SIMATS.digitalpds.network.RetrofitClient
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    var dashboardStats by mutableStateOf<AdminDashboardStats?>(null)
        private set

    var notifications by mutableStateOf<List<AdminNotification>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var distributionsList by mutableStateOf<List<AdminDistributionDto>>(emptyList())
        private set

    var isDistributionsLoading by mutableStateOf(false)
        private set

    fun fetchDashboardStats(token: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getAdminDashboardStats("Bearer $token")
                if (response.isSuccessful) {
                    val stats = response.body()
                    if (stats != null) {
                        dashboardStats = stats
                    } else {
                        errorMessage = "Empty response from server"
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No technical details available"
                    errorMessage = "Server Error: ${response.code()}\n\nTechnical Details:\n$errorBody"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchNotifications(token: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getAdminNotifications("Bearer $token")
                if (response.isSuccessful) {
                    notifications = response.body() ?: emptyList()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No technical details available"
                    errorMessage = "Server Error: ${response.code()}\n\nTechnical Details:\n$errorBody"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchAdminDistributions(token: String) {
        viewModelScope.launch {
            isDistributionsLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getAdminDistributions("Bearer $token")
                if (response.isSuccessful) {
                    distributionsList = response.body() ?: emptyList()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No technical details available"
                    errorMessage = "Server Error: ${response.code()}\n\nTechnical Details:\n$errorBody"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isDistributionsLoading = false
            }
        }
    }
}
