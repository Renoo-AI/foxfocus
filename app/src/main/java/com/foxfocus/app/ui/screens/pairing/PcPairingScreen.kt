package com.foxfocus.app.ui.screens.pairing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foxfocus.app.auth.AuthRepository
import com.foxfocus.app.data.firestore.PairingMirror
import com.foxfocus.app.data.firestore.PcPairingRepository
import com.foxfocus.app.data.firestore.UserProfileRepository
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.SuccessBg
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.PrimaryButton
import com.foxfocus.app.ui.components.SecondaryButton
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private val pcPairingRepository = PcPairingRepository()

@Composable
fun PcPairingScreen(
  repository: FoxRepository,
  authRepository: AuthRepository,
  userProfileRepository: UserProfileRepository,
) {
  var pairedCode by remember { mutableStateOf<String?>(null) }
  var scanning by remember { mutableStateOf(false) }
  var statusMessage by remember { mutableStateOf<String?>(null) }
  val scope = rememberCoroutineScope()

  Column(
    Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text("💻 ربط بجهاز الكمبيوتر", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
    Text(
      "افتح foxfocus.app/web على جهازك، وامسح رمز QR الظاهر هناك بكاميرا هاتفك لعرض تقدّمك مباشرة على الكمبيوتر.",
      style = MaterialTheme.typography.bodyMedium,
      color = TextSecondary,
    )

    statusMessage?.let {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Text(it, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
      }
    }

    if (pairedCode == null) {
      PrimaryButton(text = "📷 مسح رمز QR", onClick = { scanning = true })
    } else {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().background(SuccessBg).padding(12.dp)) {
          Text("✅ متصل بالكمبيوتر — يتم تحديث بياناتك مباشرة", color = Success, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(12.dp))
        SecondaryButton(
          text = "قطع الاتصال",
          onClick = {
            val code = pairedCode ?: return@SecondaryButton
            scope.launch {
              runCatching { pcPairingRepository.revoke(code) }
              pairedCode = null
              statusMessage = "تم قطع الاتصال بالكمبيوتر"
            }
          },
        )
      }
    }
  }

  if (scanning) {
    QrScannerScreen(
      onCodeScanned = { code ->
        scanning = false
        val user = authRepository.currentUser
        if (user == null) {
          statusMessage = "يجب تسجيل الدخول أولاً"
          return@QrScannerScreen
        }
        scope.launch {
          val mirror = PairingMirror(
            provider = authRepository.providerKind(user).name.lowercase(),
            updatedAtEpochMs = System.currentTimeMillis(),
          )
          val ok = runCatching { pcPairingRepository.approve(code, user.uid, mirror) }.getOrDefault(false)
          statusMessage = if (ok) {
            pairedCode = code
            "تم الربط بنجاح 🎉"
          } else {
            "رمز غير صالح أو منتهي، جرّب رمزاً جديداً"
          }
        }
      },
      onCancel = { scanning = false },
    )
  }

  // While paired, keep republishing a read-only snapshot the desktop can render.
  LaunchedEffect(pairedCode) {
    val code = pairedCode ?: return@LaunchedEffect
    val uid = authRepository.currentUser?.uid ?: return@LaunchedEffect

    combine(repository.playerState, userProfileRepository.observeProfile(uid)) { playerState, profile ->
      PairingMirror(
        displayName = profile.displayName,
        avatarId = profile.avatarId,
        provider = profile.provider,
        level = playerState.level,
        streakDays = playerState.streakDays,
        coinBalance = playerState.coinBalance,
        diamondBalance = playerState.diamondBalance,
        isPremium = true,
        updatedAtEpochMs = System.currentTimeMillis(),
      )
    }
      .distinctUntilChanged()
      .collect { mirror -> runCatching { pcPairingRepository.pushMirror(code, mirror) } }
  }
}
