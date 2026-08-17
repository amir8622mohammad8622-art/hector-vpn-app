package com.example.vpnapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpnapp.network.ParsedServer
import com.example.vpnapp.network.XuiRepository
import com.example.vpnapp.vpn.VpnConnectionState
import com.example.vpnapp.vpn.XrayManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AppUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val servers: List<ParsedServer> = emptyList(),
    val selectedServer: ParsedServer? = null
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val xrayManager = XrayManager(application.applicationContext)
    val connectionState: StateFlow<VpnConnectionState> = xrayManager.state

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState

    private var repository: XuiRepository? = null

    /** آدرس پنل رو نرمالایز می‌کنه تا حتماً با / تموم بشه (چون Retrofit اینو لازم داره) */
    private fun normalizeUrl(url: String): String =
        if (url.endsWith("/")) url else "$url/"

    fun login(panelUrl: String, username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val repo = XuiRepository(normalizeUrl(panelUrl))
                val loggedIn = repo.login(username, password)
                if (!loggedIn) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "ورود ناموفق بود — یوزرنیم/پسورد یا آدرس پنل رو چک کن"
                    )
                    return@launch
                }
                repository = repo
                val servers = repo.fetchServers()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    servers = servers,
                    selectedServer = servers.firstOrNull()
                )
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "خطا در اتصال به پنل: ${e.message}"
                )
            }
        }
    }

    fun selectServer(server: ParsedServer) {
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
