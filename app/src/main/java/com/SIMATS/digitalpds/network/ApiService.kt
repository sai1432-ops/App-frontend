package com.SIMATS.digitalpds.network

import com.SIMATS.digitalpds.DealerInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("user/register")
    suspend fun userRegister(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("user/login")
    suspend fun userLogin(
        @Body request: LoginRequest
    ): Response<AuthResponse>

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

    @PUT("user/update-profile/{userId}")
    suspend fun updateUserProfile(
        @Path("userId") userId: Int,
        @Body request: ProfileUpdateRequest
    ): Response<ApiMessageResponse>

    @PUT("dealer/update-profile/{dealerId}")
    suspend fun updateDealerProfile(
        @Path("dealerId") dealerId: Int,
        @Body request: ProfileUpdateRequest
    ): Response<ApiMessageResponse>

    @PUT("admin/update-profile/{adminId}")
    suspend fun updateAdminProfile(
        @Path("adminId") adminId: Int,
        @Body request: ProfileUpdateRequest
    ): Response<ApiMessageResponse>

    @POST("api/family-members")
    suspend fun addFamilyMember(
        @Body request: FamilyMemberRequest
    ): Response<ApiMessageResponse>

    @GET("api/family-members/{userId}")
    suspend fun getFamilyMembers(
        @Path("userId") userId: Int
    ): Response<List<FamilyMemberResponse>>

    @PUT("api/family-members/{memberId}")
    suspend fun updateFamilyMember(
        @Path("memberId") memberId: Int,
        @Body request: FamilyMemberRequest
    ): Response<ApiMessageResponse>

    @DELETE("api/family-members/{memberId}")
    suspend fun deleteFamilyMember(
        @Path("memberId") memberId: Int,
        @Query("user_id") userId: Int
    ): Response<ApiMessageResponse>

    @PUT("user/update-brush-count/{memberId}")
    suspend fun updateBrushCount(
        @Path("memberId") memberId: Int,
        @Body request: BrushCountRequest
    ): Response<ApiMessageResponse>

    @POST("user/checkin")
    suspend fun brushingCheckIn(
        @Body request: CheckinRequest
    ): Response<CheckinResponse>

    @GET("api/user/weekly-progress/{userId}")
    suspend fun getWeeklyProgress(
        @Path("userId") userId: Int,
        @Query("member_id") memberId: Int? = null
    ): Response<WeeklyProgressResponse>

    @GET("api/user/monthly-usage/{userId}")
    suspend fun getMonthlyUsage(
        @Path("userId") userId: Int,
        @Query("member_id") memberId: Int? = null,
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null
    ): Response<MonthlyProgressResponse>

    @POST("api/user/link-pds")
    suspend fun linkPdsCard(
        @Body request: LinkPdsRequest
    ): Response<ApiMessageResponse>

    @POST("admin/add-clinic")
    suspend fun addClinic(
        @Body request: ClinicRequest
    ): Response<ApiMessageResponse>

    @GET("user/view-clinics")
    suspend fun viewClinics(): Response<List<ClinicResponse>>

    @GET("user/nearby-clinics")
    suspend fun getNearbyClinics(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("max_km") maxKm: Double = 15.0
    ): Response<List<ClinicResponse>>

    @POST("user/book-appointment")
    suspend fun bookAppointment(
        @Body request: BookAppointmentRequest
    ): Response<ApiMessageResponse>

    @GET("user/view-appointments/{userId}")
    suspend fun getAppointments(
        @Path("userId") userId: Int
    ): Response<List<AppointmentResponse>>

    // Keep this only if you later add this backend route.
    // Otherwise you can delete it safely.
    @GET("user/upcoming-appointment/{userId}")
    suspend fun getUpcomingAppointment(
        @Path("userId") userId: Int,
        @Query("member_id") memberId: Int? = null
    ): Response<AppointmentResponse?>

    @Multipart
    @POST("user/teeth-ai")
    suspend fun analyzeTeeth(
        @Part image: MultipartBody.Part,
        @Part("user_id") userId: RequestBody,
        @Part("member_id") memberId: RequestBody?
    ): Response<AiPredictionResponse>

    @GET("user/view-teeth-reports/{userId}")
    suspend fun getTeethReports(
        @Path("userId") userId: Int
    ): Response<List<TeethReportResponse>>

    @POST("user/add-teeth-report")
    suspend fun addTeethReport(
        @Body request: Map<String, @JvmSuppressWildcards Any?>
    ): Response<ApiMessageResponse>

    @POST("dealer/add-stock")
    suspend fun addDealerStock(
        @Body request: StockAddRequest
    ): Response<ApiMessageResponse>

    @GET("dealer/view-stock/{dealerId}")
    suspend fun viewDealerStock(
        @Path("dealerId") dealerId: Int
    ): Response<List<StockItem>>

    @PUT("dealer/update-stock")
    suspend fun updateDealerStock(
        @Body request: StockAddRequest
    ): Response<ApiMessageResponse>

    @POST("dealer/request-stock")
    suspend fun requestStock(
        @Body request: StockRequestBody
    ): Response<StockRequestSubmitResponse>

    @GET("admin/stock-requests")
    suspend fun getAdminStockRequests(): Response<List<AdminStockRequestDto>>

    @PUT("admin/approve-stock/{requestId}")
    suspend fun approveStockRequest(
        @Path("requestId") requestId: Int
    ): Response<ApiMessageResponse>

    @PUT("admin/dispatch-stock/{requestId}")
    suspend fun dispatchStockRequest(
        @Path("requestId") requestId: Int
    ): Response<ApiMessageResponse>

    @PUT("admin/reject-stock/{requestId}")
    suspend fun rejectStockRequest(
        @Path("requestId") requestId: Int,
        @Body request: RejectStockRequestBody
    ): Response<ApiMessageResponse>

    @POST("dealer/generate-kit")
    suspend fun generateKit(
        @Body request: KitGenerateRequest
    ): Response<KitGenerateResponse>

    @POST("user/confirm-kit")
    suspend fun confirmKit(
        @Body request: KitConfirmRequest
    ): Response<ApiMessageResponse>

    @POST("user/confirm-kit-by-dealer-qr")
    suspend fun confirmKitByDealerQR(
        @Body request: DealerQRConfirmRequest
    ): Response<ApiMessageResponse>

    @POST("dealer/confirm-distribution")
    suspend fun dealerConfirmDistribution(
        @Body request: DealerManualDistributionRequest
    ): Response<ApiMessageResponse>

    @GET("dealer/qr/{dealerId}")
    suspend fun getDealerQRData(
        @Path("dealerId") dealerId: Int
    ): Response<DealerQRResponse>

    @GET("admin/get-dealers")
    suspend fun getDealers(): Response<List<DealerInfo>>

    @GET("admin/dashboard-stats")
    suspend fun getAdminDashboardStats(): Response<AdminDashboardStats>

    @GET("dealer/dashboard-stats/{dealerId}")
    suspend fun getDealerDashboardStats(
        @Path("dealerId") dealerId: Int
    ): Response<DealerDashboardStats>

    @GET("admin/beneficiaries")
    suspend fun getAdminBeneficiaries(): Response<List<AdminBeneficiaryResponse>>

    @GET("dealer/beneficiaries/{dealerId}")
    suspend fun getDealerBeneficiaries(
        @Path("dealerId") dealerId: Int
    ): Response<List<Beneficiary>>
}