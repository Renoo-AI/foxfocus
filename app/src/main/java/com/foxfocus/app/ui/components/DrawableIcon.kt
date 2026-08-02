package com.foxfocus.app.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.LockedBg

/** Renders a real launcher-icon Drawable pulled from PackageManager (used for the actually-blocked app). */
@Composable
fun AppIconImage(drawable: Drawable?, modifier: Modifier = Modifier, size: Dp = 56.dp) {
  if (drawable == null) {
    androidx.compose.foundation.layout.Box(modifier.size(size).background(LockedBg, RoundedCornerShape(16.dp)))
    return
  }
  val bitmap = remember(drawable) {
    val width = drawable.intrinsicWidth.coerceAtLeast(1)
    val height = drawable.intrinsicHeight.coerceAtLeast(1)
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    bmp.asImageBitmap()
  }
  Image(bitmap = bitmap, contentDescription = null, modifier = modifier.size(size))
}
