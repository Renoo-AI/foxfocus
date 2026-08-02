package com.foxfocus.app.ui.screens.missions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.foxfocus.app.R
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameEconomy
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.theme.CategoryMindBg
import com.foxfocus.app.theme.CategoryMindIcon
import com.foxfocus.app.theme.Surface
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.games.GAME_THEMES

@Composable
fun MissionsScreen(repository: FoxRepository, onPlayGame: (GameId) -> Unit) {
  LazyColumn(
    Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    item {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          Modifier.size(28.dp).background(CategoryMindBg, CircleShape),
          contentAlignment = Alignment.Center,
        ) {
          Text("🧠", style = MaterialTheme.typography.bodyMedium)
        }
        Text(
          "  " + stringResource(R.string.category_mind_games),
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimary,
        )
      }
    }

    items(GameId.entries, key = { it.name }) { gameId ->
      MindGameCard(gameId, EconomyConfig.economyFor(gameId), onClick = { onPlayGame(gameId) })
    }
  }
}

@Composable
private fun MindGameCard(gameId: GameId, economy: GameEconomy, onClick: () -> Unit) {
  val theme = GAME_THEMES.getValue(gameId)
  FoxCard(modifier = Modifier.fillMaxWidth(), paddingDp = 12) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      Box(
        Modifier.size(48.dp).background(theme.accentBg, CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Icon(theme.icon, contentDescription = null, tint = theme.accent, modifier = Modifier.size(24.dp))
      }
      Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
        Text(stringResource(theme.nameRes), style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Text(stringResource(theme.descRes), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
      }
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          "+${previewReward(economy)}",
          style = MaterialTheme.typography.labelMedium,
          color = theme.accent,
        )
        Box(
          Modifier
            .padding(top = 4.dp)
            .background(theme.accent, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
          Text(stringResource(R.string.btn_start), style = MaterialTheme.typography.labelMedium, color = Surface)
        }
      }
    }
  }
}

private fun previewReward(economy: GameEconomy): Int = when {
  economy.baseCoins > 0 -> economy.baseCoins + economy.bonusCoins
  economy.performanceMax > 0 -> economy.performanceMax
  else -> economy.baseCoins
}
