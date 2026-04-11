package com.SIMATS.digitalpds.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import com.google.gson.annotations.SerializedName

interface ApiService {

    @Multipart
    @POST("user/register")
    suspend fun userRegister(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("age") age: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part("education") education: RequestBody?,
        @Part("employment") employment: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part profile_image: MultipartBody.Part?
    ): Response<AuthResponse>

    @POST("user/send-registration-otp")
    suspend fun sendRegistrationOtp(
        @Body request: RegistrationOtpRequest
    ): Response<ApiMessageResponse>

    @POST("user/verify-registration-otp")
    suspend fun verifyRegistrationOtp(
        @Body request: VerifyRegistrationOtpRequest
    ): Response<ApiMessageResponse>

    @POST("user/login")
    suspend fun userLogin(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("user/check-pds/{pdsCardNo}")
    suspend fun checkPdsRegistration(
        @Path("pdsCardNo") pdsCardNo: String
    ): Response<ApiMessageResponse>

    @POST("dealer/login")
    suspend fun dealerLogin(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("admin/login")
    suspend fun adminLogin(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("admin/register")
    suspend fun adminRegister(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("dealer/register")
    suspend fun dealerRegister(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("dealer/send-registration-otp")
    suspend fun sendDealerRegistrationOtp(
        @Body request: RegistrationOtpRequest
    ): Response<ApiMessageResponse>

    @POST("dealer/verify-registration-otp")
    suspend fun verifyDealerRegistrationOtp(
        @Body request: VerifyRegistrationOtpRequest
    ): Response<ApiMessageResponse>

    @Multipart
    @POST("dealer/register-household")
    suspend fun registerDealerHousehold(
        @Header("Authorization") token: String,
        @Part("dealer_id") dealerId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody?,
        @Part("phone") phone: RequestBody,
        @Part("age") age: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part("education") education: RequestBody?,
        @Part("employment") employment: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("pds_card_no") pdsCardNo: RequestBody,
        @Part("members") members: RequestBody,
        @Part pds_front: MultipartBody.Part?,
        @Part pds_back: MultipartBody.Part?
    ): Response<ApiMessageResponse>

    @PUT("user/update-profile/{userId}")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Body request: ProfileUpdateRequest
    ): Response<ApiMessageResponse>

    @PUT("user/change-password")
    suspend fun changeUserPassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ApiMessageResponse>

    @GET("dealer/profile/{dealerId}")
    suspend fun getDealerProfile(
        @Header("Authorization") token: String,
        @Path("dealerId") dealerId: Int
    ): Response<DealerProfileResponse>

    @PUT("dealer/update-profile/{dealerId}")
    suspend fun updateDealerProfile(
        @Header("Authorization") token: String,
        @Path("dealerId") dealerId: Int,
        @Body request: ProfileUpdateRequest
    ): Response<ApiMessageResponse>

    @GET("admin/profile")
    suspend fun getAdminProfile(
        @Header("Authorization") token: String
    ): Response<AdminProfileResponse>

    @Multipart
    @PUT("admin/update-profile")
    suspend fun updateAdminProfile(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("office_location") officeLocation: RequestBody
    ): Response<ApiMessageResponse>

    @PUT("admin/change-password")
    suspend fun changeAdminPassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ApiMessageResponse>

    @POST("api/family-members")
    suspend fun addFamilyMember(
        @Header("Authorization") token: String,
        @Body request: FamilyMemberRequest
    ): Response<ApiMessageResponse>

    @GET("api/family-members/{userId}")
    suspend fun getFamilyMembers(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<List<FamilyMemberResponse>>

    @PUT("api/family-members/{memberId}")
    suspend fun updateFamilyMember(
        @Header("Authorization") token: String,
        @Path("memberId") memberId: Int,
        @Body request: FamilyMemberRequest
    ): Response<ApiMessageResponse>

    @DELETE("api/family-members/{memberId}")
    suspend fun deleteFamilyMember(
        @Header("Authorization") token: String,
        @Path("memberId") memberId: Int,
        @Query("user_id") userId: Int
    ): Response<ApiMessageResponse>

    @PUT("user/update-brush-count/{memberId}")
    suspend fun updateBrushCount(
        @Header("Authorization") token: String,
        @Path("memberId") memberId: Int,
        @Body request: BrushCountRequest
    ): Response<ApiMessageResponse>

    @POST("user/checkin")
    suspend fun brushingCheckIn(
        @Header("Authorization") token: String,
        @Body request: CheckinRequest
    ): Response<CheckinResponse>

    @GET("api/user/weekly-progress/{userId}")
    suspend fun getWeeklyProgress(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Query("member_id") memberId: Int? = null
    ): Response<WeeklyProgressResponse>

    @GET("api/user/monthly-usage/{userId}")
    suspend fun getMonthlyUsage(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Query("member_id") memberId: Int? = null,
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null
    ): Response<MonthlyProgressResponse>

    @POST("user/link-identity")
    suspend fun linkPdsCard(
        @Header("Authorization") token: String,
        @Body request: LinkPdsRequest
    ): Response<ApiMessageResponse>

    @POST("admin/add-clinic")
    suspend fun addClinic(
        @Header("Authorization") token: String,
        @Body request: ClinicRequest
    ): Response<ApiMessageResponse>

    @DELETE("admin/delete-clinic/{clinicId}")
    suspend fun deleteClinic(
        @Header("Authorization") token: String,
        @Path("clinicId") clinicId: Int
    ): Response<ApiMessageResponse>

    @GET("user/view-clinics")
    suspend fun viewClinics(
        @Header("Authorization") token: String
    ): Response<List<ClinicResponse>>


    @Multipart
    @POST("user/teeth-ai")
    suspend fun analyzeTeeth(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("user_id") userId: RequestBody,
        @Part("member_id") memberId: RequestBody
    ): Response<AiPredictionResponse>

    @GET("user/view-teeth-reports/{userId}")
    suspend fun getTeethReports(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<List<TeethReportResponse>>

    @GET("api/member/{memberId}/latest-report")
    suspend fun getLatestMemberReport(
        @Header("Authorization") token: String,
        @Path("memberId") memberId: Int,
        @Query("user_id") userId: Int
    ): Response<TeethReportResponse>

    @POST("user/add-teeth-report")
    suspend fun addTeethReport(
        @Header("Authorization") token: String,
        @Body request: Map<String, @JvmSuppressWildcards Any?>
    ): Response<ApiMessageResponse>

    @POST("dealer/add-stock")
    suspend fun addDealerStock(
        @Header("Authorization") token: String,
        @Body request: StockAddRequest
    ): Response<ApiMessageResponse>

    @GET("dealer/view-stock/{dealerId}")
    suspend fun viewDealerStock(
        @Header("Authorization") token: String,
        @Path("dealerId") dealerId: Int
    ): Response<List<StockItem>>

    @PUT("dealer/update-stock")
    suspend fun updateDealerStock(
        @Header("Authorization") token: String,
        @Body request: StockAddRequest
    ): Response<ApiMessageResponse>

    @POST("dealer/request-stock")
    suspend fun requestStock(
        @Header("Authorization") token: String,
        @Body request: StockRequestBody
    ): Response<StockRequestSubmitResponse>

    @GET("admin/stock-requests")
    suspend fun getAdminStockRequests(
        @Header("Authorization") token: String
    ): Response<List<AdminStockRequestDto>>

    @PUT("admin/approve-stock/{requestId}")
    suspend fun approveStockRequest(
        @Header("Authorization") token: String,
        @Path("requestId") requestId: Int,
        @Body request: Map<String, String>
    ): Response<ApiMessageResponse>

    @PUT("admin/dispatch-stock/{requestId}")
    suspend fun dispatchStockRequest(
        @Header("Authorization") token: String,
        @Path("requestId") requestId: Int,
        @Body body: DispatchStockRequestBody
    ): Response<ApiMessageResponse>

    @PUT("admin/reject-stock/{requestId}")
    suspend fun rejectStockRequest(
        @Header("Authorization") token: String,
        @Path("requestId") requestId: Int,
        @Body request: RejectStockRequestBody
    ): Response<ApiMessageResponse>

    @PUT("dealer/confirm-delivery/{requestGroupId}")
    suspend fun confirmDelivery(
        @Header("Authorization") token: String,
        @Path("requestGroupId") requestGroupId: String,
        @Body request: Map<String, String>
    ): Response<ApiMessageResponse>

    @POST("dealer/generate-kit")
    suspend fun generateKit(
        @Header("Authorization") token: String,
        @Body request: KitGenerateRequest
    ): Response<KitGenerateResponse>

    @POST("user/confirm-kit")
    suspend fun confirmKit(
        @Header("Authorization") token: String,
        @Body request: KitConfirmRequest
    ): Response<KitConfirmResponse>

    @GET("dealer/kit-received/{kitUniqueId}")
    suspend fun getKitReceived(
        @Header("Authorization") token: String,
        @Path("kitUniqueId") kitUniqueId: String
    ): Response<KitReceivedResponse>

    @GET("dealer/kit-received-list/{dealerId}")
    suspend fun getKitReceivedList(
        @Header("Authorization") token: String,
        @Path("dealerId") dealerId: Int
    ): Response<List<KitReceivedSummary>>

    @POST("user/confirm-kit-by-dealer-qr")
    suspend fun confirmKitByDealerQR(
        @Header("Authorization") token: String,
        @Body request: DealerQRConfirmRequest
    ): Response<ConfirmKitByDealerQrResponse>

    @POST("dealer/confirm-distribution")
    suspend fun dealerConfirmDistribution(
        @Header("Authorization") token: String,
        @Body request: DealerManualDistributionRequest
    ): Response<DealerConfirmDistributionResponse>

    @GET("dealer/qr/{dealerId}")
    suspend fun getDealerQRData(
        @Header("Authorization") token: String,
        @Path("dealerId") dealerId: Int
    ): Response<DealerQRResponse>

    @POST("dealer/verify-qr")
    suspend fun verifyDealerQR(
        @Header("Authorization") token: String,
        @Body request: VerifyDealerQRRequest
    ): Response<VerifyDealerQRResponse>

    @GET("admin/get-dealers")
    suspend fun getDealers(
        @Header("Authorization") token: String
    ): Response<List<DealerNetworkInfo>>

    @GET("admin/dashboard-stats")
    suspend fun getAdminDashboardStats(
        @Header("Authorization") token: String
    ): Response<AdminDashboardStats>

    @GET("dealer/dashboard-stats/{dealerId}")
    suspend fun getDealerDashboardStats(
        @Header("Authorization") token: String,
        @Path("dealerId") dealerId: Int
    ): Response<DealerDashboardStats>

    @GET("admin/beneficiaries")
    suspend fun getAdminBeneficiaries(
        @Header("Authorization") token: String
    ): Response<List<com.SIMATS.digitalpds.AdminBeneficiary>>

    @GET("admin/beneficiary/{userId}")
    suspend fun getAdminBeneficiaryDetails(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<AdminBeneficiaryDetailResponse>

    @GET("dealer/beneficiary/{userId}")
    suspend fun getDealerBeneficiaryDetails(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<AdminBeneficiaryDetailResponse>

    @POST("admin/register-beneficiary")
    suspend fun adminRegisterBeneficiary(
        @Header("Authorization") token: String,
        @Body request: RegisterRequest
    ): Response<ApiMessageResponse>

    @Multipart
    @POST("api/admin/beneficiaries")
    suspend fun adminCreateBeneficiary(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("family_head") familyHead: RequestBody,
        @Part("email") email: RequestBody?,
        @Part("ration_card_no") rationCardNo: RequestBody,
        @Part("address") address: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("age") age: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part("education") education: RequestBody?,
        @Part("employment") employment: RequestBody?,
        @Part("dealer_id") dealerId: RequestBody,
        @Part("members") members: RequestBody,
        @Part pds_front: MultipartBody.Part?,
        @Part pds_back: MultipartBody.Part?
    ): Response<ApiMessageResponse>

    @PUT("admin/update-beneficiary/{userId}")
    suspend fun adminUpdateBeneficiary(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Body request: AdminUpdateBeneficiaryRequest
    ): Response<ApiMessageResponse>

    @DELETE("admin/delete-beneficiary/{userId}")
    suspend fun adminDeleteBeneficiary(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<ApiMessageResponse>

    @GET("dealer/{dealerId}/beneficiaries")
    suspend fun getDealerBeneficiaries(
        @Header("Authorization") token: String,
        @Path("dealerId") dealerId: Int
    ): Response<List<Beneficiary>>

    // User Password Recovery
    @POST("user/forgot-password")
    suspend fun userForgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ApiMessageResponse>

    @POST("user/reset-password/{code}")
    suspend fun userResetPassword(
        @Path("code") code: String,
        @Body request: ResetPasswordRequest
    ): Response<ApiMessageResponse>

    // Dealer Password Recovery
    @POST("dealer/forgot-password")
    suspend fun dealerForgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ApiMessageResponse>

    @POST("dealer/reset-password/{code}")
    suspend fun dealerResetPassword(
        @Path("code") code: String,
        @Body request: ResetPasswordRequest
    ): Response<ApiMessageResponse>

    @POST("admin/generate-missing-dealer-qrs")
    suspend fun generateMissingDealerQRs(
        @Header("Authorization") token: String
    ): Response<ApiMessageResponse>

    @POST("admin/regenerate-all-dealer-qrs")
    suspend fun regenerateAllDealerQRs(
        @Header("Authorization") token: String
    ): Response<ApiMessageResponse>

    @POST("dealer/verify-beneficiary")
    suspend fun verifyBeneficiary(
        @Header("Authorization") token: String,
        @Body request: VerifyBeneficiaryRequest
    ): Response<VerifyBeneficiaryResponse>

    @GET("dealer/household/{userId}")
    suspend fun getDealerHousehold(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<DealerHouseholdResponse>

    @GET("dealer/distribution-history/{dealerId}")
    suspend fun getDealerDistributionHistory(
        @Header("Authorization") token: String,
        @Path("dealerId") dealerId: Int
    ): Response<List<DealerDistributionHistoryResponse>>

    @GET("user/distribution-history/{userId}")
    suspend fun getUserDistributionHistory(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<List<KitReceivedSummary>>

    @GET("api/locations")
    suspend fun getLocations(): Response<List<Location>>

    @POST("api/user/select-location")
    suspend fun selectLocation(
        @Header("Authorization") token: String,
        @Body request: SelectLocationRequest
    ): Response<ApiMessageResponse>

    @GET("api/dealers")
    suspend fun getUserDealers(): Response<List<DealerNetworkInfo>>

    @POST("api/user/select-dealer")
    suspend fun selectDealer(
        @Header("Authorization") token: String,
        @Body request: SelectDealerRequest
    ): Response<ApiMessageResponse>

    @POST("admin/add-location")
    suspend fun addLocation(
        @Header("Authorization") token: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiMessageResponse>

    @GET("admin/notifications")
    suspend fun getAdminNotifications(
        @Header("Authorization") token: String
    ): Response<List<AdminNotification>>

    @PUT("dealer/update-profile/{dealerId}")
    suspend fun adminUpdateDealer(
        @Header("Authorization") token: String,
        @Path("dealerId") dealerId: Int,
        @Body request: ProfileUpdateRequest
    ): Response<ApiMessageResponse>

    @PUT("dealer/change-password")
    suspend fun changeDealerPassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ApiMessageResponse>

    @DELETE("admin/delete-dealer/{dealerId}")
    suspend fun adminDeleteDealer(
        @Header("Authorization") token: String,
        @Path("dealerId") dealerId: Int
    ): Response<ApiMessageResponse>

    @GET("admin/distributions")
    suspend fun getAdminDistributions(
        @Header("Authorization") token: String
    ): Response<List<AdminDistributionDto>>

    @GET("api/user/profile/{userId}")
    suspend fun getUserProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<UserProfileResponse>

    @Multipart
    @POST("api/user/upload-profile-picture/{userId}")
    suspend fun uploadUserProfilePicture(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Part profileImage: MultipartBody.Part
    ): Response<ProfilePictureUploadResponse>

    @DELETE("api/user/delete-account/{userId}")
    suspend fun deleteUserAccount(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<ApiMessageResponse>
}

data class ProfilePictureUploadResponse(
    val message: String,
    val profile_image: String?
)

data class UserProfileResponse(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val age: Int?,
    val gender: String?,
    val education: String?,
    val employment: String?,
    val address: String?,
    val pds_card_no: String?,
    val pds_verified: Boolean,
    val pds_linked_at: String?,
    val profile_image: String? = null,
    @SerializedName("created_by_type") val createdByType: String? = null,
    val dealer_id: Int? = null,
    val dealer_name: String? = null,
    val dealer_location: String? = null
)
