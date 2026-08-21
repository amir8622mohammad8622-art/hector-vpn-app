package com.example.vpnapp.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject

/** از روی یه [SubServer] (که از لینک subscription پارس شده) کانفیگ کامل Xray می‌سازه */
object XrayConfigBuilder {

    fun buildClientConfigJson(server: SubServer): String {
        val root = JsonObject()

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

    private fun buildProxyOutbound(server: SubServer): JsonObject {
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
            addProperty("id", server.id)
            if (server.protocol == "vless") addProperty("encryption", "none")
        }
        users.add(user)
        serverEntry.add("users", users)
        vnext.add(serverEntry)
        settings.add("vnext", vnext)
        outbound.add("settings", settings)

        val streamSettings = JsonObject()
        streamSettings.addProperty("network", server.network)
        streamSettings.addProperty("security", server.security)
        server.rawStreamSettings.entrySet().forEach { (key, value) ->
            streamSettings.add(key, value)
        }
        outbound.add("streamSettings", streamSettings)

        return outbound
    }
}
