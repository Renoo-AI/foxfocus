package com.foxfocus.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = TextPrimary) {
  FoxCard(modifier = modifier, paddingDp = 16) {
    Column {
      Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
      Text(value, style = MaterialTheme.typography.headlineSmall, color = valueColor)
    }
  }
}
