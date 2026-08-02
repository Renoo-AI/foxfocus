package com.foxfocus.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.foxfocus.app.MainActivity
import com.foxfocus.app.R

class FoxFocusWidgetProvider : AppWidgetProvider() {

  override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
    for (appWidgetId in appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId)
    }
  }

  companion object {
    fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
      val intent = Intent(context, MainActivity::class.java)
      val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      // Try updating small, medium, large views
      val viewsSmall = RemoteViews(context.packageName, R.layout.widget_small).apply {
        setTextViewText(R.id.widget_streak_text, "🔥 15 يوم")
        setOnClickPendingIntent(R.id.widget_streak_text, pendingIntent)
      }

      val viewsMedium = RemoteViews(context.packageName, R.layout.widget_medium).apply {
        setTextViewText(R.id.widget_medium_streak, "🔥 15 يوم تركيز متتالي")
        setTextViewText(R.id.widget_medium_coins, "🪙 1,250 Fox Coins اليوم")
        setTextViewText(R.id.widget_medium_quote, "\"أنت أقوى مما تعتقد!\"")
        setOnClickPendingIntent(R.id.widget_btn_focus, pendingIntent)
      }

      val viewsLarge = RemoteViews(context.packageName, R.layout.widget_large).apply {
        setTextViewText(R.id.widget_large_streak, "🔥 15 يوم")
        setTextViewText(R.id.widget_large_coins, "🪙 رصيد العملات الكلي: 12,450 FC")
        setTextViewText(R.id.widget_large_challenge, "🏆 التحدي الأسبوعي: 8/10 ساعات مكتملة")
        setOnClickPendingIntent(R.id.widget_large_btn_start, pendingIntent)
      }

      try {
        appWidgetManager.updateAppWidget(appWidgetId, viewsLarge)
      } catch (e: Exception) {
        try {
          appWidgetManager.updateAppWidget(appWidgetId, viewsMedium)
        } catch (e2: Exception) {
          appWidgetManager.updateAppWidget(appWidgetId, viewsSmall)
        }
      }
    }
  }
}
