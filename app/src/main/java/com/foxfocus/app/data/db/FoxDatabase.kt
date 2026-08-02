package com.foxfocus.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.foxfocus.app.data.db.dao.ActivityLogDao
import com.foxfocus.app.data.db.dao.BadgeDao
import com.foxfocus.app.data.db.dao.BlockedAppDao
import com.foxfocus.app.data.db.dao.DealDao
import com.foxfocus.app.data.db.dao.FamilyDao
import com.foxfocus.app.data.db.dao.GameStatsDao
import com.foxfocus.app.data.db.dao.GiftDao
import com.foxfocus.app.data.db.dao.LeaderboardDao
import com.foxfocus.app.data.db.dao.PlayerStateDao
import com.foxfocus.app.data.db.dao.UnlockSessionDao
import com.foxfocus.app.data.db.entity.ActivityLogEntity
import com.foxfocus.app.data.db.entity.BadgeEntity
import com.foxfocus.app.data.db.entity.BlockedAppEntity
import com.foxfocus.app.data.db.entity.DailyDealEntity
import com.foxfocus.app.data.db.entity.FamilyMemberEntity
import com.foxfocus.app.data.db.entity.GameStatsEntity
import com.foxfocus.app.data.db.entity.GiftItemEntity
import com.foxfocus.app.data.db.entity.LeaderboardEntity
import com.foxfocus.app.data.db.entity.PlayerStateEntity
import com.foxfocus.app.data.db.entity.UnlockSessionEntity

@Database(
  entities = [
    PlayerStateEntity::class,
    ActivityLogEntity::class,
    BlockedAppEntity::class,
    UnlockSessionEntity::class,
    GameStatsEntity::class,
    BadgeEntity::class,
    FamilyMemberEntity::class,
    DailyDealEntity::class,
    GiftItemEntity::class,
    LeaderboardEntity::class,
  ],
  version = 3,
  exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class FoxDatabase : RoomDatabase() {
  abstract fun playerStateDao(): PlayerStateDao
  abstract fun activityLogDao(): ActivityLogDao
  abstract fun blockedAppDao(): BlockedAppDao
  abstract fun unlockSessionDao(): UnlockSessionDao
  abstract fun gameStatsDao(): GameStatsDao
  abstract fun badgeDao(): BadgeDao
  abstract fun familyDao(): FamilyDao
  abstract fun dealDao(): DealDao
  abstract fun giftDao(): GiftDao
  abstract fun leaderboardDao(): LeaderboardDao
}
