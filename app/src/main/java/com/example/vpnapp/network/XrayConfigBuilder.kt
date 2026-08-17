package com.example.vpnapp.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject

/**
 * از روی یه [ParsedServer] (که از پنل سنایی گرفتیم) یه کانفیگ کامل Xray می‌سازه.
 * این JSON دقیقاً همون فرمتیه که Xray-core (چه در دسکتاپ چه موبایل) به عنوان
 * فایل کانفیگ قبول می‌کنه: { "inbounds": [...], "outbounds": [...] }
 *
 * فعلاً VLESS و VMess پوشش داده شده (پرکاربردترین حالت‌ها در پنل‌های x-ui).
 * برای Trojan/Shadowsocks باید یه branch مشابه اضافه کنی.
 */
object XrayConfigBuilder {

    fun buildClientConfigJson(server: ParsedServer): String {
        val root = JsonObject()

        // inbound محلی روی گوشی — پروکسی SOCKS داخلی که Xray برای اپلیکیشن‌ها فراهم می‌کنه
        val inbounds = JsonArray()
        val socksInbound = JsonObject().apply {
            addProperty("tag", "socks-in")
            addProperty("port", 10808)
            addProperty("protocol", "socks")
            add("settings", JsonObject().apply { addProperty("udp", true) })
        }
        inbounds.add(socksInbound)
        root.add("inbounds", inbounds)

        val outbounds = JsonArray()
        outbounds.add(buildProxyOutbound(server))
        outbounds.add(JsonObject().apply {
            addProperty("tag", "direct")
            addProperty("protocol", "freedom")
        })
        root.add("outbounds", outbounds)

        return root.toString()
    }

    private fun buildProxyOutbound(server: ParsedServer): JsonObject {
        val outbound = JsonObject()
        outbound.addProperty("tag", "proxy")
        outbound.addProperty("protocol", server.protocol)

        val settings = JsonObject()
        val vnext = JsonArray()
        val serverEntry = JsonObject().apply {
            addProperty("address", server.address)
            addProperty("port", server.port)
        }
        val users = JsonArray()
        val user = JsonObject().apply {
            addProperty("id", server.clientId)
            if (server.protocol == "vless") {
                addProperty("encryption", "none")
            }
        }
        users.add(user)
        serverEntry.add("users", users)
        vnext.add(serverEntry)
        settings.add("vnext", vnext)
        outbound.add("settings", settings)

        val streamSettings = JsonObject()
        streamSettings.addProperty("network", server.network)
        streamSettings.addProperty("security", server.security)
        // تنظیمات دقیق tls/reality/ws-path و ... رو مستقیم از پنل کپی می‌کنیم
        server.rawStreamSettings?.let { raw ->
            raw.entrySet().forEach { (key, value) ->
                if (!streamSettings.has(key)) streamSettings.add(key, value)
            }
        }
        outbound.add("streamSettings", streamSettings)

        return outbound
    }
}
