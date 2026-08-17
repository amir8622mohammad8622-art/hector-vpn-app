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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vpnapp.network.ParsedServer
import com.example.vpnapp.ui.theme.BackgroundDark
import com.example.vpnapp.ui.theme.CardDark
import com.example.vpnapp.ui.theme.TextSecondary

@Composable
fun ServerListScreen(
    servers: List<ParsedServer>,
    onBack: () -> Unit,
    onServerSelected: (ParsedServer) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = servers.filter { it.remark.contains(query, ignoreCase = true) }

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
            Text("اینباندهای پنل", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
private fun ServerRow(server: ParsedServer, onClick: () -> Unit) {
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
            Text(server.protocol.uppercase(), color = Color(0xFF4ADE80), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("${server.network} / ${server.security}", color = TextSecondary, fontSize = 11.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(server.remark, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("پورت ${server.port}", color = TextSecondary, fontSize = 12.sp)
        }
    }
}
