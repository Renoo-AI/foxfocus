package com.foxfocus.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.theme.Background
import com.foxfocus.app.ui.games.CompletionOverlay
import com.foxfocus.app.ui.games.GAME_THEMES
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTopBar
import com.foxfocus.app.ui.games.games.ColorSortGame
import com.foxfocus.app.ui.games.games.ConnectDotsGame
import com.foxfocus.app.ui.games.games.DualNBackGame
import com.foxfocus.app.ui.games.games.FallingCatchGame
import com.foxfocus.app.ui.games.games.FlowFreeGame
import com.foxfocus.app.ui.games.games.Game2048
import com.foxfocus.app.ui.games.games.GuessNumberGame
import com.foxfocus.app.ui.games.games.MazeGame
import com.foxfocus.app.ui.games.games.MemoryMatchGame
import com.foxfocus.app.ui.games.games.NumberBalanceGame
import com.foxfocus.app.ui.games.games.OddOneOutGame
import com.foxfocus.app.ui.games.games.Puzzle8Game
import com.foxfocus.app.ui.games.games.QuickMathGame
import com.foxfocus.app.ui.games.games.ReflexTestGame
import com.foxfocus.app.ui.games.games.RpsPredictorGame
import com.foxfocus.app.ui.games.games.SchulteTableGame
import com.foxfocus.app.ui.games.games.SimonSaysGame
import com.foxfocus.app.ui.games.games.SlidingPuzzleGame
import com.foxfocus.app.ui.games.games.SteadyHandGame
import com.foxfocus.app.ui.games.games.StroopTestGame
import com.foxfocus.app.ui.games.games.TicTacToeGame
import com.foxfocus.app.ui.games.games.WhereBallGame

@Composable
fun GamePlayScreen(gameId: GameId, repository: FoxRepository, onExit: () -> Unit) {
  val theme = GAME_THEMES.getValue(gameId)
  val title = stringResource(theme.nameRes)

  // Odd One Out is an endless session (no single terminal round) — it awards coins and
  // spends hints live via the repository instead of the one-shot GameResult flow below.
  if (gameId == GameId.ODD_ONE_OUT) {
    var hud by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.lerp(Background, theme.accentBg, 0.55f))) {
      GameTopBar(title = title, accent = theme.accent, hud = hud, onExit = onExit)
      OddOneOutGame(theme, repository) { hud = it }
    }
    return
  }

  var hud by remember(gameId) { mutableStateOf("") }
  var result by remember(gameId) { mutableStateOf<GameResult?>(null) }
  var awarded by remember(gameId) { mutableStateOf<Int?>(null) }

  LaunchedEffect(result) {
    val r = result ?: return@LaunchedEffect
    awarded = repository.recordGameResult(gameId, r.rawCoins, title, r.statsUpdate)
  }

  Column(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.lerp(Background, theme.accentBg, 0.55f))) {
    GameTopBar(title = title, accent = theme.accent, hud = hud, onExit = onExit)
    Box(Modifier.fillMaxSize()) {
      if (result == null) {
        val onHud: (String) -> Unit = { hud = it }
        val onComplete: (GameResult) -> Unit = { result = it }
        when (gameId) {
          GameId.SCHULTE -> SchulteTableGame(theme, onHud, onComplete)
          GameId.QUICK_MATH -> QuickMathGame(theme, onHud, onComplete)
          GameId.NUMBER_BALANCE -> NumberBalanceGame(theme, onHud, onComplete)
          GameId.MAZE -> MazeGame(theme, onHud, onComplete)
          GameId.MEMORY_MATCH -> MemoryMatchGame(theme, onHud, onComplete)
          GameId.STROOP -> StroopTestGame(theme, onHud, onComplete)
          GameId.COLOR_SORT -> ColorSortGame(theme, onHud, onComplete)
          GameId.FLOW_FREE -> FlowFreeGame(theme, onHud, onComplete)
          GameId.REFLEX -> ReflexTestGame(theme, onHud, onComplete)
          GameId.SIMON_SAYS -> SimonSaysGame(theme, onHud, onComplete)
          GameId.DUAL_N_BACK -> DualNBackGame(theme, onHud, onComplete)
          GameId.SLIDING_PUZZLE -> SlidingPuzzleGame(theme, onHud, onComplete)
          GameId.GAME_2048 -> Game2048(theme, onHud, onComplete)
          GameId.PUZZLE_8 -> Puzzle8Game(theme, onHud, onComplete)
          GameId.STEADY_HAND -> SteadyHandGame(theme, onHud, onComplete)
          GameId.WHERE_BALL -> WhereBallGame(theme, onHud, onComplete)
          GameId.FALLING_CATCH -> FallingCatchGame(theme, onHud, onComplete)
          GameId.CONNECT_DOTS -> ConnectDotsGame(theme, onHud, onComplete)
          GameId.RPS_PREDICTOR -> RpsPredictorGame(theme, onHud, onComplete)
          GameId.GUESS_NUMBER -> GuessNumberGame(theme, onHud, onComplete)
          GameId.TIC_TAC_TOE -> TicTacToeGame(theme, onHud, onComplete)
          GameId.ODD_ONE_OUT -> Unit // handled above, unreachable
        }
      }
      awarded?.let { coins ->
        CompletionOverlay(coins = coins, bonusEarned = false, onDone = onExit)
      }
    }
  }
}
