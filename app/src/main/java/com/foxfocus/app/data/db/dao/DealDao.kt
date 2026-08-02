package com.foxfocus.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.foxfocus.app.data.db.entity.DailyDealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DealDao {
  @Query("SELECT * FROM daily_deals WHERE isWeekly = 0 ORDER BY expiryEpochMs ASC LIMIT 1")
  fun getDailyDeal(): Flow<DailyDealEntity?>

  @Query("SELECT * FROM daily_deals WHERE isWeekly = 1 ORDER BY expiryEpochMs ASC LIMIT 1")
  fun getWeeklyDeal(): Flow<DailyDealEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDeal(deal: DailyDealEntity)

  @Query("UPDATE daily_deals SET isPurchased = 1 WHERE dealId = :dealId")
  suspend fun markPurchased(dealId: String)
}
