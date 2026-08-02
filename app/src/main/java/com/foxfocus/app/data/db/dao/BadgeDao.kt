package com.foxfocus.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.foxfocus.app.data.db.entity.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
  @Query("SELECT * FROM badges")
  fun getAllBadges(): Flow<List<BadgeEntity>>

  @Query("SELECT * FROM badges WHERE badgeId = :badgeId LIMIT 1")
  suspend fun getBadgeById(badgeId: String): BadgeEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBadges(badges: List<BadgeEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun updateBadge(badge: BadgeEntity)

  @Query("SELECT COUNT(*) FROM badges WHERE isUnlocked = 1")
  suspend fun getUnlockedCount(): Int
}
