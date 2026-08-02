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
import androidx.compose.ui.unit.dp
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.theme.LockedBg
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.SuccessBg
import com.foxfocus.app.theme.Surface
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.ui.components.ShadowTier
import com.foxfocus.app.ui.components.foxShadow
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme

private const val BLANK8 = 0

private fun neighbors8(slot: Int): List<Int> {
  val row = slot / 3
  val col = slot % 3
  val result = mutableListOf<Int>()
  if (row > 0) result.add(slot - 3)
  if (row < 2) result.add(slot + 3)
  if (col > 0) result.add(slot - 1)
  if (col < 2) result.add(slot + 1)
  return result
}

private fun solvedBoard8(): List<Int> = (1..8) + listOf(BLANK8)

private fun shuffledSolvable8(): List<Int> {
  val tiles = solvedBoard8().toMutableList()
  var blank = tiles.indexOf(BLANK8)
  repeat(200) {
    val target = neighbors8(blank).random()
    tiles[blank] = tiles[target]
    tiles[target] = BLANK8
    blank = target
  }
  return tiles
}

@Composable
fun Puzzle8Game(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  var tiles by remember { mutableStateOf(shuffledSolvable8()) }
  var moves by remember { mutableIntStateOf(0) }

  LaunchedEffect(moves) { onHud("$moves") }

  fun onTap(slot: Int) {
    val blank = tiles.indexOf(BLANK8)
    if (slot !in neighbors8(blank)) return
    val newTiles = tiles.toMutableList()
    newTiles[blank] = newTiles[slot]
    newTiles[slot] = BLANK8
    tiles = newTiles
    moves++
    if (newTiles == solvedBoard8()) {
      val bonus = if (moves <= EconomyConfig.PUZZLE8_BONUS_MOVES) EconomyConfig.PUZZLE8_BONUS else 0
      onComplete(GameResult(EconomyConfig.PUZZLE8_BASE + bonus) { it.copy(bestScore = maxOf(it.bestScore, 1)) })
    }
  }

  Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
      Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (row in 0..2) {
          Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            for (col in 0..2) {
              val slot = row * 3 + col
              val value = tiles[slot]
              Box(
                Modifier
                  .weight(1f)
                  .fillMaxSize()
                  .foxShadow(12, ShadowTier.SM)
                  .background(if (value == BLANK8) LockedBg else Surface, RoundedCornerShape(12.dp))
                  .then(if (value != BLANK8) Modifier.clickable { onTap(slot) } else Modifier),
                contentAlignment = Alignment.Center,
              ) {
                if (value != BLANK8) {
                  Text("$value", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                }
              }
            }
          }
        }
      }
    }
  }
}
