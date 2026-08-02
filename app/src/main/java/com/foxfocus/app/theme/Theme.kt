package com.foxfocus.app.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val FoxColorScheme = lightColorScheme(
  primary = Primary,
  onPrimary = Surface,
  secondary = CoinGold,
  background = Background,
  onBackground = TextPrimary,
  surface = Surface,
  onSurface = TextPrimary,
  surfaceVariant = CategoryBodyBg,
  onSurfaceVariant = TextSecondary,
  error = Danger,
  onError = Surface,
  outline = Border,
)

private val FoxShapes = Shapes(
  extraSmall = RoundedCornerShape(8.dp),
  small = RoundedCornerShape(12.dp),
  medium = RoundedCornerShape(16.dp),
  large = RoundedCornerShape(20.dp),
  extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun FoxFocusTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = FoxColorScheme,
    typography = FoxTypography,
    shapes = FoxShapes,
    content = content,
  )
}
