package com.SIMATS.digitalpds

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.digitalpds.network.AdminDashboardStats
import com.SIMATS.digitalpds.network.RetrofitClient
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    var dashboardStats by mutableStateOf<AdminDashboardStats?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchDashboardStats() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getAdminDashboardStats()
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
}