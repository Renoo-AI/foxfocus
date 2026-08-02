package com.foxfocus.app.data.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager {
  private val _prefs = MutableStateFlow(SettingsPreferences())
  val prefs: StateFlow<SettingsPreferences> = _prefs.asStateFlow()

  fun updateStudentEmail(email: String): Boolean {
    val isEdu = email.endsWith(".edu") || email.endsWith(".ac") || email.contains("student")
    _prefs.value = _prefs.value.copy(
      studentEmail = email,
      isStudentVerified = isEdu
    )
    return isEdu
  }

  fun updateTheme(themeName: String) {
    _prefs.value = _prefs.value.copy(selectedTheme = themeName)
  }

  fun updateWallpaper(wallpaper: String) {
    _prefs.value = _prefs.value.copy(activeWallpaper = wallpaper)
  }

  fun updateMascotPose(pose: String) {
    _prefs.value = _prefs.value.copy(selectedMascotPose = pose)
  }

  fun setSoundFxEnabled(enabled: Boolean) {
    _prefs.value = _prefs.value.copy(soundFxEnabled = enabled)
  }

  fun setSoundFxVolume(volume: Float) {
    _prefs.value = _prefs.value.copy(soundFxVolume = volume)
  }

  fun setFocusMusicEnabled(enabled: Boolean) {
    _prefs.value = _prefs.value.copy(focusMusicEnabled = enabled)
  }

  fun setFocusMusicVolume(volume: Float) {
    _prefs.value = _prefs.value.copy(focusMusicVolume = volume)
  }

  fun setMusicTrack(trackName: String) {
    _prefs.value = _prefs.value.copy(selectedMusicTrack = trackName)
  }

  fun setHapticsEnabled(enabled: Boolean) {
    _prefs.value = _prefs.value.copy(hapticFeedbackEnabled = enabled)
  }

  fun setHapticIntensity(intensity: String) {
    _prefs.value = _prefs.value.copy(hapticIntensity = intensity)
  }

  fun setPinLock(enabled: Boolean, pin: String = "") {
    _prefs.value = _prefs.value.copy(pinLockEnabled = enabled, pinCode = pin)
  }

  fun setStrictMode(enabled: Boolean) {
    _prefs.value = _prefs.value.copy(strictModeEnabled = enabled)
  }

  fun toggleNudgeCategory(category: String, enabled: Boolean) {
    _prefs.value = when (category) {
      "motivational" -> _prefs.value.copy(nudgeMotivationalEnabled = enabled)
      "urgent" -> _prefs.value.copy(nudgeUrgentEnabled = enabled)
      "humorous" -> _prefs.value.copy(nudgeHumorousEnabled = enabled)
      "challenge" -> _prefs.value.copy(nudgeChallengeEnabled = enabled)
      "achievement" -> _prefs.value.copy(nudgeAchievementEnabled = enabled)
      else -> _prefs.value
    }
  }

  fun setNudgeDailyCap(cap: Int) {
    _prefs.value = _prefs.value.copy(nudgeDailyCap = cap.coerceIn(1, 5))
  }

  fun setNudgeScheduleTime(time: String) {
    _prefs.value = _prefs.value.copy(nudgeScheduleTime = time)
  }
}
