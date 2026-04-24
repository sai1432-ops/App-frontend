package com.SIMATS.digitalpds

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.digitalpds.network.AdminProfileResponse
import com.SIMATS.digitalpds.network.ChangePasswordRequest
import com.SIMATS.digitalpds.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class AdminProfileViewModel : ViewModel() {
    var adminProfile by mutableStateOf<AdminProfileResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Profile updates
    fun fetchAdminProfile(token: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getAdminProfile("Bearer $token")
                if (response.isSuccessful) {
                    adminProfile = response.body()
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

    fun updateAdminProfile(
        token: String,
        name: String,
        phone: String,
        location: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
                val locationBody = location.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = RetrofitClient.apiService.updateAdminProfile(
                    "Bearer $token",
                    nameBody,
                    phoneBody,
                    locationBody
                )

                if (response.isSuccessful) {
                    // Locally update the profile so UI reflects instantly
                    adminProfile = adminProfile?.copy(
                        name = name,
                        phone = phone,
                        officeLocation = location
                    )
                    onSuccess()
                } else {
                    onError("Failed to update profile: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("Error updating profile: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun changeAdminPassword(
        token: String,
        request: ChangePasswordRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.apiService.changeAdminPassword("Bearer $token", request)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Failed to change password: ${response.errorBody()?.string() ?: response.message()}")
                }
            } catch (e: Exception) {
                onError("Error changing password: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}
