package com.foxfocus.app.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.TextPrimary

/** Matches --shadow-sm/md/lg from the design kit: soft, warm-tinted card shadows, not default Material black. */
fun Modifier.foxShadow(radius: Int = 20, tier: ShadowTier = ShadowTier.SM): Modifier = this.shadow(
  elevation = tier.elevation,
  shape = RoundedCornerShape(radius.dp),
  ambientColor = TextPrimary.copy(alpha = tier.alpha),
  spotColor = TextPrimary.copy(alpha = tier.alpha),
)

enum class ShadowTier(val elevation: androidx.compose.ui.unit.Dp, val alpha: Float) {
  SM(2.dp, 0.06f),
  MD(8.dp, 0.10f),
  LG(16.dp, 0.16f),
}
