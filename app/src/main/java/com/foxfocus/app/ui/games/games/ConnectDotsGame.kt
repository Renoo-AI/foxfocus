package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.theme.Border
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlin.math.sqrt
import kotlin.random.Random

private const val DOT_COUNT = 25

private fun scatterDots(): List<Offset> {
  val points = mutableListOf<Offset>()
  var attempts = 0
  while (points.size < DOT_COUNT && attempts < 5000) {
    attempts++
    val candidate = Offset(Random.nextFloat() * 0.86f + 0.07f, Random.nextFloat() * 0.86f + 0.07f)
    val tooClose = points.any { p ->
      val dx = p.x - candidate.x
      val dy = p.y - candidate.y
      sqrt(dx * dx + dy * dy) < 0.14f
    }
    if (!tooClose) points.add(candidate)
  }
  while (points.size < DOT_COUNT) points.add(Offset(Random.nextFloat(), Random.nextFloat()))
  return points
}

@Composable
fun ConnectDotsGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val dots = remember { scatterDots() }
  var nextExpected by remember { mutableIntStateOf(1) }
  var pathIndices by remember { mutableStateOf(listOf<Int>()) }
  var startTime by remember { mutableLongStateOf(0L) }
  var failedFlash by remember { mutableStateOf(false) }
  var finished by remember { mutableStateOf(false) }
  var elapsedSeconds by remember { mutableIntStateOf(0) }

  LaunchedEffect(nextExpected) { onHud("$nextExpected / ${DOT_COUNT + 1}") }

  LaunchedEffect(startTime) {
    if (startTime == 0L) return@LaunchedEffect
    while (!finished) {
      elapsedSeconds = ((System.currentTimeMillis() - startTime) / 1000).toInt()
      kotlinx.coroutines.delay(200)
    }
  }

  fun reset() {
    nextExpected = 1
    pathIndices = emptyList()
    startTime = 0L
    failedFlash = true
  }

  fun nearestDot(pos: Offset, canvasSize: androidx.compose.ui.unit.IntSize): Int? {
    var best = -1
    var bestDist = Float.MAX_VALUE
    dots.forEachIndexed { index, dot ->
      val dx = dot.x * canvasSize.width - pos.x
      val dy = dot.y * canvasSize.height - pos.y
      val dist = dx * dx + dy * dy
      if (dist < bestDist) { bestDist = dist; best = index }
    }
    val threshold = canvasSize.width * 0.06f
    return if (bestDist < threshold * threshold) best else null
  }

  Canvas(
    Modifier
      .fillMaxWidth()
      .aspectRatio(1f)
      .padding(16.dp)
      .pointerInput(Unit) {
        detectDragGestures(
          onDragStart = { offset ->
            if (finished) return@detectDragGestures
            val hit = nearestDot(offset, size)
            if (hit == 0 && nextExpected == 1) {
              pathIndices = listOf(0)
              nextExpected = 2
              startTime = System.currentTimeMillis()
              failedFlash = false
            }
          },
          onDragEnd = {
            if (!finished && nextExpected != DOT_COUNT + 1) reset()
          },
        ) { change, _ ->
          if (finished || startTime == 0L) return@detectDragGestures
          val hit = nearestDot(change.position, size) ?: return@detectDragGestures
          if (hit == nextExpected - 1) {
            pathIndices = pathIndices + hit
            nextExpected++
            if (nextExpected == DOT_COUNT + 1) {
              finished = true
              val bonus = if (elapsedSeconds < EconomyConfig.CONNECT_DOTS_BONUS_SECONDS) EconomyConfig.CONNECT_DOTS_BONUS else 0
              onComplete(GameResult(EconomyConfig.CONNECT_DOTS_BASE + bonus) { it.copy(bestTimeMs = it.bestTimeMs?.let { b -> minOf(b, elapsedSeconds.toLong()) } ?: elapsedSeconds.toLong()) })
            }
          } else if (hit != nextExpected - 2 && pathIndices.isNotEmpty() && hit != pathIndices.last()) {
            reset()
          }
        }
      }
  ) {
    val w = size.width
    val h = size.height
    if (pathIndices.size > 1) {
      for (i in 0 until pathIndices.size - 1) {
        val a = dots[pathIndices[i]]
        val b = dots[pathIndices[i + 1]]
        drawLine(Primary, Offset(a.x * w, a.y * h), Offset(b.x * w, b.y * h), strokeWidth = 6f)
      }
    }
    dots.forEachIndexed { index, dot ->
      val visited = index < nextExpected - 1
      drawCircle(if (visited) Success else Border, radius = w * 0.032f, center = Offset(dot.x * w, dot.y * h))
      drawContext.canvas.nativeCanvas.drawText(
        "${index + 1}",
        dot.x * w - 10f,
        dot.y * h + 8f,
        android.graphics.Paint().apply {
          color = android.graphics.Color.WHITE
          textSize = 24f
          isFakeBoldText = true
        },
      )
    }
  }
}
