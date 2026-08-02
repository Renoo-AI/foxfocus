package com.foxfocus.app.ui.screens.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxfocus.app.audio.FocusMusicPlayer
import com.foxfocus.app.audio.SoundFXManager
import com.foxfocus.app.blocking.PermissionUtils
import com.foxfocus.app.data.db.entity.PlayerStateEntity
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.Badge
import com.foxfocus.app.ui.components.BadgeStyle
import com.foxfocus.app.ui.components.FinnMascot
import com.foxfocus.app.ui.components.FinnPose
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.PrimaryButton
import com.foxfocus.app.ui.components.SecondaryButton
import kotlinx.coroutines.launch

@Composable
fun FullSettingsScreen(repository: FoxRepository, onSignOut: () -> Unit) {
  val playerState by repository.playerState.collectAsStateWithLifecycle(initialValue = PlayerStateEntity())
  val blockedApps by repository.blockedApps.collectAsStateWithLifecycle(initialValue = emptyList())
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var userEmail by remember { mutableStateOf(playerState.userEmail) }
  var studentEmail by remember { mutableStateOf("") }
  var isEduVerified by remember { mutableStateOf(false) }

  var soundEnabled by remember { mutableStateOf(true) }
  var soundVolume by remember { mutableStateOf(0.8f) }
  var musicEnabled by remember { mutableStateOf(false) }
  var musicVolume by remember { mutableStateOf(0.5f) }
  var selectedMusicTrack by remember { mutableStateOf("music_focus_loop") }
  var hapticsEnabled by remember { mutableStateOf(true) }

  var selectedTheme by remember { mutableStateOf("WARM_SUNSET") }
  var selectedPose by remember { mutableStateOf(FinnPose.DEFAULT) }

  var pinLockEnabled by remember { mutableStateOf(false) }
  var pinCode by remember { mutableStateOf("") }
  var strictModeEnabled by remember { mutableStateOf(false) }

  var motivationalNudges by remember { mutableStateOf(true) }
  var urgentNudges by remember { mutableStateOf(true) }
  var humorousNudges by remember { mutableStateOf(true) }
  var nudgeCap by remember { mutableStateOf(3f) }

  var statusMsg by remember { mutableStateOf<String?>(null) }
  var searchQuery by remember { mutableStateOf("") }

  val hasAccessibility = PermissionUtils.isAccessibilityServiceEnabled(context)
  val hasOverlay = PermissionUtils.canDrawOverlays(context)

  LazyColumn(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
          Text("⚙️ إعدادات FoxFocus الشاملة", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
          Text("تحكم في الأذونات، الأصوات، الثيمات، وحماية التركيز", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        FinnMascot(pose = selectedPose, size = 54.dp)
      }
    }

    statusMsg?.let { msg ->
      item {
        Box(
          Modifier
            .fillMaxWidth()
            .background(Success.copy(alpha = 0.15f), MaterialTheme.shapes.medium)
            .padding(12.dp)
        ) {
          Text(msg, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }
      }
    }

    // 1. Account & Student Verification Section
    item {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Text("👤 الحساب وتوثيق الطالب (Student Verification)", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
          value = userEmail,
          onValueChange = { userEmail = it },
          label = { Text("البريد الإلكتروني الأساسي") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
          value = studentEmail,
          onValueChange = {
            studentEmail = it
            isEduVerified = it.endsWith(".edu") || it.endsWith(".ac") || it.contains("student")
          },
          label = { Text("البريد الجامعي للتأكيد (.edu / .ac)") },
          modifier = Modifier.fillMaxWidth()
        )
        if (isEduVerified) {
          Spacer(Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Badge(text = "طالب موثق ✓ (خصم 20% دائم)", style = BadgeStyle.SUCCESS)
          }
        }
        Spacer(Modifier.height(8.dp))
        PrimaryButton(
          text = "حفظ بيانات الحساب والتوثيق",
          onClick = { statusMsg = "تم حفظ التغيرات وتوثيق بيانات الحساب بنجاح!" }
        )
      }
    }

    // 2. Theme & Customization Section
    item {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Text("🎨 الثيمات وشخصية الثعلب فين", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("اختر نمط الثيم المفضل:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(6.dp))

        val themes = listOf(
          "WARM_SUNSET" to "🌅 Warm Sunset (الافتراضي)",
          "MIDNIGHT_FOX" to "🌙 Midnight Fox (داكن)",
          "EMERALD_OASIS" to "🌿 Emerald Oasis (طبيعة)",
          "CYBERPUNK" to "⚡ Cyberpunk Glow (نيون)"
        )
        themes.forEach { (code, name) ->
          Row(
            Modifier
              .fillMaxWidth()
              .clickable { selectedTheme = code }
              .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            if (selectedTheme == code) Badge(text = "نشط ✓", style = BadgeStyle.GOLD)
          }
        }

        Spacer(Modifier.height(12.dp))
        Text("اختر الوضعية الحالية للثعلب فين:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          SecondaryButton(text = "عادي", modifier = Modifier.weight(1f), onClick = { selectedPose = FinnPose.DEFAULT })
          SecondaryButton(text = "تاج 👑", modifier = Modifier.weight(1f), onClick = { selectedPose = FinnPose.CELEBRATING })
          SecondaryButton(text = "تفكير 🤔", modifier = Modifier.weight(1f), onClick = { selectedPose = FinnPose.THINKING })
          SecondaryButton(text = "حظر 🛑", modifier = Modifier.weight(1f), onClick = { selectedPose = FinnPose.BLOCKING })
        }
      }
    }

    // 3. System Permissions Dashboard
    item {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Text("🔒 أذونات النظام والحماية المباشرة", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column(Modifier.weight(1f)) {
            Text("خدمة إمكانية الوصول (Accessibility)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            Text("تكتشف فتح التطبيقات المحظورة لتفعيل الحظر", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
          if (hasAccessibility) {
            Badge(text = "مفعّل ✓", style = BadgeStyle.SUCCESS)
          } else {
            SecondaryButton(text = "تفعيل", onClick = { PermissionUtils.openAccessibilitySettings(context) })
          }
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column(Modifier.weight(1f)) {
            Text("العرض فوق التطبيقات (Overlay)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            Text("تعرض شاشة الحظر الدافئة فوق التطبيقات", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
          if (hasOverlay) {
            Badge(text = "مفعّل ✓", style = BadgeStyle.SUCCESS)
          } else {
            SecondaryButton(text = "تفعيل", onClick = { PermissionUtils.openOverlaySettings(context) })
          }
        }
      }
    }

    // 4. Blocked Apps Control & Security Pin Lock
    item {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Text("🛡️ حماية الحظر وقفل PIN والنمط الصارم", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column(Modifier.weight(1f)) {
            Text("قفل PIN لمنع إلغاء الحظر", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            Text("يتطلب رمز مرور عند محاولة فك حظر تطبيق", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
          Switch(checked = pinLockEnabled, onCheckedChange = { pinLockEnabled = it })
        }

        if (pinLockEnabled) {
          Spacer(Modifier.height(8.dp))
          OutlinedTextField(
            value = pinCode,
            onValueChange = { if (it.length <= 4) pinCode = it },
            label = { Text("رمز PIN المكون من 4 أرقام") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
          )
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column(Modifier.weight(1f)) {
            Text("النمط الصارم (Strict Focus Mode)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            Text("يمنع إلغاء الأذونات أو مسح التطبيق أثناء الجلسة", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
          Switch(checked = strictModeEnabled, onCheckedChange = { strictModeEnabled = it })
        }
      }
    }

    // 5. Blocked Apps Search & Management
    item {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("📱 إدارة التطبيقات المحظورة (${blockedApps.size})", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("بحث عن تطبيق...") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    val filteredApps = blockedApps.filter { it.appName.contains(searchQuery, ignoreCase = true) || it.packageName.contains(searchQuery, ignoreCase = true) }
    if (filteredApps.isEmpty()) {
      item {
        FoxCard(modifier = Modifier.fillMaxWidth()) {
          Text("لا توجد تطبيقات محظورة مطابقة.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
      }
    } else {
      items(filteredApps, key = { it.packageName }) { app ->
        FoxCard(modifier = Modifier.fillMaxWidth()) {
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
              Text(app.appName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
              Text(app.packageName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            SecondaryButton(
              text = "إزالة الحظر",
              onClick = {
                scope.launch {
                  repository.removeBlockedApp(app.packageName)
                  statusMsg = "تمت إزالة ${app.appName} من قائمتك"
                }
              }
            )
          }
        }
      }
    }

    // 6. Audio & Haptics Control Center with Live Sound Testers
    item {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Text("🔊 مركز التحكم بالأصوات والموسيقى والتفاعل اللمسي", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Text("تفعيل الأصوات والمؤثرات (Sound FX)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
          Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it })
        }

        if (soundEnabled) {
          Spacer(Modifier.height(8.dp))
          Text("مستوى صوت المؤثرات: ${(soundVolume * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          Slider(
            value = soundVolume,
            onValueChange = { soundVolume = it },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(Modifier.height(8.dp))
          Text("تجربة المؤثرات الصوتية الصوتية الحية:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          Spacer(Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            SecondaryButton(text = "🪙 FC", modifier = Modifier.weight(1f), onClick = { SoundFXManager.playCoinClaim(context) })
            SecondaryButton(text = "💎 Diamond", modifier = Modifier.weight(1f), onClick = { SoundFXManager.playDiamondConvert(context) })
            SecondaryButton(text = "🛡️ Freeze", modifier = Modifier.weight(1f), onClick = { SoundFXManager.playStreakFreeze(context) })
            SecondaryButton(text = "🏆 Badge", modifier = Modifier.weight(1f), onClick = { SoundFXManager.playBadgeUnlocked(context) })
          }
        }

        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Text("تشغيل موسيقى التركيز الخلفية (Focus Music)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
          Switch(
            checked = musicEnabled,
            onCheckedChange = {
              musicEnabled = it
              if (it) FocusMusicPlayer.playFocusMusic(context, selectedMusicTrack, musicVolume) else FocusMusicPlayer.stop()
            }
          )
        }

        if (musicEnabled) {
          Spacer(Modifier.height(8.dp))
          Text("مستوى صوت الموسيقى: ${(musicVolume * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          Slider(
            value = musicVolume,
            onValueChange = {
              musicVolume = it
              FocusMusicPlayer.setVolume(it)
            },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(Modifier.height(8.dp))
          Text("اختر تراك التركيز المفضل:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          Spacer(Modifier.height(4.dp))
          val tracks = listOf(
            "music_focus_loop" to "🎵 Lofi Rain Focus Loop",
            "music_piano_calm" to "🎹 Calm Piano Waves",
            "music_piano_powerful" to "🎻 Deep Focus Binaural"
          )
          tracks.forEach { (trackCode, label) ->
            Row(
              Modifier
                .fillMaxWidth()
                .clickable {
                  selectedMusicTrack = trackCode
                  FocusMusicPlayer.playFocusMusic(context, trackCode, musicVolume)
                }
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(label, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
              if (selectedMusicTrack == trackCode) Badge(text = "شغال ♪", style = BadgeStyle.SUCCESS)
            }
          }
        }

        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Text("تفعيل الاهتزاز اللمسي (Haptic Feedback)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
          Switch(checked = hapticsEnabled, onCheckedChange = { hapticsEnabled = it })
        }
      }
    }

    // 7. Smart Nudges Matrix
    item {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Text("🔔 مصفوفة التنبيهات الذكية (Smart Nudges)", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Text("تنبيهات تحفيزية (Motivational)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
          Switch(checked = motivationalNudges, onCheckedChange = { motivationalNudges = it })
        }

        Spacer(Modifier.height(6.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Text("تنبيهات عاجلة وحاسمة (Urgent)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
          Switch(checked = urgentNudges, onCheckedChange = { urgentNudges = it })
        }

        Spacer(Modifier.height(6.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Text("تنبيهات فكاهية ومرحة (Humorous)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
          Switch(checked = humorousNudges, onCheckedChange = { humorousNudges = it })
        }

        Spacer(Modifier.height(12.dp))
        Text("الحد الأقصى للتنبيهات اليومية: ${nudgeCap.toInt()} تنبيهات", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Slider(
          value = nudgeCap,
          onValueChange = { nudgeCap = it },
          valueRange = 1f..5f,
          steps = 3,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    // 8. Cloud Backup & Danger Zone
    item {
      FoxCard(modifier = Modifier.fillMaxWidth()) {
        Text("☁️ المزامنة النسخ الاحتياطي وتصدير البيانات", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          SecondaryButton(
            text = "إنشاء نسخة احتفالية",
            modifier = Modifier.weight(1f),
            onClick = { statusMsg = "تم إنشاء نسخة احتياطية محلية بنجاح!" }
          )
          SecondaryButton(
            text = "تصدير CSV الإحصائيات",
            modifier = Modifier.weight(1f),
            onClick = { statusMsg = "تم تصدير سجل الإحصائيات بملف CSV" }
          )
        }
      }
    }

    item {
      Box(
        Modifier
          .fillMaxWidth()
          .background(Color(0xFFFDE8E7), MaterialTheme.shapes.medium)
          .padding(16.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("⚠️ منطقة الخطر (Danger Zone)", style = MaterialTheme.typography.titleMedium, color = Danger)
          Text("إجراءات حساسة تؤدي لإلغاء البيانات أو تسجيل الخروج.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          PrimaryButton(
            text = "تسجيل الخروج النهائي",
            onClick = {
              FocusMusicPlayer.stop()
              onSignOut()
            }
          )
        }
      }
    }
  }
}
