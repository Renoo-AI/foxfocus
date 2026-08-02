package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.economy.dualNBackReward
import com.foxfocus.app.theme.CategoryMindBg
import com.foxfocus.app.theme.CategoryMindIcon
import com.foxfocus.app.theme.LockedBg
import com.foxfocus.app.theme.Surface
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val LETTER_POOL = "ABCDEFGH"

@Composable
fun DualNBackGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val economy = remember { EconomyConfig.economyFor(GameId.DUAL_N_BACK) }
  val positions = remember { List(economy.rounds) { Random.nextInt(9) } }
  val letters = remember { List(economy.rounds) { Random.nextInt(8) } }
  var roundIndex by remember { mutableIntStateOf(0) }
  var posPressed by remember { mutableStateOf(false) }
  var letterPressed by remember { mutableStateOf(false) }
  var score by remember { mutableIntStateOf(0) }

  LaunchedEffect(roundIndex) { onHud("${roundIndex + 1} / ${economy.rounds}") }

  LaunchedEffect(roundIndex) {
    posPressed = false
    letterPressed = false
    delay(2500)
    val actualPosMatch = roundIndex > 0 && positions[roundIndex] == positions[roundIndex - 1]
    val actualLetterMatch = roundIndex > 0 && letters[roundIndex] == letters[roundIndex - 1]
    if (posPressed == actualPosMatch && letterPressed == actualLetterMatch) score++
    if (roundIndex >= economy.rounds - 1) {
      onComplete(GameResult(dualNBackReward(economy, score)) { it.copy(bestScore = maxOf(it.bestScore, score)) })
    } else {
      roundIndex++
    }
  }

  Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    com.foxfocus.app.ui.components.FoxProgressBar(progress = roundIndex.toFloat() / economy.rounds, fillColor = theme.accent)
    androidx.compose.foundation.layout.Spacer(Modifier.height(12.dp))
    Text(LETTER_POOL[letters[roundIndex]].toString(), style = MaterialTheme.typography.headlineMedium, color = TextPrimary)

    Box(Modifier.fillMaxWidth().aspectRatio(1f).padding(16.dp)) {
      Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (row in 0..2) {
          Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            for (col in 0..2) {
              val cellIndex = row * 3 + col
              val active = positions[roundIndex] == cellIndex
              Box(
                Modifier
                  .weight(1f)
                  .fillMaxSize()
                  .background(if (active) CategoryMindIcon else LockedBg, RoundedCornerShape(12.dp))
              )
            }
          }
        }
      }
    }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      NBackToggleButton(
        label = stringResource(R.string.nback_position),
        pressed = posPressed,
        onClick = { posPressed = !posPressed },
        modifier = Modifier.weight(1f),
      )
      NBackToggleButton(
        label = stringResource(R.string.nback_letter),
        pressed = letterPressed,
        onClick = { letterPressed = !letterPressed },
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun NBackToggleButton(label: String, pressed: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Box(
    modifier
      .height(52.dp)
      .background(if (pressed) CategoryMindIcon else Surface, RoundedCornerShape(14.dp))
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    Text(label, style = MaterialTheme.typography.labelLarge, color = if (pressed) Surface else TextPrimary)
  }
}
