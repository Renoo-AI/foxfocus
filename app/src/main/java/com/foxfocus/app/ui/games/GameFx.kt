package com.foxfocus.app.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

/** Real motion, not decoration: a quick left-right shake for wrong answers. */
@Composable
fun rememberShake(trigger: Any?): Animatable<Float, androidx.compose.animation.core.AnimationVector1D> {
  val anim = remember { Animatable(0f) }
  LaunchedEffect(trigger) {
    if (trigger == null) return@LaunchedEffect
    listOf(-10f, 10f, -8f, 8f, -4f, 4f, 0f).forEach { anim.animateTo(it, tween(45)) }
  }
  return anim
}

/** A quick scale bump for correct answers / wins. */
@Composable
fun rememberPulse(trigger: Any?): Animatable<Float, androidx.compose.animation.core.AnimationVector1D> {
  val anim = remember { Animatable(1f) }
  LaunchedEffect(trigger) {
    if (trigger == null) return@LaunchedEffect
    anim.animateTo(1.18f, tween(120))
    anim.animateTo(1f, tween(140))
  }
  return anim
}

/** N on/off flashes, used for hints. */
@Composable
fun rememberBlink(trigger: Any?, times: Int = 3): Animatable<Float, androidx.compose.animation.core.AnimationVector1D> {
  val anim = remember { Animatable(0f) }
  LaunchedEffect(trigger) {
    if (trigger == null) return@LaunchedEffect
    repeat(times) {
      anim.animateTo(1f, tween(150))
      anim.animateTo(0f, tween(150))
    }
  }
  return anim
}
