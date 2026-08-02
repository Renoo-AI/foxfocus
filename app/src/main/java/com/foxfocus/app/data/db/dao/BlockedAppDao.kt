package com.foxfocus.app.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.foxfocus.app.data.db.entity.BlockedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {
  @Query("SELECT * FROM blocked_apps ORDER BY appName")
  fun observeAll(): Flow<List<BlockedAppEntity>>

  @Query("SELECT * FROM blocked_apps WHERE isBlocked = 1")
  fun observeBlocked(): Flow<List<BlockedAppEntity>>

  @Query("SELECT * FROM blocked_apps WHERE isBlocked = 1")
  suspend fun getBlockedOnce(): List<BlockedAppEntity>

  @Upsert
  suspend fun upsert(app: BlockedAppEntity)

  @Query("DELETE FROM blocked_apps WHERE packageName = :packageName")
  suspend fun delete(packageName: String)
}
