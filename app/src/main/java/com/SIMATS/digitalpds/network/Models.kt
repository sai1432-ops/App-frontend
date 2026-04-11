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
    @SerializedName("pds_verified") val pdsVerified: Boolean? = null,
    @SerializedName("dealer_qr_value") val dealerQrValue: String? = null,
    @SerializedName("dealer_qr_image") val dealer_qr_image: String? = null,
    @SerializedName("profile_image") val profileImage: String? = null,
    @SerializedName("created_by_type") val createdByType: String? = null
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
    val age: String? = null,
    val gender: String? = null,
    val education: String? = null,
    val employment: String? = null,
    val address: String? = null,
    @SerializedName("company_name") val companyName: String? = null,
    val username: String? = null,
    val city: String? = null,
    val state: String? = null,
    val location: String? = null,
    val is_active: Boolean = true
)

data class ProfileUpdateRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val password: String? = null,
    val company_name: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val username: String? = null,
    val age: Int? = null,

    val gender: String? = null,
    val education: String? = null,
    val employment: String? = null,
    val location: String? = null,
    val is_active: Boolean? = null
)

data class UpdateDealerRequest(
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    @SerializedName("company_name") val companyName: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val location: String? = null,
    val is_active: Boolean? = null
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
    @SerializedName("total_kits") val totalKits: Int,
    val urgency: String = "Normal"
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
    @SerializedName("total_kits") val totalKits: Int,
    val status: String,
    @SerializedName("request_date") val requestDate: String,
    @SerializedName("approved_at") val approvedAt: String? = null,
    @SerializedName("rejected_at") val rejectedAt: String? = null,
    @SerializedName("dispatched_at") val dispatchedAt: String? = null,
    @SerializedName("admin_note") val adminNote: String? = null,
    @SerializedName("courier_name") val courierName: String? = null,
    @SerializedName("tracking_id") val trackingId: String? = null,
    @SerializedName("dealer_address") val dealerAddress: String? = null,
    @SerializedName("contact_phone") val contactPhone: String? = null,
    @SerializedName("dispatch_address") val dispatchAddress: String? = null,
    @SerializedName("dispatch_city") val dispatchCity: String? = null,
    @SerializedName("dispatch_state") val dispatchState: String? = null,
    @SerializedName("delivered_at") val deliveredAt: String? = null
)

data class ConfirmDeliveryRequest(
    @SerializedName("dealer_id") val dealerId: Int
)

data class RejectStockRequestBody(
    val reason: String? = null
)

data class DispatchStockRequestBody(
    @SerializedName("courier_name") val courierName: String,
    @SerializedName("tracking_id") val trackingId: String,
    @SerializedName("admin_note") val adminNote: String? = null
)

data class ApproveStockRequestBody(
    @SerializedName("admin_note") val adminNote: String? = null
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
    val error: String? = null,
    @SerializedName("dev_code") val devCode: String? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("dealer_id") val dealerId: Int? = null,
    @SerializedName("distribution_id") val distributionId: Int? = null,
    @SerializedName("updated_count") val updatedCount: Int? = null,
    @SerializedName("dealer_name") val dealerName: String? = null,
    @SerializedName("pds_card_no") val pdsCardNo: String? = null,
    @SerializedName("pds_verified") val pdsVerified: Boolean? = null,
    @SerializedName("created_by_type") val createdByType: String? = null,
    @SerializedName("next_step") val nextStep: String? = null,
    @SerializedName("household_id") val householdId: String? = null,
    val category: String? = null
)

data class CheckinRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("member_id") val memberId: Int?,
    val session: String
)

data class CheckinResponse(
    val message: String? = null,
    val error: String? = null,
    val session: String? = null,
    val date: String? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("member_id") val memberId: Int? = null
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
    @SerializedName("id") val id: Int,
    @SerializedName("clinic_name") val clinicName: String,
    @SerializedName("website") val website: String? = null
)

data class ClinicRequest(
    @SerializedName("clinic_name") val clinicName: String,
    @SerializedName("website") val website: String? = null
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
    @SerializedName("totalDealers") val totalDealers: String? = null,
    @SerializedName("totalDealersChange") val totalDealersChange: String? = null,
    @SerializedName("isDealersPositive") val isDealersPositive: Boolean? = null,
    @SerializedName("activeBeneficiaries") val activeBeneficiaries: String? = null,
    @SerializedName("activeBeneficiariesChange") val activeBeneficiariesChange: String? = null,
    @SerializedName("isBeneficiariesPositive") val isBeneficiariesPositive: Boolean? = null,
    @SerializedName("totalDistributions") val totalDistributions: String? = null,
    @SerializedName("totalDistributionsChange") val totalDistributionsChange: String? = null,
    @SerializedName("isDistributionsPositive") val isDistributionsPositive: Boolean? = null,
    @SerializedName("returnRate") val returnRate: String? = null,
    @SerializedName("returnRateChange") val returnRateChange: String? = null,
    @SerializedName("isReturnRatePositive") val isReturnRatePositive: Boolean? = null,
    @SerializedName("kitGivenPercentage") val kitGivenPercentage: Int? = null,
    @SerializedName("kitReturnedPercentage") val kitReturnedPercentage: Int? = null,
    @SerializedName("kitPendingPercentage") val kitPendingPercentage: Int? = null,
    @SerializedName("distributionTrends") val distributionTrends: List<TrendData>? = null,
    @SerializedName("dealerTrends") val dealerTrends: List<TrendData>? = null
)

data class TrendData(
    val label: String? = null,
    val value: Float? = null
)

data class DealerDashboardStats(
    @SerializedName("todayDistributions") val todayDistributions: String,
    @SerializedName("performancePercentage") val performancePercentage: Int,
    @SerializedName("totalKits") val totalKits: String,
    @SerializedName("distributedKits") val distributedKits: String,
    @SerializedName("remainingKits") val remainingKits: String,
    @SerializedName("returnedKits") val returnedKits: String,
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
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String,
    @SerializedName("ration_id") val rationId: String,
    @SerializedName("household_id") val householdId: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_by_type") val createdByType: String? = null,
    @SerializedName("dealer_id") val dealerId: Int? = null
)

data class LinkPdsRequest(
    @SerializedName("userId") val userId: Int,
    @SerializedName("identityCardNo") val identityCardNo: String
)

data class KitGenerateRequest(
    @SerializedName("dealer_id") val dealerId: Int,
    @SerializedName("beneficiary_id") val beneficiaryId: Int
)

data class KitGenerateResponse(
    val message: String,
    @SerializedName("kit_unique_id") val kit_unique_id: String? = null,
    val expiry: String? = null
)

data class KitConfirmRequest(
    @SerializedName("kit_unique_id")
    val kit_unique_id: String,

    @SerializedName("brush_received")
    val brushReceived: Int,

    @SerializedName("paste_received")
    val pasteReceived: Int,

    @SerializedName("iec_received")
    val iecReceived: Int,

    @SerializedName("old_kit_returned")
    val oldKitReturned: Boolean
)

data class KitConfirmData(
    @SerializedName("kit_unique_id") val kit_unique_id: String,
    @SerializedName("status") val status: String,
    @SerializedName("brush_received") val brushReceived: Int? = null,
    @SerializedName("paste_received") val pasteReceived: Int? = null,
    @SerializedName("iec_received") val iecReceived: Int? = null,
    @SerializedName("old_kit_returned") val oldKitReturned: Boolean,
    @SerializedName("old_kit_return_status") val old_kit_return_status: String? = null,
    @SerializedName("show_red_alert") val show_red_alert: Boolean = false,
    @SerializedName("confirmed_at") val confirmed_at: String? = null
)

data class KitConfirmResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: KitConfirmData
)

data class KitReceivedResponse(
    @SerializedName("kit_unique_id")
    val kit_unique_id: String? = null,

    @SerializedName("beneficiary")
    val beneficiary: BeneficiaryKitInfo? = null,

    @SerializedName("dealer")
    val dealer: DealerKitInfo? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("confirmation_mode")
    val confirmation_mode: String? = null,

    @SerializedName("brush_received")
    val brushReceived: Int = 0,

    @SerializedName("paste_received")
    val pasteReceived: Int = 0,

    @SerializedName("iec_received")
    val iecReceived: Int = 0,

    @SerializedName("old_kit_returned")
    val oldKitReturned: Boolean = false,

    @SerializedName("old_kit_return_status")
    val old_kit_return_status: String? = null,

    @SerializedName("show_red_alert") val show_red_alert: Boolean = false,

    @SerializedName("red_alert_message")
    val red_alert_message: String? = null,

    @SerializedName("confirmed_at")
    val confirmed_at: String? = null,

    @SerializedName("created_at")
    val created_at: String? = null
)

data class KitReceivedSummary(
    @SerializedName("kit_unique_id") val kit_unique_id: String,
    @SerializedName("beneficiary_id") val beneficiaryId: Int,
    @SerializedName("beneficiary_name") val beneficiary_name: String? = null,
    @SerializedName("brush_received") val brushReceived: Int = 0,
    @SerializedName("paste_received") val pasteReceived: Int = 0,
    @SerializedName("iec_received") val iecReceived: Int = 0,
    @SerializedName("old_kit_returned") val oldKitReturned: Boolean = false,
    @SerializedName("old_kit_return_status") val old_kit_return_status: String? = null,
    @SerializedName("show_red_alert") val show_red_alert: Boolean = false,
    @SerializedName("status") val status: String? = null,
    @SerializedName("confirmed_at") val confirmed_at: String? = null
)

data class BeneficiaryKitInfo(
    val id: Int?,
    val name: String?,
    val phone: String?,
    val email: String?
)

data class DealerKitInfo(
    val id: Int?,
    val name: String?,
    val phone: String?,
    @SerializedName("company_name") val companyName: String?,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    @SerializedName("contact_person") val contactPerson: String? = null,
    @SerializedName("contact_phone") val contactPhone: String? = null
)

data class DealerQRConfirmRequest(
    @SerializedName("dealer_qr_value")
    val dealer_qr_value: String,

    @SerializedName("beneficiary_id")
    val beneficiaryId: Int
)

data class DealerManualDistributionRequest(
    @SerializedName("dealer_id") val dealerId: Int,
    @SerializedName("beneficiary_id") val beneficiaryId: Int,
    @SerializedName("brush_received") val brushReceived: Int,
    @SerializedName("paste_received") val pasteReceived: Int,
    @SerializedName("iec_received") val iecReceived: Int,
    @SerializedName("old_kit_returned") val oldKitReturned: Boolean
)

data class DealerConfirmDistributionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("distribution_id") val distributionId: Int,
    @SerializedName("kit_unique_id") val kit_unique_id: String?,
    val status: String
)

data class ConfirmKitByDealerQrResponse(
    val message: String,
    @SerializedName("dealer_id") val dealerId: Int? = null,
    @SerializedName("distribution_id") val distributionId: Int? = null,
    @SerializedName("kit_unique_id") val kit_unique_id: String? = null,
    val status: String? = null
)

data class DealerQRResponse(
    val type: String,
    @SerializedName("dealer_id") val dealerId: Int,
    @SerializedName("dealer_name") val dealerName: String,
    @SerializedName("dealer_qr_value") val dealerQrValue: String,
    @SerializedName("dealer_qr_image") val dealer_qr_image: String
)

data class AdminBeneficiaryResponse(
    val id: Int,
    val name: String,
    @SerializedName("pds_card_no") val pdsCardNo: String?,
    @SerializedName("pds_verified") val pdsVerified: Boolean,
    @SerializedName("pds_linked_at") val pdsLinkedAt: String?,
    val phone: String?,
    val email: String?,
    @SerializedName("created_by_type") val createdByType: String? = null
)

data class DealerHouseholdRequest(
    @SerializedName("dealer_id") val dealerId: Int,
    val name: String,
    val email: String? = null,
    val phone: String,
    @SerializedName("pds_card_no") val pdsCardNo: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val password: String,
    @SerializedName("confirm_password") val confirmPassword: String
)

data class RegistrationOtpRequest(
    val email: String
)

data class VerifyRegistrationOtpRequest(
    val email: String,
    val otp: String
)


data class VerifyDealerQRRequest(
    @SerializedName("dealer_qr_value") val dealer_qr_value: String
)

data class VerifyDealerQRResponse(
    val valid: Boolean,
    val message: String,
    val dealer: DealerNetworkInfo?
)

data class DealerNetworkInfo(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    @SerializedName("company_name") val companyName: String?,
    @SerializedName("dealer_qr_value") val dealerQrValue: String? = null,
    @SerializedName("dealer_qr_image") val dealer_qr_image: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    @SerializedName("contact_person") val contactPerson: String? = null,
    @SerializedName("contact_phone") val contactPhone: String? = null,
    val username: String? = null,
    val location: String? = null,
    @SerializedName("email_verified") val emailVerified: Boolean = false,
    @SerializedName("active_status") val activeStatus: String? = null
)

data class DealerProfileResponse(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    @SerializedName("company_name") val companyName: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    @SerializedName("contact_person") val contactPerson: String? = null,
    @SerializedName("contact_phone") val contactPhone: String? = null,
    val username: String? = null,
    @SerializedName("dealer_qr_value") val dealerQrValue: String? = null,
    @SerializedName("dealer_qr_image") val dealerQrImage: String? = null,
    @SerializedName("profile_image") val profileImage: String? = null
)

data class DealerProfileImageResponse(
    @SerializedName("message") val message: String,
    @SerializedName("profile_image") val profileImage: String
)

data class VerifyBeneficiaryRequest(
    @SerializedName("dealer_id") val dealerId: Int,
    @SerializedName("pds_card_no") val pdsCardNo: String
)

data class VerifyBeneficiaryResponse(
    val valid: Boolean,
    val linked: Boolean,
    val message: String,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("beneficiary_id") val beneficiaryId: Int? = null,
    @SerializedName("household_id") val householdId: String? = null,
    val name: String? = null,
    @SerializedName("pds_card_no") val pdsCardNo: String? = null
)

data class DealerHouseholdResponse(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("household_id") val householdId: String,
    @SerializedName("head_name") val headName: String,
    @SerializedName("pds_card_no") val pdsCardNo: String?,
    val category: String,
    val members: List<FamilyMemberResponse>
)

data class DealerDistributionHistoryResponse(
    val id: Int,
    @SerializedName("beneficiary_name") val beneficiary_name: String,
    val time: String,
    val date: String,
    val category: String,
    @SerializedName("items_summary") val items_summary: String,
    @SerializedName("old_kit_returned") val oldKitReturned: Boolean
)

data class Location(
    val id: Int,
    @SerializedName("location_name") val location_name: String,
    @SerializedName("dealer_id") val dealer_id: Int,
    @SerializedName("dealer_name") val dealer_name: String?
)

data class SelectLocationRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("location_id") val location_id: Int
)

data class SelectDealerRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("dealer_id") val dealer_id: Int
)

data class AdminBeneficiaryDetailResponse(
    val id: Int,
    val name: String,
    val phone: String,
    val email: String?,
    val age: Int?,
    val gender: String?,
    val education: String?,
    val employment: String?,
    val location: String, // mapped to location_name
    @SerializedName("pds_card_no") val pdsCardNo: String, 
    @SerializedName("household_id") val householdId: String? = null,
    val category: String? = null,
    val address: String, 
    val status: String, // mapped to pds_verified equivalent
    val createdAt: String,
    val createdByRole: String,
    val createdById: String,
    val createdByName: String,
    val history: List<KitHistoryItemResponse>,
    val familyMembers: List<FamilyMemberResponse>,
    @SerializedName("pds_card_front") val pdsCardFront: String? = null,
    @SerializedName("pds_card_back") val pdsCardBack: String? = null,
    @SerializedName("profile_image") val profileImage: String? = null
)

data class AdminUpdateBeneficiaryRequest(
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val education: String? = null,
    val employment: String? = null,
    val address: String? = null,
    val pds_card_no: String? = null,
    val status: String? = null,
    val category: String? = null,
    @SerializedName("admin_note") val adminNote: String? = null
)


data class KitHistoryItemResponse(
    val id: String,
    val beneficiaryId: Int,
    val kitName: String,
    val kitType: String,
    val quantity: String,
    val status: String,
    val date: String,
    val givenBy: String,
    val trackingId: String? = null,
    val returnDate: String? = null,
    val returnReason: String? = null,
    val notes: String? = null
)

data class AdminNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: AdminNotificationType,
    val isRead: Boolean = false
)

enum class AdminNotificationType {
    STOCK_REQUEST, NEW_DEALER, ALERT, GENERAL
}

data class AdminProfileResponse(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    @SerializedName("office_location") val officeLocation: String
)

data class ChangePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String
)

data class AdminDistributionDto(
    val id: Int,
    @SerializedName("kit_unique_id") val kitUniqueId: String,
    @SerializedName("beneficiary_name") val beneficiaryName: String,
    @SerializedName("pds_card_no") val pdsCardNo: String?,
    @SerializedName("dealer_name") val dealerName: String,
    @SerializedName("confirmed_at") val confirmedAt: String?,
    @SerializedName("brush_received") val brushReceived: Int,
    @SerializedName("paste_received") val pasteReceived: Int,
    @SerializedName("iec_received") val iecReceived: Int
)

data class FamilyMemberInput(
    val name: String,
    val age: String,
    val relation: String
)

data class AdminCreateBeneficiaryRequest(
    val name: String,
    @SerializedName("family_head") val familyHead: String,
    @SerializedName("ration_card_no") val rationCardNo: String,
    val address: String,
    val phone: String,
    @SerializedName("dealer_id") val dealerId: Int,
    val members: List<FamilyMemberInput> = emptyList()
)
