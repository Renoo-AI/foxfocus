package com.foxfocus.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.foxfocus.app.data.db.entity.GiftItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GiftDao {
  @Query("SELECT * FROM gift_records ORDER BY createdAtEpochMs DESC")
  fun getAllGifts(): Flow<List<GiftItemEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGift(gift: GiftItemEntity)

  @Query("UPDATE gift_records SET status = :status WHERE giftId = :giftId")
  suspend fun updateGiftStatus(giftId: String, status: String)
}
