package com.foxfocus.app.data.firestore

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Firestore-backed profile store: one document per user at `users/{uid}`, kept in sync in real time. */
class UserProfileRepository {

  private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()
  private fun doc(uid: String) = db.collection("users").document(uid)

  /** Creates the profile document on first sign-in (or leaves it untouched if it already exists). */
  suspend fun ensureProfile(user: FirebaseUser, providerName: String) {
    val ref = doc(user.uid)
    val snapshot = ref.get().await()
    if (!snapshot.exists()) {
      val profile = UserProfile(
        uid = user.uid,
        displayName = user.displayName?.takeIf { it.isNotBlank() } ?: "ثعلب التركيز",
        provider = providerName,
        email = user.email.orEmpty(),
        isPremium = true,
        createdAtEpochMs = System.currentTimeMillis(),
        updatedAtEpochMs = System.currentTimeMillis(),
      )
      ref.set(profile).await()
    } else if (snapshot.getBoolean("isPremium") != true) {
      // Premium is free for everyone — heal any legacy/free-tier flag.
      ref.update("isPremium", true).await()
    }
  }

  fun observeProfile(uid: String): Flow<UserProfile> = callbackFlow {
    val registration = doc(uid).addSnapshotListener { snapshot, _ ->
      val profile = snapshot?.toObject(UserProfile::class.java)?.copy(uid = uid) ?: UserProfile(uid = uid)
      trySend(profile.copy(isPremium = true))
    }
    awaitClose { registration.remove() }
  }

  suspend fun updateProfile(uid: String, displayName: String, bio: String, avatarId: String) {
    doc(uid).update(
      mapOf(
        "displayName" to displayName,
        "bio" to bio,
        "avatarId" to avatarId,
        "updatedAtEpochMs" to System.currentTimeMillis(),
      )
    ).await()
  }
}
