package com.example.vpnapp.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build

/**
 * سرویس پایه VPN اندروید.
 *
 * نکته مهم: اکثر کتابخونه‌های Xray-core برای اندروید (مثل libXray یا AndroidLibXrayLite)
 * خودشون یه پیاده‌سازی از VpnService همراه دارن که TUN interface رو می‌سازه و
 * ترافیک رو به Xray-core پاس می‌ده. وقتی .aar واقعی رو اضافه کردی، به احتمال زیاد
 * باید از همون کلاس VpnService که خود کتابخونه می‌ده استفاده کنی (یا این کلاس رو
 * طبق نمونه‌شون تکمیل کنی: ساخت TUN با establish()، وصل کردنش به Xray-core، و غیره).
 *
 * فعلاً این فقط یه سرویس Foreground استاندارده که نوتیفیکیشن نشون میده تا اندروید
 * اپ رو در پس‌زمینه نکشه.
 */
class OxeronVpnService : VpnService() {

    companion object {
        private const val CHANNEL_ID = "oxeron_vpn_channel"
        private const val NOTIF_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannelIfNeeded()
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIF_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIF_ID, notification)
        }
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "اتصال VPN",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("HECTOR VPN")
            .setContentText("اتصال VPN فعال است")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .build()
    }
}
