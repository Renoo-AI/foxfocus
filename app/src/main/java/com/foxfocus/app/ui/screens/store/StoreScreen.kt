package com.foxfocus.app.ui.screens.store

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxfocus.app.audio.SoundFXManager
import com.foxfocus.app.data.db.entity.PlayerStateEntity
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.theme.HeroGradientBottom
import com.foxfocus.app.theme.HeroGradientTop
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.CoinPill
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.PrimaryButton
import com.foxfocus.app.ui.components.SecondaryButton
import kotlinx.coroutines.launch

@Composable
fun StoreScreen(repository: FoxRepository) {
  val playerState by repository.playerState.collectAsStateWithLifecycle(initialValue = PlayerStateEntity())
  val dailyDeal by repository.dailyDeal.collectAsStateWithLifecycle(initialValue = null)
  val scope = rememberCoroutineScope()
  val scrollState = rememberScrollState()
  val context = LocalContext.current

  var friendEmail by remember { mutableStateOf("") }
  var statusMessage by remember { mutableStateOf<String?>(null) }

  Column(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(scrollState)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar: Dual Currency Header
    Row(
      Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("🛒 متجر FoxFocus", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CoinPill(amount = playerState.coinBalance)
        Box(
          Modifier
            .background(Color(0xFFE0F7FA), RoundedCornerShape(99.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text("💎 ${String.format("%.2f", playerState.diamondBalance)}", style = MaterialTheme.typography.labelLarge, color = Color(0xFF00838F))
        }
      }
    }

    statusMessage?.let { msg ->
      Box(
        Modifier
          .fillMaxWidth()
          .background(Success.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
          .padding(12.dp)
      ) {
        Text(msg, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
      }
    }

    // 🔄 Currency Conversion Tool (Appears when balance >= 1,000 FC)
    if (playerState.coinBalance >= 1000) {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column(Modifier.weight(1f)) {
            Text("💎 أداة تحويل العملات", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text("1,000 FC = 1 Diamond (💎)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
          PrimaryButton(
            text = "تحويل 1,000 FC",
            onClick = {
              scope.launch {
                val ok = repository.convertCoinsToDiamonds(1000)
                if (ok) {
                  SoundFXManager.playDiamondConvert(context)
                  statusMessage = "تم تحويل 1,000 FC إلى 1 💎 بنجاح!"
                } else {
                  statusMessage = "الرصيد غير كافٍ"
                }
              }
            }
          )
        }
      }
    }

    // 🔥 Daily Deal Section
    dailyDeal?.let { deal ->
      Box(
        Modifier
          .fillMaxWidth()
          .background(
            Brush.linearGradient(listOf(HeroGradientTop, HeroGradientBottom)),
            RoundedCornerShape(20.dp)
          )
          .padding(16.dp)
      ) {
        Column {
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("🔥 عرض اليوم المميز (خصم 50%)", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text("⏳ ينتهي خلال 12 ساعة", style = MaterialTheme.typography.labelMedium, color = Color(0xFFD8443C))
          }
          Spacer(Modifier.height(8.dp))
          Text(deal.titleAr, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
          Text(deal.descriptionAr, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          Spacer(Modifier.height(12.dp))
          PrimaryButton(
            text = "شراء بسعر ${deal.discountedPriceCoins} FC (بدلاً من ${deal.originalPriceCoins} FC)",
            onClick = {
              scope.launch {
                val ok = repository.spendCoinsGeneric(deal.discountedPriceCoins, deal.dealId, deal.titleAr)
                if (ok) {
                  SoundFXManager.playCoinClaim(context)
                  statusMessage = "تم شراء عرض اليوم بنجاح!"
                } else {
                  statusMessage = "الرصيد غير كافٍ"
                }
              }
            }
          )
        }
      }
    }

    // 🛡️ Streak Freeze Section
    val freezeLimit = EconomyConfig.getStreakFreezeMaxLimit(playerState.streakDays)
    FoxCard(modifier = Modifier.fillMaxWidth()) {
      Text("🛡️ تجميد السلسلة (Streak Freeze)", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
      Spacer(Modifier.height(4.dp))
      Text("المخزون الحالي: ${playerState.streakFreezeCount} / $freezeLimit (السلسلة الحالية: ${playerState.streakDays} يوم)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
      Spacer(Modifier.height(12.dp))
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("تفعيل التجميد التلقائي عند الغياب", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        Switch(
          checked = playerState.streakFreezeAutoEnabled,
          onCheckedChange = { scope.launch { repository.toggleStreakFreezeAuto(it) } }
        )
      }
      Spacer(Modifier.height(8.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SecondaryButton(
          text = "شراء (150 FC)",
          modifier = Modifier.weight(1f),
          onClick = {
            scope.launch {
              val ok = repository.buyStreakFreeze(payWithDiamonds = false)
              if (ok) {
                SoundFXManager.playStreakFreeze(context)
                statusMessage = "تم شراء تجميد السلسلة!"
              } else {
                statusMessage = "الرصيد غير كافٍ أو تجاوزت الحد الأقصى ($freezeLimit)"
              }
            }
          }
        )
        SecondaryButton(
          text = "شراء (0.15 💎)",
          modifier = Modifier.weight(1f),
          onClick = {
            scope.launch {
              val ok = repository.buyStreakFreeze(payWithDiamonds = true)
              if (ok) {
                SoundFXManager.playStreakFreeze(context)
                statusMessage = "تم شراء تجميد السلسلة بالماس!"
              } else {
                statusMessage = "رصيد الماس غير كافٍ"
              }
            }
          }
        )
      }
    }

    // 🛍️ Fox Coins Shop (Packs)
    Text("🛍️ شراء Fox Coins (حزم)", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
    EconomyConfig.COIN_PACKS.forEach { pack ->
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(pack.titleAr, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
              if (pack.savingsLabel.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                Box(Modifier.background(Success.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(4.dp)) {
                  Text(pack.savingsLabel, style = MaterialTheme.typography.labelSmall, color = Success)
                }
              }
            }
            Text("🪙 ${pack.coinAmount} FC", style = MaterialTheme.typography.bodyLarge, color = Primary)
          }
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            PrimaryButton(
              text = "\$${pack.priceUsd}",
              onClick = {
                scope.launch {
                  repository.buyCoinPack(pack, payWithDiamonds = false)
                  SoundFXManager.playCoinClaim(context)
                  statusMessage = "تم شراء ${pack.titleAr}! تمت مشاركة النسب مع العائلة."
                }
              }
            )
            SecondaryButton(
              text = "${pack.priceDiamonds} 💎",
              onClick = {
                scope.launch {
                  val ok = repository.buyCoinPack(pack, payWithDiamonds = true)
                  if (ok) {
                    SoundFXManager.playCoinClaim(context)
                    statusMessage = "تم شراء ${pack.titleAr} بالماس!"
                  } else {
                    statusMessage = "رصيد الماس غير كافٍ"
                  }
                }
              }
            )
          }
        }
      }
    }

    // ⚡ Spend Store (Boosters & Cosmetics)
    Text("⚡ أدوات تعزيز التركيز (Boosters)", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
    EconomyConfig.BOOSTERS.forEach { booster ->
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column(Modifier.weight(1f)) {
            Text(booster.titleAr, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(booster.descriptionAr, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
          SecondaryButton(
            text = "${booster.priceCoins} FC",
            onClick = {
              scope.launch {
                val ok = repository.buyBooster(booster)
                if (ok) {
                  SoundFXManager.playCoinClaim(context)
                  statusMessage = "تم تفعيل ${booster.titleAr}"
                } else {
                  statusMessage = "الرصيد غير كافٍ"
                }
              }
            }
          )
        }
      }
    }

    // 🎁 Gifting Coins to Friends
    FoxCard(modifier = Modifier.fillMaxWidth()) {
      Text("🎁 إرسال Fox Coins لصديق", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
      Spacer(Modifier.height(8.dp))
      OutlinedTextField(
        value = friendEmail,
        onValueChange = { friendEmail = it },
        label = { Text("البريد الإلكتروني لصديقك") },
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(Modifier.height(8.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SecondaryButton(
          text = "100 FC (تكلفة 110)",
          modifier = Modifier.weight(1f),
          onClick = {
            if (friendEmail.isNotEmpty()) {
              scope.launch {
                val ok = repository.sendCoinGift(friendEmail, 100)
                statusMessage = if (ok) "تم إرسال 100 FC إلى $friendEmail" else "الرصيد غير كافٍ"
              }
            }
          }
        )
        SecondaryButton(
          text = "500 FC (تكلفة 530)",
          modifier = Modifier.weight(1f),
          onClick = {
            if (friendEmail.isNotEmpty()) {
              scope.launch {
                val ok = repository.sendCoinGift(friendEmail, 500)
                statusMessage = if (ok) "تم إرسال 500 FC إلى $friendEmail" else "الرصيد غير كافٍ"
              }
            }
          }
        )
      }
    }

    // 👑 FoxFocus Premium — free & unlocked for everyone, no purchase needed
    Box(
      Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF))),
          RoundedCornerShape(20.dp)
        )
        .padding(16.dp)
    ) {
      Column {
        Text("👑 FoxFocus Premium — مجاني للجميع", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Text(
          "جلسات تركيز غير محدودة • بدون إعلانات • مزامنة سحابية • تلميحات غير محدودة — مفعّلة تلقائياً في حسابك",
          style = MaterialTheme.typography.bodySmall,
          color = Color.White.copy(alpha = 0.8f)
        )
        Spacer(Modifier.height(8.dp))
        Box(Modifier.background(Color(0xFFFFD700), RoundedCornerShape(8.dp)).padding(8.dp)) {
          Text("✅ Premium مفعّل بالفعل على حسابك — لا حاجة للدفع", style = MaterialTheme.typography.labelLarge, color = Color.Black)
        }
      }
    }
  }
}
