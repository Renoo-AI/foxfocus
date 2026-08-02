package com.foxfocus.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class BadgeEntity(
  @PrimaryKey val badgeId: String,
  val titleAr: String,
  val descriptionAr: String,
  val rewardCoins: Int,
  val iconName: String,
  val isUnlocked: Boolean = false,
  val unlockedAtEpochMs: Long = 0L,
  val isExclusive: Boolean = false,
)
