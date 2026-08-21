package com.example.vpnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vpnapp.network.SubServer
import com.example.vpnapp.ui.screens.AccountSummary
import com.example.vpnapp.ui.screens.ChangeLanguageSheet
import com.example.vpnapp.ui.screens.HomeScreen
import com.example.vpnapp.ui.screens.LoginScreen
import com.example.vpnapp.ui.screens.ServerListScreen
import com.example.vpnapp.ui.screens.SettingsScreen
import com.example.vpnapp.ui.theme.OxeronVpnTheme
import com.example.vpnapp.vpn.VpnConnectionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OxeronVpnTheme {
                val vm: AppViewModel = viewModel()
                AppNavHost(vm)
            }
        }
    }
}

@Composable
fun AppNavHost(vm: AppViewModel) {
    val navController = rememberNavController()
    val uiState by vm.uiState.collectAsState()
    val connectionState by vm.connectionState.collectAsState()
    var showLanguageSheet by remember { mutableStateOf(false) }
    var selectedLanguageCode by remember { mutableStateOf("fa") }

    if (showLanguageSheet) {
        ChangeLanguageSheet(
            selectedCode = selectedLanguageCode,
            onSelect = { lang ->
                selectedLanguageCode = lang.code
                showLanguageSheet = false
            },
            onDismiss = { showLanguageSheet = false }
        )
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                isLoading = uiState.isLoading,
                isGuestLoading = uiState.isGuestLoading,
                errorMessage = uiState.errorMessage,
                onLogin = { username, password ->
                    vm.login(username, password) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onGuestLogin = {
                    vm.loginAsGuest {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                selectedServer = uiState.selectedServer,
                connectionState = connectionState,
                onToggleConnect = {
                    if (connectionState == VpnConnectionState.CONNECTED ||
                        connectionState == VpnConnectionState.CONNECTING
                    ) {
                        vm.disconnect()
                    } else {
                        vm.connectToSelected()
                    }
                },
                onOpenServerList = { navController.navigate("servers") },
                onOpenSettings = { navController.navigate("settings") },
                onOpenNotifications = { /* بعداً: صفحه اعلان‌ها */ }
            )
        }
        composable("servers") {
            ServerListScreen(
                servers = uiState.servers,
                onBack = { navController.popBackStack() },
                onServerSelected = { server: SubServer ->
                    vm.selectServer(server)
                    navController.popBackStack()
                },
                onTestAllServers = { /* بعداً: پینگ گرفتن از همه سرورها */ },
                onAutoSelectBest = {
                    uiState.servers.firstOrNull()?.let { vm.selectServer(it) }
                    navController.popBackStack()
                }
            )
        }
        composable("settings") {
            SettingsScreen(
                account = AccountSummary(
                    deviceCount = 1,
                    totalVolumeGb = if (uiState.totalBytes > 0) "${uiState.totalBytes / 1024 / 1024 / 1024} گیگ" else "نامشخص",
                    remainingVolumeGb = if (uiState.totalBytes > 0)
                        "${(uiState.totalBytes - uiState.usedBytes).coerceAtLeast(0) / 1024 / 1024 / 1024} گیگ"
                    else "نامشخص",
                    remainingDays = 30,
                    isActive = true
                ),
                onBack = { navController.popBackStack() },
                onOpenAppFilter = { /* بعداً: صفحه فیلتر برنامه‌ها */ },
                onOpenConnectedDevices = { /* بعداً: لیست دستگاه‌های متصل */ },
                onOpenChangeLanguage = { showLanguageSheet = true },
                onCheckForUpdate = { /* بعداً: چک آپدیت از گیت‌هاب ریلیز */ },
                onOpenFaq = { /* بعداً: صفحه سوالات متداول */ },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
