package com.example.vpnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vpnapp.network.SubServer
import com.example.vpnapp.ui.theme.AccentGreen
import com.example.vpnapp.ui.theme.BackgroundDark
import com.example.vpnapp.ui.theme.CardDark
import com.example.vpnapp.ui.theme.TextSecondary

@Composable
fun ServerListScreen(
    servers: List<SubServer>,
    onBack: () -> Unit,
    onServerSelected: (SubServer) -> Unit,
    onTestAllServers: () -> Unit = {},
    onAutoSelectBest: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var showInfoDialog by remember { mutableStateOf(true) }
    val filtered = servers.filter { it.remark.contains(query, ignoreCase = true) }

    // دیالوگ راهنما — دقیقاً مطابق اپ نمونه، اولین بار که این صفحه باز میشه نشون داده میشه
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("سرور های مختلف") },
            text = {
                Text("کاربران گرامی، بخش لیست سرور ها میتونین به بقیه سرور ها متصل بشین و فقط وابسته به یک سرور نیست.")
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) { Text("بستن") }
            },
            dismissButton = {
                TextButton(onClick = { showInfoDialog = false }) { Text("دیگر نشان نده") }
            }
        )
    }

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
            Text("لوکیشن سرورها", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.width(40.dp))
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("جستجو", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(Modifier.height(14.dp))

        // دکمه تست همه سرورها
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Transparent)
                .then(Modifier)
        ) {
            OutlinedButton(
                onClick = onTestAllServers,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGreen)
            ) {
                Text("تست همه سرورها", color = AccentGreen)
            }
        }

        Spacer(Modifier.height(10.dp))

        // دکمه انتخاب بهترین لوکیشن خودکار
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Color(0xFF22D3C5), Color(0xFF8B5CF6))
                    )
                )
                .clickable(onClick = onAutoSelectBest)
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("انتخاب بهترین لوکیشن خودکار", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(20.dp))

        if (servers.isEmpty()) {
            Text(
                "هیچ اینباندی از پنل دریافت نشد — مطمئن شو پنل فعاله و اینباند فعال داره",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(filtered) { server ->
                ServerRow(server = server, onClick = { onServerSelected(server) })
            }
        }
    }
}

@Composable
private fun ServerRow(server: SubServer, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("رایگان", color = Color(0xFF4ADE80), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("${server.network} / ${server.security}", color = TextSecondary, fontSize = 11.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(server.remark, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("${server.protocol.uppercase()} · پورت ${server.port}", color = TextSecondary, fontSize = 12.sp)
        }
    }
}
