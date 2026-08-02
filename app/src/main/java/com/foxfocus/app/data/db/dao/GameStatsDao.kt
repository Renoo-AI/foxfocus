package com.foxfocus.app.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.foxfocus.app.data.db.entity.GameStatsEntity
import com.foxfocus.app.economy.GameId
import kotlinx.coroutines.flow.Flow

@Dao
interface GameStatsDao {
  @Query("SELECT * FROM game_stats WHERE gameId = :gameId")
  fun observe(gameId: GameId): Flow<GameStatsEntity?>

  @Query("SELECT * FROM game_stats WHERE gameId = :gameId")
  suspend fun get(gameId: GameId): GameStatsEntity?

  @Query("SELECT * FROM game_stats")
  fun observeAll(): Flow<List<GameStatsEntity>>

  @Upsert
  suspend fun upsert(stats: GameStatsEntity)
}
