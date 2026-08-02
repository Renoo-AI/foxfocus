package com.foxfocus.app.ui.screens.leaderboard

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxfocus.app.data.db.entity.LeaderboardEntity
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.theme.HeroGradientBottom
import com.foxfocus.app.theme.HeroGradientTop
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.Badge
import com.foxfocus.app.ui.components.BadgeStyle
import com.foxfocus.app.ui.components.FinnMascot
import com.foxfocus.app.ui.components.FinnPose
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.PrimaryButton
import com.foxfocus.app.ui.components.SecondaryButton

@Composable
fun LeaderboardScreen(repository: FoxRepository) {
  val leaderboardList by repository.leaderboard.collectAsStateWithLifecycle(initialValue = emptyList())
  var activeTab by remember { mutableStateOf("global") } // global vs weekly

  val userEntry = leaderboardList.find { it.isUser }

  Column(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      Text("🏆 قائمة المتصدرين (Global Ranks)", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
      Badge(text = "XP Live ⚡", style = BadgeStyle.GOLD)
    }

    // Personal Rank Banner
    userEntry?.let { entry ->
      Box(
        Modifier
          .fillMaxWidth()
          .background(
            Brush.linearGradient(listOf(HeroGradientTop, HeroGradientBottom)),
            RoundedCornerShape(20.dp)
          )
          .padding(16.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          FinnMascot(pose = FinnPose.CELEBRATING, size = 64.dp)
          Spacer(Modifier.width(12.dp))
          Column(Modifier.weight(1f)) {
            Text("ترتيبك العالمي الحالي: #${entry.rank}", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text("${entry.username} — ${entry.xp} XP | سلسلة ${entry.streakDays} يوم 🔥", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
        }
      }
    }

    // Filter Buttons (All Time vs Weekly)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      if (activeTab == "global") {
        PrimaryButton(text = "📊 المتصدرين الكل", modifier = Modifier.weight(1f), onClick = { activeTab = "global" })
      } else {
        SecondaryButton(text = "📊 المتصدرين الكل", modifier = Modifier.weight(1f), onClick = { activeTab = "global" })
      }

      if (activeTab == "weekly") {
        PrimaryButton(text = "📅 هذا الأسبوع", modifier = Modifier.weight(1f), onClick = { activeTab = "weekly" })
      } else {
        SecondaryButton(text = "📅 هذا الأسبوع", modifier = Modifier.weight(1f), onClick = { activeTab = "weekly" })
      }
    }

    // Global Rankings Stream from Room DB
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      itemsIndexed(leaderboardList, key = { _, item -> item.userId }) { index, entry ->
        val rankNumber = index + 1
        FoxCard(modifier = Modifier.fillMaxWidth()) {
          Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                Modifier
                  .background(
                    when (rankNumber) {
                      1 -> Color(0xFFFFD700).copy(alpha = 0.3f)
                      2 -> Color(0xFFC0C0C0).copy(alpha = 0.3f)
                      3 -> Color(0xFFCD7F32).copy(alpha = 0.3f)
                      else -> Color.LightGray.copy(alpha = 0.15f)
                    },
                    RoundedCornerShape(12.dp)
                  )
                  .padding(horizontal = 12.dp, vertical = 8.dp)
              ) {
                Text(
                  when (rankNumber) {
                    1 -> "🥇 #1"
                    2 -> "🥈 #2"
                    3 -> "🥉 #3"
                    else -> "#$rankNumber"
                  },
                  style = MaterialTheme.typography.titleMedium,
                  color = TextPrimary
                )
              }
              Spacer(Modifier.width(12.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(entry.username, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                  if (entry.isUser) {
                    Spacer(Modifier.width(6.dp))
                    Badge(text = "أنت", style = BadgeStyle.SUCCESS)
                  }
                }
                Text("مستوى ${entry.level} • 🔥 ${entry.streakDays} يوم", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
              }
            }
            Text("${entry.xp} XP", style = MaterialTheme.typography.titleMedium, color = Primary)
          }
        }
      }
    }
  }
}
