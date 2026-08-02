package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxfocus.app.R
import com.foxfocus.app.data.db.entity.ActivityKind
import com.foxfocus.app.data.db.entity.PlayerStateEntity
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.economy.Tier
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.DangerBg
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.SuccessBg
import com.foxfocus.app.theme.Surface
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.Badge
import com.foxfocus.app.ui.components.BadgeStyle
import com.foxfocus.app.ui.components.CoinPill
import com.foxfocus.app.ui.components.ShadowTier
import com.foxfocus.app.ui.components.foxShadow
import com.foxfocus.app.ui.games.GameTheme
import com.foxfocus.app.ui.games.rememberBlink
import com.foxfocus.app.ui.games.rememberPulse
import com.foxfocus.app.ui.games.rememberShake
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val PATTERNS: Map<Tier, List<Pair<String, String>>> = mapOf(
  Tier.EASY to listOf(
    "😑" to "😐", "😊" to "☺️", "🔴" to "🔵", "⬛" to "⬜", "❌" to "✅",
    "👍" to "👎", "🐱" to "🐶", "🍎" to "🍌", "🌞" to "🌙", "♥️" to "♦️",
  ),
  Tier.MEDIUM to listOf(
    "👿" to "😈", "😎" to "🧐", "🥺" to "😢", "🤔" to "🤨", "🧡" to "❤️",
    "💚" to "💙", "🌍" to "🌎", "⭐" to "🌟", "🚗" to "🚘", "✈️" to "🛩️",
  ),
  Tier.HARD to listOf(
    "🇵🇸" to "🇸🇩", "🇦🇺" to "🇳🇿", "🇯🇴" to "🇰🇼", "🇾🇪" to "🇸🇾",
    "3" to "8", "6" to "9", "0" to "O", "1" to "l",
    "👨" to "👩", "🧑" to "👦", "🟥" to "🟧", "🟦" to "🟪",
    "♠️" to "♣️", "♦️" to "♥️", "🅰️" to "🅱️", "🆎" to "🅾️",
  ),
)

private const val GRID_CELLS = 36

private fun generateRound(tier: Tier): Pair<List<String>, Int> {
  val pattern = PATTERNS.getValue(tier).random()
  val oddIndex = (0 until GRID_CELLS).random()
  val grid = List(GRID_CELLS) { i -> if (i == oddIndex) pattern.second else pattern.first }
  return grid to oddIndex
}

private enum class RoundState { PLAYING, CORRECT, EXHAUSTED }

@Composable
fun OddOneOutGame(theme: GameTheme, repository: FoxRepository, onHud: (String) -> Unit) {
  val scope = rememberCoroutineScope()
  val playerState by repository.playerState.collectAsStateWithLifecycle(initialValue = PlayerStateEntity())

  var streak by remember { mutableIntStateOf(0) }
  var tier by remember { mutableStateOf(Tier.EASY) }
  var roundData by remember { mutableStateOf(generateRound(Tier.EASY)) }
  var wrongAttempts by remember { mutableIntStateOf(0) }
  var roundState by remember { mutableStateOf(RoundState.PLAYING) }
  var message by remember { mutableStateOf<String?>(null) }
  var hintUsedThisRound by remember { mutableStateOf(false) }
  var shakeKey by remember { mutableIntStateOf(0) }
  var pulseKey by remember { mutableIntStateOf(0) }
  var hintKey by remember { mutableIntStateOf(0) }
  var wrongTapIndex by remember { mutableStateOf<Int?>(null) }

  LaunchedEffect(Unit) {
    repository.updateGameStats(GameId.ODD_ONE_OUT) { it.copy(timesPlayed = it.timesPlayed + 1) }
  }

  LaunchedEffect(streak, roundState) {
    onHud("🔥 $streak")
  }

  fun startNewRound() {
    tier = EconomyConfig.oddOneOutTierForStreak(streak)
    roundData = generateRound(tier)
    wrongAttempts = 0
    hintUsedThisRound = false
    roundState = RoundState.PLAYING
    message = null
  }

  fun onCorrect() {
    scope.launch {
      val coins = EconomyConfig.oddOneOutCoinsForTier(tier).let { if (hintUsedThisRound) it / 2 else it }
      repository.incrementMissionCount(ActivityKind.GAME, GameId.ODD_ONE_OUT.name, "Odd One Out", coins)
      streak++
      repository.updateGameStats(GameId.ODD_ONE_OUT) { it.copy(bestScore = maxOf(it.bestScore, streak)) }

      message = when (streak) {
        5 -> {
          repository.incrementMissionCount(ActivityKind.GAME, GameId.ODD_ONE_OUT.name, "Odd One Out streak", EconomyConfig.ODD_STREAK_BONUS_5)
          "odd_streak5"
        }
        10 -> {
          repository.incrementMissionCount(ActivityKind.GAME, GameId.ODD_ONE_OUT.name, "Odd One Out streak", EconomyConfig.ODD_STREAK_BONUS_10)
          "odd_streak10"
        }
        20 -> {
          repository.incrementMissionCount(ActivityKind.GAME, GameId.ODD_ONE_OUT.name, "Odd One Out streak", EconomyConfig.ODD_STREAK_BONUS_20)
          "odd_streak20"
        }
        else -> "odd_correct"
      }
      pulseKey++
      roundState = RoundState.CORRECT
      delay(1500)
      startNewRound()
    }
  }

  fun onWrong(tappedIndex: Int) {
    wrongAttempts++
    streak = 0
    shakeKey++
    wrongTapIndex = tappedIndex
    scope.launch {
      delay(500)
      wrongTapIndex = null
    }
    if (wrongAttempts >= EconomyConfig.ODD_MAX_WRONG_ATTEMPTS) {
      roundState = RoundState.EXHAUSTED
      message = "odd_out_of_attempts"
      scope.launch {
        delay(2000)
        startNewRound()
      }
    } else {
      message = "odd_wrong"
    }
  }

  fun onTapCell(index: Int) {
    if (roundState != RoundState.PLAYING) return
    if (index == roundData.second) onCorrect() else onWrong(index)
  }

  fun onUseHint() {
    if (hintUsedThisRound || roundState != RoundState.PLAYING) return
    if (playerState.coinBalance < EconomyConfig.ODD_HINT_COST) return
    scope.launch {
      val ok = repository.spendCoinsGeneric(EconomyConfig.ODD_HINT_COST, GameId.ODD_ONE_OUT.name, "تلميح")
      if (ok) {
        hintUsedThisRound = true
        hintKey++
      }
    }
  }

  val shake = rememberShake(if (shakeKey > 0) shakeKey else null)
  val pulse = rememberPulse(if (pulseKey > 0) pulseKey else null)
  val hintBlink = rememberBlink(if (hintKey > 0) hintKey else null)

  Column(Modifier.fillMaxSize().padding(12.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      CoinPill(amount = playerState.coinBalance)
      Badge(text = tierLabel(tier), style = BadgeStyle.GOLD)
      Text(stringResource(R.string.odd_attempts, wrongAttempts, EconomyConfig.ODD_MAX_WRONG_ATTEMPTS), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
    Spacer(Modifier.height(8.dp))

    message?.let {
      Text(
        text = messageText(it),
        style = MaterialTheme.typography.titleMedium,
        color = if (it == "odd_wrong" || it == "odd_out_of_attempts") Danger else Success,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
      )
      Spacer(Modifier.height(6.dp))
    }

    LazyVerticalGrid(
      columns = GridCells.Fixed(6),
      modifier = Modifier.fillMaxWidth().graphicsLayer { translationX = shake.value },
    ) {
      items(roundData.first.size) { index ->
        val isOdd = index == roundData.second
        val revealGreen = roundState == RoundState.CORRECT && isOdd
        val revealYellow = roundState == RoundState.EXHAUSTED && isOdd
        val flashRed = wrongTapIndex == index
        val bg = when {
          revealGreen -> SuccessBg
          revealYellow -> Color(0xFFFCF0C8)
          flashRed -> DangerBg
          else -> Surface
        }
        Box(
          Modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .foxShadow(10, ShadowTier.SM)
            .background(bg, RoundedCornerShape(10.dp))
            .then(
              if (isOdd) Modifier.scale(if (revealGreen) pulse.value else 1f).alpha(if (revealYellow) 0.6f + hintBlink.value * 0.4f else 1f)
              else Modifier
            )
            .clickable(enabled = roundState == RoundState.PLAYING) { onTapCell(index) },
          contentAlignment = Alignment.Center,
        ) {
          Text(roundData.first[index], style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        }
      }
    }

    Spacer(Modifier.height(8.dp))
    val hintAvailable = !hintUsedThisRound && roundState == RoundState.PLAYING && playerState.coinBalance >= EconomyConfig.ODD_HINT_COST
    Box(
      Modifier
        .fillMaxWidth()
        .height(44.dp)
        .background(if (hintAvailable) Color(0xFFFCF0C8) else com.foxfocus.app.theme.LockedBg, RoundedCornerShape(12.dp))
        .then(if (hintAvailable) Modifier.clickable { onUseHint() } else Modifier),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        stringResource(R.string.odd_hint_cost, EconomyConfig.ODD_HINT_COST),
        style = MaterialTheme.typography.labelLarge,
        color = if (hintAvailable) TextPrimary else TextSecondary,
      )
    }
  }
}

@Composable
private fun tierLabel(tier: Tier): String = when (tier) {
  Tier.EASY -> stringResource(R.string.odd_level_easy)
  Tier.MEDIUM -> stringResource(R.string.odd_level_medium)
  Tier.HARD -> stringResource(R.string.odd_level_hard)
}

@Composable
private fun messageText(key: String): String = when (key) {
  "odd_correct" -> stringResource(R.string.odd_correct)
  "odd_wrong" -> stringResource(R.string.odd_wrong)
  "odd_out_of_attempts" -> stringResource(R.string.odd_out_of_attempts)
  "odd_streak5" -> stringResource(R.string.odd_streak5)
  "odd_streak10" -> stringResource(R.string.odd_streak10)
  "odd_streak20" -> stringResource(R.string.odd_streak20)
  else -> ""
}
