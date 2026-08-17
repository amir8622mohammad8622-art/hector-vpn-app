package com.example.vpnapp.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** یه CookieJar ساده در حافظه — چون پنل x-ui بعد از لاگین با session cookie کار می‌کنه */
class InMemoryCookieJar : CookieJar {
    private val store = mutableMapOf<String, List<Cookie>>()
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        store[url.host] = cookies
    }
    override fun loadForRequest(url: HttpUrl): List<Cookie> = store[url.host] ?: emptyList()
}

/**
 * ارتباط با پنل سنایی (x-ui) خودت.
 * panelBaseUrl مثلاً: "https://your-domain.com:54321/" (باید با / تموم بشه)
 */
class XuiRepository(private val panelBaseUrl: String) {

    private val cookieJar = InMemoryCookieJar()

    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val api: XuiApiService = Retrofit.Builder()
        .baseUrl(panelBaseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(XuiApiService::class.java)

    suspend fun login(username: String, password: String): Boolean {
        val response = api.login(username, password)
        return response.isSuccessful && response.body()?.success == true
    }

    /**
     * لیست اینباندهای فعال پنل رو می‌گیره و برای هر کدوم اولین کلاینت رو
     * به یه [ParsedServer] قابل استفاده تبدیل می‌کنه.
     */
    suspend fun fetchServers(): List<ParsedServer> {
        val response = api.getInbounds()
        if (!response.isSuccessful) return emptyList()
        val inbounds = response.body()?.obj ?: return emptyList()
        val gson = Gson()

        return inbounds.filter { it.enable }.mapNotNull { inbound ->
            try {
                val settingsJson = gson.fromJson(inbound.settings, JsonObject::class.java)
                val clientsArray = settingsJson.getAsJsonArray("clients") ?: return@mapNotNull null
                if (clientsArray.size() == 0) return@mapNotNull null
                val firstClient = clientsArray[0].asJsonObject

                val streamJson = gson.fromJson(inbound.streamSettings, JsonObject::class.java)
                val network = streamJson?.get("network")?.asString ?: "tcp"
                val security = streamJson?.get("security")?.asString ?: "none"

                val panelHost = panelBaseUrl.toHttpUrlOrNull()?.host ?: return@mapNotNull null

                ParsedServer(
                    inboundId = inbound.id,
                    remark = inbound.remark,
                    protocol = inbound.protocol,
                    address = panelHost,
                    port = inbound.port,
                    clientId = firstClient.get("id")?.asString
                        ?: firstClient.get("password")?.asString ?: "",
                    network = network,
                    security = security,
                    rawStreamSettings = streamJson
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

/** یه سرور آماده‌شده از روی اینباند پنل، که بعداً به کانفیگ Xray تبدیل میشه */
data class ParsedServer(
    val inboundId: Int,
    val remark: String,
    val protocol: String,
    val address: String,
    val port: Int,
    val clientId: String,
    val network: String,
    val security: String,
    val rawStreamSettings: JsonObject?
)
