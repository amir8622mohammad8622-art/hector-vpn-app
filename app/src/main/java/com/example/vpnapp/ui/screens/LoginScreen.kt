package com.example.vpnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vpnapp.ui.theme.AccentGreen
import com.example.vpnapp.ui.theme.BackgroundDark
import com.example.vpnapp.ui.theme.TextSecondary

/**
 * صفحه ورود — حالا واقعاً به پنل سنایی (x-ui) خودت وصل میشه.
 * panelUrl مثلاً: https://your-domain.com:54321/  (باید با / تموم بشه)
 */
@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onLogin: (panelUrl: String, username: String, password: String) -> Unit
) {
    var panelUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(BackgroundDark, Color(0xFF1B2A3D)))
            )
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFFB5342E), shape = RoundedCornerShape(50))
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("HECTOR VPN", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("اتصال به پنل سنایی", color = TextSecondary, fontSize = 13.sp)

        Spacer(modifier = Modifier.height(36.dp))

        OutlinedTextField(
            value = panelUrl,
            onValueChange = { panelUrl = it },
            placeholder = { Text("آدرس پنل — مثلا https://domain.com:54321/", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("نام کاربری ادمین", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("پسورد ادمین", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(errorMessage, color = Color(0xFFE57373), fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onLogin(panelUrl.trim(), username.trim(), password) },
            enabled = !isLoading && panelUrl.isNotBlank() && username.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
            } else {
                Text("ورود و دریافت سرورها", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}
