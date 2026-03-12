package com.SIMATS.digitalpds

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FamilyHealthViewModel : ViewModel() {

    private val _memberReports = MutableStateFlow<Map<Int, MemberAiReport>>(emptyMap())
    val memberReports: StateFlow<Map<Int, MemberAiReport>> = _memberReports

    fun saveOrUpdateMemberReport(
        memberId: Int,
        imageUri: Uri?,
        result: com.SIMATS.digitalpds.network.AiPredictionResponse
    ) {
        val riskLevel = result.riskLevel ?: "LOW"
        val score = when (riskLevel.uppercase()) {
            "LOW" -> 85
            "MEDIUM" -> 60
            "HIGH" -> 35
            else -> 70
        }

        val scanDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val report = MemberAiReport(
            memberId = memberId,
            imageUri = imageUri?.toString(),
            analysisResult = result,
            scanDate = scanDate,
            oralHealthScore = score,
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
