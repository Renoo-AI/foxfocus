package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.economy.reflexReward
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

private enum class ReflexPhase { WAITING, GO, RESULT }

@Composable
fun ReflexTestGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val economy = remember { EconomyConfig.economyFor(GameId.REFLEX) }
  var round by remember { mutableIntStateOf(1) }
  var roundKey by remember { mutableIntStateOf(0) }
  var phase by remember { mutableStateOf(ReflexPhase.WAITING) }
  var goTime by remember { mutableLongStateOf(0L) }
  var correctCount by remember { mutableIntStateOf(0) }
  var message by remember { mutableStateOf("") }
  val reactionTimes = remember { mutableStateListOf<Long>() }

  LaunchedEffect(round) { onHud("$round / ${economy.rounds}") }

  LaunchedEffect(roundKey) {
    phase = ReflexPhase.WAITING
    delay(Random.nextLong(1500, 4001))
    goTime = System.currentTimeMillis()
    phase = ReflexPhase.GO
  }

  fun finishIfDone() {
    if (round > economy.rounds) {
      val avg = if (reactionTimes.isNotEmpty()) reactionTimes.average().toLong() else 0L
      onComplete(GameResult(reflexReward(economy, correctCount)) { it.copy(bestAverageReflexMs = it.bestAverageReflexMs?.let { b -> minOf(b, avg) } ?: avg) })
    }
  }

  fun onTap() {
    when (phase) {
      ReflexPhase.WAITING -> {
        message = "❌ بكرة! انتظر حتى يصبح أخضر"
        phase = ReflexPhase.RESULT
      }
      ReflexPhase.GO -> {
        val elapsed = System.currentTimeMillis() - goTime
        reactionTimes.add(elapsed)
        if (elapsed < 800) {
          correctCount++
          message = "✅ ${elapsed}ms!"
        } else {
          message = "🐢 بطيء جدًا (${elapsed}ms)"
        }
        phase = ReflexPhase.RESULT
        round++
      }
      ReflexPhase.RESULT -> return
    }
  }

  LaunchedEffect(phase) {
    if (phase != ReflexPhase.RESULT) return@LaunchedEffect
    delay(700)
    if (round > economy.rounds) {
      finishIfDone()
    } else {
      roundKey++
    }
  }

  val bg = when (phase) {
    ReflexPhase.GO -> Color(0xFF4CAF7A)
    else -> Color(0xFF141414)
  }

  Box(
    Modifier
      .fillMaxSize()
      .background(bg)
      .clickable { onTap() },
    contentAlignment = Alignment.Center,
  ) {
    Text(
      when (phase) {
        ReflexPhase.WAITING -> "انتظر…"
        ReflexPhase.GO -> "اضغط الآن!"
        ReflexPhase.RESULT -> message
      },
      style = MaterialTheme.typography.headlineSmall,
      color = Color.White,
    )
  }
}
