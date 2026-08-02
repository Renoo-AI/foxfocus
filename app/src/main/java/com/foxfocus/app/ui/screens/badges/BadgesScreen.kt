package com.foxfocus.app.ui.screens.badges

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.Badge
import com.foxfocus.app.ui.components.BadgeStyle
import com.foxfocus.app.ui.components.FoxCard

@Composable
fun BadgesScreen(repository: FoxRepository) {
  val badgesList by repository.badges.collectAsStateWithLifecycle(initialValue = emptyList())
  val unlockedCount = badgesList.count { it.isUnlocked }

  LazyColumn(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("🏆 الشارات والإنجازات", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Badge(text = "$unlockedCount / ${badgesList.size} مكتملة", style = BadgeStyle.GOLD)
      }
    }

    item {
      Box(
        Modifier
          .fillMaxWidth()
          .background(Color(0xFFFFF3E4), RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Column {
          Text("قاعدة صارمة 🔒", style = MaterialTheme.typography.titleSmall, color = Primary)
          Spacer(Modifier.height(4.dp))
          Text(
            "لا يمكن شراء أي شارة أو إنجاز بالمال أو العملات نهائياً. يجب كسبها جميعاً بالجهد والالتزام الحقيقي!",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
      }
    }

    items(badgesList) { badgeItem ->
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              Modifier
                .background(if (badgeItem.isUnlocked) Primary.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(12.dp)
            ) {
              Text(
                if (badgeItem.isUnlocked) "🏅" else "🔒",
                style = MaterialTheme.typography.titleLarge
              )
            }
            Spacer(Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(badgeItem.titleAr, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                if (badgeItem.isExclusive) {
                  Spacer(Modifier.width(6.dp))
                  Badge(text = "خاصة", style = BadgeStyle.FLAME)
                }
              }
              Spacer(Modifier.height(4.dp))
              Text(badgeItem.descriptionAr, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
          }
          Column(horizontalAlignment = Alignment.End) {
            Box(
              Modifier
                .background(if (badgeItem.isUnlocked) Success.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                if (badgeItem.isUnlocked) "مكسبة ✓" else "مفعلة بالجهد",
                style = MaterialTheme.typography.labelSmall,
                color = if (badgeItem.isUnlocked) Success else TextSecondary
              )
            }
            Spacer(Modifier.height(4.dp))
            Text("🪙 +${badgeItem.rewardCoins} FC", style = MaterialTheme.typography.labelMedium, color = Primary)
          }
        }
      }
    }
  }
}
