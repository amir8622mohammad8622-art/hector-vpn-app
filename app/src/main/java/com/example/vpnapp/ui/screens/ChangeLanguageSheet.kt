package com.example.vpnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vpnapp.ui.theme.CardDark

data class AppLanguage(val code: String, val label: String, val flagEmoji: String)

val SupportedLanguages = listOf(
    AppLanguage("fa", "فارسی", "🇮🇷"),
    AppLanguage("en", "English", "🇺🇸"),
    AppLanguage("ru", "Русский", "🇷🇺"),
    AppLanguage("zh", "中文", "🇨🇳")
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ChangeLanguageSheet(
    selectedCode: String,
    onSelect: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = CardDark) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            SupportedLanguages.forEach { lang ->
                val isSelected = lang.code == selectedCode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .then(
                            if (isSelected) Modifier.background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF22D3C5), Color(0xFF3B82F6))
                                )
                            ) else Modifier.background(Color.White.copy(alpha = 0.05f))
                        )
                        .clickable { onSelect(lang) }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(lang.flagEmoji, fontSize = 20.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        lang.label,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
