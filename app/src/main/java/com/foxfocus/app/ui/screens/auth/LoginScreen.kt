package com.foxfocus.app.ui.screens.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.foxfocus.app.theme.Danger
import com.foxfocus.app.theme.DangerBg
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.TextPrimary
import com.foxfocus.app.theme.TextSecondary
import com.foxfocus.app.ui.components.FinnMascot
import com.foxfocus.app.ui.components.FinnPose
import com.foxfocus.app.ui.components.FoxCard
import com.foxfocus.app.ui.components.PrimaryButton
import com.foxfocus.app.ui.components.SecondaryButton

@Composable
fun LoginScreen(
  isLoading: Boolean,
  errorMessage: String?,
  onEmailSubmit: (email: String, password: String, isSignUp: Boolean) -> Unit,
  onGoogleSignIn: () -> Unit,
  onGuestSignIn: () -> Unit,
  onNavigateToReset: () -> Unit,
) {
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var isSignUp by remember { mutableStateOf(false) }

  val canSubmit = email.isNotBlank() && password.length >= 6 && !isLoading

  Column(
    Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp),
  ) {
    Spacer(Modifier.height(4.dp))
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
      FinnMascot(pose = FinnPose.DEFAULT, size = 100.dp)
    }

    Text(
      if (isSignUp) "أنشئ حساب FoxFocus" else "تسجيل الدخول إلى FoxFocus",
      style = MaterialTheme.typography.headlineMedium,
      color = TextPrimary,
    )
    Text(
      "Premium مجاني للجميع 👑 — سجّل دخولك لمزامنة تقدمك عبر السحابة.",
      style = MaterialTheme.typography.bodyMedium,
      color = TextSecondary,
    )

    errorMessage?.let { msg ->
      Box(
        Modifier.fillMaxWidth().background(DangerBg, RoundedCornerShape(12.dp)).padding(12.dp)
      ) {
        Text(msg, style = MaterialTheme.typography.bodySmall, color = Danger)
      }
    }

    FoxCard(modifier = Modifier.fillMaxWidth()) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("البريد الإلكتروني") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
          value = password,
          onValueChange = { password = it },
          label = { Text("كلمة السر (6 أحرف على الأقل)") },
          visualTransformation = PasswordVisualTransformation(),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
        )

        Row(
          Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          TextButton(onClick = { isSignUp = !isSignUp }) {
            Text(
              if (isSignUp) "لديك حساب؟ سجّل الدخول" else "حساب جديد؟ أنشئ حساباً",
              color = Primary,
              style = MaterialTheme.typography.bodySmall,
            )
          }
          TextButton(onClick = onNavigateToReset) {
            Text("نسيت كلمة السر؟", color = Primary, style = MaterialTheme.typography.bodySmall)
          }
        }
      }
    }

    if (isLoading) {
      Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Primary)
      }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      PrimaryButton(
        text = if (isSignUp) "إنشاء الحساب" else "تسجيل الدخول",
        enabled = canSubmit,
        onClick = { onEmailSubmit(email, password, isSignUp) },
      )
      SecondaryButton(text = "المتابعة عبر Google", onClick = onGoogleSignIn)
      SecondaryButton(text = "المتابعة كضيف", onClick = onGuestSignIn)
    }
  }
}
