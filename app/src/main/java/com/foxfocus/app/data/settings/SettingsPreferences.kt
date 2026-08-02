package com.foxfocus.app.data.settings

data class SettingsPreferences(
  val userEmail: String = "user@foxfocus.app",
  val studentEmail: String = "",
  val isStudentVerified: Boolean = false,
  val ageYears: Int = 20,
  val selectedTheme: String = "WARM_SUNSET", // WARM_SUNSET, MIDNIGHT_FOX, EMERALD_OASIS, CYBERPUNK
  val activeWallpaper: String = "bg_warm_autumn",
  val selectedMascotPose: String = "DEFAULT",
  val soundFxEnabled: Boolean = true,
  val soundFxVolume: Float = 0.8f,
  val focusMusicEnabled: Boolean = true,
  val focusMusicVolume: Float = 0.6f,
  val selectedMusicTrack: String = "music_focus_loop",
  val hapticFeedbackEnabled: Boolean = true,
  val hapticIntensity: String = "MEDIUM", // SOFT, MEDIUM, STRONG
  val pinLockEnabled: Boolean = false,
  val pinCode: String = "",
  val strictModeEnabled: Boolean = false,
  val nudgeMotivationalEnabled: Boolean = true,
  val nudgeUrgentEnabled: Boolean = true,
  val nudgeHumorousEnabled: Boolean = true,
  val nudgeChallengeEnabled: Boolean = true,
  val nudgeAchievementEnabled: Boolean = true,
  val nudgeDailyCap: Int = 3,
  val nudgeScheduleTime: String = "19:30",
  val familyPurchaseAlertsEnabled: Boolean = true,
  val autoCloudSyncEnabled: Boolean = true,
)
