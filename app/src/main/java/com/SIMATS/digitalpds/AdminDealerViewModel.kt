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

    fun fetchDealers(token: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getDealers("Bearer $token")
                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    dealers = body.map { networkDealer ->
                        // Transform network model to UI model
                        DealerInfo(
                            id = networkDealer.id,
                            name = networkDealer.name,
                            email = networkDealer.email,
                            phone = networkDealer.phone,
                            companyName = networkDealer.companyName,
                            address = networkDealer.address,
                            city = networkDealer.city,
                            state = networkDealer.state,
                            username = networkDealer.username,
                            handle = networkDealer.username ?: "@${networkDealer.name.lowercase().replace(" ", "")}",
                            location = networkDealer.location,
                            activeStatus = networkDealer.activeStatus ?: "Active",
                            emailVerified = networkDealer.emailVerified
                        )
                    }
                } else {
                    errorMessage = when (response.code()) {
                        401 -> "Session expired. Please login again."
                        403 -> "You do not have permission to view this."
                        else -> "Error: ${response.code()} ${response.message()}"
                    }
                }


            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
    fun deleteDealer(token: String, dealerId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.apiService.adminDeleteDealer("Bearer $token", dealerId)
                if (response.isSuccessful) {
                    dealers = dealers.filterNot { it.id == dealerId }
                    onSuccess()
                } else {
                    onError("Failed to delete dealer: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("Error deleting dealer: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}
