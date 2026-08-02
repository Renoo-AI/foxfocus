package com.foxfocus.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard")
data class LeaderboardEntity(
  @PrimaryKey val userId: String,
  val username: String,
  val avatarResName: String = "finn_default",
  val rank: Int,
  val level: Int,
  val xp: Int,
  val streakDays: Int,
  val isUser: Boolean = false,
)
