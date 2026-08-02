package com.foxfocus.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unlock_sessions")
data class UnlockSessionEntity(
  @PrimaryKey val packageName: String,
  val expiresAtEpochMillis: Long,
)
