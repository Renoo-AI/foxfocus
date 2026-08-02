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
fun AgeSegmentationScreen(onNext: (Int) -> Unit) {
  var selectedAgeBand by remember { mutableStateOf("18-24") }
  var ageYears by remember { mutableStateOf(20) }

  Column(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(20.dp),
    verticalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("خطوة 2 من 5", style = MaterialTheme.typography.labelLarge, color = Primary)
        Text("40%", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
      }
      FoxProgressBar(progress = 0.40f)

      Spacer(Modifier.height(8.dp))
      Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        FinnMascot(pose = FinnPose.THINKING, size = 100.dp)
      }

      Text("حدد فئتك العمرية", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
      Text("تساعدنا الفئة العمرية على تخصيص عروض الاشتراكات (خصم الطلاب 20% للفئة 18-24 سنة!).", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

      Spacer(Modifier.height(12.dp))

      val bands = listOf(
        "<18" to "أقل من 18 سنة (طالب مدرسي)",
        "18-24" to "18 - 24 سنة (طالب جامعي - خصم 20%)",
        "25-34" to "25 - 34 سنة (موظف / مهني)",
        "35+" to "35 سنة أو أكبر",
      )

      bands.forEach { (bandCode, label) ->
        FoxCard(modifier = Modifier.fillMaxWidth()) {
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
            if (selectedAgeBand == bandCode) {
              PrimaryButton(text = "محدد ✓", onClick = {
                selectedAgeBand = bandCode
                ageYears = when (bandCode) {
                  "<18" -> 16
                  "18-24" -> 20
                  "25-34" -> 28
                  else -> 40
                }
              })
            } else {
              SecondaryButton(text = "اختيار", onClick = {
                selectedAgeBand = bandCode
                ageYears = when (bandCode) {
                  "<18" -> 16
                  "18-24" -> 20
                  "25-34" -> 28
                  else -> 40
                }
              })
            }
          }
        }
      }
    }

    PrimaryButton(text = "التالي", onClick = { onNext(ageYears) })
  }
}
