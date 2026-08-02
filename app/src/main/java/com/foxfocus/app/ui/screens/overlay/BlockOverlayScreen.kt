package com.foxfocus.app.ui.screens.overlay

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxfocus.app.MainActivity
import com.foxfocus.app.data.db.entity.PlayerStateEntity
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.theme.Background
import com.foxfocus.app.theme.CategoryBodyBg
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.AppIconImage
import com.foxfocus.app.ui.components.CoinPill
import com.foxfocus.app.ui.components.FinnMascot
import com.foxfocus.app.ui.components.FinnPose
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.GhostButton
import com.foxfocus.app.ui.components.PrimaryButton
import com.foxfocus.app.ui.components.SecondaryButton
import kotlinx.coroutines.launch

private enum class OverlayStep { BLOCKED, DURATION, CONFIRM }

@Composable
fun BlockOverlayScreen(
  blockedPackageName: String,
  repository: FoxRepository,
  packageManager: PackageManager,
  onDismiss: () -> Unit,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var step by remember(blockedPackageName) { mutableStateOf(OverlayStep.BLOCKED) }
  var selectedMinutes by remember { mutableStateOf(EconomyConfig.UNLOCK_DURATIONS_MINUTES[1]) }

  val playerState by repository.playerState.collectAsStateWithLifecycle(initialValue = PlayerStateEntity())
  val balance = playerState.coinBalance

  val appName = remember(blockedPackageName) {
    runCatching {
      packageManager.getApplicationLabel(packageManager.getApplicationInfo(blockedPackageName, 0)).toString()
    }.getOrDefault(blockedPackageName)
  }
  val appIcon = remember(blockedPackageName) {
    runCatching { packageManager.getApplicationIcon(blockedPackageName) }.getOrNull()
  }

  Box(Modifier.fillMaxSize().background(Background)) {
    BlockedContent(
      appName = appName,
      appIcon = appIcon,
      balance = balance,
      onPlayGames = {
        context.startActivity(
          Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(MainActivity.EXTRA_OPEN_TAB, MainActivity.TAB_MISSIONS)
        )
        onDismiss()
      },
      onPayCoins = { step = OverlayStep.DURATION },
      onBackHome = onDismiss,
    )

    if (step == OverlayStep.DURATION) {
      DurationPickerSheet(
        balance = balance,
        selectedMinutes = selectedMinutes,
        onSelect = { selectedMinutes = it },
        onDismiss = { step = OverlayStep.BLOCKED },
        onContinue = { step = OverlayStep.CONFIRM },
      )
    }

    if (step == OverlayStep.CONFIRM) {
      ConfirmSpendSheet(
        appName = appName,
        minutes = selectedMinutes,
        balance = balance,
        onNeverMind = { step = OverlayStep.DURATION },
        onConfirm = {
          scope.launch {
            val ok = repository.spendCoinsToUnlock(blockedPackageName, appName, selectedMinutes)
            if (ok) onDismiss() else step = OverlayStep.DURATION
          }
        },
      )
    }
  }
}

@Composable
private fun BlockedContent(
  appName: String,
  appIcon: android.graphics.drawable.Drawable?,
  balance: Int,
  onPlayGames: () -> Unit,
  onPayCoins: () -> Unit,
  onBackHome: () -> Unit,
) {
  Column(
    Modifier.fillMaxSize().padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(Modifier.height(48.dp))
    AppIconImage(appIcon, size = 56.dp)
    Spacer(Modifier.height(12.dp))
    Text(
      "$appName محظور",
      style = MaterialTheme.typography.titleLarge,
      color = TextPrimary,
      textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(12.dp))
    CoinPill(amount = balance)
    Spacer(Modifier.height(24.dp))
    FinnMascot(FinnPose.BLOCKING, size = 128.dp)
    Spacer(Modifier.weight(1f))

    PrimaryButton(text = "العب لتربح عملات", onClick = onPlayGames)
    Spacer(Modifier.height(12.dp))
    SecondaryButton(text = "افتح بالعملات", onClick = onPayCoins)
    Spacer(Modifier.height(4.dp))
    GhostButton(text = "العودة للرئيسية", onClick = onBackHome)
  }
}

@Composable
private fun DurationPickerSheet(
  balance: Int,
  selectedMinutes: Int,
  onSelect: (Int) -> Unit,
  onDismiss: () -> Unit,
  onContinue: () -> Unit,
) {
  SheetScrim(onDismiss)
  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
    Column(
      Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        .padding(24.dp)
    ) {
      Text("كم تحتاج من الوقت؟", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
      Text("$balance عملة متاحة", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
      Spacer(Modifier.height(20.dp))

      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        EconomyConfig.UNLOCK_DURATIONS_MINUTES.forEach { minutes ->
          val cost = EconomyConfig.unlockCost(minutes)
          val affordable = cost <= balance
          val selected = minutes == selectedMinutes
          DurationOptionCard(
            minutes = minutes,
            cost = cost,
            affordable = affordable,
            selected = selected,
            shortfall = (cost - balance).coerceAtLeast(0),
            onClick = { onSelect(minutes) },
            modifier = Modifier.weight(1f),
          )
        }
      }

      Spacer(Modifier.height(20.dp))
      val cost = EconomyConfig.unlockCost(selectedMinutes)
      val canContinue = cost <= balance
      PrimaryButton(
        text = if (canContinue) "متابعة — $cost عملة" else "تحتاج ${cost - balance} عملة إضافية",
        onClick = onContinue,
        enabled = canContinue,
      )
      if (!canContinue) {
        Spacer(Modifier.height(8.dp))
        GhostButton(text = "اربح عملات بدلاً من ذلك", onClick = onDismiss, modifier = Modifier.fillMaxWidth())
      }
    }
  }
}

@Composable
private fun DurationOptionCard(
  minutes: Int,
  cost: Int,
  affordable: Boolean,
  selected: Boolean,
  shortfall: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val borderColor = if (selected) Primary else com.foxfocus.app.theme.Border
  val bg = if (selected) CategoryBodyBg else MaterialTheme.colorScheme.surface
  Column(
    modifier
      .background(bg, RoundedCornerShape(14.dp))
      .border(if (selected) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(vertical = 12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text("$minutes د", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
    Text(
      "$cost عملة",
      style = MaterialTheme.typography.bodySmall,
      color = if (affordable) TextSecondary else Danger,
      fontWeight = FontWeight.Bold,
    )
    if (!affordable) {
      Text("+$shortfall", style = MaterialTheme.typography.bodySmall, color = Danger)
    }
  }
}

@Composable
private fun ConfirmSpendSheet(
  appName: String,
  minutes: Int,
  balance: Int,
  onNeverMind: () -> Unit,
  onConfirm: () -> Unit,
) {
  val cost = EconomyConfig.unlockCost(minutes)
  val after = balance - cost
  SheetScrim(onNeverMind)
  Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
    FoxCard(paddingDp = 24) {
      Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        FinnMascot(FinnPose.THINKING, size = 72.dp)
        Spacer(Modifier.height(12.dp))
        Text(
          "إنفاق $cost عملة؟",
          style = MaterialTheme.typography.headlineSmall,
          color = TextPrimary,
          textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
          "سيفتح $appName لمدة $minutes دقيقة.",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary,
          textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))

        Column(
          Modifier
            .fillMaxWidth()
            .background(Background, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          BalanceRow("رصيدك الحالي", balance.toString(), TextPrimary)
          Spacer(Modifier.height(6.dp))
          BalanceRow("بعد الفتح", after.toString(), if (after >= cost) Success else Danger)
        }

        Spacer(Modifier.height(20.dp))
        PrimaryButton(text = "نعم، افتح", onClick = onConfirm)
        Spacer(Modifier.height(8.dp))
        GhostButton(text = "تراجع", onClick = onNeverMind, modifier = Modifier.fillMaxWidth())
      }
    }
  }
}

@Composable
private fun BalanceRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
  Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    Text(value, style = MaterialTheme.typography.labelLarge, color = valueColor)
  }
}

@Composable
private fun SheetScrim(onDismiss: () -> Unit) {
  Box(
    Modifier
      .fillMaxSize()
      .background(androidx.compose.ui.graphics.Color(0x73000000))
      .clickable(
        indication = null,
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
        onClick = onDismiss,
      )
  )
}
