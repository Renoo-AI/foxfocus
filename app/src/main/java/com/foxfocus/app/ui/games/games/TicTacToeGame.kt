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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.foxfocus.app.R
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.LockedBg
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlinx.coroutines.delay

private const val PLAYER = 'X'
private const val AI = 'O'

private val WIN_LINES = listOf(
  listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
  listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
  listOf(0, 4, 8), listOf(2, 4, 6),
)

private fun winnerOf(board: List<Char?>): Char? =
  WIN_LINES.firstNotNullOfOrNull { line ->
    val (a, b, c) = line
    if (board[a] != null && board[a] == board[b] && board[b] == board[c]) board[a] else null
  }

private fun minimax(board: MutableList<Char?>, depth: Int, isMax: Boolean, alpha: Int, beta: Int): Int {
  winnerOf(board)?.let { return if (it == AI) 10 - depth else depth - 10 }
  if (board.none { it == null }) return 0

  var a = alpha
  var b = beta
  return if (isMax) {
    var best = Int.MIN_VALUE
    for (i in board.indices) {
      if (board[i] != null) continue
      board[i] = AI
      best = maxOf(best, minimax(board, depth + 1, false, a, b))
      board[i] = null
      a = maxOf(a, best)
      if (b <= a) break
    }
    best
  } else {
    var best = Int.MAX_VALUE
    for (i in board.indices) {
      if (board[i] != null) continue
      board[i] = PLAYER
      best = minOf(best, minimax(board, depth + 1, true, a, b))
      board[i] = null
      b = minOf(b, best)
      if (b <= a) break
    }
    best
  }
}

private fun bestAiMove(board: List<Char?>): Int {
  val working = board.toMutableList()
  var bestScore = Int.MIN_VALUE
  var bestMove = -1
  for (i in working.indices) {
    if (working[i] != null) continue
    working[i] = AI
    val score = minimax(working, 0, false, Int.MIN_VALUE, Int.MAX_VALUE)
    working[i] = null
    if (score > bestScore) { bestScore = score; bestMove = i }
  }
  return bestMove
}

@Composable
fun TicTacToeGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  var board by remember { mutableStateOf<List<Char?>>(List(9) { null }) }
  var playerTurn by remember { mutableStateOf(true) }
  var finished by remember { mutableStateOf(false) }
  var outcome by remember { mutableStateOf<Char?>(null) }

  LaunchedEffect(playerTurn, finished) {
    onHud(if (finished) "" else if (playerTurn) "" else "…")
  }

  fun evaluateEnd(current: List<Char?>) {
    val winner = winnerOf(current)
    if (winner != null) {
      finished = true
      outcome = winner
    } else if (current.none { it == null }) {
      finished = true
      outcome = null
    }
  }

  LaunchedEffect(playerTurn, finished) {
    if (finished || playerTurn) return@LaunchedEffect
    delay(500)
    val move = bestAiMove(board)
    if (move >= 0) {
      board = board.toMutableList().also { it[move] = AI }
      evaluateEnd(board)
    }
    playerTurn = true
  }

  LaunchedEffect(finished) {
    if (!finished) return@LaunchedEffect
    delay(1200)
    val coins = when (outcome) {
      PLAYER -> EconomyConfig.TTT_WIN
      null -> EconomyConfig.TTT_DRAW
      else -> EconomyConfig.TTT_LOSS
    }
    onComplete(GameResult(coins) { it.copy(bestScore = if (outcome == PLAYER) it.bestScore + 1 else it.bestScore) })
  }

  fun onTapCell(index: Int) {
    if (finished || !playerTurn || board[index] != null) return
    board = board.toMutableList().also { it[index] = PLAYER }
    evaluateEnd(board)
    playerTurn = false
  }

  Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      when {
        finished && outcome == PLAYER -> stringResource(R.string.ttt_win)
        finished && outcome == AI -> stringResource(R.string.ttt_lose)
        finished -> stringResource(R.string.ttt_draw)
        playerTurn -> stringResource(R.string.ttt_your_turn)
        else -> stringResource(R.string.ttt_ai_turn)
      },
      style = MaterialTheme.typography.titleLarge,
      color = when {
        finished && outcome == PLAYER -> Success
        finished && outcome == AI -> Danger
        else -> TextPrimary
      },
    )

    androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp))

    Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
      Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (row in 0..2) {
          Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            for (col in 0..2) {
              val index = row * 3 + col
              Box(
                Modifier
                  .weight(1f)
                  .fillMaxSize()
                  .background(LockedBg, RoundedCornerShape(12.dp))
                  .clickable { onTapCell(index) },
                contentAlignment = Alignment.Center,
              ) {
                board[index]?.let {
                  Text(
                    it.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (it == PLAYER) Primary else Danger,
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
