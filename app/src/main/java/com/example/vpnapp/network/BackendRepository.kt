package com.example.vpnapp.network

import com.example.vpnapp.AppConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object BackendRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val api: BackendApiService = Retrofit.Builder()
        .baseUrl(AppConfig.BACKEND_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BackendApiService::class.java)

    /** نتیجه‌ی موفق ورود — شامل سرورهای آماده‌ی اتصال */
    data class LoginResult(
        val username: String,
        val servers: List<SubServer>,
        val usedBytes: Long,
        val totalBytes: Long,
        val expiryMs: Long
    )

    suspend fun login(username: String, password: String): Result<LoginResult> {
        return try {
            val response = api.login(LoginRequest(username, password))
            val body = response.body()
            if (!response.isSuccessful || body?.error != null || body?.sub_url == null) {
                return Result.failure(Exception(body?.error ?: "خطای ورود"))
            }
            val servers = SubscriptionFetcher.fetchAndParse(body.sub_url)
            Result.success(
                LoginResult(
                    username = body.username ?: username,
                    servers = servers,
                    usedBytes = body.used_bytes,
                    totalBytes = body.total_bytes,
                    expiryMs = body.expiry_ms
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** ساخت اکانت مهمان (1 گیگ / 7 روز) — فقط یه‌بار در هر دستگاه */
    suspend fun loginAsGuest(deviceId: String): Result<LoginResult> {
        return try {
            val response = api.guest(GuestRequest(deviceId))
            val body = response.body()
            if (!response.isSuccessful || body?.error != null || body?.username == null) {
                val errorMsg = when (body?.error) {
                    "device_already_used" -> "این دستگاه قبلاً یه‌بار از حالت مهمان استفاده کرده"
                    else -> body?.error ?: "خطا در ساخت اکانت مهمان"
                }
                return Result.failure(Exception(errorMsg))
            }
            // بعد از ساخت اکانت مهمان، بلافاصله باهاش لاگین می‌کنیم تا کانفیگ‌ها رو بگیریم
            login(body.username, body.password ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
