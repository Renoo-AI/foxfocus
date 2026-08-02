package com.foxfocus.app.ui.screens.blocker

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.SecondaryButton

@Composable
fun AccessibilityPermissionCard(granted: Boolean) {
  val context = LocalContext.current
  PermissionCard(
    title = "خدمة إمكانية الوصول",
    body = "يحتاجها فوكس فوكس ليعرف فقط اسم التطبيق المفتوح، ليعرض شاشة الحظر فورًا.",
    granted = granted,
    onOpenSettings = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) },
  )
}

@Composable
fun OverlayPermissionCard(granted: Boolean) {
  val context = LocalContext.current
  PermissionCard(
    title = "إذن العرض فوق التطبيقات",
    body = "يلزم لعرض شاشة الحظر فوق التطبيق المحظور مباشرة.",
    granted = granted,
    onOpenSettings = {
      context.startActivity(
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
      )
    },
  )
}

@Composable
private fun PermissionCard(title: String, body: String, granted: Boolean, onOpenSettings: () -> Unit) {
  FoxCard(modifier = Modifier.fillMaxWidth()) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween) {
      Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
      Text(
        if (granted) "مفعّل" else "غير مفعّل",
        style = MaterialTheme.typography.labelMedium,
        color = if (granted) Success else Danger,
      )
    }
    Spacer(Modifier.height(6.dp))
    Text(body, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    if (!granted) {
      Spacer(Modifier.height(12.dp))
      SecondaryButton(text = "فتح الإعدادات", onClick = onOpenSettings)
    }
  }
}
