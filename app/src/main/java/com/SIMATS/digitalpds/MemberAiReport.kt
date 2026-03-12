package com.SIMATS.digitalpds

import com.SIMATS.digitalpds.network.AiPredictionResponse

data class MemberAiReport(
    val memberId: Int,
    val imageUri: String?,
    val analysisResult: AiPredictionResponse,
    val scanDate: String,
    val oralHealthScore: Int,
    val riskLevel: String
)
