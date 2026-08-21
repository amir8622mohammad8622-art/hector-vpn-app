package com.example.vpnapp.network

import android.util.Base64
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI
import java.net.URLDecoder

/** یه سرور که از پارس‌کردن لینک subscription به‌دست اومده */
data class SubServer(
    val remark: String,
    val protocol: String,   // vless, vmess, trojan
    val address: String,
    val port: Int,
    val id: String,          // UUID یا پسورد
    val network: String,     // tcp, ws, grpc
    val security: String,    // none, tls, reality
    val rawStreamSettings: JsonObject
)

object SubscriptionFetcher {

    private val client = OkHttpClient()

    /** لینک subscription رو می‌گیره، base64 decode می‌کنه و لیست سرورها رو برمی‌گردونه */
    fun fetchAndParse(subUrl: String): List<SubServer> {
        val request = Request.Builder().url(subUrl).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return emptyList()
            val body = response.body?.string() ?: return emptyList()
            val decoded = try {
                String(Base64.decode(body.trim(), Base64.DEFAULT))
            } catch (e: Exception) {
                body // شاید از قبل decode شده باشه
            }
            return decoded.lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .mapNotNull { parseLink(it) }
        }
    }

    private fun parseLink(link: String): SubServer? {
        return try {
            when {
                link.startsWith("vless://") -> parseVless(link)
                link.startsWith("vmess://") -> parseVmess(link)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseVless(link: String): SubServer {
        // vless://UUID@host:port?params#remark
        val uri = URI(link.replace("vless://", "https://"))
        val id = uri.userInfo
        val address = uri.host
        val port = uri.port
        val remark = uri.fragment?.let { URLDecoder.decode(it, "UTF-8") } ?: address

        val params = mutableMapOf<String, String>()
        uri.query?.split("&")?.forEach { pair ->
            val idx = pair.indexOf("=")
            if (idx > 0) params[pair.substring(0, idx)] = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
        }

        val network = params["type"] ?: "tcp"
        val security = params["security"] ?: "none"

        val stream = JsonObject()
        if (security == "tls" || security == "reality") {
            stream.addProperty("serverName", params["sni"] ?: address)
        }
        if (security == "reality") {
            stream.addProperty("publicKey", params["pbk"] ?: "")
            stream.addProperty("shortId", params["sid"] ?: "")
            stream.addProperty("fingerprint", params["fp"] ?: "chrome")
        }
        if (network == "ws") {
            stream.addProperty("path", params["path"] ?: "/")
            stream.addProperty("host", params["host"] ?: address)
        }
        if (network == "grpc") {
            stream.addProperty("serviceName", params["serviceName"] ?: "")
        }
        params["flow"]?.let { stream.addProperty("flow", it) }

        return SubServer(
            remark = remark, protocol = "vless", address = address, port = port,
            id = id ?: "", network = network, security = security, rawStreamSettings = stream
        )
    }

    private fun parseVmess(link: String): SubServer {
        val jsonPart = link.removePrefix("vmess://")
        val decoded = String(Base64.decode(jsonPart, Base64.DEFAULT))
        val obj = JsonParser.parseString(decoded).asJsonObject

        val network = obj.get("net")?.asString ?: "tcp"
        val security = if (obj.get("tls")?.asString == "tls") "tls" else "none"

        val stream = JsonObject()
        if (security == "tls") {
            stream.addProperty("serverName", obj.get("sni")?.asString ?: obj.get("add")?.asString ?: "")
        }
        if (network == "ws") {
            stream.addProperty("path", obj.get("path")?.asString ?: "/")
            stream.addProperty("host", obj.get("host")?.asString ?: "")
        }

        return SubServer(
            remark = obj.get("ps")?.asString ?: "server",
            protocol = "vmess",
            address = obj.get("add")?.asString ?: "",
            port = obj.get("port")?.asString?.toIntOrNull() ?: 443,
            id = obj.get("id")?.asString ?: "",
            network = network,
            security = security,
            rawStreamSettings = stream
        )
    }
}
