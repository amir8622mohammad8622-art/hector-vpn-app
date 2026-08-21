package com.example.vpnapp

import android.app.Application
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpnapp.network.BackendRepository
import com.example.vpnapp.network.SubServer
import com.example.vpnapp.vpn.VpnConnectionState
import com.example.vpnapp.vpn.XrayManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AppUiState(
    val isLoading: Boolean = false,
    val isGuestLoading: Boolean = false,
    val errorMessage: String? = null,
    val servers: List<SubServer> = emptyList(),
    val selectedServer: SubServer? = null,
    val username: String? = null,
    val usedBytes: Long = 0,
    val totalBytes: Long = 0
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val xrayManager = XrayManager(application.applicationContext)
    val connectionState: StateFlow<VpnConnectionState> = xrayManager.state

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState

    /**
     * شناسه‌ی شبه‌دائمی دستگاه — برای محدودیت «هر دستگاه فقط یه‌بار مهمان».
     * این مقدار با پاک نصب دوباره‌ی اپ عوض نمیشه (فقط با ریست کارخانه یا تغییر امضای اپ).
     */
    private fun getDeviceId(): String {
        return Settings.Secure.getString(
            getApplication<Application>().contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown-device"
    }

    fun login(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = withContext(Dispatchers.IO) { BackendRepository.login(username, password) }
            result.onSuccess { loginResult ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    username = loginResult.username,
                    servers = loginResult.servers,
                    selectedServer = loginResult.servers.firstOrNull(),
                    usedBytes = loginResult.usedBytes,
                    totalBytes = loginResult.totalBytes
                )
                onSuccess()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "خطا در ورود"
                )
            }
        }
    }

    fun loginAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGuestLoading = true, errorMessage = null)
            val deviceId = getDeviceId()
            val result = withContext(Dispatchers.IO) { BackendRepository.loginAsGuest(deviceId) }
            result.onSuccess { loginResult ->
                _uiState.value = _uiState.value.copy(
                    isGuestLoading = false,
                    username = loginResult.username,
                    servers = loginResult.servers,
                    selectedServer = loginResult.servers.firstOrNull(),
                    usedBytes = loginResult.usedBytes,
                    totalBytes = loginResult.totalBytes
                )
                onSuccess()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isGuestLoading = false,
                    errorMessage = e.message ?: "خطا در ساخت اکانت مهمان"
                )
            }
        }
    }

    fun selectServer(server: SubServer) {
        _uiState.value = _uiState.value.copy(selectedServer = server)
    }

    fun connectToSelected() {
        val server = _uiState.value.selectedServer ?: return
        xrayManager.connect(server)
    }

    fun disconnect() {
        xrayManager.disconnect()
    }
}
