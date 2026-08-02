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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.economy.simonSaysReward
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

private enum class SimonPhase { SHOWING, INPUT }

private val PAD_COLORS = listOf(Color(0xFFD8443C), Color(0xFF6FA84B), Color(0xFF4C6FE7), Color(0xFFE8B923))
private val PAD_COLORS_DIM = listOf(Color(0xFFF3C9C6), Color(0xFFC9E0BB), Color(0xFFC3D0F5), Color(0xFFF6E4A8))

@Composable
fun SimonSaysGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val economy = remember { EconomyConfig.economyFor(GameId.SIMON_SAYS) }
  val sequence = remember { mutableStateListOf(Random.nextInt(4)) }
  var phase by remember { mutableStateOf(SimonPhase.SHOWING) }
  var playerIndex by remember { mutableIntStateOf(0) }
  var activeFlash by remember { mutableStateOf(-1) }
  var sequenceVersion by remember { mutableIntStateOf(0) }

  LaunchedEffect(sequence.size) { onHud("${sequence.size} / ${economy.rounds}") }

  LaunchedEffect(sequenceVersion) {
    phase = SimonPhase.SHOWING
    playerIndex = 0
    delay(400)
    for (pad in sequence) {
      activeFlash = pad
      delay(350)
      activeFlash = -1
      delay(120)
    }
    phase = SimonPhase.INPUT
  }

  fun onPadTap(pad: Int) {
    if (phase != SimonPhase.INPUT) return
    if (pad == sequence[playerIndex]) {
      playerIndex++
      if (playerIndex == sequence.size) {
        if (sequence.size >= economy.rounds) {
          onComplete(GameResult(simonSaysReward(economy, sequence.size, won = true)) { it.copy(difficultyLevelReached = maxOf(it.difficultyLevelReached, sequence.size)) })
        } else {
          sequence.add(Random.nextInt(4))
          sequenceVersion++
        }
      }
    } else {
      val roundsCleared = sequence.size - 1
      onComplete(GameResult(simonSaysReward(economy, roundsCleared, won = false)) { it.copy(difficultyLevelReached = maxOf(it.difficultyLevelReached, roundsCleared)) })
    }
  }

  Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
      Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (row in 0..1) {
          Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (col in 0..1) {
              val pad = row * 2 + col
              val lit = activeFlash == pad
              Box(
                Modifier
                  .weight(1f)
                  .fillMaxSize()
                  .background(if (lit) PAD_COLORS[pad] else PAD_COLORS_DIM[pad], RoundedCornerShape(20.dp))
                  .clickable { onPadTap(pad) }
              )
            }
          }
        }
      }
    }
  }
}
