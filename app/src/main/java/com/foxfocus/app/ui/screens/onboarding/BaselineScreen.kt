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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.FinnMascot
import com.foxfocus.app.ui.components.FinnPose
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.FoxProgressBar
import com.foxfocus.app.ui.components.PrimaryButton

@Composable
fun BaselineScreen(onFinish: () -> Unit) {
  var sliderValue by remember { mutableFloatStateOf(4.5f) }

  Column(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(20.dp),
    verticalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("خطوة 5 من 5", style = MaterialTheme.typography.labelLarge, color = Primary)
        Text("100%", style = MaterialTheme.typography.labelLarge, color = Success)
      }
      FoxProgressBar(progress = 1.0f)

      Spacer(Modifier.height(8.dp))
      Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        FinnMascot(pose = FinnPose.CELEBRATING, size = 110.dp)
      }

      Text("تقدير وقت الشاشة اليومي", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
      Text("كم ساعة تقضي تقريباً على هاتفك يومياً؟ سنجعل هذه النقطة خط بداية لحساب الساعات الموفرة!", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

      Spacer(Modifier.height(16.dp))

      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("معدلك الحالي المقدر", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
          Spacer(Modifier.height(8.dp))
          Text("${String.format("%.1f", sliderValue)} ساعة / يومياً", style = MaterialTheme.typography.headlineLarge, color = Primary)
          Spacer(Modifier.height(16.dp))
          Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 1f..10f,
            steps = 18,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      Box(
        Modifier
          .fillMaxWidth()
          .background(Success.copy(alpha = 0.15f), MaterialTheme.shapes.medium)
          .padding(14.dp)
      ) {
        Text(
          "💡 مع FoxFocus، نهدف لتقليل هذا المعدل بنسبة 50% وتوفير أكثر من ${(sliderValue * 0.5 * 30).toInt()} ساعة شهرياً لك!",
          style = MaterialTheme.typography.bodySmall,
          color = TextPrimary
        )
      }
    }

    PrimaryButton(text = "ابدأ استخدام FoxFocus 🦊", onClick = onFinish)
  }
}
