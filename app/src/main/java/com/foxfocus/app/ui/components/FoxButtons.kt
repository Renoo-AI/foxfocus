package com.foxfocus.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.Border
import com.foxfocus.app.theme.CategoryBodyBg
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.DangerBg
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.PrimaryPressed
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.SuccessBg
import com.foxfocus.app.theme.Surface
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary

/** Tactile keycap button: idle floats with a 4dp shadow strip below it; pressed sinks to a 1dp strip
 *  (mirrors the HTML kit's `box-shadow: 0 4px 0 ...` → `:active { transform: translateY(3px) }`). */
@Composable
fun PrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  val bg = if (enabled) Primary else Border
  val shadow = if (enabled) PrimaryPressed else Border
  val textColor = if (enabled) Surface else TextSecondary
  val interactionSource = remember { MutableInteractionSource() }
  val pressed by interactionSource.collectIsPressedAsState()
  val pressOffset by animateDpAsState(if (pressed) 3.dp else 0.dp, tween(90), label = "primaryButtonPress")

  Box(modifier.fillMaxWidth().height(52.dp)) {
    Box(
      Modifier
        .fillMaxWidth()
        .height(48.dp)
        .align(Alignment.BottomCenter)
        .background(shadow, RoundedCornerShape(14.dp))
    )
    Box(
      Modifier
        .fillMaxWidth()
        .height(48.dp)
        .offset(y = pressOffset)
        .background(bg, RoundedCornerShape(14.dp))
        .clickable(
          enabled = enabled,
          interactionSource = interactionSource,
          indication = null,
          onClick = onClick,
        ),
      contentAlignment = Alignment.Center,
    ) {
      Text(text, color = textColor, style = MaterialTheme.typography.labelLarge)
    }
  }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  val interactionSource = remember { MutableInteractionSource() }
  val pressed by interactionSource.collectIsPressedAsState()
  val pressOffset by animateDpAsState(if (pressed) 2.dp else 0.dp, tween(90), label = "secondaryButtonPress")

  Box(modifier.fillMaxWidth().height(50.dp)) {
    Box(
      Modifier
        .fillMaxWidth()
        .height(48.dp)
        .align(Alignment.BottomCenter)
        .background(Border, RoundedCornerShape(14.dp))
    )
    Box(
      Modifier
        .fillMaxWidth()
        .height(48.dp)
        .offset(y = pressOffset)
        .background(Surface, RoundedCornerShape(14.dp))
        .border(1.5.dp, Border, RoundedCornerShape(14.dp))
        .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
      contentAlignment = Alignment.Center,
    ) {
      Text(text, color = TextPrimary, style = MaterialTheme.typography.labelLarge)
    }
  }
}

@Composable
fun GhostButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Box(
    modifier
      .clickable(onClick = onClick)
      .padding(PaddingValues(horizontal = 16.dp, vertical = 10.dp)),
    contentAlignment = Alignment.Center,
  ) {
    Text(text, color = TextSecondary, style = MaterialTheme.typography.labelLarge)
  }
}

/** Soft stadium pill button — btn-pill in the kit (category-body-bg / primary text). */
@Composable
fun PillButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Box(
    modifier
      .height(44.dp)
      .background(CategoryBodyBg, RoundedCornerShape(99.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 24.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(text, color = Primary, style = MaterialTheme.typography.labelLarge)
  }
}

/** Soft destructive button — btn-danger. */
@Composable
fun DangerButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Box(
    modifier
      .height(44.dp)
      .background(DangerBg, RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 18.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(text, color = Danger, style = MaterialTheme.typography.labelLarge)
  }
}

/** Soft success button — btn-success. */
@Composable
fun SuccessButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Box(
    modifier
      .height(44.dp)
      .background(SuccessBg, RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 18.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(text, color = Success, style = MaterialTheme.typography.labelLarge)
  }
}
