package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.LockedBg
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class FallingBall(val id: Int, val x: Float, var y: Float, val isBlue: Boolean, val speed: Float)

@Composable
fun FallingCatchGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  var balls by remember { mutableStateOf(listOf<FallingBall>()) }
  var score by remember { mutableIntStateOf(0) }
  var finished by remember { mutableStateOf(false) }
  var nextId by remember { mutableIntStateOf(0) }
  var ticksSinceSpawn by remember { mutableIntStateOf(999) }

  LaunchedEffect(score) { onHud("$score") }

  fun endGame() {
    if (finished) return
    finished = true
    val coins = (score / 10) * EconomyConfig.FALLING_PER_10_BONUS + if (score >= 20) EconomyConfig.FALLING_20_TOTAL_BONUS else 0
    onComplete(GameResult(coins) { it.copy(bestScore = maxOf(it.bestScore, score)) })
  }

  LaunchedEffect(Unit) {
    while (!finished) {
      delay(50)
      val spawnInterval = (14 - (score / 5)).coerceAtLeast(6)
      ticksSinceSpawn++
      if (ticksSinceSpawn >= spawnInterval) {
        ticksSinceSpawn = 0
        val isBlue = Random.nextInt(5) != 0
        val speed = 0.012f + (score * 0.0003f)
        balls = balls + FallingBall(nextId++, Random.nextFloat() * 0.86f + 0.02f, 0f, isBlue, speed)
      }
      balls = balls.map { it.copy(y = it.y + it.speed) }
      val reachedBottom = balls.any { it.y >= 1f }
      if (reachedBottom) {
        endGame()
      }
      balls = balls.filter { it.y < 1f }
    }
  }

  fun onTapBall(id: Int) {
    if (finished) return
    val ball = balls.find { it.id == id } ?: return
    if (ball.isBlue) {
      score++
      balls = balls.filter { it.id != id }
    } else {
      balls = balls.filter { it.id != id }
      endGame()
    }
  }

  Canvas(
    Modifier
      .fillMaxWidth()
      .aspectRatio(0.7f)
      .padding(16.dp)
      .pointerInput(Unit) {
        detectTapGestures { offset ->
          val hit = balls.minByOrNull { ball ->
            val bx = ball.x * size.width
            val by = ball.y * size.height
            val dx = bx - offset.x
            val dy = by - offset.y
            dx * dx + dy * dy
          }
          if (hit != null) {
            val bx = hit.x * size.width
            val by = hit.y * size.height
            val dist = (bx - offset.x) * (bx - offset.x) + (by - offset.y) * (by - offset.y)
            if (dist < (size.width * 0.06f) * (size.width * 0.06f)) onTapBall(hit.id)
          }
        }
      }
  ) {
    drawRect(LockedBg)
    balls.forEach { ball ->
      drawCircle(
        if (ball.isBlue) Primary else Danger,
        radius = size.width * 0.05f,
        center = Offset(ball.x * size.width, ball.y * size.height),
      )
    }
  }
}
