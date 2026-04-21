package com.dae.stems_campus.data.repository

import android.util.Log
import com.dae.stems_campus.data.model.SchoolHostModel
import com.dae.stems_campus.network.DiscoveryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DiscoveryRepository @Inject constructor(
    private val discoveryService: DiscoveryService
) {

    suspend fun getSchoolHosts(): BaseRepository.Result<List<SchoolHostModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = discoveryService.getSchoolHosts()
                if (response.success == true && response.data != null) {
                    BaseRepository.Result.Success(response.data)
                } else {
                    BaseRepository.Result.Error(response.message ?: "請求失敗")
                }
            } catch (e: Exception) {
                Log.e("DAE_Develop", "Discovery API 錯誤", e)
                BaseRepository.Result.Error(e.message ?: "網路錯誤")
            }
        }
    }
}
