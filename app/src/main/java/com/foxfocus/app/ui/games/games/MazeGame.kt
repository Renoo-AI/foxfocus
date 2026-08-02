package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.theme.Border
import com.foxfocus.app.theme.CoinGold
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlin.math.abs
import kotlin.random.Random

private const val MAZE_SIZE = 11

private class MazeCell {
  var top = true
  var right = true
  var bottom = true
  var left = true
  var visited = false
}

private enum class Dir(val dx: Int, val dy: Int) { UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0) }

private fun generateMaze(size: Int): Array<Array<MazeCell>> {
  val grid = Array(size) { Array(size) { MazeCell() } }
  val stack = ArrayDeque<Pair<Int, Int>>()
  var (cx, cy) = 0 to 0
  grid[cy][cx].visited = true
  stack.addLast(cx to cy)

  while (stack.isNotEmpty()) {
    val (x, y) = stack.last()
    val neighbors = Dir.entries.shuffled().mapNotNull { dir ->
      val nx = x + dir.dx
      val ny = y + dir.dy
      if (nx in 0 until size && ny in 0 until size && !grid[ny][nx].visited) Triple(dir, nx, ny) else null
    }
    if (neighbors.isEmpty()) {
      stack.removeLast()
      continue
    }
    val (dir, nx, ny) = neighbors.first()
    when (dir) {
      Dir.UP -> { grid[y][x].top = false; grid[ny][nx].bottom = false }
      Dir.DOWN -> { grid[y][x].bottom = false; grid[ny][nx].top = false }
      Dir.LEFT -> { grid[y][x].left = false; grid[ny][nx].right = false }
      Dir.RIGHT -> { grid[y][x].right = false; grid[ny][nx].left = false }
    }
    grid[ny][nx].visited = true
    stack.addLast(nx to ny)
  }
  return grid
}

@Composable
fun MazeGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val economy = remember { EconomyConfig.economyFor(GameId.MAZE) }
  val maze = remember { generateMaze(MAZE_SIZE) }
  var playerX by remember { mutableIntStateOf(0) }
  var playerY by remember { mutableIntStateOf(0) }
  var moves by remember { mutableIntStateOf(0) }
  val goal = MAZE_SIZE - 1

  LaunchedEffect(playerX, playerY) {
    val distance = abs(goal - playerX) + abs(goal - playerY)
    onHud("$distance خطوة")
  }

  fun move(dir: Dir) {
    val cell = maze[playerY][playerX]
    val canMove = when (dir) {
      Dir.UP -> !cell.top
      Dir.DOWN -> !cell.bottom
      Dir.LEFT -> !cell.left
      Dir.RIGHT -> !cell.right
    }
    if (!canMove) return
    playerX += dir.dx
    playerY += dir.dy
    moves++
    if (playerX == goal && playerY == goal) {
      val bonus = if (moves <= economy.bonusThreshold) economy.bonusCoins else 0
      onComplete(GameResult(economy.baseCoins + bonus) { it.copy(bestScore = maxOf(it.bestScore, 1)) })
    }
  }

  Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Box(
      Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
    ) {
      var dragStart by remember { mutableStateOf(Offset.Zero) }
      Canvas(
        Modifier
          .fillMaxSize()
          .pointerInput(Unit) {
            detectDragGestures(
              onDragStart = { dragStart = it },
              onDragEnd = {},
            ) { change, _ ->
              val delta = change.position - dragStart
              if (abs(delta.x) > 40 || abs(delta.y) > 40) {
                if (abs(delta.x) > abs(delta.y)) {
                  move(if (delta.x > 0) Dir.RIGHT else Dir.LEFT)
                } else {
                  move(if (delta.y > 0) Dir.DOWN else Dir.UP)
                }
                dragStart = change.position
              }
            }
          }
      ) {
        val cellSize = size.minDimension / MAZE_SIZE
        for (y in 0 until MAZE_SIZE) {
          for (x in 0 until MAZE_SIZE) {
            val cell = maze[y][x]
            val left = x * cellSize
            val top = y * cellSize
            val right = left + cellSize
            val bottom = top + cellSize
            val strokeWidth = 3f
            if (cell.top) drawLine(Border.copy(alpha = 1f), Offset(left, top), Offset(right, top), strokeWidth)
            if (cell.left) drawLine(Border.copy(alpha = 1f), Offset(left, top), Offset(left, bottom), strokeWidth)
            if (cell.right) drawLine(Border.copy(alpha = 1f), Offset(right, top), Offset(right, bottom), strokeWidth)
            if (cell.bottom) drawLine(Border.copy(alpha = 1f), Offset(left, bottom), Offset(right, bottom), strokeWidth)
          }
        }
        val goalCenter = Offset((goal + 0.5f) * cellSize, (goal + 0.5f) * cellSize)
        drawCircle(CoinGold, radius = cellSize * 0.3f, center = goalCenter)
        val playerCenter = Offset((playerX + 0.5f) * cellSize, (playerY + 0.5f) * cellSize)
        drawCircle(Primary, radius = cellSize * 0.3f, center = playerCenter)
      }
    }

    Column(
      Modifier.padding(top = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      IconButton(onClick = { move(Dir.UP) }) { Icon(Icons.Filled.KeyboardArrowUp, null, tint = TextPrimary) }
      Row {
        IconButton(onClick = { move(Dir.LEFT) }) { Icon(Icons.Filled.KeyboardArrowLeft, null, tint = TextPrimary) }
        androidx.compose.foundation.layout.Spacer(Modifier.size(48.dp))
        IconButton(onClick = { move(Dir.RIGHT) }) { Icon(Icons.Filled.KeyboardArrowRight, null, tint = TextPrimary) }
      }
      IconButton(onClick = { move(Dir.DOWN) }) { Icon(Icons.Filled.KeyboardArrowDown, null, tint = TextPrimary) }
    }
  }
}
