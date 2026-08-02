package com.foxfocus.app.ui.screens.blocker

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxfocus.app.blocking.PermissionUtils
import com.foxfocus.app.data.db.entity.BlockedAppEntity
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.AppIconImage
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.SecondaryButton
import kotlinx.coroutines.launch

@Composable
fun BlockerScreen(repository: FoxRepository) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val blockedApps by repository.blockedApps.collectAsStateWithLifecycle(initialValue = emptyList())

  var accessibilityGranted by remember { mutableStateOf(PermissionUtils.isAccessibilityServiceEnabled(context)) }
  var overlayGranted by remember { mutableStateOf(PermissionUtils.canDrawOverlays(context)) }
  var showAddAppDialog by remember { mutableStateOf(false) }

  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        accessibilityGranted = PermissionUtils.isAccessibilityServiceEnabled(context)
        overlayGranted = PermissionUtils.canDrawOverlays(context)
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  LazyColumn(
    Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item { Text("حماية التطبيقات", style = MaterialTheme.typography.headlineSmall, color = TextPrimary) }

    if (!accessibilityGranted || !overlayGranted) {
      item {
        com.foxfocus.app.ui.components.AlertBox(
          text = "أكمل الإعداد لتفعيل الحظر الفعلي",
          style = com.foxfocus.app.ui.components.AlertStyle.DANGER,
          modifier = Modifier.fillMaxWidth(),
        )
      }
      if (!accessibilityGranted) item { AccessibilityPermissionCard(granted = false) }
      if (!overlayGranted) item { OverlayPermissionCard(granted = false) }
    }

    item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("التطبيقات المحمية", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        SecondaryButton(text = "+ إضافة", onClick = { showAddAppDialog = true }, modifier = Modifier.width(120.dp).height(40.dp))
      }
    }

    if (blockedApps.isEmpty()) {
      item {
        Text("لم تحظر أي تطبيق بعد", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
      }
    }

    items(blockedApps, key = { it.packageName }) { app ->
      BlockedAppRow(
        app = app,
        onToggle = { checked ->
          scope.launch { repository.toggleAppBlocked(app.packageName, app.appName, app.iconType, checked) }
        },
      )
    }
  }

  if (showAddAppDialog) {
    AddAppDialog(
      packageManager = context.packageManager,
      alreadyBlocked = blockedApps.map { it.packageName }.toSet(),
      onDismiss = { showAddAppDialog = false },
      onAdd = { info, label ->
        scope.launch { repository.toggleAppBlocked(info.packageName, label, null, true) }
        showAddAppDialog = false
      },
    )
  }
}

@Composable
private fun BlockedAppRow(app: BlockedAppEntity, onToggle: (Boolean) -> Unit) {
  FoxCard(modifier = Modifier.fillMaxWidth(), paddingDp = 12) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      Column(Modifier.weight(1f)) {
        Text(app.appName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Text(
          if (app.isBlocked) "محظور" else "متوقف",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
        )
      }
      Switch(
        checked = app.isBlocked,
        onCheckedChange = onToggle,
        colors = SwitchDefaults.colors(checkedTrackColor = Primary),
      )
    }
  }
}

@Composable
private fun AddAppDialog(
  packageManager: PackageManager,
  alreadyBlocked: Set<String>,
  onDismiss: () -> Unit,
  onAdd: (ApplicationInfo, String) -> Unit,
) {
  var query by remember { mutableStateOf("") }
  var apps by remember { mutableStateOf<List<Pair<ApplicationInfo, String>>>(emptyList()) }

  LaunchedEffect(Unit) {
    val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    val resolved = packageManager.queryIntentActivities(launcherIntent, 0)
    apps = resolved
      .map { it.activityInfo.applicationInfo }
      .distinctBy { it.packageName }
      .map { it to packageManager.getApplicationLabel(it).toString() }
      .sortedBy { it.second.lowercase() }
  }

  val filtered = apps.filter { (info, label) ->
    info.packageName !in alreadyBlocked && label.contains(query, ignoreCase = true)
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {},
    title = { Text("إضافة تطبيق") },
    text = {
      Column {
        OutlinedTextField(
          value = query,
          onValueChange = { query = it },
          placeholder = { Text("ابحث عن تطبيق") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.height(360.dp)) {
          items(filtered, key = { it.first.packageName }) { (info, label) ->
            Row(
              Modifier
                .fillMaxWidth()
                .clickable { onAdd(info, label) }
                .padding(vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              AppIconImage(runCatching { packageManager.getApplicationIcon(info) }.getOrNull(), size = 32.dp)
              Spacer(Modifier.width(12.dp))
              Text(label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            }
          }
        }
      }
    },
    dismissButton = {},
  )
}
