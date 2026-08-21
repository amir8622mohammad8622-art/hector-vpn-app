package com.example.vpnapp.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)
data class GuestRequest(val device_id: String)

data class AccountResponse(
    val username: String? = null,
    val xui_email: String? = null,
    val account_type: String? = null,
    val sub_url: String? = null,
    val used_bytes: Long = 0,
    val total_bytes: Long = 0,
    val expiry_ms: Long = 0,
    val enabled: Boolean = true,
    val error: String? = null
)

data class GuestResponse(
    val username: String? = null,
    val password: String? = null,
    val expiry_days: Int = 0,
    val total_bytes: Long = 0,
    val error: String? = null
)

interface BackendApiService {
    @POST("api/app/login")
    suspend fun login(@Body body: LoginRequest): Response<AccountResponse>

    @POST("api/app/guest")
    suspend fun guest(@Body body: GuestRequest): Response<GuestResponse>
}
