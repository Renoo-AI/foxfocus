package com.foxfocus.app.ui.screens.family

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxfocus.app.data.db.entity.FamilyMemberEntity
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
import com.foxfocus.app.ui.components.FinnMascot
import com.foxfocus.app.ui.components.FinnPose
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.PrimaryButton
import kotlinx.coroutines.launch

@Composable
fun FoxFamilyScreen(repository: FoxRepository) {
  val familyMembers by repository.familyMembers.collectAsStateWithLifecycle(initialValue = emptyList())
  val scope = rememberCoroutineScope()

  LazyColumn(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("👨‍👩‍👧‍👦 عائلة الثعالب (Fox Family)", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Badge(text = "${familyMembers.size} / 5 أعضاء", style = BadgeStyle.GOLD)
      }
    }

    item {
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
          Column {
            Text("نظام مشاركة المكافآت العائلي", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(
              "عندما يشتري أي عضو حزمة Fox Coins، يحصل باقي الأعضاء على نسبة 25% - 50% بناءً على سلسلة تركيزهم الشخصية!",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }
      }
    }

    item {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Text("جدول نسب المكافآت العائلية المستحقة", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("• 0 – 2 يوم تركيز متتالي: 0% (لا مكافأة)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          Text("• 3 – 6 أيام: 25% مجاناً", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          Text("• 7 – 13 يوم: 35% مجاناً", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          Text("• 14 – 20 يوم: 45% مجاناً", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          Text("• 21+ يوم: 50% مجاناً 🔥", style = MaterialTheme.typography.bodyMedium, color = Primary)
        }
      }
    }

    items(familyMembers) { member ->
      val sharePct = EconomyConfig.getFamilySharePercentage(member.streakDays)
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(member.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
              if (member.isUser) {
                Spacer(Modifier.width(6.dp))
                Badge(text = "أنت", style = BadgeStyle.SUCCESS)
              }
            }
            Spacer(Modifier.height(4.dp))
            Text("سلسلة التركيز: ${member.streakDays} يوم 🔥", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
          Box(
            Modifier
              .background(if (sharePct > 0) Success.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              if (sharePct > 0) "نسبة المشاركة: ${(sharePct * 100).toInt()}%" else "0% (يحتاج تركيز أكثر)",
              style = MaterialTheme.typography.labelMedium,
              color = if (sharePct > 0) Success else TextSecondary
            )
          }
        }
      }
    }

    item {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Text("تنسيق الشراء والمشاركة الإثباتية", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text("جرب شراء حزمة 12,000 FC لترى كيف يتم توزيع 4,200 FC على سارة و 3,000 FC على خالد تلقائياً!", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(12.dp))
        PrimaryButton(
          text = "محاكاة شراء حزمة 12,000 FC للـ Family",
          onClick = {
            scope.launch {
              val pack = EconomyConfig.COIN_PACKS.first { it.id == "pack_large" }
              repository.buyCoinPack(pack)
            }
          }
        )
      }
    }
  }
}
