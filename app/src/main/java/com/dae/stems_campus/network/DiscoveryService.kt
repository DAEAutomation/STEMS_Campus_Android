package com.dae.stems_campus.network

import com.dae.stems_campus.data.model.APIResponse
import com.dae.stems_campus.data.model.SchoolHostModel
import retrofit2.http.GET

interface DiscoveryService {

    /**
     * 取得學校網址清單（不需要 token，固定 host）
     */
    @GET("api/school-hosts")
    suspend fun getSchoolHosts(): APIResponse.ApiResponse<List<SchoolHostModel>>
}
