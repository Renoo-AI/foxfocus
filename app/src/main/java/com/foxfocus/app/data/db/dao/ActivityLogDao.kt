package com.foxfocus.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.foxfocus.app.data.db.entity.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {
  @Query("SELECT * FROM activity_log ORDER BY timestampEpochMillis DESC")
  fun observeAll(): Flow<List<ActivityLogEntity>>

  @Insert
  suspend fun insert(entry: ActivityLogEntity)

  @Query("SELECT COALESCE(SUM(coinsDelta), 0) FROM activity_log WHERE coinsDelta > 0")
  fun observeLifetimeCoinsEarned(): Flow<Int>
}
