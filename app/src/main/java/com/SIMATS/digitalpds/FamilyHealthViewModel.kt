package com.SIMATS.digitalpds

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.digitalpds.network.KitReceivedSummary
import com.SIMATS.digitalpds.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FamilyHealthViewModel : ViewModel() {

    private val _memberReports = MutableStateFlow<Map<Int, MemberAiReport>>(emptyMap())
    val memberReports: StateFlow<Map<Int, MemberAiReport>> = _memberReports

    private val _distributionHistory = MutableStateFlow<List<KitReceivedSummary>>(emptyList())
    val distributionHistory: StateFlow<List<KitReceivedSummary>> = _distributionHistory.asStateFlow()

    private val _isLoadingHistory = MutableStateFlow(false)
    val isLoadingHistory: StateFlow<Boolean> = _isLoadingHistory.asStateFlow()

    fun fetchDistributionHistory(token: String, userId: Int) {
        viewModelScope.launch {
            _isLoadingHistory.value = true
            try {
                val response = RetrofitClient.apiService.getUserDistributionHistory("Bearer $token", userId)
                if (response.isSuccessful) {
                    _distributionHistory.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingHistory.value = false
            }
        }
    }

    fun saveOrUpdateMemberReport(
        memberId: Int,
        imageUri: Uri?,
        result: com.SIMATS.digitalpds.network.AiPredictionResponse
    ) {
        val riskLevel = result.riskLevel ?: "LOW"

        val createdAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val report = MemberAiReport(
            memberId = memberId,
            imagePath = imageUri?.toString(),
            aiResult = result,
            createdAt = createdAt,
            riskLevel = riskLevel
        )

        _memberReports.update { current ->
            current + (memberId to report)
        }
    }

    fun getMemberReport(memberId: Int): MemberAiReport? {
        return _memberReports.value[memberId]
    }
}
