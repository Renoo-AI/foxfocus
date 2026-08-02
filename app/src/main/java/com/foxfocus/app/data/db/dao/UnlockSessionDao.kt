package com.foxfocus.app.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.foxfocus.app.data.db.entity.UnlockSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UnlockSessionDao {
  @Query("SELECT * FROM unlock_sessions WHERE packageName = :packageName")
  suspend fun get(packageName: String): UnlockSessionEntity?

  @Query("SELECT * FROM unlock_sessions")
  fun observeAll(): Flow<List<UnlockSessionEntity>>

  @Upsert
  suspend fun upsert(session: UnlockSessionEntity)

  @Query("DELETE FROM unlock_sessions WHERE expiresAtEpochMillis < :nowEpochMillis")
  suspend fun clearExpired(nowEpochMillis: Long)
}
