package com.dae.stems_campus.data.repository


import com.dae.stems_campus.data.model.ScanModel
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.viewmodel.TokenManager
import javax.inject.Inject

open class ScanRepository @Inject constructor(apiService: ApiService, tokenManager: TokenManager): BaseRepository(apiService, tokenManager){

    suspend fun scanInfo(qrCode: String, deviceID: String): Result<ScanModel.ScanData> {
        return executeAuthenticatedRequest {
            apiService.scanInfo(ScanModel.ScanRequest(
                qr_token = qrCode,
                device_id = deviceID
            ))
        }
    }

    suspend fun startPower(aDeviceCode: String, aDeviceID: String, aSessionToken: String): Result<ScanModel.StartPowerData> {
        return executeAuthenticatedRequest {
            apiService.startPower(ScanModel.StartPowerRequest(
                device_code = aDeviceCode,
                device_id = aDeviceID,
                session_token = aSessionToken
            ))
        }
    }

    suspend fun startPowerByStudent(aDeviceCode: String, aDeviceID: String, aSessionToken: String, aAc: Boolean): Result<ScanModel.StartPowerData> {
        return executeAuthenticatedRequest {
            apiService.startPowerByStudent(ScanModel.StartPowerByStudentRequest(
                device_code = aDeviceCode,
                device_id = aDeviceID,
                session_token = aSessionToken,
                ac = aAc
            ))
        }
    }

    suspend fun stopPower(aDeviceCode: String, aDeviceID: String, aControlToken: String): Result<ScanModel.StopPowerData> {
        return executeAuthenticatedRequest {
            apiService.stopPower(ScanModel.StopPowerRequest(
                device_code = aDeviceCode,
                device_id = aDeviceID,
                control_token = aControlToken
            ))
        }
    }

    suspend fun control(aDeviceID: String, aControlToken: String, aCommand: String): Result<ScanModel.ControlData> {
        return executeAuthenticatedRequest {
            apiService.control(ScanModel.ControlRequest(
                device_id = aDeviceID,
                control_token = aControlToken,
                command = aCommand
            ))
        }
    }
}