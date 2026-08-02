package com.foxfocus.app.ui.games.games

import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.detectDragGestures
import com.foxfocus.app.R
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.theme.LockedBg
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlin.math.abs

private const val SIZE = 4

private fun emptyBoard(): List<Int> = List(SIZE * SIZE) { 0 }

private fun spawnRandom(board: List<Int>): List<Int> {
  val emptyIndices = board.indices.filter { board[it] == 0 }
  if (emptyIndices.isEmpty()) return board
  val index = emptyIndices.random()
  val value = if ((0..9).random() == 0) 4 else 2
  return board.toMutableList().apply { this[index] = value }
}

private fun mergeLine(line: List<Int>): Pair<List<Int>, Int> {
  val nonZero = line.filter { it != 0 }.toMutableList()
  var score = 0
  var i = 0
  while (i < nonZero.size - 1) {
    if (nonZero[i] == nonZero[i + 1]) {
      nonZero[i] = nonZero[i] * 2
      score += nonZero[i]
      nonZero.removeAt(i + 1)
    }
    i++
  }
  while (nonZero.size < SIZE) nonZero.add(0)
  return nonZero to score
}

private fun rows(board: List<Int>): List<List<Int>> = (0 until SIZE).map { r -> board.subList(r * SIZE, r * SIZE + SIZE) }
private fun cols(board: List<Int>): List<List<Int>> = (0 until SIZE).map { c -> (0 until SIZE).map { r -> board[r * SIZE + c] } }

private fun moveLeft(board: List<Int>): Pair<List<Int>, Int> {
  var score = 0
  val result = rows(board).flatMap { row -> mergeLine(row).also { score += it.second }.first }
  return result to score
}
private fun moveRight(board: List<Int>): Pair<List<Int>, Int> {
  var score = 0
  val result = rows(board).flatMap { row -> mergeLine(row.reversed()).also { score += it.second }.first.reversed() }
  return result to score
}
private fun moveUp(board: List<Int>): Pair<List<Int>, Int> {
  var score = 0
  val newCols = cols(board).map { col -> mergeLine(col).also { score += it.second }.first }
  val result = (0 until SIZE * SIZE).map { i -> newCols[i % SIZE][i / SIZE] }
  return result to score
}
private fun moveDown(board: List<Int>): Pair<List<Int>, Int> {
  var score = 0
  val newCols = cols(board).map { col -> mergeLine(col.reversed()).also { score += it.second }.first.reversed() }
  val result = (0 until SIZE * SIZE).map { i -> newCols[i % SIZE][i / SIZE] }
  return result to score
}

private fun hasMoves(board: List<Int>): Boolean {
  if (board.any { it == 0 }) return true
  for (r in 0 until SIZE) for (c in 0 until SIZE) {
    val v = board[r * SIZE + c]
    if (c < SIZE - 1 && board[r * SIZE + c + 1] == v) return true
    if (r < SIZE - 1 && board[(r + 1) * SIZE + c] == v) return true
  }
  return false
}

private fun tileColor(value: Int): Color = when (value) {
  0 -> LockedBg
  2 -> Color(0xFFFBEFDD)
  4 -> Color(0xFFF6DFB8)
  8 -> Color(0xFFF0C48C)
  16 -> Color(0xFFECA95F)
  32 -> Color(0xFFE8863A)
  64 -> Color(0xFFE06A2A)
  128 -> Color(0xFFD8672A)
  256 -> Color(0xFFC2540C)
  512 -> Color(0xFFA84508)
  1024 -> Color(0xFF8E3806)
  else -> Color(0xFF6F2C05)
}

@Composable
fun Game2048(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  var board by remember { mutableStateOf(spawnRandom(spawnRandom(emptyBoard()))) }
  var score by remember { mutableIntStateOf(0) }
  var finished by remember { mutableStateOf(false) }

  LaunchedEffect(score) { onHud("$score") }

  fun applyMove(mover: (List<Int>) -> Pair<List<Int>, Int>) {
    if (finished) return
    val (moved, gained) = mover(board)
    if (moved == board) return
    score += gained
    board = spawnRandom(moved)
    val won = board.any { it >= 2048 }
    if (won) {
      finished = true
      onComplete(GameResult(EconomyConfig.GAME_2048_WIN_BONUS) { it.copy(bestScore = maxOf(it.bestScore, score)) })
    } else if (!hasMoves(board)) {
      finished = true
      onComplete(GameResult(0) { it.copy(bestScore = maxOf(it.bestScore, score)) })
    }
  }

  Column(
    Modifier
      .fillMaxSize()
      .padding(16.dp)
      .pointerInput(Unit) {
        var dragStart = Offset.Zero
        detectDragGestures(
          onDragStart = { dragStart = it },
          onDragEnd = {},
        ) { change, _ ->
          val delta = change.position - dragStart
          if (abs(delta.x) > 40 || abs(delta.y) > 40) {
            if (abs(delta.x) > abs(delta.y)) {
              applyMove(if (delta.x > 0) ::moveRight else ::moveLeft)
            } else {
              applyMove(if (delta.y > 0) ::moveDown else ::moveUp)
            }
            dragStart = change.position
          }
        }
      },
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(
      Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(LockedBg, RoundedCornerShape(16.dp))
        .padding(6.dp)
    ) {
      Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (r in 0 until SIZE) {
          Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            for (c in 0 until SIZE) {
              val value = board[r * SIZE + c]
              val bg by animateColorAsState(tileColor(value), tween(150), label = "tile")
              Box(
                Modifier.weight(1f).fillMaxSize().background(bg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
              ) {
                if (value != 0) {
                  Text(
                    "$value",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (value <= 4) TextPrimary else Color.White,
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
