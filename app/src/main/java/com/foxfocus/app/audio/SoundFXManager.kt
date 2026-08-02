package com.foxfocus.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

object SoundFXManager {
  private var soundPool: SoundPool? = null
  private val soundMap = mutableMapOf<String, Int>()
  private var isInitialized = false

  fun init(context: Context) {
    if (isInitialized) return

    val audioAttributes = AudioAttributes.Builder()
      .setUsage(AudioAttributes.USAGE_GAME)
      .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
      .build()

    soundPool = SoundPool.Builder()
      .setMaxStreams(10)
      .setAudioAttributes(audioAttributes)
      .build()

    val rawResources = mapOf(
      "coin_claim" to getRawId(context, "sfx_coin_claim"),
      "diamond_convert" to getRawId(context, "sfx_diamond_convert"),
      "streak_freeze" to getRawId(context, "sfx_streak_freeze"),
      "app_blocked" to getRawId(context, "sfx_app_blocked"),
      "badge_unlocked" to getRawId(context, "sfx_badge_unlocked"),
      "game_correct" to getRawId(context, "sfx_game_correct"),
      "game_wrong" to getRawId(context, "sfx_game_wrong"),
      "game_win" to getRawId(context, "sfx_game_win"),
      "streak_flame" to getRawId(context, "sfx_streak_flame"),
    )

    rawResources.forEach { (key, rawId) ->
      if (rawId != 0) {
        soundPool?.load(context, rawId, 1)?.let { soundId ->
          soundMap[key] = soundId
        }
      }
    }
    isInitialized = true
  }

  private fun getRawId(context: Context, resName: String): Int {
    return context.resources.getIdentifier(resName, "raw", context.packageName)
  }

  fun playSound(context: Context, key: String) {
    if (!isInitialized) init(context)
    val soundId = soundMap[key] ?: return
    soundPool?.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
  }

  fun playCoinClaim(context: Context) = playSound(context, "coin_claim")
  fun playDiamondConvert(context: Context) = playSound(context, "diamond_convert")
  fun playStreakFreeze(context: Context) = playSound(context, "streak_freeze")
  fun playAppBlocked(context: Context) = playSound(context, "app_blocked")
  fun playBadgeUnlocked(context: Context) = playSound(context, "badge_unlocked")
  fun playGameCorrect(context: Context) = playSound(context, "game_correct")
  fun playGameWrong(context: Context) = playSound(context, "game_wrong")
  fun playGameWin(context: Context) = playSound(context, "game_win")
  fun playStreakFlame(context: Context) = playSound(context, "streak_flame")
}
