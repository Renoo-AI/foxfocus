package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.PrimaryButton
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlinx.coroutines.delay

@Composable
fun GuessNumberGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val secret = remember { (1..100).random() }
  var low by remember { mutableIntStateOf(1) }
  var high by remember { mutableIntStateOf(100) }
  var guess by remember { mutableFloatStateOf(50f) }
  var attempts by remember { mutableIntStateOf(0) }
  var finished by remember { mutableStateOf(false) }
  var hint by remember { mutableStateOf<String?>(null) }
  var won by remember { mutableStateOf(false) }

  LaunchedEffect(attempts) { onHud("${EconomyConfig.GUESS_MAX_ATTEMPTS - attempts}") }

  fun submit() {
    if (finished) return
    val value = guess.toInt()
    attempts++
    when {
      value == secret -> {
        finished = true
        won = true
      }
      value < secret -> {
        low = (value + 1).coerceAtMost(high)
        hint = "higher"
      }
      else -> {
        high = (value - 1).coerceAtLeast(low)
        hint = "lower"
      }
    }
    if (!finished) {
      guess = ((low + high) / 2).toFloat()
      if (attempts >= EconomyConfig.GUESS_MAX_ATTEMPTS) finished = true
    }
  }

  LaunchedEffect(finished) {
    if (!finished) return@LaunchedEffect
    delay(1400)
    val coins = if (won) EconomyConfig.guessNumberReward(attempts) else 0
    onComplete(GameResult(coins) { it.copy(bestScore = if (won && (it.bestScore == 0 || attempts < it.bestScore)) attempts else it.bestScore) })
  }

  Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    if (finished) {
      Text(
        if (won) stringResource(R.string.guess_correct, secret) else stringResource(R.string.guess_out_of_attempts, secret),
        style = MaterialTheme.typography.headlineSmall,
        color = if (won) Success else Danger,
      )
    } else {
      Text(stringResource(R.string.guess_prompt), style = MaterialTheme.typography.titleMedium, color = TextPrimary)
      Spacer(Modifier.height(16.dp))
      Text("${guess.toInt()}", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
      hint?.let {
        Text(
          if (it == "higher") stringResource(R.string.guess_higher) else stringResource(R.string.guess_lower),
          style = MaterialTheme.typography.bodyMedium,
          color = theme.accent,
        )
      }
      Spacer(Modifier.height(16.dp))
      Slider(
        value = guess,
        onValueChange = { guess = it },
        valueRange = low.toFloat()..high.toFloat(),
        colors = SliderDefaults.colors(thumbColor = Primary, activeTrackColor = Primary),
      )
      Text("$low — $high", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
      Spacer(Modifier.height(16.dp))
      PrimaryButton(text = "خمن", onClick = { submit() })
    }
  }
}
