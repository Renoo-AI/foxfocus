package com.foxfocus.app.ui.games.games

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.foxfocus.app.R
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.theme.CoinGold
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.LockedBg
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

private enum class WhereBallPhase { REVEAL, SHUFFLING, PICKING, RESULT }

@Composable
fun WhereBallGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  var round by remember { mutableIntStateOf(1) }
  var correctCount by remember { mutableIntStateOf(0) }
  var ballSlot by remember { mutableIntStateOf(Random.nextInt(3)) }
  var phase by remember { mutableStateOf(WhereBallPhase.REVEAL) }
  var pickedSlot by remember { mutableStateOf<Int?>(null) }
  var bounceSlot by remember { mutableIntStateOf(-1) }

  LaunchedEffect(round) { onHud("$round / ${EconomyConfig.WHERE_BALL_ROUNDS}") }

  LaunchedEffect(round) {
    ballSlot = Random.nextInt(3)
    pickedSlot = null
    phase = WhereBallPhase.REVEAL
    delay(700)
    phase = WhereBallPhase.SHUFFLING
    val swaps = 4 + round
    repeat(swaps) {
      bounceSlot = Random.nextInt(3)
      ballSlot = Random.nextInt(3)
      delay(250)
    }
    bounceSlot = -1
    phase = WhereBallPhase.PICKING
  }

  fun onPick(slot: Int) {
    if (phase != WhereBallPhase.PICKING) return
    pickedSlot = slot
    phase = WhereBallPhase.RESULT
    if (slot == ballSlot) correctCount++
  }

  LaunchedEffect(phase) {
    if (phase != WhereBallPhase.RESULT) return@LaunchedEffect
    delay(1200)
    if (round >= EconomyConfig.WHERE_BALL_ROUNDS) {
      val allCorrect = correctCount == EconomyConfig.WHERE_BALL_ROUNDS
      val reward = correctCount * EconomyConfig.WHERE_BALL_PER_ROUND + if (allCorrect) EconomyConfig.WHERE_BALL_STREAK_BONUS else 0
      onComplete(GameResult(reward) { it.copy(bestScore = maxOf(it.bestScore, correctCount)) })
    } else {
      round++
    }
  }

  Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      when (phase) {
        WhereBallPhase.REVEAL -> stringResource(R.string.whereball_prompt)
        WhereBallPhase.SHUFFLING -> stringResource(R.string.whereball_watch)
        WhereBallPhase.PICKING -> stringResource(R.string.whereball_prompt)
        WhereBallPhase.RESULT -> if (pickedSlot == ballSlot) "✅" else "❌"
      },
      style = MaterialTheme.typography.titleLarge,
      color = TextPrimary,
    )
    androidx.compose.foundation.layout.Spacer(Modifier.height(32.dp))

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
      for (slot in 0..2) {
        val bounce by animateDpAsState(if (bounceSlot == slot) (-16).dp else 0.dp, tween(120), label = "bounce")
        val showBall = (phase == WhereBallPhase.REVEAL && slot == ballSlot) ||
          (phase == WhereBallPhase.RESULT && slot == ballSlot)
        val bg = when {
          phase == WhereBallPhase.RESULT && slot == pickedSlot && slot == ballSlot -> Success
          phase == WhereBallPhase.RESULT && slot == pickedSlot -> Danger
          else -> LockedBg
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          if (showBall) {
            Box(Modifier.size(20.dp).background(CoinGold, RoundedCornerShape(50)))
            androidx.compose.foundation.layout.Spacer(Modifier.height(4.dp))
          } else {
            Box(Modifier.size(20.dp))
            androidx.compose.foundation.layout.Spacer(Modifier.height(4.dp))
          }
          Box(
            Modifier
              .offset(y = bounce)
              .size(64.dp)
              .background(bg, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 24.dp, bottomEnd = 24.dp))
              .then(if (phase == WhereBallPhase.PICKING) Modifier.clickable { onPick(slot) } else Modifier),
          )
        }
      }
    }

    androidx.compose.foundation.layout.Spacer(Modifier.height(24.dp))
    Text("$correctCount / $round", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
  }
}
