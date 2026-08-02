package com.foxfocus.app.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Web/server client id from google-services.json (oauth_client, client_type 3) — required by Credential Manager. */
private const val WEB_CLIENT_ID = "891857313091-obo9j1j91sauoa1icol1vkht632ad9bv.apps.googleusercontent.com"

enum class AuthProviderKind { EMAIL, GOOGLE, GUEST }

/** Thin wrapper around Firebase Auth: email/password, Google (Credential Manager), and anonymous guest sign-in. */
class AuthRepository {

  private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

  val currentUser: FirebaseUser? get() = auth.currentUser

  val isSignedIn: Boolean get() = currentUser != null

  fun providerKind(user: FirebaseUser?): AuthProviderKind = when {
    user == null -> AuthProviderKind.GUEST
    user.isAnonymous -> AuthProviderKind.GUEST
    user.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } -> AuthProviderKind.GOOGLE
    else -> AuthProviderKind.EMAIL
  }

  /** Emits the current user on every auth state change (sign-in, sign-out, linking). */
  fun authState(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
    auth.addAuthStateListener(listener)
    awaitClose { auth.removeAuthStateListener(listener) }
  }

  suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> = runCatching {
    auth.signInWithEmailAndPassword(email.trim(), password).await().user
      ?: error("تعذر تسجيل الدخول")
  }

  suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser> = runCatching {
    auth.createUserWithEmailAndPassword(email.trim(), password).await().user
      ?: error("تعذر إنشاء الحساب")
  }

  suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
    auth.sendPasswordResetEmail(email.trim()).await()
  }

  suspend fun signInAsGuest(): Result<FirebaseUser> = runCatching {
    auth.signInAnonymously().await().user ?: error("تعذر الدخول كضيف")
  }

  /** Launches the system Google account picker (Credential Manager) and signs into Firebase with the returned ID token. */
  suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> = runCatching {
    val googleIdOption = GetGoogleIdOption.Builder()
      .setFilterByAuthorizedAccounts(false)
      .setServerClientId(WEB_CLIENT_ID)
      .build()

    val request = GetCredentialRequest.Builder()
      .addCredentialOption(googleIdOption)
      .build()

    val credentialManager = CredentialManager.create(context)
    val response = try {
      credentialManager.getCredential(context, request)
    } catch (e: GetCredentialException) {
      throw IllegalStateException("تم إلغاء تسجيل الدخول عبر Google", e)
    }

    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(response.credential.data)
    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
    auth.signInWithCredential(firebaseCredential).await().user ?: error("تعذر تسجيل الدخول عبر Google")
  }

  suspend fun signOut(context: Context) {
    auth.signOut()
    runCatching { CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest()) }
  }
}
