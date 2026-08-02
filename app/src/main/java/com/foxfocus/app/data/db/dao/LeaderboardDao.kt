package com.foxfocus.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.foxfocus.app.data.db.entity.LeaderboardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaderboardDao {
  @Query("SELECT * FROM leaderboard ORDER BY xp DESC")
  fun observeLeaderboard(): Flow<List<LeaderboardEntity>>

  @Query("SELECT * FROM leaderboard ORDER BY xp DESC")
  suspend fun getLeaderboardList(): List<LeaderboardEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLeaderboard(entries: List<LeaderboardEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertEntry(entry: LeaderboardEntity)

  @Query("DELETE FROM leaderboard")
  suspend fun clear()
}
