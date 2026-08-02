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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.FinnMascot
import com.foxfocus.app.ui.components.FinnPose
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.FoxProgressBar
import com.foxfocus.app.ui.components.PrimaryButton
import com.foxfocus.app.ui.components.SecondaryButton
import kotlinx.coroutines.launch

@Composable
fun AppSelectionScreen(repository: FoxRepository, onNext: () -> Unit) {
  val scope = rememberCoroutineScope()
  val sampleApps = listOf(
    "com.instagram.android" to "Instagram",
    "com.zhiliaoapp.musically" to "TikTok",
    "com.google.android.youtube" to "YouTube",
    "com.snapchat.android" to "Snapchat",
    "com.twitter.android" to "Twitter / X",
    "com.pubg.krmobile" to "PUBG Mobile",
  )

  val selectedPackages = remember { mutableStateListOf("com.instagram.android", "com.zhiliaoapp.musically") }

  Column(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(20.dp),
    verticalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("خطوة 4 من 5", style = MaterialTheme.typography.labelLarge, color = Primary)
        Text("80%", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
      }
      FoxProgressBar(progress = 0.80f)

      Spacer(Modifier.height(4.dp))
      Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        FinnMascot(pose = FinnPose.BLOCKING, size = 90.dp)
      }

      Text("اختر التطبيقات الأكثر استهلاكاً لوقتك", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
      Text("سنساعدك في حظرها أثناء جلسات التركيز كخط دفاع أول.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

      LazyColumn(
        modifier = Modifier.height(280.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(sampleApps) { (pkg, name) ->
          val isSelected = selectedPackages.contains(pkg)
          FoxCard(modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Text(name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
              if (isSelected) {
                PrimaryButton(text = "محظور ✓", onClick = { selectedPackages.remove(pkg) })
              } else {
                SecondaryButton(text = "حظر", onClick = { selectedPackages.add(pkg) })
              }
            }
          }
        }
      }
    }

    PrimaryButton(
      text = "التالي (${selectedPackages.size} محددة)",
      onClick = {
        scope.launch {
          selectedPackages.forEach { pkg ->
            val name = sampleApps.find { it.first == pkg }?.second ?: "App"
            repository.toggleAppBlocked(pkg, name, null, true)
          }
          onNext()
        }
      }
    )
  }
}
