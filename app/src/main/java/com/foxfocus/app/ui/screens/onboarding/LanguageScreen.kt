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
fun LanguageScreen(onNext: () -> Unit) {
  var selectedLang by remember { mutableStateOf("ar") }

  Column(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(20.dp),
    verticalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("خطوة 1 من 5", style = MaterialTheme.typography.labelLarge, color = Primary)
        Text("20%", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
      }
      FoxProgressBar(progress = 0.20f)

      Spacer(Modifier.height(16.dp))
      Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        FinnMascot(pose = FinnPose.DEFAULT, size = 110.dp)
      }

      Text("اختر لغة التطبيق Preferred Language", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
      Text("يدعم فوكس فوكس الواجهات ثنائية الاتجاه (RTL & LTR) بالكامل.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

      Spacer(Modifier.height(12.dp))

      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column {
            Text("العربية (RTL)", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text("الواجهة العربية الكاملة", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
          if (selectedLang == "ar") {
            PrimaryButton(text = "محدد ✓", onClick = { selectedLang = "ar" })
          } else {
            SecondaryButton(text = "اختيار", onClick = { selectedLang = "ar" })
          }
        }
      }

      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column {
            Text("English (LTR)", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text("Full English Interface", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
          if (selectedLang == "en") {
            PrimaryButton(text = "Selected ✓", onClick = { selectedLang = "en" })
          } else {
            SecondaryButton(text = "Select", onClick = { selectedLang = "en" })
          }
        }
      }
    }

    PrimaryButton(text = "التالي", onClick = onNext)
  }
}
