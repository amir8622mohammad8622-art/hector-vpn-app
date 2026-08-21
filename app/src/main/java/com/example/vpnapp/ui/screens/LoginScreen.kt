package com.example.vpnapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vpnapp.ui.theme.AccentGreen
import com.example.vpnapp.ui.theme.BackgroundDark
import com.example.vpnapp.ui.theme.TextSecondary

/**
 * صفحه‌ی ورود — دقیقاً مطابق طرح مرجع:
 * لوگوی دایره‌ای بزرگ، اسم اپ، فیلد یوزرنیم (با آیکون شخص)، فیلد پسورد
 * (با آیکون قفل + چشم برای نمایش)، دکمه‌ی سبز ورود، و دکمه‌ی حاشیه‌دار «ورود به عنوان مهمان».
 */
@Composable
fun LoginScreen(
    isLoading: Boolean,
    isGuestLoading: Boolean,
    errorMessage: String?,
    onLogin: (username: String, password: String) -> Unit,
    onGuestLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B1420), Color(0xFF16283A), Color(0xFF0B1420))
                )
            )
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // لوگوی دایره‌ای با افکت درخشش ملایم پشتش
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(AccentGreen.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF16283A), Color(0xFF0B1420)))
                    )
                    .border(2.dp, AccentGreen.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = 46.sp, color = AccentGreen)
            }
        }

        Spacer(modifier = Modifier.height(22.dp))
        Text("HECTOR VPN", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("SECURE · FAST · RELIABLE", color = TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)

        Spacer(modifier = Modifier.height(44.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("نام کاربری", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White.copy(alpha = 0.06f),
                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = AccentGreen.copy(alpha = 0.5f)
            )
        )
        Spacer(modifier = Modifier.height(14.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("پسورد", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = TextSecondary) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White.copy(alpha = 0.06f),
                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = AccentGreen.copy(alpha = 0.5f)
            )
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(errorMessage, color = Color(0xFFE57373), fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(26.dp))

        Button(
            onClick = { onLogin(username.trim(), password) },
            enabled = !isLoading && !isGuestLoading && username.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black, strokeWidth = 2.dp)
            } else {
                Text("ورود", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onGuestLogin,
            enabled = !isLoading && !isGuestLoading,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE8862B))
        ) {
            if (isGuestLoading) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color(0xFFE8862B), strokeWidth = 2.dp)
            } else {
                Text("ورود به عنوان مهمان", color = Color(0xFFE8862B), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
