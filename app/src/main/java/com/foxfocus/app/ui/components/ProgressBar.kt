package com.foxfocus.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.Border
import com.foxfocus.app.theme.Primary

@Composable
fun FoxProgressBar(progress: Float, modifier: Modifier = Modifier, trackColor: Color = Border, fillColor: Color = Primary) {
  val animated by animateFloatAsState(progress.coerceIn(0f, 1f), tween(220), label = "progress")
  androidx.compose.foundation.layout.Box(
    modifier
      .fillMaxWidth()
      .height(10.dp)
      .clip(RoundedCornerShape(99.dp))
      .background(trackColor),
  ) {
    androidx.compose.foundation.layout.Box(
      Modifier
        .fillMaxHeight()
        .fillMaxWidth(animated)
        .background(fillColor, RoundedCornerShape(99.dp)),
    )
  }
}
