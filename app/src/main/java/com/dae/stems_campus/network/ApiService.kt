package com.dae.stems_campus.network
import com.dae.stems_campus.data.model.APIResponse
import com.dae.stems_campus.data.model.AccountModel
import com.dae.stems_campus.data.model.HistoryModel
import com.dae.stems_campus.data.model.LoginModel
import com.dae.stems_campus.data.model.NotificationsModel
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.model.RefundModel
import com.dae.stems_campus.data.model.ScanModel
import com.dae.stems_campus.data.model.ServiceCheckModel
import com.dae.stems_campus.data.model.TokenModel
import com.dae.stems_campus.data.model.TopUpModel
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    /**
     * service-check
     */
    @POST("api/app/service-check")
    suspend fun serviceCheck(
        @Body request: ServiceCheckModel.ServiceCheckRequest
    ): APIResponse.ApiResponse<ServiceCheckModel.ServiceCheckData>


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
     * 發送驗證碼
     */
    @POST("api/app/auth/send-code")
    suspend fun registerSendEmail(
        @Body request: AccountModel.SendEmailRequest
    ): APIResponse.ApiResponse<Unit>


    /**
     * 驗證驗證碼
     */
    @POST("api/app/auth/verify-code")
    suspend fun registerVerifyCode(
        @Body request: AccountModel.VerifyCodeRequest
    ): APIResponse.ApiResponse<AccountModel.VerifyCodeData>


    /**
     * 學號驗證
     */
    @POST("api/app/auth/lookup-student")
    suspend fun getStudentInfo(
        @Body request: AccountModel.GetStudentInfoRequest
    ): APIResponse.ApiResponse<AccountModel.StudentData>


    /**
     * 註冊
     */
    @POST("api/app/auth/register-student")
    suspend fun registerInfo(
        @Body request: AccountModel.RegisterRequest
    ): APIResponse.ApiResponse<Unit>


    /**
     * 重設密碼
     */
    @POST("api/app/auth/reset-password")
    suspend fun resetPassword(
        @Body request: AccountModel.ResetPasswordRequest
    ): APIResponse.ApiResponse<Unit>


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


    /**
     * 錢包儲值紀錄
     */
    @POST("api/app/records/wallet")
    suspend fun walletHistory(
        @Body request: HistoryModel.HistoryRequest
    ): APIResponse.ApiResponse<List<HistoryModel.WalletHistory>>


    /**
     * 時數發給紀錄
     */
    @POST("api/app/records/hours")
    suspend fun hoursHistory(
        @Body request: HistoryModel.HistoryRequest
    ): APIResponse.ApiResponse<List<HistoryModel.HoursHistory>>


    /**
     * 教室使用紀錄
     */
    @POST("api/app/records/classroom")
    suspend fun classroomHistory(
        @Body request: HistoryModel.HistoryRequest
    ): APIResponse.ApiResponse<List<HistoryModel.ClassroomHistory>>


    /**
     * 宿舍使用紀錄
     */
    @POST("api/app/records/dormitory")
    suspend fun dormitoryHistory(
        @Body request: HistoryModel.HistoryRequest
    ): APIResponse.ApiResponse<List<HistoryModel.DormitoryHistory>>

    /**
     * 退款紀錄
     */
    @POST("api/app/records/refunds")
    suspend fun refundHistory(
        @Body request: HistoryModel.HistoryRequest
    ): APIResponse.ApiResponse<List<HistoryModel.RefundHistory>>


    /**
     * 申請退款
     */
    @POST("api/app/refund/request")
    suspend fun refundRequest(
        @Body request: RefundModel.RefundRequest
    ): APIResponse.ApiResponse<RefundModel.RefundRequestData>


    /**
     * 進行退款-悠遊卡
     */
    @POST("api/app/refund/execute")
    suspend fun startRefund(
        @Body request: RefundModel.StartRefundRequest
    ): APIResponse.ApiResponse<RefundModel.StartRefundData>


    /**
     * 退款取消
     */
    @POST("api/app/refund/cancel")
    suspend fun cancelRefund(
        @Body request: RefundModel.CancelRefundRequest
    ): APIResponse.ApiResponse<RefundModel.CancelRefundData>


    /**
     * 退款狀態查詢
     */
    @POST("api/app/refund/status")
    suspend fun refundStatus(
    ): APIResponse.ApiResponse<RefundModel.RefundStatusData>


    /**
     * 通知
     */
    @POST("api/app/notifications")
    suspend fun getNotifications(
    ): APIResponse.ApiResponse<List<NotificationsModel.NotificationsData>>
}