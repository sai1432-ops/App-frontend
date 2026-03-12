package com.SIMATS.digitalpds

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.digitalpds.network.RetrofitClient
import kotlinx.coroutines.launch

class AdminDealerViewModel : ViewModel() {
    var dealers by mutableStateOf<List<DealerInfo>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchDealers() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getDealers()
                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    dealers = body.map {
                        it.copy(
                            handle = "@${it.name.lowercase().replace(" ", "")}",
                            location = it.companyName,
                            activeStatus = if (it.isEnabled) "Active" else "Inactive"
                        )
                    }
                } else {
                    errorMessage = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
}