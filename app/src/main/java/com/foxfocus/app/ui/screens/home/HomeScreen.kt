package com.foxfocus.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxfocus.app.data.db.entity.PlayerStateEntity
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.theme.HeroGradientBottom
import com.foxfocus.app.theme.HeroGradientTop
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.Badge
import com.foxfocus.app.ui.components.BadgeStyle
import com.foxfocus.app.ui.components.CoinPill
import com.foxfocus.app.ui.components.FinnMascot
import com.foxfocus.app.ui.components.FinnPose
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.FoxProgressBar
import com.foxfocus.app.ui.components.PrimaryButton
import com.foxfocus.app.ui.components.SecondaryButton

@Composable
fun HomeScreen(
  repository: FoxRepository,
  onNavigateToMissions: () -> Unit,
  onNavigateToStore: () -> Unit = {},
  onNavigateToHistory: () -> Unit = {},
) {
  val playerState by repository.playerState.collectAsStateWithLifecycle(initialValue = PlayerStateEntity())
  val blockedApps by repository.activeBlockedApps.collectAsStateWithLifecycle(initialValue = emptyList())
  val dailyDeal by repository.dailyDeal.collectAsStateWithLifecycle(initialValue = null)
  val pose = if (playerState.streakDays <= 0) FinnPose.SAD else FinnPose.DEFAULT
  val dailyCap = EconomyConfig.dailyCap(playerState.level)
  val dailyProgress = if (dailyCap > 0) playerState.dailyCoinsEarnedToday.toFloat() / dailyCap else 0f
  val scrollState = rememberScrollState()

  Column(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(scrollState)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    // Header with Dual Currency & Streak Freeze
    Row(
      Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        CoinPill(amount = playerState.coinBalance)
        Box(
          Modifier
            .background(Color(0xFFE0F7FA), RoundedCornerShape(99.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Text("💎 ${String.format("%.1f", playerState.diamondBalance)}", style = MaterialTheme.typography.labelMedium, color = Color(0xFF00838F))
        }
      }
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Badge(text = "🛡️ ${playerState.streakFreezeCount}", style = BadgeStyle.SUCCESS)
        Badge(text = "🔥 ${playerState.streakDays}", style = BadgeStyle.FLAME)
      }
    }

    // Hero Mascot Banner
    Row(
      Modifier
        .fillMaxWidth()
        .background(
          Brush.linearGradient(listOf(HeroGradientTop, HeroGradientBottom)),
          RoundedCornerShape(20.dp),
        )
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      FinnMascot(pose, size = 72.dp)
      Spacer(Modifier.width(12.dp))
      Column {
        Text("مرحباً بك في FoxFocus 🦊", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        Text("${playerState.coinBalance} FC | ${String.format("%.1f", playerState.diamondBalance)} 💎", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        if (playerState.isPremium) {
          Text("👑 مشترك المميز (Premium Active)", style = MaterialTheme.typography.labelSmall, color = Primary)
        }
      }
    }

    // Daily Deal Alert Card
    dailyDeal?.let { deal ->
      Box(
        Modifier
          .fillMaxWidth()
          .background(Color(0xFFFFF3E4), RoundedCornerShape(16.dp))
          .padding(14.dp)
      ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column(Modifier.weight(1f)) {
            Text("🔥 عرض اليوم: ${deal.titleAr}", style = MaterialTheme.typography.titleSmall, color = Primary)
            Text("خصم ${deal.discountPercent}% ينتهي اليوم!", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
          PrimaryButton(text = "عرض", onClick = onNavigateToStore)
        }
      }
    }

    // Daily Cap Progress & History Trigger
    FoxCard(modifier = Modifier.fillMaxWidth()) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("سقف اليوم", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
        Text("${playerState.dailyCoinsEarnedToday} / $dailyCap", style = MaterialTheme.typography.labelLarge, color = Success)
      }
      Spacer(Modifier.height(8.dp))
      FoxProgressBar(progress = dailyProgress)
      Spacer(Modifier.height(10.dp))
      SecondaryButton(text = "📜 عرض سجل النشاط بالكامل (History)", onClick = onNavigateToHistory)
    }

    // Blocked Apps Card
    FoxCard(modifier = Modifier.fillMaxWidth()) {
      Text(
        if (blockedApps.isEmpty()) "لا توجد تطبيقات محمية" else "${blockedApps.size} تطبيقات محمية",
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
      )
      Spacer(Modifier.height(4.dp))
      Text(
        blockedApps.joinToString(", ") { it.appName }.ifEmpty { "أضف تطبيقات من تبويب الحظر" },
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
      )
    }

    // Mind Games Call to Action
    FoxCard(modifier = Modifier.fillMaxWidth()) {
      Text("ألعاب الذهن والتركيز", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
      Spacer(Modifier.height(4.dp))
      Text("انتهت عملاتك؟ العب 22 لعبة ذهنية لتربح المزيد من Fox Coins!", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
      Spacer(Modifier.height(12.dp))
      PrimaryButton(text = "ابدأ اللعب الآن", onClick = onNavigateToMissions)
    }
  }
}
