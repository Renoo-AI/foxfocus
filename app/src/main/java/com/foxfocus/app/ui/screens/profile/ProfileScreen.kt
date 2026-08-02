package com.foxfocus.app.ui.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxfocus.app.auth.AuthRepository
import com.foxfocus.app.data.db.entity.PlayerStateEntity
import com.foxfocus.app.data.firestore.UserProfile
import com.foxfocus.app.data.firestore.UserProfileRepository
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.notifications.NudgeType
import com.foxfocus.app.notifications.SmartNudgeManager
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.Badge
import com.foxfocus.app.ui.components.BadgeStyle
import com.foxfocus.app.ui.components.FinnMascot
import com.foxfocus.app.ui.components.FinnPose
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.FoxProgressBar
import com.foxfocus.app.ui.components.SecondaryButton
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
  repository: FoxRepository,
  authRepository: AuthRepository,
  userProfileRepository: UserProfileRepository,
  onOpenSettings: () -> Unit,
  onOpenPcPairing: () -> Unit,
) {
  val playerState by repository.playerState.collectAsStateWithLifecycle(initialValue = PlayerStateEntity())
  val uid = authRepository.currentUser?.uid
  val profile by (uid?.let { userProfileRepository.observeProfile(it) } ?: emptyProfileFlow())
    .collectAsStateWithLifecycle(initialValue = UserProfile())
  val scope = rememberCoroutineScope()
  val scrollState = rememberScrollState()
  val context = LocalContext.current

  var notificationsEnabled by remember { mutableStateOf(true) }
  var isEditing by remember { mutableStateOf(false) }
  var draftName by remember { mutableStateOf(profile.displayName) }
  var draftBio by remember { mutableStateOf(profile.bio) }
  var draftAvatar by remember { mutableStateOf(profile.avatarId) }

  LaunchedEffect(profile.uid) {
    draftName = profile.displayName
    draftBio = profile.bio
    draftAvatar = profile.avatarId
  }

  Column(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(scrollState)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      Text("👤 الملف الشخصي والإعدادات", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
      Badge(text = "👑 Premium — مجاني", style = BadgeStyle.GOLD)
    }

    // Customizable Profile Card
    FoxCard(modifier = Modifier.fillMaxWidth()) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        FinnMascot(pose = avatarIdToPose(profile.avatarId), size = 72.dp)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
          Text(profile.displayName.ifBlank { "ثعلب التركيز" }, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
          Text(profile.email.ifBlank { "حساب ضيف" }, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          if (profile.bio.isNotBlank()) {
            Text(profile.bio, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
          Spacer(Modifier.height(8.dp))
          Text("مستوى ${playerState.level} • ${profile.provider}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
          FoxProgressBar(progress = 0.75f)
        }
      }

      Spacer(Modifier.height(12.dp))
      if (isEditing) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = draftName,
            onValueChange = { draftName = it },
            label = { Text("اسم العرض") },
            modifier = Modifier.fillMaxWidth(),
          )
          OutlinedTextField(
            value = draftBio,
            onValueChange = { draftBio = it },
            label = { Text("نبذة قصيرة") },
            modifier = Modifier.fillMaxWidth(),
          )
          Text("اختر رمز الثعلب الشخصي", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UserProfile.AVAILABLE_AVATARS.forEach { avatarId ->
              val selected = avatarId == draftAvatar
              Box(
                Modifier
                  .background(
                    if (selected) Primary.copy(alpha = 0.15f) else Color.Transparent,
                    RoundedCornerShape(12.dp)
                  )
                  .border(
                    if (selected) 2.dp else 0.dp,
                    if (selected) Primary else Color.Transparent,
                    RoundedCornerShape(12.dp)
                  )
                  .clickable { draftAvatar = avatarId }
                  .padding(6.dp)
              ) {
                FinnMascot(pose = avatarIdToPose(avatarId), size = 44.dp)
              }
            }
          }
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SecondaryButton(
              text = "حفظ",
              modifier = Modifier.weight(1f),
              onClick = {
                val targetUid = uid ?: return@SecondaryButton
                scope.launch {
                  userProfileRepository.updateProfile(targetUid, draftName, draftBio, draftAvatar)
                  isEditing = false
                }
              },
            )
            SecondaryButton(
              text = "إلغاء",
              modifier = Modifier.weight(1f),
              onClick = { isEditing = false },
            )
          }
        }
      } else {
        SecondaryButton(text = "✏️ تعديل الملف الشخصي", onClick = { isEditing = true })
      }
    }

    // Metric Summary Grid
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      FoxCard(modifier = Modifier.weight(1f)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("🔥 تتابع أيام", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
          Text("${playerState.streakDays}", style = MaterialTheme.typography.titleLarge, color = Primary)
        }
      }
      FoxCard(modifier = Modifier.weight(1f)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("🪙 عملات FC", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
          Text("${playerState.coinBalance}", style = MaterialTheme.typography.titleLarge, color = Primary)
        }
      }
      FoxCard(modifier = Modifier.weight(1f)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("💎 ماسات", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
          Text("${String.format("%.1f", playerState.diamondBalance)}", style = MaterialTheme.typography.titleLarge, color = Color(0xFF00838F))
        }
      }
    }

    // 🔔 Smart Nudges Settings & Test Notification
    FoxCard(modifier = Modifier.fillMaxWidth()) {
      Text("🔔 التذكير الذكي (Smart Nudges)", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
      Spacer(Modifier.height(8.dp))
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
          Text("تفعيل التذكير التحفيزي والعاجل", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
          Text("يرسل تنبيهات ذكية قبل 30 دقيقة من وقتك المعتاد", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Switch(
          checked = notificationsEnabled,
          onCheckedChange = { notificationsEnabled = it }
        )
      }
      Spacer(Modifier.height(12.dp))
      SecondaryButton(
        text = "إرسال إشعار تحفيزي اختباري 🦊",
        onClick = {
          SmartNudgeManager.sendNudge(
            context = context,
            type = NudgeType.MOTIVATIONAL,
            customTitle = "🦊 ثعلب التركيز يشجعك!",
            customMessage = "🔥 أنت على بُعد جلسة واحدة من تحقيق إنجاز يومك!"
          )
        }
      )
    }

    // Account, Settings & PC Pairing
    FoxCard(modifier = Modifier.fillMaxWidth()) {
      Text("🔐 الحساب", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
      Spacer(Modifier.height(4.dp))
      Text("طريقة الدخول: ${profile.provider}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
      Spacer(Modifier.height(12.dp))
      SecondaryButton(text = "⚙️ الإعدادات الكاملة", onClick = onOpenSettings)
      Spacer(Modifier.height(8.dp))
      SecondaryButton(text = "💻 ربط بجهاز الكمبيوتر (QR)", onClick = onOpenPcPairing)
      Spacer(Modifier.height(8.dp))
      SecondaryButton(
        text = "🚪 تسجيل الخروج",
        onClick = { scope.launch { authRepository.signOut(context) } }
      )
    }
  }
}

private fun emptyProfileFlow() = kotlinx.coroutines.flow.flowOf(UserProfile())

private fun avatarIdToPose(avatarId: String): FinnPose =
  FinnPose.entries.find { it.name.equals(avatarId.removePrefix("finn_"), ignoreCase = true) } ?: FinnPose.DEFAULT
