package com.foxfocus.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.foxfocus.app.R
import com.foxfocus.app.theme.CategoryBodyBg
import com.foxfocus.app.theme.TextPrimary

@Composable
fun CoinPill(amount: Int, modifier: Modifier = Modifier) {
  Row(
    modifier
      .background(CategoryBodyBg, RoundedCornerShape(99.dp))
      .padding(horizontal = 12.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Image(painterResource(R.drawable.coin_icon), null, Modifier.size(16.dp))
    Text(
      "  $amount",
      style = MaterialTheme.typography.labelLarge,
      color = TextPrimary,
    )
  }
}
