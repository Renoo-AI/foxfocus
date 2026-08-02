package com.foxfocus.app.blocking

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager

/** Reads the real system state — never an optimistic flag we set ourselves. */
object PermissionUtils {

  fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val expectedComponent = "${context.packageName}/${AppBlockAccessibilityService::class.java.name}"

    val enabledServicesSetting = Settings.Secure.getString(
      context.contentResolver,
      Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
    ) ?: return false

    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServicesSetting)
    while (colonSplitter.hasNext()) {
      if (colonSplitter.next().equals(expectedComponent, ignoreCase = true)) return true
    }

    val accessibilityManager =
      context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
    return accessibilityManager
      ?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
      ?.any { it.id.equals(expectedComponent, ignoreCase = true) } == true
  }

  fun canDrawOverlays(context: Context): Boolean = Settings.canDrawOverlays(context)

  fun openAccessibilitySettings(context: Context) {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
  }

  fun openOverlaySettings(context: Context) {
    val intent = Intent(
      Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
      Uri.parse("package:${context.packageName}")
    ).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
  }
}
