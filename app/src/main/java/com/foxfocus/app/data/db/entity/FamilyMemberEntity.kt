package com.foxfocus.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
  @PrimaryKey val memberId: String,
  val name: String,
  val avatarUrl: String = "",
  val streakDays: Int = 0,
  val isUser: Boolean = false,
  val joinedAtEpochMs: Long = System.currentTimeMillis(),
)
