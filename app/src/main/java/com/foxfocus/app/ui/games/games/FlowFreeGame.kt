package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.theme.Border
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme

private const val GRID = 5

private data class FlowPair(val color: Color, val a: Pair<Int, Int>, val b: Pair<Int, Int>)

private val FLOW_PAIRS = listOf(
  FlowPair(Color(0xFFD8443C), 0 to 0, 2 to 4),
  FlowPair(Color(0xFF4C6FE7), 1 to 0, 1 to 3),
  FlowPair(Color(0xFF6FA84B), 2 to 0, 2 to 3),
  FlowPair(Color(0xFFE8B923), 3 to 0, 4 to 4),
  FlowPair(Color(0xFF9B6FC7), 4 to 0, 4 to 3),
)

private fun adjacent(a: Pair<Int, Int>, b: Pair<Int, Int>) =
  (a.first == b.first && kotlin.math.abs(a.second - b.second) == 1) ||
    (a.second == b.second && kotlin.math.abs(a.first - b.first) == 1)

@Composable
fun FlowFreeGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val economy = remember { EconomyConfig.economyFor(GameId.FLOW_FREE) }
  val paths = remember { mutableStateOf(FLOW_PAIRS.associate { it.color.value to listOf(it.a) }) }
  var selected by remember { mutableStateOf<Color?>(null) }
  var moves by remember { mutableIntStateOf(0) }
  var completed by remember { mutableStateOf(setOf<Color>()) }

  LaunchedEffect(moves) { onHud("$moves حركة") }

  fun otherEndpointOf(color: Color, cell: Pair<Int, Int>): Pair<Int, Int>? {
    val pair = FLOW_PAIRS.first { it.color == color }
    return when (cell) {
      pair.a -> pair.b
      pair.b -> pair.a
      else -> null
    }
  }

  fun occupiedByOthers(exclude: Color?): Set<Pair<Int, Int>> =
    paths.value.filterKeys { it != exclude?.value }.values.flatten().toSet()

  fun onCellTap(cell: Pair<Int, Int>) {
    val endpointPair = FLOW_PAIRS.firstOrNull { it.a == cell || it.b == cell }

    if (endpointPair != null && endpointPair.color != selected) {
      if (endpointPair.color in completed) return
      selected = endpointPair.color
      paths.value = paths.value.toMutableMap().apply { put(endpointPair.color.value, listOf(cell)) }
      return
    }

    val current = selected ?: return
    val currentPath = paths.value[current.value] ?: return
    val last = currentPath.last()
    if (!adjacent(last, cell)) return
    if (cell in currentPath) return
    if (cell in occupiedByOthers(current)) return

    val newPath = currentPath + cell
    paths.value = paths.value.toMutableMap().apply { put(current.value, newPath) }
    moves++

    val target = otherEndpointOf(current, currentPath.first())
    if (cell == target) {
      completed = completed + current
      selected = null
      if (completed.size == FLOW_PAIRS.size) {
        val bonus = if (moves <= economy.bonusThreshold) economy.bonusCoins else 0
        onComplete(GameResult(economy.baseCoins + bonus) { it.copy(bestScore = maxOf(it.bestScore, 1)) })
      }
    }
  }

  Canvas(
    Modifier
      .fillMaxWidth()
      .aspectRatio(1f)
      .padding(16.dp)
      .pointerInput(Unit) {
        detectTapGestures { offset ->
          val cellSize = size.width / GRID.toFloat()
          val col = (offset.x / cellSize).toInt().coerceIn(0, GRID - 1)
          val row = (offset.y / cellSize).toInt().coerceIn(0, GRID - 1)
          onCellTap(row to col)
        }
      }
  ) {
    val cellSize = size.width / GRID
    for (i in 0..GRID) {
      drawLine(Border, Offset(0f, i * cellSize), Offset(size.width, i * cellSize), 2f)
      drawLine(Border, Offset(i * cellSize, 0f), Offset(i * cellSize, size.height), 2f)
    }

    paths.value.forEach { (colorValue, path) ->
      val color = Color(colorValue)
      if (path.size > 1) {
        for (i in 0 until path.size - 1) {
          val (r1, c1) = path[i]
          val (r2, c2) = path[i + 1]
          drawLine(
            color,
            Offset((c1 + 0.5f) * cellSize, (r1 + 0.5f) * cellSize),
            Offset((c2 + 0.5f) * cellSize, (r2 + 0.5f) * cellSize),
            strokeWidth = cellSize * 0.22f,
          )
        }
      }
    }

    FLOW_PAIRS.forEach { pair ->
      listOf(pair.a, pair.b).forEach { (r, c) ->
        drawCircle(pair.color, radius = cellSize * 0.3f, center = Offset((c + 0.5f) * cellSize, (r + 0.5f) * cellSize))
      }
    }
  }
}
