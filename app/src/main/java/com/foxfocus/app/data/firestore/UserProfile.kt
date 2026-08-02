package com.foxfocus.app.data.firestore

/** Customizable public profile stored at `users/{uid}` in Firestore. */
data class UserProfile(
  val uid: String = "",
  val displayName: String = "ثعلب التركيز",
  val bio: String = "",
  val avatarId: String = "finn_default",
  val provider: String = "guest",
  val email: String = "",
  val isPremium: Boolean = true,
  val createdAtEpochMs: Long = 0L,
  val updatedAtEpochMs: Long = 0L,
) {
  companion object {
    /** Fixed catalog of selectable mascot avatars (matches FinnPose drawables) — no upload needed to customize a profile. */
    val AVAILABLE_AVATARS = listOf(
      "finn_default", "finn_celebrating", "finn_thinking", "finn_blocking", "finn_sad",
    )
  }
}
