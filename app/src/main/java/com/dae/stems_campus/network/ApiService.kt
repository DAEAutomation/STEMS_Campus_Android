package com.dae.stems_campus.network
import com.dae.stems_campus.data.model.APIResponse
import com.dae.stems_campus.data.model.LoginModel
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.model.TokenModel
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
}