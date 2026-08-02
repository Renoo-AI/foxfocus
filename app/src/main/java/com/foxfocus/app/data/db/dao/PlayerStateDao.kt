package com.foxfocus.app.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.foxfocus.app.data.db.entity.PlayerStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerStateDao {
  @Query("SELECT * FROM player_state WHERE id = ${PlayerStateEntity.SINGLETON_ID}")
  fun observe(): Flow<PlayerStateEntity?>

  @Query("SELECT * FROM player_state WHERE id = ${PlayerStateEntity.SINGLETON_ID}")
  suspend fun get(): PlayerStateEntity?

  @Upsert
  suspend fun upsert(state: PlayerStateEntity)
}
