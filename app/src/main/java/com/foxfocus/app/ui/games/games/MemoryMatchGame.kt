package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foxfocus.app.ui.components.ShadowTier
import com.foxfocus.app.ui.components.foxShadow
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.theme.CategoryBodyBg
import com.foxfocus.app.theme.SuccessBg
import com.foxfocus.app.theme.Surface
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val MEMORY_ICONS = listOf("🦊", "🌟", "🍎", "🎈", "🐸", "🌈", "🎯", "🍀")

@Composable
fun MemoryMatchGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val economy = remember { EconomyConfig.economyFor(GameId.MEMORY_MATCH) }
  val cards = remember { (MEMORY_ICONS + MEMORY_ICONS).shuffled() }
  val revealed = remember { mutableStateListOf(*Array(16) { false }) }
  val matched = remember { mutableStateListOf(*Array(16) { false }) }
  var firstIndex by remember { mutableStateOf<Int?>(null) }
  var locked by remember { mutableStateOf(false) }
  var attempts by remember { mutableIntStateOf(0) }
  val scope = rememberCoroutineScope()

  LaunchedEffect(attempts) { onHud("$attempts محاولة") }

  fun onTap(i: Int) {
    if (locked || matched[i] || revealed[i]) return
    revealed[i] = true
    val first = firstIndex
    if (first == null) {
      firstIndex = i
      return
    }
    attempts++
    if (cards[first] == cards[i]) {
      matched[first] = true
      matched[i] = true
      firstIndex = null
      if (matched.all { it }) {
        val bonus = if (attempts <= economy.bonusThreshold) economy.bonusCoins else 0
        onComplete(GameResult(economy.baseCoins + bonus) { it.copy(bestScore = maxOf(it.bestScore, 1)) })
      }
    } else {
      locked = true
      scope.launch {
        delay(800)
        revealed[first] = false
        revealed[i] = false
        locked = false
        firstIndex = null
      }
    }
  }

  LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.padding(16.dp)) {
    items(cards.size) { i ->
      val bg = when {
        matched[i] -> SuccessBg
        revealed[i] -> CategoryBodyBg
        else -> Surface
      }
      androidx.compose.foundation.layout.Box(
        Modifier
          .padding(4.dp)
          .aspectRatio(1f)
          .foxShadow(12, ShadowTier.SM)
          .background(bg, RoundedCornerShape(12.dp))
          .clickable { onTap(i) },
        contentAlignment = Alignment.Center,
      ) {
        Text(if (revealed[i] || matched[i]) cards[i] else "?", style = MaterialTheme.typography.headlineSmall)
      }
    }
  }
}
