package com.foxfocus.app.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.foxfocus.app.data.db.entity.ActivityLogEntity
import com.foxfocus.app.data.db.entity.PlayerStateEntity
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.HeroGradientBottom
import com.foxfocus.app.theme.HeroGradientTop
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.StreakFlame
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.Badge
import com.foxfocus.app.ui.components.BadgeStyle
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.FoxProgressBar
import com.foxfocus.app.ui.components.StatCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(repository: FoxRepository) {
  val activity by repository.activityLog.collectAsStateWithLifecycle(initialValue = emptyList())
  val lifetimeCoins by repository.lifetimeCoinsEarned.collectAsStateWithLifecycle(initialValue = 0)
  val playerState by repository.playerState.collectAsStateWithLifecycle(initialValue = PlayerStateEntity())

  val baselineHours = 4.5f
  val currentHours = 2.1f
  val savingsPercent = ((baselineHours - currentHours) / baselineHours * 100).toInt()

  Column(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      Text("📜 سجل النشاط وتوفر الوقت", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
      Badge(text = "مباشر ⚡", style = BadgeStyle.SUCCESS)
    }

    // Baseline Screen Time Savings Card
    Box(
      Modifier
        .fillMaxWidth()
        .background(
          Brush.linearGradient(listOf(HeroGradientTop, HeroGradientBottom)),
          RoundedCornerShape(20.dp)
        )
        .padding(16.dp)
    ) {
      Column {
        Text("⏱️ وفرت 2.4 ساعة يومياً (وفر $savingsPercent%)", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text("معدلك السابق: $baselineHours ساعة/يوم ➔ المعدل الحالي: $currentHours ساعة/يوم", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        FoxProgressBar(progress = 0.53f)
      }
    }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      StatCard(label = "إجمالي العملات", value = "$lifetimeCoins FC", modifier = Modifier.weight(1f))
      StatCard(label = "أفضل تتابع", value = "🔥 ${playerState.bestStreakDays} يوم", modifier = Modifier.weight(1f), valueColor = StreakFlame)
    }

    Text("سجل الأحداث الأخيرة", style = MaterialTheme.typography.titleSmall, color = TextSecondary)

    if (activity.isEmpty()) {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Text("لا يوجد نشاط مسجل بعد — كمل أول جلسة تركيز!", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
      }
    } else {
      LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(activity, key = { it.id }) { entry -> ActivityRow(entry) }
      }
    }
  }
}

private val timeFormat = SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault())

@Composable
private fun ActivityRow(entry: ActivityLogEntity) {
  FoxCard(modifier = Modifier.fillMaxWidth(), paddingDp = 12) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      Column {
        Text(entry.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Text(timeFormat.format(Date(entry.timestampEpochMillis)), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
      }
      Text(
        if (entry.coinsDelta >= 0) "+${entry.coinsDelta} FC" else "${entry.coinsDelta} FC",
        style = MaterialTheme.typography.labelLarge,
        color = if (entry.coinsDelta >= 0) Success else Danger,
      )
    }
  }
}
