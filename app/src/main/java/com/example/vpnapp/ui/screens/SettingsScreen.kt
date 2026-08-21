package com.example.vpnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vpnapp.ui.theme.AccentGreen
import com.example.vpnapp.ui.theme.BackgroundDark
import com.example.vpnapp.ui.theme.CardDark
import com.example.vpnapp.ui.theme.TextSecondary

/** اطلاعات حساب که در بالای صفحه تنظیمات نشون داده میشه */
data class AccountSummary(
    val deviceCount: Int = 1,
    val totalVolumeGb: String = "نامحدود",
    val remainingVolumeGb: String = "نامحدود",
    val remainingDays: Int = 0,
    val isActive: Boolean = true
)

@Composable
fun SettingsScreen(
    account: AccountSummary,
    onBack: () -> Unit,
    onOpenAppFilter: () -> Unit,
    onOpenConnectedDevices: () -> Unit,
    onOpenChangeLanguage: () -> Unit,
    onCheckForUpdate: () -> Unit,
    onOpenFaq: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.weight(1f))
            Text("⚡ HECTOR VPN", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.width(40.dp))
        }

        Spacer(Modifier.height(20.dp))

        // خلاصه اطلاعات حساب — مطابق ترتیب اپ نمونه
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .padding(18.dp)
        ) {
            SettingsInfoRow("تعداد دستگاه :", "${account.deviceCount} دستگاه")
            SettingsInfoRow("حجم کل :", account.totalVolumeGb)
            SettingsInfoRow("مانده حجم :", account.remainingVolumeGb)
            SettingsInfoRow("روز های مانده :", "${account.remainingDays} روز")
            SettingsInfoRow(
                "وضعیت اکانت :",
                if (account.isActive) "فعال" else "غیرفعال",
                valueColor = if (account.isActive) AccentGreen else Color(0xFFD9534F)
            )
        }

        Spacer(Modifier.height(24.dp))

        // گزینه‌های منو — دقیقاً به همون ترتیب اپ نمونه
        SettingsMenuItem(
            title = "فیلتر برنامه ها",
            subtitle = "محدودیت اتصال فیلترشکن از برنامه",
            onClick = onOpenAppFilter
        )
        SettingsMenuItem(
            title = "دستگاه های متصل",
            subtitle = "لیست دستگاه های وارد شده به اکانت",
            onClick = onOpenConnectedDevices
        )
        SettingsMenuItem(
            title = "تغییر زبان",
            subtitle = "تغییر زبان اپلیکیشن",
            onClick = onOpenChangeLanguage
        )
        SettingsMenuItem(
            title = "بررسی نسخه جدید",
            subtitle = "دریافت آخرین نسخه اپلیکیشن",
            onClick = onCheckForUpdate
        )
        SettingsMenuItem(
            title = "سوالات متداول",
            subtitle = "توضیحات بیشتر درباره برنامه",
            onClick = onOpenFaq
        )
        SettingsMenuItem(
            title = "خروج از حساب",
            subtitle = "برای خروج از اکانت کلیک کنید",
            onClick = onLogout,
            titleColor = Color(0xFFD9534F)
        )

        Spacer(Modifier.weight(1f))

        Text(
            "نسخه برنامه 1.0",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun SettingsInfoRow(label: String, value: String, valueColor: Color = Color.White) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextSecondary, fontSize = 14.sp)
    }
}

@Composable
private fun SettingsMenuItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    titleColor: Color = Color.White
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp)
    ) {
        Text(title, color = titleColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Text(subtitle, color = TextSecondary, fontSize = 12.sp)
    }
    Divider(color = Color.White.copy(alpha = 0.08f))
}
