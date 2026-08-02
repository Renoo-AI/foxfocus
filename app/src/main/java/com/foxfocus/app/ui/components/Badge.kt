package com.foxfocus.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.StreakFlame
import com.foxfocus.app.theme.Success

enum class BadgeStyle(val bg: Color, val fg: Color) {
  GOLD(Color(0x26F5A623), Color(0xFFD88300)),
  FLAME(Color(0xFFFFE8DC), StreakFlame),
  SUCCESS(Color(0xFFE9F3E1), Success),
}

@Composable
fun Badge(text: String, style: BadgeStyle, modifier: Modifier = Modifier) {
  androidx.compose.foundation.layout.Box(
    modifier
      .background(style.bg, RoundedCornerShape(8.dp))
      .padding(horizontal = 10.dp, vertical = 4.dp),
  ) {
    Text(text, color = style.fg, style = MaterialTheme.typography.labelSmall)
  }
}
