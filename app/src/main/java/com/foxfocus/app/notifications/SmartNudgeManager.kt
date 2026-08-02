package com.foxfocus.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.foxfocus.app.R

enum class NudgeType { MOTIVATIONAL, URGENT, HUMOROUS, CHALLENGE, ACHIEVEMENT }

object SmartNudgeManager {
  private const val CHANNEL_ID = "foxfocus_smart_nudges"
  private const val CHANNEL_NAME = "Smart Focus Nudges"

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "تنبيهات وإشعارات ذكية لتحفيز التركيز والحفاظ على السلسلة"
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun sendNudge(context: Context, type: NudgeType, customTitle: String? = null, customMessage: String? = null) {
    createNotificationChannel(context)

    val (title, message) = when (type) {
      NudgeType.MOTIVATIONAL -> (customTitle ?: "🔥 تحفيز اليوم") to (customMessage ?: "🦊 أنت على بُعد جلسة واحدة من إنجاز جديد! استمر!")
      NudgeType.URGENT -> (customTitle ?: "⏳ سلسلتك في خطر!") to (customMessage ?: "⚠️ تذكير: لم تفتح التطبيق اليوم! استخدم تجميد السلسلة الآن لحمايتها.")
      NudgeType.HUMOROUS -> (customTitle ?: "🦊 الثعلب ينتظرك!") to (customMessage ?: "😢 الثعلب حزين لأنه لم يرك اليوم... تعال نركز معاً!")
      NudgeType.CHALLENGE -> (customTitle ?: "📅 التحدي الأسبوعي") to (customMessage ?: "🏆 أنت قريب من حصد 2,000 Fox Coins! أكمل التحدي الآن.")
      NudgeType.ACHIEVEMENT -> (customTitle ?: "🎉 إنجاز رائع!") to (customMessage ?: "🏅 تهانينا! أنجزت جلسة تركيز ممتازة وحصلت على مكافأة!")
    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle(title)
      .setContentText(message)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(type.ordinal + 100, builder.build())
  }
}
