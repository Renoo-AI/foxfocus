package com.foxfocus.app.blocking

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.foxfocus.app.FoxFocusApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private const val TAG = "AppBlockService"

/**
 * Watches foreground app changes and launches [BlockOverlayActivity] the moment a
 * blocked package comes to the front, unless that package already has an active
 * unlock session. Both the blocklist and active sessions are kept as in-memory
 * snapshots updated from Room via Flow, so the per-event check on the main thread
 * never blocks on a DB read.
 */
class AppBlockAccessibilityService : AccessibilityService() {

  private lateinit var scope: CoroutineScope

  @Volatile private var blockedPackages: Set<String> = emptySet()
  @Volatile private var unlockExpiryByPackage: Map<String, Long> = emptyMap()
  private var lastForegroundBlockedPackage: String? = null

  override fun onServiceConnected() {
    super.onServiceConnected()
    Log.i(TAG, "AppBlockAccessibilityService connected and watching for blocked apps")

    val repository = (application as FoxFocusApp).repository
    scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    scope.launch {
      repository.activeBlockedApps.collect { apps ->
        blockedPackages = apps.map { it.packageName }.toSet()
      }
    }
    scope.launch {
      repository.unlockSessions.collect { sessions ->
        unlockExpiryByPackage = sessions.associate { it.packageName to it.expiresAtEpochMillis }
      }
    }
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
    val pkg = event.packageName?.toString() ?: return
    if (pkg == packageName) {
      // Our own block-overlay task is in front. Clear the dedupe guard so that when the
      // blocked app resurfaces (e.g. user hit "never mind" without unlocking), it is
      // treated as a fresh foreground event and gets re-blocked instead of being skipped.
      lastForegroundBlockedPackage = null
      return
    }

    if (pkg !in blockedPackages) {
      lastForegroundBlockedPackage = null
      return
    }

    val expiresAt = unlockExpiryByPackage[pkg]
    if (expiresAt != null && expiresAt > System.currentTimeMillis()) {
      return
    }

    if (pkg == lastForegroundBlockedPackage) return
    lastForegroundBlockedPackage = pkg
    launchBlockOverlay(pkg)
  }

  private fun launchBlockOverlay(packageName: String) {
    val intent = Intent(this, BlockOverlayActivity::class.java).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      putExtra(BlockOverlayActivity.EXTRA_PACKAGE_NAME, packageName)
    }
    startActivity(intent)
  }

  override fun onInterrupt() {
    Log.w(TAG, "AppBlockAccessibilityService interrupted")
  }

  override fun onDestroy() {
    super.onDestroy()
    if (::scope.isInitialized) scope.cancel()
  }
}
