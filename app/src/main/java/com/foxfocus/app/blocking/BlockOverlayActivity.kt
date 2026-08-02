package com.foxfocus.app.blocking

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.foxfocus.app.FoxFocusApp
import com.foxfocus.app.audio.SoundFXManager
import com.foxfocus.app.theme.FoxFocusTheme
import com.foxfocus.app.ui.screens.overlay.BlockOverlayScreen

class BlockOverlayActivity : ComponentActivity() {

  companion object {
    const val EXTRA_PACKAGE_NAME = "extra_package_name"
  }

  private var packageNameState by mutableStateOf("")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    packageNameState = intent.getStringExtra(EXTRA_PACKAGE_NAME).orEmpty()

    SoundFXManager.playAppBlocked(this)

    val repository = (application as FoxFocusApp).repository
    setContent {
      FoxFocusTheme {
        BlockOverlayScreen(
          blockedPackageName = packageNameState,
          repository = repository,
          packageManager = packageManager,
          onDismiss = { finish() },
        )
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    packageNameState = intent.getStringExtra(EXTRA_PACKAGE_NAME).orEmpty()
    SoundFXManager.playAppBlocked(this)
  }

  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
    moveTaskToBack(true)
  }
}
