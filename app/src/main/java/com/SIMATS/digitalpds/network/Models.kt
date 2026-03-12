package com.SIMATS.digitalpds.network

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val message: String? = null,
    val error: String? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("dealer_id") val dealerId: Int? = null,
    @SerializedName("admin_id") val adminId: Int? = null,
    @SerializedName("access_token") val token: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    @SerializedName("pds_card_no") val pdsCardNo: String? = null,
    @SerializedName("pds_verified") val pdsVerified: Boolean? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    @SerializedName("company_name") val companyName: String? = null
)

data class ProfileUpdateRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val password: String? = null,
    @SerializedName("company_name") val companyName: String? = null
)

data class StockItem(
    val id: Int? = null,
    @SerializedName("dealer_id") val dealerId: Int? = null,
    @SerializedName("item_name") val itemName: String,
    val quantity: Int? = null,
    @SerializedName("requested_quantity") val requestedQuantity: Int? = null,
    val status: String? = null,
    @SerializedName("requested_at") val requestedAt: String? = null
)

data class StockAddRequest(
    @SerializedName("dealer_id") val dealerId: Int,
    @SerializedName("item_name") val itemName: String,
    val quantity: Int
)

data class StockRequestBody(
    @SerializedName("dealer_id") val dealerId: Int,
    @SerializedName("adult_brush_qty") val adultBrushQty: Int,
    @SerializedName("child_brush_qty") val childBrushQty: Int,
    @SerializedName("paste_qty") val pasteQty: Int,
    @SerializedName("iec_qty") val iecQty: Int,
    val urgency: String
)

data class StockRequestSubmitResponse(
    val message: String? = null,
    @SerializedName("request_group_id") val requestGroupId: String? = null
)

data class AdminStockRequestDto(
    val id: Int,
    @SerializedName("request_id") val requestId: String,
    @SerializedName("dealer_id") val dealerId: Int,
    @SerializedName("dealer_name") val dealerName: String,
    val location: String,
    @SerializedName("kit_type") val kitType: String,
    val quantity: String,
    val status: String,
    @SerializedName("request_date") val requestDate: String,
    @SerializedName("approved_at") val approvedAt: String? = null,
    @SerializedName("rejected_at") val rejectedAt: String? = null,
    @SerializedName("dispatched_at") val dispatchedAt: String? = null,
    @SerializedName("admin_note") val adminNote: String? = null,
    @SerializedName("courier_name") val courierName: String? = null,
    @SerializedName("tracking_id") val trackingId: String? = null
)

data class RejectStockRequestBody(
    val reason: String? = null
)

data class FamilyMemberResponse(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("member_name") val memberName: String,
    val age: Int,
    val relation: String,
    @SerializedName("brushing_target") val brushingTarget: Int,
    @SerializedName("weekly_brush_count") val weeklyBrushCount: Int
)

data class FamilyMemberRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("member_name") val memberName: String,
    val age: Int,
    val relation: String,
    @SerializedName("brushing_target") val brushingTarget: Int = 14
)

data class BrushCountRequest(
    @SerializedName("weekly_brush_count") val weeklyBrushCount: Int
)

data class ApiMessageResponse(
    val message: String? = null,
    val error: String? = null
)

data class CheckinRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("member_id") val memberId: Int?,
    val session: String
)

data class CheckinResponse(
    val message: String,
    val session: String,
    val date: String
)

data class WeeklyProgressResponse(
    @SerializedName("week_start") val weekStart: String,
    @SerializedName("week_end") val weekEnd: String,
    @SerializedName("total_completed") val totalCompleted: Int,
    @SerializedName("total_possible") val totalPossible: Int,
    val sessions: List<BrushingSessionItem>
)

data class MonthlyProgressResponse(
    @SerializedName("month_start") val monthStart: String,
    @SerializedName("month_end") val monthEnd: String,
    @SerializedName("total_completed") val totalCompleted: Int,
    @SerializedName("total_possible") val totalPossible: Int,
    val sessions: List<BrushingSessionItem>
)

data class BrushingSessionItem(
    val date: String,
    val morning: Boolean,
    val evening: Boolean
)

data class ClinicResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("clinic_name")
    val clinicName: String,

    @SerializedName("address")
    val address: String = "",

    @SerializedName("district")
    val district: String = "",

    @SerializedName("contact_number")
    val contactNumber: String? = null,

    @SerializedName("latitude")
    val latitude: Double? = null,

    @SerializedName("longitude")
    val longitude: Double? = null,

    @SerializedName("booking_available")
    val bookingAvailable: Boolean = false,

    @SerializedName("distance_km")
    val distanceKm: Double? = null,

    @SerializedName("website")
    val website: String? = null,

    @SerializedName("google_maps_uri")
    val googleMapsUri: String? = null
)

data class ClinicRequest(
    @SerializedName("clinic_name") val clinicName: String,
    val address: String,
    val district: String,
    @SerializedName("contact_number") val contactNumber: String
)

data class BookAppointmentRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("member_id") val memberId: Int? = null,
    @SerializedName("clinic_id") val clinicId: Int,
    @SerializedName("appointment_date") val appointmentDate: String,
    @SerializedName("time_slot") val timeSlot: String
)

data class AppointmentResponse(
    val id: Int,
    @SerializedName("clinic_id") val clinicId: Int,
    @SerializedName("clinic_name") val clinicName: String? = null,
    val address: String? = null,
    val district: String? = null,
    @SerializedName("member_id") val memberId: Int? = null,
    @SerializedName("appointment_date") val appointmentDate: String,
    @SerializedName("time_slot") val timeSlot: String,
    val status: String
)

data class TeethReportResponse(
    val id: Int? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("member_id") val memberId: Int? = null,
    @SerializedName("image_path") val imagePath: String,
    @SerializedName("ai_result") val aiResult: String,
    @SerializedName("risk_level") val riskLevel: String,
    @SerializedName("created_at") val createdAt: String? = null
)

data class AiPredictionResponse(
    val message: String,
    @SerializedName("reportId") val reportId: Int? = null,
    @SerializedName("riskLevel") val riskLevel: String? = null,
    val detections: List<AiDetection> = emptyList()
)

data class AiDetection(
    @SerializedName("class") val detectedClass: String,
    val confidence: Float,
    val bbox: List<Float>
)

data class AdminDashboardStats(
    @SerializedName("totalDealers") val totalDealers: String,
    @SerializedName("totalDealersChange") val totalDealersChange: String,
    @SerializedName("isDealersPositive") val isDealersPositive: Boolean,
    @SerializedName("activeBeneficiaries") val activeBeneficiaries: String,
    @SerializedName("activeBeneficiariesChange") val activeBeneficiariesChange: String,
    @SerializedName("isBeneficiariesPositive") val isBeneficiariesPositive: Boolean,
    @SerializedName("totalDistributions") val totalDistributions: String,
    @SerializedName("totalDistributionsChange") val totalDistributionsChange: String,
    @SerializedName("isDistributionsPositive") val isDistributionsPositive: Boolean,
    @SerializedName("returnRate") val returnRate: String,
    @SerializedName("returnRateChange") val returnRateChange: String,
    @SerializedName("isReturnRatePositive") val isReturnRatePositive: Boolean,
    @SerializedName("kitGivenPercentage") val kitGivenPercentage: Int,
    @SerializedName("kitReturnedPercentage") val kitReturnedPercentage: Int,
    @SerializedName("kitPendingPercentage") val kitPendingPercentage: Int
)

data class DealerDashboardStats(
    @SerializedName("todayDistributions") val todayDistributions: String,
    @SerializedName("performancePercentage") val performancePercentage: Int,
    @SerializedName("totalKits") val totalKits: String,
    @SerializedName("totalKitsChange") val totalKitsChange: String,
    @SerializedName("isTotalKitsPositive") val isTotalKitsPositive: Boolean,
    @SerializedName("distributedKits") val distributedKits: String,
    @SerializedName("distributedKitsChange") val distributedKitsChange: String,
    @SerializedName("isDistributedPositive") val isDistributedPositive: Boolean,
    @SerializedName("remainingKits") val remainingKits: String,
    @SerializedName("remainingKitsChange") val remainingKitsChange: String,
    @SerializedName("isRemainingPositive") val isRemainingPositive: Boolean,
    @SerializedName("returnedKits") val returnedKits: String,
    @SerializedName("returnedKitsChange") val returnedKitsChange: String,
    @SerializedName("isReturnedPositive") val isReturnedPositive: Boolean,
    @SerializedName("itemCounts") val itemCounts: List<ItemCount>,
    @SerializedName("recentTransactions") val recentTransactions: List<RecentTransaction>
)

data class ItemCount(
    val name: String,
    val count: String
)

data class RecentTransaction(
    val name: String,
    val details: String,
    val quantity: String
)

data class Beneficiary(
    val name: String,
    @SerializedName("ration_id") val rationId: String,
    @SerializedName("household_id") val householdId: String,
    @SerializedName("is_active") val isActive: Boolean
)

data class LinkPdsRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("pds_card_no") val pdsCardNo: String
)

data class KitGenerateRequest(
    @SerializedName("dealer_id") val dealerId: Int,
    @SerializedName("beneficiary_id") val beneficiaryId: Int
)

data class KitGenerateResponse(
    val message: String,
    @SerializedName("kit_unique_id") val kitUniqueId: String? = null,
    val expiry: String? = null
)

data class KitConfirmRequest(
    @SerializedName("kit_unique_id") val kitUniqueId: String
)

data class DealerQRConfirmRequest(
    @SerializedName("dealer_id") val dealerId: Int,
    @SerializedName("beneficiary_id") val beneficiaryId: Int,
    @SerializedName("old_kit_returned") val oldKitReturned: Boolean,
    @SerializedName("brush_received") val brushReceived: Boolean,
    @SerializedName("paste_received") val pasteReceived: Boolean,
    @SerializedName("iec_received") val iecReceived: Boolean
)

data class DealerManualDistributionRequest(
    @SerializedName("dealer_id") val dealerId: Int,
    @SerializedName("beneficiary_id") val beneficiaryId: Int,
    @SerializedName("old_kit_returned") val oldKitReturned: Boolean,
    @SerializedName("brush_received") val brushReceived: Boolean,
    @SerializedName("paste_received") val pasteReceived: Boolean,
    @SerializedName("iec_received") val iecReceived: Boolean
)

data class DealerQRResponse(
    val type: String,
    @SerializedName("dealer_id") val dealerId: Int,
    @SerializedName("dealer_name") val dealerName: String
)

data class AdminBeneficiaryResponse(
    val id: Int,
    val name: String,
    @SerializedName("pds_card_no") val pdsCardNo: String?,
    @SerializedName("pds_verified") val pdsVerified: Boolean,
    @SerializedName("pds_linked_at") val pdsLinkedAt: String?,
    val phone: String?,
    val email: String?
)