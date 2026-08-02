package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.theme.Border
import com.foxfocus.app.theme.LockedBg
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme

private val TUBE_COLORS = listOf(Color(0xFF4C6FE7), Color(0xFFD8443C), Color(0xFF6FA84B))

private fun generateSolvedShuffle(): List<List<Int>> {
  while (true) {
    val flat = (0..2).flatMap { c -> List(3) { c } }.shuffled()
    val tubes = flat.chunked(3)
    val alreadySorted = tubes.all { it.toSet().size == 1 }
    if (!alreadySorted) return tubes
  }
}

@Composable
fun ColorSortGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val economy = remember { EconomyConfig.economyFor(GameId.COLOR_SORT) }
  var tubes by remember { mutableStateOf(generateSolvedShuffle()) }
  var selected by remember { mutableStateOf<Int?>(null) }
  var moves by remember { mutableIntStateOf(0) }

  LaunchedEffect(moves) { onHud("$moves حركة") }

  fun onTap(index: Int) {
    val sel = selected
    if (sel == null) {
      if (tubes[index].isNotEmpty()) selected = index
      return
    }
    if (sel == index) {
      selected = null
      return
    }
    val ball = tubes[sel].lastOrNull()
    val dest = tubes[index]
    val legal = ball != null && dest.size < 3 && (dest.isEmpty() || dest.last() == ball)
    if (legal) {
      val movingBall = ball!!
      val newTubes = tubes.toMutableList()
      newTubes[sel] = newTubes[sel].dropLast(1)
      newTubes[index] = newTubes[index] + movingBall
      tubes = newTubes
      moves++
      selected = null
      if (newTubes.all { it.size == 3 && it.toSet().size == 1 }) {
        val bonus = if (moves + 1 <= economy.bonusThreshold) economy.bonusCoins else 0
        onComplete(GameResult(economy.baseCoins + bonus) { it.copy(bestScore = maxOf(it.bestScore, 1)) })
      }
    } else {
      selected = if (tubes[index].isNotEmpty()) index else null
    }
  }

  Row(
    Modifier.fillMaxSize().padding(32.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
  ) {
    tubes.forEachIndexed { index, tube ->
      val isSelected = selected == index
      Column(
        Modifier
          .width(64.dp)
          .background(LockedBg, RoundedCornerShape(16.dp))
          .border(if (isSelected) 3.dp else 1.dp, if (isSelected) Primary else Border, RoundedCornerShape(16.dp))
          .clickable { onTap(index) }
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        for (slot in 2 downTo 0) {
          val ball = tube.getOrNull(slot)
          Box(
            Modifier
              .size(44.dp)
              .background(if (ball != null) TUBE_COLORS[ball] else Color.Transparent, CircleShape)
          )
        }
      }
    }
  }
}
