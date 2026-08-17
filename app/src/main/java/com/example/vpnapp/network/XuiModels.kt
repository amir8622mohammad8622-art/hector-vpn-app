package com.example.vpnapp.network

import com.google.gson.annotations.SerializedName

/** پاسخ لاگین پنل — معمولاً فقط success/msg برمی‌گردونه و کوکی session رو در هدر ست می‌کنه */
data class XuiLoginResponse(
    val success: Boolean,
    val msg: String?
)

/** پاسخ /panel/api/inbounds/list */
data class XuiInboundListResponse(
    val success: Boolean,
    val msg: String?,
    val obj: List<XuiInbound>?
)

data class XuiInbound(
    val id: Int,
    val remark: String,
    val port: Int,
    val protocol: String,       // vless, vmess, trojan, shadowsocks, ...
    val enable: Boolean,
    val settings: String,       // JSON رشته‌ای — شامل لیست clientها و id/password هرکدوم
    val streamSettings: String, // JSON رشته‌ای — شامل نوع شبکه (tcp/ws/grpc)، امنیت (tls/reality) و ...
    @SerializedName("expiryTime") val expiryTime: Long? = null
)

/** یک کلاینت داخل settings هر inbound (بعد از پارس JSON داخلی) */
data class XuiClient(
    val id: String? = null,       // برای vless/vmess: UUID
    val password: String? = null, // برای trojan
    val email: String? = null,
    val flow: String? = null
)
