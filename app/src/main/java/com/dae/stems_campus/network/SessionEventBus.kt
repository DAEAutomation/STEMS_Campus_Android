package com.dae.stems_campus.network

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 全域 session 事件總線：BaseRepository 在 token 完全失效時 emit，
 * AuthViewModel 監聽後切 _authState = Unauthenticated，
 * 讓 MainActivity 的頂層 NavHost 自動換到 signIn。
 */
object SessionEventBus {
    private val _forceLogout = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val forceLogout: SharedFlow<Unit> = _forceLogout.asSharedFlow()

    fun notifyForceLogout() {
        _forceLogout.tryEmit(Unit)
    }
}
