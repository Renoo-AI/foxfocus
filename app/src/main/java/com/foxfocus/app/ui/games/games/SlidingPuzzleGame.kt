package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.theme.HeroGradientBottom
import com.foxfocus.app.theme.HeroGradientTop
import com.foxfocus.app.theme.LockedBg
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlin.random.Random

private const val BLANK = 8

private fun neighborsOf(slot: Int): List<Int> {
  val row = slot / 3
  val col = slot % 3
  val result = mutableListOf<Int>()
  if (row > 0) result.add(slot - 3)
  if (row < 2) result.add(slot + 3)
  if (col > 0) result.add(slot - 1)
  if (col < 2) result.add(slot + 1)
  return result
}

private fun shuffledSolvable(): List<Int> {
  val tiles = (0..8).toMutableList()
  var blank = 8
  repeat(150) {
    val target = neighborsOf(blank).random()
    tiles[blank] = tiles[target]
    tiles[target] = BLANK
    blank = target
  }
  return tiles
}

@Composable
fun SlidingPuzzleGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val economy = remember { EconomyConfig.economyFor(GameId.SLIDING_PUZZLE) }
  var tiles by remember { mutableStateOf(shuffledSolvable()) }
  var moves by remember { mutableIntStateOf(0) }

  LaunchedEffect(moves) { onHud("$moves حركة") }

  fun onTap(slot: Int) {
    val blank = tiles.indexOf(BLANK)
    if (slot !in neighborsOf(blank)) return
    val newTiles = tiles.toMutableList()
    newTiles[blank] = newTiles[slot]
    newTiles[slot] = BLANK
    tiles = newTiles
    moves++
    if (newTiles == (0..8).toList()) {
      val bonus = if (moves <= economy.bonusThreshold) economy.bonusCoins else 0
      onComplete(GameResult(economy.baseCoins + bonus) { it.copy(bestScore = maxOf(it.bestScore, 1)) })
    }
  }

  Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    PuzzlePreview(Modifier.size(72.dp))
    androidx.compose.foundation.layout.Spacer(Modifier.padding(8.dp))

    Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
      Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (row in 0..2) {
          Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (col in 0..2) {
              val slot = row * 3 + col
              val tileId = tiles[slot]
              Box(
                Modifier
                  .weight(1f)
                  .fillMaxSize()
                  .clip(RoundedCornerShape(10.dp))
                  .let { m -> if (tileId != BLANK) m.clickable { onTap(slot) } else m }
              ) {
                if (tileId != BLANK) {
                  PuzzleTileContent(tileId, Modifier.fillMaxSize().background(LockedBg))
                } else {
                  Box(Modifier.fillMaxSize().background(LockedBg))
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun PuzzlePreview(modifier: Modifier = Modifier) {
  Box(modifier) {
    Column(Modifier.fillMaxSize()) {
      for (row in 0..2) {
        Row(Modifier.fillMaxWidth().weight(1f)) {
          for (col in 0..2) {
            PuzzleTileContent(row * 3 + col, Modifier.weight(1f).fillMaxSize())
          }
        }
      }
    }
  }
}

@Composable
private fun PuzzleTileContent(originalIndex: Int, modifier: Modifier) {
  val row = originalIndex / 3
  val col = originalIndex % 3
  Canvas(modifier) {
    val t = (row + col) / 4f
    drawRect(lerp(HeroGradientTop, HeroGradientBottom, t))
    val center = Offset(size.width / 2f, size.height / 2f)
    if (originalIndex == 4) {
      drawCircle(Color.White.copy(alpha = 0.35f), radius = size.minDimension * 0.28f, center = center)
    } else {
      drawCircle(Color.White.copy(alpha = 0.18f), radius = size.minDimension * 0.16f, center = center)
    }
  }
}
