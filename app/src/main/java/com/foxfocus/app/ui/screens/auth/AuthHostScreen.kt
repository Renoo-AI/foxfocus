package com.foxfocus.app.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.foxfocus.app.auth.AuthRepository
import com.foxfocus.app.data.firestore.UserProfileRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

sealed class AuthScreenState {
  data object Login : AuthScreenState()
  data object ResetPassword : AuthScreenState()
}

@Composable
fun AuthHostScreen(
  authRepository: AuthRepository,
  userProfileRepository: UserProfileRepository,
  onAuthSuccess: () -> Unit,
) {
  var authState by remember { mutableStateOf<AuthScreenState>(AuthScreenState.Login) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var infoMessage by remember { mutableStateOf<String?>(null) }

  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  suspend fun completeSignIn(result: Result<FirebaseUser>, providerName: String) {
    result
      .onSuccess { user ->
        userProfileRepository.ensureProfile(user, providerName)
        onAuthSuccess()
      }
      .onFailure { errorMessage = it.message ?: "حدث خطأ غير متوقع، حاول مرة أخرى" }
  }

  when (authState) {
    AuthScreenState.Login -> LoginScreen(
      isLoading = isLoading,
      errorMessage = errorMessage,
      onEmailSubmit = { email, password, isSignUp ->
        scope.launch {
          isLoading = true
          errorMessage = null
          val result = if (isSignUp) {
            authRepository.signUpWithEmail(email, password)
          } else {
            authRepository.signInWithEmail(email, password)
          }
          completeSignIn(result, "email")
          isLoading = false
        }
      },
      onGoogleSignIn = {
        scope.launch {
          isLoading = true
          errorMessage = null
          completeSignIn(authRepository.signInWithGoogle(context), "google")
          isLoading = false
        }
      },
      onGuestSignIn = {
        scope.launch {
          isLoading = true
          errorMessage = null
          completeSignIn(authRepository.signInAsGuest(), "guest")
          isLoading = false
        }
      },
      onNavigateToReset = {
        errorMessage = null
        infoMessage = null
        authState = AuthScreenState.ResetPassword
      },
    )

    AuthScreenState.ResetPassword -> ResetPasswordScreen(
      isLoading = isLoading,
      errorMessage = errorMessage,
      infoMessage = infoMessage,
      onSendReset = { email ->
        scope.launch {
          isLoading = true
          errorMessage = null
          infoMessage = null
          authRepository.sendPasswordReset(email)
            .onSuccess { infoMessage = "تم إرسال رابط إعادة التعيين إلى بريدك الإلكتروني ✅" }
            .onFailure { errorMessage = it.message ?: "تعذر إرسال رابط إعادة التعيين" }
          isLoading = false
        }
      },
      onBack = {
        errorMessage = null
        infoMessage = null
        authState = AuthScreenState.Login
      },
    )
  }
}
