package com.foxfocus.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.foxfocus.app.R

enum class FinnPose(val drawableRes: Int) {
  DEFAULT(R.drawable.finn_default),
  CELEBRATING(R.drawable.finn_celebrating),
  SAD(R.drawable.finn_sad),
  BLOCKING(R.drawable.finn_blocking),
  THINKING(R.drawable.finn_thinking),
}

/** Real art, no circular avatar clipping and no idle bounce loop — flat, static, on brand. */
@Composable
fun FinnMascot(pose: FinnPose, modifier: Modifier = Modifier, size: Dp = 96.dp) {
  Image(
    painter = painterResource(pose.drawableRes),
    contentDescription = null,
    contentScale = ContentScale.Fit,
    modifier = modifier.size(size),
  )
}
