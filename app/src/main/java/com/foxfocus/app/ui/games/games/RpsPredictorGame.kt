package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.Surface
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

private val MOVE_LABELS = listOf(R.string.rps_rock, R.string.rps_paper, R.string.rps_scissors)

private enum class RpsStep { PICK_MOVE, PREDICT, REVEAL }

@Composable
fun RpsPredictorGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  var round by remember { mutableIntStateOf(1) }
  var score by remember { mutableIntStateOf(0) }
  var step by remember { mutableStateOf(RpsStep.PICK_MOVE) }
  var lastAiMove by remember { mutableStateOf(Random.nextInt(3)) }
  var prediction by remember { mutableStateOf<Int?>(null) }
  var aiMove by remember { mutableStateOf<Int?>(null) }

  LaunchedEffect(round) { onHud("$round / ${EconomyConfig.RPS_ROUNDS}") }

  LaunchedEffect(step) {
    if (step != RpsStep.REVEAL) return@LaunchedEffect
    delay(1200)
    if (round >= EconomyConfig.RPS_ROUNDS) {
      val coins = (score / EconomyConfig.RPS_POINTS_PER_COIN) + if (score >= EconomyConfig.RPS_BONUS_THRESHOLD) EconomyConfig.RPS_BONUS_COINS else 0
      onComplete(GameResult(coins) { it.copy(bestScore = maxOf(it.bestScore, score)) })
    } else {
      round++
      prediction = null
      aiMove = null
      step = RpsStep.PICK_MOVE
    }
  }

  fun onPredict(guess: Int) {
    prediction = guess
    val next = if (Random.nextInt(10) < 7) (lastAiMove + 1) % 3 else Random.nextInt(3)
    aiMove = next
    lastAiMove = next
    if (guess == next) score++
    step = RpsStep.REVEAL
  }

  Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Text("$score / ${EconomyConfig.RPS_ROUNDS}", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
    Spacer(Modifier.height(16.dp))

    when (step) {
      RpsStep.PICK_MOVE -> {
        Text(stringResource(R.string.rps_predict_prompt), style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        Spacer(Modifier.height(16.dp))
        MoveRow { step = RpsStep.PREDICT }
      }
      RpsStep.PREDICT -> {
        Text(stringResource(R.string.rps_predict_prompt), style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(16.dp))
        MoveRow { onPredict(it) }
      }
      RpsStep.REVEAL -> {
        val correct = prediction == aiMove
        Text(
          stringResource(MOVE_LABELS[aiMove ?: 0]),
          style = MaterialTheme.typography.headlineSmall,
          color = if (correct) Success else Danger,
        )
        Spacer(Modifier.height(8.dp))
        Text(if (correct) "✅" else "❌", style = MaterialTheme.typography.headlineMedium)
      }
    }
  }
}

@Composable
private fun MoveRow(onPick: (Int) -> Unit) {
  Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    MOVE_LABELS.forEachIndexed { index, res ->
      Box(
        Modifier
          .weight(1f)
          .height(64.dp)
          .background(Surface, RoundedCornerShape(14.dp))
          .clickable { onPick(index) },
        contentAlignment = Alignment.Center,
      ) {
        Text(stringResource(res), style = MaterialTheme.typography.titleMedium, color = TextPrimary)
      }
    }
  }
}
