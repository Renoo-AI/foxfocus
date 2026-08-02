package com.foxfocus.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_deals")
data class DailyDealEntity(
  @PrimaryKey val dealId: String,
  val isWeekly: Boolean = false,
  val titleAr: String,
  val descriptionAr: String,
  val originalPriceCoins: Int,
  val discountedPriceCoins: Int,
  val discountPercent: Int,
  val expiryEpochMs: Long,
  val isPurchased: Boolean = false,
  val itemType: String, // BOOSTER, COSMETIC, STREAK_FREEZE, PREMIUM_PACK, HINT
  val targetItemId: String,
)
