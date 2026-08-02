package com.foxfocus.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.foxfocus.app.navigation.FoxTab
import com.foxfocus.app.theme.FoxFocusTheme
import com.foxfocus.app.ui.FoxFocusScaffold
import com.foxfocus.app.ui.screens.auth.AuthHostScreen
import kotlinx.coroutines.flow.stateIn

class MainActivity : ComponentActivity() {

  companion object {
    const val EXTRA_OPEN_TAB = "extra_open_tab"
    const val TAB_MISSIONS = "missions"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val startTab = when (intent.getStringExtra(EXTRA_OPEN_TAB)) {
      TAB_MISSIONS -> FoxTab.Missions
      else -> FoxTab.Home
    }

    val app = application as FoxFocusApp
    val repository = app.repository
    val authRepository = app.authRepository
    val userProfileRepository = app.userProfileRepository

    setContent {
      FoxFocusTheme {
        val currentUser by authRepository.authState()
          .stateIn(app.applicationScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, authRepository.currentUser)
          .collectAsState()

        if (currentUser == null) {
          AuthHostScreen(
            authRepository = authRepository,
            userProfileRepository = userProfileRepository,
            onAuthSuccess = { /* recomposition follows currentUser automatically */ },
          )
        } else {
          FoxFocusScaffold(
            repository = repository,
            startTab = startTab,
            authRepository = authRepository,
            userProfileRepository = userProfileRepository,
          )
        }
      }
    }
  }
}
