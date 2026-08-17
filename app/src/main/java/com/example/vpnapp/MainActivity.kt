package com.example.vpnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vpnapp.network.ParsedServer
import com.example.vpnapp.ui.screens.HomeScreen
import com.example.vpnapp.ui.screens.LoginScreen
import com.example.vpnapp.ui.screens.ServerListScreen
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

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                onLogin = { panelUrl, username, password ->
                    vm.login(panelUrl, username, password) {
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
                onOpenServerList = { navController.navigate("servers") }
            )
        }
        composable("servers") {
            ServerListScreen(
                servers = uiState.servers,
                onBack = { navController.popBackStack() },
                onServerSelected = { server: ParsedServer ->
                    vm.selectServer(server)
                    navController.popBackStack()
                }
            )
        }
    }
}
