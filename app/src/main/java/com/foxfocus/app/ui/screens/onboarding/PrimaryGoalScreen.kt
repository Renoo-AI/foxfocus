package com.foxfocus.app.ui.screens.onboarding

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.FinnMascot
import com.foxfocus.app.ui.components.FinnPose
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.FoxProgressBar
import com.foxfocus.app.ui.components.PrimaryButton
import com.foxfocus.app.ui.components.SecondaryButton

@Composable
fun PrimaryGoalScreen(onNext: () -> Unit) {
  var selectedGoal by remember { mutableStateOf("study") }

  Column(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(20.dp),
    verticalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("خطوة 3 من 5", style = MaterialTheme.typography.labelLarge, color = Primary)
        Text("60%", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
      }
      FoxProgressBar(progress = 0.60f)

      Spacer(Modifier.height(8.dp))
      Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        FinnMascot(pose = FinnPose.DEFAULT, size = 100.dp)
      }

      Text("ما هو هدفك الرئيسي من التركيز؟", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
      Text("سنخصص التحديات اليومية والتنبيهات الذكية لتناسب هدفك الشخصي.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

      Spacer(Modifier.height(12.dp))

      val goals = listOf(
        "study" to "🎓 التركيز في الدراسة والعمل",
        "sleep" to "🌙 تحسين جودة النوم قبل وقت النوم",
        "scroll" to "🛑 التوقف عن التصفح العشوائي (Doomscrolling)",
        "family" to "👨‍👩‍👧‍👦 قضاء وقت أطول مع العائلة والأصدقاء",
      )

      goals.forEach { (code, label) ->
        FoxCard(modifier = Modifier.fillMaxWidth()) {
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
            if (selectedGoal == code) {
              PrimaryButton(text = "محدد ✓", onClick = { selectedGoal = code })
            } else {
              SecondaryButton(text = "اختيار", onClick = { selectedGoal = code })
            }
          }
        }
      }
    }

    PrimaryButton(text = "التالي", onClick = onNext)
  }
}
