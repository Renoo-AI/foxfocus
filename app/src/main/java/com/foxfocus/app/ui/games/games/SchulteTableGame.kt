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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.ui.components.ShadowTier
import com.foxfocus.app.ui.components.foxShadow
import com.foxfocus.app.theme.Border
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.SuccessBg
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.DangerBg
import com.foxfocus.app.theme.Surface
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SchulteTableGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val economy = remember { EconomyConfig.economyFor(GameId.SCHULTE) }
  val numbers = remember { (1..25).shuffled() }
  var next by remember { mutableIntStateOf(1) }
  var startTime by remember { mutableLongStateOf(0L) }
  var wrongNumber by remember { mutableIntStateOf(-1) }
  val scope = rememberCoroutineScope()

  LaunchedEffect(startTime) {
    if (startTime == 0L) return@LaunchedEffect
    while (next <= 25) {
      onHud("${(System.currentTimeMillis() - startTime) / 1000}s")
      delay(200)
    }
  }

  LazyVerticalGrid(
    columns = GridCells.Fixed(5),
    modifier = Modifier.padding(16.dp),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp),
  ) {
    items(numbers) { value ->
      val done = value < next
      val wrong = value == wrongNumber
      val bg = when {
        done -> SuccessBg
        wrong -> DangerBg
        else -> Surface
      }
      val fg = when {
        done -> Success
        wrong -> Danger
        else -> TextPrimary
      }
      androidx.compose.foundation.layout.Box(
        Modifier
          .padding(4.dp)
          .aspectRatio(1f)
          .foxShadow(12, ShadowTier.SM)
          .background(bg, RoundedCornerShape(12.dp))
          .then(if (!done) Modifier.clickable {
            if (value == next) {
              if (next == 1) startTime = System.currentTimeMillis()
              next++
              if (next > 25) {
                val elapsed = System.currentTimeMillis() - startTime
                val bonus = if (elapsed < economy.bonusThreshold * 1000L) economy.bonusCoins else 0
                onComplete(
                  GameResult(economy.baseCoins + bonus) { stats ->
                    stats.copy(
                      bestTimeMs = stats.bestTimeMs?.let { minOf(it, elapsed) } ?: elapsed,
                      difficultyLevelReached = maxOf(stats.difficultyLevelReached, 1),
                    )
                  }
                )
              }
            } else {
              wrongNumber = value
              scope.launch { delay(220); wrongNumber = -1 }
            }
          } else Modifier),
        contentAlignment = Alignment.Center,
      ) {
        Text("$value", style = MaterialTheme.typography.titleMedium, color = fg)
      }
    }
  }
}
