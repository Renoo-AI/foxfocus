package com.foxfocus.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.foxfocus.app.economy.GameId

@Entity(tableName = "game_stats")
data class GameStatsEntity(
  @PrimaryKey val gameId: GameId,
  val timesPlayed: Int = 0,
  val bestScore: Int = 0,
  val bestTimeMs: Long? = null,
  val difficultyLevelReached: Int = 0,
  val bestAverageReflexMs: Long? = null,
  val lastPlayedAtEpochMillis: Long = 0L,
)
