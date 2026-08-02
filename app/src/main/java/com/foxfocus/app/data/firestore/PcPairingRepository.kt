package com.foxfocus.app.data.firestore

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Snapshot mirrored from the phone into the pairing doc so a paired desktop can render a live read-only view. */
data class PairingMirror(
  val displayName: String = "",
  val avatarId: String = "finn_default",
  val provider: String = "",
  val level: Int = 1,
  val streakDays: Int = 0,
  val coinBalance: Int = 0,
  val diamondBalance: Double = 0.0,
  val isPremium: Boolean = true,
  val updatedAtEpochMs: Long = 0L,
)

enum class PairingStatus { PENDING, APPROVED, REVOKED, UNKNOWN }

data class Pairing(
  val code: String = "",
  val status: PairingStatus = PairingStatus.UNKNOWN,
  val ownerUid: String? = null,
)

/**
 * WhatsApp/Telegram-style desktop pairing, built entirely on Firestore client rules —
 * no custom backend or Cloud Functions required. The phone never hands over its Firebase
 * session; it only approves a code and republishes a read-only [PairingMirror] snapshot.
 */
class PcPairingRepository {

  private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()
  private fun doc(code: String) = db.collection("pairings").document(code)

  /** Called by the phone after scanning the desktop's QR code. */
  suspend fun approve(code: String, ownerUid: String, mirror: PairingMirror): Boolean {
    val snapshot = doc(code).get().await()
    if (!snapshot.exists() || snapshot.getString("status") != "pending") return false

    doc(code).update(
      mapOf(
        "status" to "approved",
        "ownerUid" to ownerUid,
        "mirror" to mirror,
      )
    ).await()
    return true
  }

  suspend fun pushMirror(code: String, mirror: PairingMirror) {
    doc(code).update("mirror", mirror).await()
  }

  suspend fun revoke(code: String) {
    doc(code).update("status", "revoked").await()
  }

  fun observePairing(code: String): Flow<Pairing?> = callbackFlow {
    val registration = doc(code).addSnapshotListener { snapshot, _ ->
      if (snapshot == null || !snapshot.exists()) {
        trySend(null)
      } else {
        val status = when (snapshot.getString("status")) {
          "pending" -> PairingStatus.PENDING
          "approved" -> PairingStatus.APPROVED
          "revoked" -> PairingStatus.REVOKED
          else -> PairingStatus.UNKNOWN
        }
        trySend(Pairing(code = code, status = status, ownerUid = snapshot.getString("ownerUid")))
      }
    }
    awaitClose { registration.remove() }
  }
}
