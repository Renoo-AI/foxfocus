package com.foxfocus.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.DangerBg
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Success
import com.foxfocus.app.theme.SuccessBg
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.FinnMascot
import com.foxfocus.app.ui.components.FinnPose
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.PrimaryButton

@Composable
fun ResetPasswordScreen(
  isLoading: Boolean,
  errorMessage: String?,
  infoMessage: String?,
  onSendReset: (email: String) -> Unit,
  onBack: () -> Unit,
) {
  var email by remember { mutableStateOf("") }

  Column(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(20.dp),
    verticalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      Spacer(Modifier.height(12.dp))
      Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        FinnMascot(pose = FinnPose.THINKING, size = 100.dp)
      }

      Text("إعادة ضبط كلمة السر", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
      Text(
        "أدخل بريدك الإلكتروني وسنرسل لك رابط إعادة ضبط كلمة السر.",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
      )

      errorMessage?.let { msg ->
        Box(Modifier.fillMaxWidth().background(DangerBg, RoundedCornerShape(12.dp)).padding(12.dp)) {
          Text(msg, style = MaterialTheme.typography.bodySmall, color = Danger)
        }
      }
      infoMessage?.let { msg ->
        Box(Modifier.fillMaxWidth().background(SuccessBg, RoundedCornerShape(12.dp)).padding(12.dp)) {
          Text(msg, style = MaterialTheme.typography.bodySmall, color = Success)
        }
      }

      FoxCard(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("البريد الإلكتروني") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
        )
      }

      if (isLoading) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator(color = Primary)
        }
      }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      PrimaryButton(
        text = "إرسال رابط إعادة الضبط",
        enabled = email.isNotBlank() && !isLoading,
        onClick = { onSendReset(email) },
      )
      TextButton(onClick = onBack) {
        Text("رجوع لتسجيل الدخول", color = Primary, style = MaterialTheme.typography.labelLarge)
      }
    }
  }
}
