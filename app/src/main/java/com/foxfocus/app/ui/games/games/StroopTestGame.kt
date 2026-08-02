package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.foxfocus.app.R
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.economy.perCorrectReward
import com.foxfocus.app.theme.Border
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.DangerBg
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.SuccessBg
import com.foxfocus.app.theme.Surface
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.ui.components.FoxProgressBar
import com.foxfocus.app.ui.components.ShadowTier
import com.foxfocus.app.ui.components.foxShadow
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlinx.coroutines.delay

private enum class StroopColor(val nameRes: Int, val ink: Color) {
  RED(R.string.color_red, Color(0xFFD8443C)),
  BLUE(R.string.color_blue, Color(0xFF4C6FE7)),
  GREEN(R.string.color_green, Color(0xFF6FA84B)),
  YELLOW(R.string.color_yellow, Color(0xFFE8B923)),
}

@Composable
fun StroopTestGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val economy = remember { EconomyConfig.economyFor(GameId.STROOP) }
  var round by remember { mutableIntStateOf(1) }
  var correctCount by remember { mutableIntStateOf(0) }
  var word by remember { mutableStateOf(StroopColor.entries.random()) }
  var ink by remember { mutableStateOf(StroopColor.entries.filter { it != word }.random()) }
  var picked by remember { mutableStateOf<StroopColor?>(null) }
  val feedback = picked?.let { it == ink }

  LaunchedEffect(round) { onHud("$round / ${economy.rounds}") }

  LaunchedEffect(picked) {
    if (picked == null) return@LaunchedEffect
    delay(300)
    picked = null
    if (round >= economy.rounds) {
      onComplete(GameResult(perCorrectReward(economy, correctCount)) { it.copy(bestScore = maxOf(it.bestScore, correctCount)) })
    } else {
      round++
      word = StroopColor.entries.random()
      ink = StroopColor.entries.filter { it != word }.random()
    }
  }

  Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    FoxProgressBar(progress = (round - 1f) / economy.rounds, fillColor = theme.accent)
    Box(Modifier.fillMaxSize(0.3f), contentAlignment = Alignment.Center) {
      Text(stringResource(word.nameRes), style = MaterialTheme.typography.headlineMedium, color = ink.ink)
    }
    feedback?.let {
      Text(if (it) "✅" else "❌", style = MaterialTheme.typography.headlineSmall)
    }
    Column(Modifier.fillMaxWidth()) {
      StroopColor.entries.chunked(2).forEach { rowColors ->
        Row(Modifier.fillMaxWidth()) {
          rowColors.forEach { c ->
            val isCorrectChoice = c == ink
            val isPicked = c == picked
            val bg = when {
              picked != null && isCorrectChoice -> SuccessBg
              picked != null && isPicked -> DangerBg
              else -> Surface
            }
            val borderColor = if (picked == null) Border else if (isCorrectChoice) Success else if (isPicked) Danger else Border
            Box(
              Modifier
                .weight(1f)
                .padding(6.dp)
                .foxShadow(14, ShadowTier.SM)
                .background(bg, RoundedCornerShape(14.dp))
                .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                .then(if (picked == null) Modifier.clickable {
                  correctCount += if (isCorrectChoice) 1 else 0
                  picked = c
                } else Modifier)
                .padding(vertical = 16.dp),
            ) {
              Text(
                stringResource(c.nameRes),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
              )
            }
          }
        }
      }
    }
  }
}
