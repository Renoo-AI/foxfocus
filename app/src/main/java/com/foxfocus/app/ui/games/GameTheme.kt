package com.foxfocus.app.ui.games

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ScatterPlot
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.foxfocus.app.R
import com.foxfocus.app.economy.GameId

/** Per-game visual identity — icon + accent pair. Same card shape and button shape everywhere;
 *  only the color and icon change per game, per the "small, tasteful edits" brief. */
data class GameTheme(
  val icon: ImageVector,
  val accent: Color,
  val accentBg: Color,
  val nameRes: Int,
  val descRes: Int,
)

val GAME_THEMES: Map<GameId, GameTheme> = mapOf(
  GameId.SCHULTE to GameTheme(Icons.Filled.GridOn, Color(0xFF4C6FE7), Color(0xFFE8ECFB), R.string.game_schulte_name, R.string.game_schulte_desc),
  GameId.QUICK_MATH to GameTheme(Icons.Filled.Calculate, Color(0xFF2FA79B), Color(0xFFE1F5F2), R.string.game_math_name, R.string.game_math_desc),
  GameId.NUMBER_BALANCE to GameTheme(Icons.Filled.Scale, Color(0xFFC6852E), Color(0xFFFBEFDD), R.string.game_balance_name, R.string.game_balance_desc),
  GameId.MAZE to GameTheme(Icons.Filled.Explore, Color(0xFF6FA84B), Color(0xFFE9F3E1), R.string.game_maze_name, R.string.game_maze_desc),
  GameId.MEMORY_MATCH to GameTheme(Icons.Filled.Style, Color(0xFFD46FA0), Color(0xFFFBE7F0), R.string.game_memory_name, R.string.game_memory_desc),
  GameId.STROOP to GameTheme(Icons.Filled.InvertColors, Color(0xFFD8443C), Color(0xFFFDE8E7), R.string.game_stroop_name, R.string.game_stroop_desc),
  GameId.COLOR_SORT to GameTheme(Icons.Filled.Science, Color(0xFF9B6FC7), Color(0xFFF0EAF7), R.string.game_colorsort_name, R.string.game_colorsort_desc),
  GameId.FLOW_FREE to GameTheme(Icons.Filled.Timeline, Color(0xFF2E9BC0), Color(0xFFE1F2F7), R.string.game_flow_name, R.string.game_flow_desc),
  GameId.REFLEX to GameTheme(Icons.Filled.Bolt, Color(0xFFFF7A29), Color(0xFFFFE8DC), R.string.game_reflex_name, R.string.game_reflex_desc),
  GameId.SIMON_SAYS to GameTheme(Icons.Filled.Apps, Color(0xFF7A52A3), Color(0xFFEEE3F6), R.string.game_simon_name, R.string.game_simon_desc),
  GameId.DUAL_N_BACK to GameTheme(Icons.Filled.Psychology, Color(0xFF4A5568), Color(0xFFE7EAEE), R.string.game_nback_name, R.string.game_nback_desc),
  GameId.SLIDING_PUZZLE to GameTheme(Icons.Filled.Extension, Color(0xFFD8672A), Color(0xFFFFEADB), R.string.game_puzzle_name, R.string.game_puzzle_desc),

  GameId.ODD_ONE_OUT to GameTheme(Icons.Filled.Search, Color(0xFF3E8E7E), Color(0xFFE1F2EE), R.string.game_odd_name, R.string.game_odd_desc),
  GameId.GAME_2048 to GameTheme(Icons.Filled.ViewModule, Color(0xFFC2540C), Color(0xFFFBE7D9), R.string.game_2048_name, R.string.game_2048_desc),
  GameId.PUZZLE_8 to GameTheme(Icons.Filled.Extension, Color(0xFF4A6FA5), Color(0xFFE5ECF5), R.string.game_puzzle8_name, R.string.game_puzzle8_desc),
  GameId.STEADY_HAND to GameTheme(Icons.Filled.TouchApp, Color(0xFFB98A1E), Color(0xFFFAF0DA), R.string.game_steady_name, R.string.game_steady_desc),
  GameId.WHERE_BALL to GameTheme(Icons.Filled.Visibility, Color(0xFF8A5B3C), Color(0xFFF3E7DC), R.string.game_whereball_name, R.string.game_whereball_desc),
  GameId.FALLING_CATCH to GameTheme(Icons.Filled.ArrowDownward, Color(0xFF3E7BC4), Color(0xFFE2ECF9), R.string.game_falling_name, R.string.game_falling_desc),
  GameId.CONNECT_DOTS to GameTheme(Icons.Filled.ScatterPlot, Color(0xFF5A5FC7), Color(0xFFE9E9F9), R.string.game_connectdots_name, R.string.game_connectdots_desc),
  GameId.RPS_PREDICTOR to GameTheme(Icons.Filled.Casino, Color(0xFFA13F4C), Color(0xFFF6E3E6), R.string.game_rps_name, R.string.game_rps_desc),
  GameId.GUESS_NUMBER to GameTheme(Icons.Filled.Help, Color(0xFF7E5AA8), Color(0xFFEEE5F5), R.string.game_guess_name, R.string.game_guess_desc),
  GameId.TIC_TAC_TOE to GameTheme(Icons.Filled.Close, Color(0xFF2F8F7A), Color(0xFFE0F0EC), R.string.game_ttt_name, R.string.game_ttt_desc),
)
