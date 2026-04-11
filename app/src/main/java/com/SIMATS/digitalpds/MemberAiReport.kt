package com.SIMATS.digitalpds

import com.SIMATS.digitalpds.network.AiPredictionResponse

data class MemberAiReport(
    val memberId: Int,
    val imagePath: String?,
    val aiResult: AiPredictionResponse,
    val createdAt: String,
    val riskLevel: String
)
