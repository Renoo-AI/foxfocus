package com.foxfocus.app.ui.games.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.economy.scaledReward
import com.foxfocus.app.theme.Border
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.DangerBg
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.SuccessBg
import com.foxfocus.app.theme.Surface
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.ui.components.ShadowTier
import com.foxfocus.app.ui.components.foxShadow
import com.foxfocus.app.ui.components.FoxProgressBar
import com.foxfocus.app.ui.games.GameResult
import com.foxfocus.app.ui.games.GameTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class MathRound(val prompt: String, val correct: Int, val options: List<Int>)

private fun generateMathRound(): MathRound {
  val op = listOf('+', '-', '×').random()
  var a = Random.nextInt(1, 13)
  var b = Random.nextInt(1, 13)
  val correct = when (op) {
    '+' -> a + b
    '-' -> { if (a < b) { val t = a; a = b; b = t }; a - b }
    else -> {
      while (a * b >= 50) b = Random.nextInt(1, maxOf(2, (49 / a) + 1))
      a * b
    }
  }
  val distractors = mutableSetOf<Int>()
  var spread = 5
  while (distractors.size < 3 && spread < 30) {
    val candidate = (correct - spread..correct + spread).filter { it != correct && it in 1..49 }
    distractors.addAll(candidate.shuffled().take(3 - distractors.size))
    spread += 5
  }
  val options = (distractors.take(3) + correct).shuffled()
  return MathRound("$a $op $b", correct, options)
}

@Composable
fun QuickMathGame(theme: GameTheme, onHud: (String) -> Unit, onComplete: (GameResult) -> Unit) {
  val economy = remember { EconomyConfig.economyFor(GameId.QUICK_MATH) }
  var round by remember { mutableIntStateOf(1) }
  var correctCount by remember { mutableIntStateOf(0) }
  var current by remember { mutableStateOf(generateMathRound()) }
  var picked by remember { mutableStateOf<Int?>(null) }

  LaunchedEffect(round) { onHud("$round / ${economy.rounds}") }

  LaunchedEffect(picked) {
    if (picked == null) return@LaunchedEffect
    delay(400)
    picked = null
    if (round >= economy.rounds) {
      onComplete(GameResult(scaledReward(economy, correctCount)) { it.copy(bestScore = maxOf(it.bestScore, correctCount)) })
    } else {
      round++
      current = generateMathRound()
    }
  }

  Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    FoxProgressBar(progress = (round - 1f) / economy.rounds, fillColor = theme.accent)
    Box(Modifier.fillMaxSize(0.35f), contentAlignment = Alignment.Center) {
      Text(current.prompt, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
    }
    current.options.forEach { option ->
      val isCorrect = option == current.correct
      val isPicked = option == picked
      val bg = when {
        picked != null && isCorrect -> SuccessBg
        picked != null && isPicked -> DangerBg
        else -> Surface
      }
      val fg = when {
        picked != null && isCorrect -> Success
        picked != null && isPicked -> Danger
        else -> TextPrimary
      }
      val borderColor = if (picked == null) Border else if (isCorrect) Success else if (isPicked) Danger else Border
      Box(
        Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
          .foxShadow(14, ShadowTier.SM)
          .background(bg, RoundedCornerShape(14.dp))
          .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
          .then(if (picked == null) Modifier.clickable {
            correctCount += if (isCorrect) 1 else 0
            picked = option
          } else Modifier)
          .padding(vertical = 14.dp),
      ) {
        Text("$option", style = MaterialTheme.typography.titleMedium, color = fg, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
      }
    }
  }
}
