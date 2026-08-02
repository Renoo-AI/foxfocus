package com.foxfocus.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_state")
data class PlayerStateEntity(
  @PrimaryKey val id: Int = SINGLETON_ID,
  val coinBalance: Int = 0,
  val diamondBalance: Double = 0.0,
  val level: Int = 1,
  val streakDays: Int = 0,
  val bestStreakDays: Int = 0,
  val lastActiveEpochDay: Long = 0L,
  val dailyCoinsEarnedToday: Int = 0,
  val dailyCoinsEpochDay: Long = 0L,
  val isPremium: Boolean = true,
  val premiumExpiryEpochMs: Long = 0L,
  val ageYears: Int = 22,
  val userEmail: String = "user@foxfocus.app",
  val streakFreezeCount: Int = 0,
  val streakFreezeAutoEnabled: Boolean = true,
  val totalFocusSessionsCompleted: Int = 0,
  val referralCount: Int = 0,
  val hasRatedApp: Boolean = false,
  val isFirstSubscription: Boolean = true,
  val lastActiveDaysAgo: Int = 0,
  val activeThemeId: String = "default",
  val activeBackgroundId: String = "default",
  val coinMultiplierExpiryMs: Long = 0L,
  val coinMultiplierValue: Double = 1.0,
  val monthlyFreeFreezeClaimedMonth: String = "",
) {
  companion object {
    const val SINGLETON_ID = 0
  }
}
