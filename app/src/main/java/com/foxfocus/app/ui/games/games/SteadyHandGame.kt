package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.theme.Border
import com.foxfocus.app.theme.CoinGold
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlin.math.abs
import kotlin.math.sqrt

private const val STEADY_SIZE = 9

private class SteadyCell {
  var top = true
  var right = true
  var bottom = true
  var left = true
  var visited = false
}

private enum class SteadyDir(val dx: Int, val dy: Int) { UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0) }

private fun generateSteadyMaze(size: Int): Array<Array<SteadyCell>> {
  val grid = Array(size) { Array(size) { SteadyCell() } }
  val stack = ArrayDeque<Pair<Int, Int>>()
  grid[0][0].visited = true
  stack.addLast(0 to 0)
  while (stack.isNotEmpty()) {
    val (x, y) = stack.last()
    val options = SteadyDir.entries.shuffled().mapNotNull { dir ->
      val nx = x + dir.dx
      val ny = y + dir.dy
      if (nx in 0 until size && ny in 0 until size && !grid[ny][nx].visited) Triple(dir, nx, ny) else null
    }
    if (options.isEmpty()) { stack.removeLast(); continue }
    val (dir, nx, ny) = options.first()
    when (dir) {
      SteadyDir.UP -> { grid[y][x].top = false; grid[ny][nx].bottom = false }
      SteadyDir.DOWN -> { grid[y][x].bottom = false; grid[ny][nx].top = false }
      SteadyDir.LEFT -> { grid[y][x].left = false; grid[ny][nx].right = false }
      SteadyDir.RIGHT -> { grid[y][x].right = false; grid[ny][nx].left = false }
    }
    grid[ny][nx].visited = true
    stack.addLast(nx to ny)
  }
  return grid
}

private fun distanceToSegment(p: Offset, a: Offset, b: Offset): Float {
  val ab = b - a
  val lenSq = ab.x * ab.x + ab.y * ab.y
  if (lenSq == 0f) return (p - a).getDistance()
  val t = (((p.x - a.x) * ab.x + (p.y - a.y) * ab.y) / lenSq).coerceIn(0f, 1f)
  val proj = Offset(a.x + ab.x * t, a.y + ab.y * t)
  return (p - proj).getDistance()
}

@Composable
fun SteadyHandGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val maze = remember { generateSteadyMaze(STEADY_SIZE) }
  var ballCell by remember { mutableStateOf(0f to 0f) }
  var finished by remember { mutableStateOf(false) }
  val goal = STEADY_SIZE - 1f

  fun wallsNear(): List<Pair<Offset, Offset>> {
    val segments = mutableListOf<Pair<Offset, Offset>>()
    for (y in 0 until STEADY_SIZE) for (x in 0 until STEADY_SIZE) {
      val cell = maze[y][x]
      val left = x.toFloat(); val top = y.toFloat(); val right = left + 1f; val bottom = top + 1f
      if (cell.top) segments.add(Offset(left, top) to Offset(right, top))
      if (cell.left) segments.add(Offset(left, top) to Offset(left, bottom))
      if (cell.right) segments.add(Offset(right, top) to Offset(right, bottom))
      if (cell.bottom) segments.add(Offset(left, bottom) to Offset(right, bottom))
    }
    return segments
  }

  val wallSegments = remember { wallsNear() }

  Canvas(
    Modifier
      .fillMaxWidth()
      .aspectRatio(1f)
      .padding(16.dp)
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          if (finished) return@detectDragGestures
          val cellSize = minOf(size.width, size.height).toFloat() / STEADY_SIZE
          val newCell = ballCell.first + dragAmount.x / cellSize to ballCell.second + dragAmount.y / cellSize
          val candidatePx = Offset(newCell.first * cellSize, newCell.second * cellSize)
          val ballRadiusPx = cellSize * 0.16f
          val touchesWall = wallSegments.any { (a, b) ->
            distanceToSegment(candidatePx, Offset(a.x * cellSize, a.y * cellSize), Offset(b.x * cellSize, b.y * cellSize)) < ballRadiusPx + 3f
          }
          if (touchesWall) {
            finished = true
            onComplete(GameResult(0) { it })
            return@detectDragGestures
          }
          ballCell = newCell.first.coerceIn(0f, STEADY_SIZE - 1f) to newCell.second.coerceIn(0f, STEADY_SIZE - 1f)
          if (abs(ballCell.first - goal) < 0.3f && abs(ballCell.second - goal) < 0.3f) {
            finished = true
            val bonus = EconomyConfig.STEADY_HAND_BONUS
            onComplete(GameResult(EconomyConfig.STEADY_HAND_BASE + bonus) { it.copy(bestScore = maxOf(it.bestScore, 1)) })
          }
        }
      }
  ) {
    val cellSize = size.minDimension / STEADY_SIZE
    wallSegments.forEach { (a, b) ->
      drawLine(Border, Offset(a.x * cellSize, a.y * cellSize), Offset(b.x * cellSize, b.y * cellSize), 4f)
    }
    drawCircle(CoinGold, radius = cellSize * 0.22f, center = Offset((goal + 0.5f) * cellSize, (goal + 0.5f) * cellSize))
    drawCircle(
      if (finished) Danger else Primary,
      radius = cellSize * 0.16f,
      center = Offset((ballCell.first + 0.5f) * cellSize, (ballCell.second + 0.5f) * cellSize),
    )
  }
}
