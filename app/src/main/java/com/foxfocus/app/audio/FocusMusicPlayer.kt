package com.foxfocus.app.audio

import android.content.Context
import android.media.MediaPlayer

object FocusMusicPlayer {
  private var mediaPlayer: MediaPlayer? = null
  private var isPlaying = false
  private var currentTrack: String? = null
  private var currentVolume = 0.5f

  fun playFocusMusic(context: Context, resName: String = "music_focus_loop", volume: Float = currentVolume) {
    if (isPlaying && currentTrack == resName) {
      setVolume(volume)
      return
    }
    stop()

    try {
      val rawId = context.resources.getIdentifier(resName, "raw", context.packageName)
      if (rawId == 0) return

      currentVolume = volume
      mediaPlayer = MediaPlayer.create(context, rawId)?.apply {
        isLooping = true
        setVolume(volume, volume)
        start()
      }
      currentTrack = resName
      isPlaying = mediaPlayer != null
    } catch (_: Exception) {
      isPlaying = false
    }
  }

  fun setVolume(volume: Float) {
    currentVolume = volume
    try {
      mediaPlayer?.setVolume(volume, volume)
    } catch (_: Exception) {}
  }

  fun stop() {
    try {
      mediaPlayer?.stop()
      mediaPlayer?.release()
    } catch (_: Exception) {}
    mediaPlayer = null
    isPlaying = false
    currentTrack = null
  }

  fun isMusicPlaying(): Boolean = isPlaying
}
