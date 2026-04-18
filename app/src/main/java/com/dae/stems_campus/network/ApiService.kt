package com.dae.stems_campus.network
import com.dae.stems_campus.data.model.APIResponse
import com.dae.stems_campus.data.model.LoginModel
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.model.ScanModel
import com.dae.stems_campus.data.model.TokenModel
import com.dae.stems_campus.data.model.TopUpModel
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    /**
     * 登入
     */
    @POST("api/app/auth/login")
    suspend fun login(
        @Body request: LoginModel.LoginRequest
    ): APIResponse.ApiResponse<LoginModel.LoginData>


    /**
     * 密碼驗證
     */
    @POST("api/app/auth/verify-password")
    suspend fun verifyPassword(
        @Body request: LoginModel.VerifyRequest
    ): APIResponse.ApiResponse<LoginModel.VerifyData>


    /**
     * 登出
     */
    @POST("api/app/auth/logout")
    suspend fun logout(): APIResponse.ApiResponse<Unit>


    /**
     * 刷新 Token
     */
    @POST("api/app/auth/refresh")
    suspend fun refreshToken (
    ): APIResponse.ApiResponse<TokenModel.TokenData>



    /**
     * Profile
     */
    @POST("api/app/user/profile")
    suspend fun fetchProfileData (
    ): APIResponse.ApiResponse<ProfileModel.ProfileData>


    /**
     * Scan 掃描取得資訊
     */
    @POST("api/app/billing/scan")
    suspend fun scanInfo(
        @Body request: ScanModel.ScanRequest
    ): APIResponse.ApiResponse<ScanModel.ScanData>


    /**
     * 啟用用電
     */
    @POST("api/app/billing/start")
    suspend fun startPower(
        @Body request: ScanModel.StartPowerRequest
    ): APIResponse.ApiResponse<ScanModel.StartPowerData>


    /**
     * 啟用用電(學生)
     */
    @POST("api/app/billing/start")
    suspend fun startPowerByStudent(
        @Body request: ScanModel.StartPowerByStudentRequest
    ): APIResponse.ApiResponse<ScanModel.StartPowerData>


    /**
     * 停止用電
     */
    @POST("api/app/billing/stop")
    suspend fun stopPower(
        @Body request: ScanModel.StopPowerRequest
    ): APIResponse.ApiResponse<ScanModel.StopPowerData>


    /**
     * 使用中裝置內容
     */
    @POST("api/app/billing/active")
    suspend fun usingDeviceDetail(
        @Body request: ProfileModel.UsingDeviceDetailRequest
    ): APIResponse.ApiResponse<ProfileModel.ActiveSession>


    /**
     * 冷氣控制
     */
    @POST("api/app/billing/control")
    suspend fun control(
        @Body request: ScanModel.ControlRequest
    ): APIResponse.ApiResponse<ScanModel.ControlData>


    /**
     * 掃描儲值機
     */
    @POST("api/app/deposit/scan")
    suspend fun scanDeposit(
        @Body request: TopUpModel.TopUpScanDepositRequest
    ): APIResponse.ApiResponse<TopUpModel.ScanDepositData>


    /**
     * 發起加值
     */
    @POST("api/app/deposit")
    suspend fun startTopUp(
        @Body request: TopUpModel.StartTopUpRequest
    ): APIResponse.ApiResponse<TopUpModel.StartTopUpData>
}