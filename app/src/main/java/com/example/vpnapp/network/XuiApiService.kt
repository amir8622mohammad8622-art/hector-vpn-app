package com.example.vpnapp.network

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * اینترفیس تماس با پنل x-ui / سنایی.
 * مسیرهای زیر مطابق نسخه‌های رایج x-ui / 3x-ui هستن؛ اگه پنل تو یه fork خاصه
 * (مسیرها یکم فرق دارن)، فقط کافیه همین Path‌ها رو با API واقعی پنلت match کنی.
 */
interface XuiApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<XuiLoginResponse>

    @GET("panel/api/inbounds/list")
    suspend fun getInbounds(): Response<XuiInboundListResponse>
}
