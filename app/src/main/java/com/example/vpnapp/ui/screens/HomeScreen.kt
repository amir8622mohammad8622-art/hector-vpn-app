package com.example.vpnapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vpnapp.network.ParsedServer
import com.example.vpnapp.ui.theme.*
import com.example.vpnapp.vpn.VpnConnectionState
import kotlin.math.min

@Composable
fun HomeScreen(
    selectedServer: ParsedServer?,
    connectionState: VpnConnectionState,
    onToggleConnect: () -> Unit,
    onOpenServerList: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(20.dp)
    ) {
        // کارت وضعیت اکانت رایگان — مشابه عکس
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(listOf(OrangeGradientStart, OrangeGradientEnd))
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("نسخه رایگان", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "شما در حال استفاده از نسخه رایگان برنامه هستید",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔒", fontSize = 18.sp)
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // کارت انتخاب سرور
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CardDark)
                .clickable(onClick = onOpenServerList)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null, tint = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(selectedServer?.remark ?: "سروری انتخاب نشده", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(selectedServer?.protocol?.uppercase() ?: "", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // آمار آپلود/دانلود
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatColumn(icon = "↓", label = "دانلود", color = DownloadGreen)
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("00:00:00", color = Color.White, fontSize = 15.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    when (connectionState) {
                        VpnConnectionState.CONNECTED -> "متصل"
                        VpnConnectionState.CONNECTING -> "در حال اتصال..."
                        VpnConnectionState.ERROR -> "خطا در اتصال"
                        else -> "اتصال برقرار نیست"
                    },
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            StatColumn(icon = "↑", label = "آپلود", color = UploadOrange)
        }

        Spacer(Modifier.weight(1f))

        // دایره اتصال بزرگ
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            ConnectionRing(connectionState = connectionState, onClick = onToggleConnect)
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun StatColumn(icon: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, color = color, fontSize = 20.sp)
        Text("0.0 B", color = color, fontSize = 13.sp)
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
private fun ConnectionRing(connectionState: VpnConnectionState, onClick: () -> Unit) {
    val ringColor = when (connectionState) {
        VpnConnectionState.CONNECTED -> AccentGreen
        VpnConnectionState.CONNECTING -> Color(0xFFE8B92B)
        VpnConnectionState.ERROR -> Color(0xFFD9534F)
        else -> AccentGreen
    }
    Box(
        modifier = Modifier
            .size(240.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val radius = min(size.width, size.height) / 2 * 0.92f
            val dashCount = 60
            for (i in 0 until dashCount) {
                val angle = (i * 360f / dashCount) * (Math.PI / 180f)
                val start = Offset(
                    x = center.x + (radius - 10) * kotlin.math.cos(angle).toFloat(),
                    y = center.y + (radius - 10) * kotlin.math.sin(angle).toFloat()
                )
                val end = Offset(
                    x = center.x + radius * kotlin.math.cos(angle).toFloat(),
                    y = center.y + radius * kotlin.math.sin(angle).toFloat()
                )
                drawLine(color = ringColor, start = start, end = end, strokeWidth = 4f)
            }
        }
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(CardDark),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚡",
                fontSize = 48.sp,
                color = ringColor
            )
        }
    }
}
