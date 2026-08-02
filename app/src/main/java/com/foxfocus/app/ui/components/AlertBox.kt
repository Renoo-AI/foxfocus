package com.foxfocus.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.CategoryBodyBg
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.DangerBg
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.SuccessBg

enum class AlertStyle(val bg: Color, val fg: Color) {
  SUCCESS(SuccessBg, Success),
  DANGER(DangerBg, Danger),
  WARNING(CategoryBodyBg, Primary),
}

@Composable
fun AlertBox(text: String, style: AlertStyle, modifier: Modifier = Modifier) {
  Row(
    modifier
      .fillMaxWidth()
      .background(style.bg, RoundedCornerShape(12.dp))
      .padding(horizontal = 16.dp, vertical = 12.dp),
  ) {
    Text(text, color = style.fg, style = MaterialTheme.typography.labelLarge)
  }
}
