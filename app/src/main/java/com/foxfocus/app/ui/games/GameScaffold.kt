package com.foxfocus.app.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.Surface
import com.foxfocus.app.theme.TextMuted
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.ui.components.FinnMascot
import com.foxfocus.app.ui.components.FinnPose
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.PrimaryButton
import com.foxfocus.app.ui.components.ShadowTier
import com.foxfocus.app.ui.components.foxShadow

@Composable
fun GameTopBar(title: String, accent: androidx.compose.ui.graphics.Color, hud: String, onExit: () -> Unit) {
  Row(
    Modifier
      .fillMaxWidth()
      .background(Surface)
      .foxShadow(radius = 0, tier = ShadowTier.SM)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    IconButton(onClick = onExit) {
      Icon(Icons.Filled.Close, contentDescription = null, tint = TextMuted)
    }
    Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
    if (hud.isNotEmpty()) {
      Box(
        Modifier
          .background(accent.copy(alpha = 0.14f), RoundedCornerShape(99.dp))
          .padding(horizontal = 12.dp, vertical = 6.dp),
      ) {
        Text(hud, style = MaterialTheme.typography.labelLarge, color = accent)
      }
    }
    Spacer(Modifier.size(8.dp))
  }
}

@Composable
fun CompletionOverlay(coins: Int, bonusEarned: Boolean, onDone: () -> Unit) {
  Box(
    Modifier
      .fillMaxSize()
      .background(androidx.compose.ui.graphics.Color(0x73000000)),
    contentAlignment = Alignment.Center,
  ) {
    FoxCard(modifier = Modifier.padding(32.dp), paddingDp = 24, shadow = ShadowTier.LG) {
      androidx.compose.foundation.layout.Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
      ) {
        FinnMascot(FinnPose.CELEBRATING, size = 72.dp)
        Spacer(Modifier.height(8.dp))
        Text("أحسنت!", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text("+$coins عملة", style = MaterialTheme.typography.titleLarge, color = Success)
        if (bonusEarned) {
          Text("مكافأة سرعة!", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
        Spacer(Modifier.height(16.dp))
        PrimaryButton(text = "تم", onClick = onDone)
      }
    }
  }
}
