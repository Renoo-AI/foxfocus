package com.foxfocus.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.Border
import com.foxfocus.app.theme.Surface

@Composable
fun FoxCard(
  modifier: Modifier = Modifier,
  paddingDp: Int = 16,
  radiusDp: Int = 20,
  shadow: ShadowTier = ShadowTier.SM,
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(
    modifier
      .foxShadow(radiusDp, shadow)
      .background(Surface, RoundedCornerShape(radiusDp.dp))
      .border(1.dp, Border, RoundedCornerShape(radiusDp.dp))
      .padding(paddingDp.dp),
    content = content,
  )
}
