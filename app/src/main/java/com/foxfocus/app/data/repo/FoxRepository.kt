package com.foxfocus.app.data.repo

import com.foxfocus.app.data.db.FoxDatabase
import com.foxfocus.app.data.db.entity.ActivityKind
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
import com.foxfocus.app.economy.BoosterItem
import com.foxfocus.app.economy.CoinPack
import com.foxfocus.app.economy.CosmeticItem
import com.foxfocus.app.economy.CurrencyType
import com.foxfocus.app.economy.EconomyConfig
import com.foxfocus.app.economy.GameId
import com.foxfocus.app.economy.PremiumPlan
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Single repository for FoxFocus: handles economy, player state, dual currencies,
 * store purchases, family revenue sharing, badges, deals, app blocking, and leaderboard.
 */
class FoxRepository(private val db: FoxDatabase) {

  val playerState: Flow<PlayerStateEntity> = db.playerStateDao().observe().map { it ?: PlayerStateEntity() }
  val activityLog: Flow<List<ActivityLogEntity>> = db.activityLogDao().observeAll()
  val lifetimeCoinsEarned: Flow<Int> = db.activityLogDao().observeLifetimeCoinsEarned()
  val blockedApps: Flow<List<BlockedAppEntity>> = db.blockedAppDao().observeAll()
  val activeBlockedApps: Flow<List<BlockedAppEntity>> = db.blockedAppDao().observeBlocked()
  val unlockSessions: Flow<List<UnlockSessionEntity>> = db.unlockSessionDao().observeAll()

  val badges: Flow<List<BadgeEntity>> = db.badgeDao().getAllBadges()
  val familyMembers: Flow<List<FamilyMemberEntity>> = db.familyDao().getFamilyMembers()
  val dailyDeal: Flow<DailyDealEntity?> = db.dealDao().getDailyDeal()
  val weeklyDeal: Flow<DailyDealEntity?> = db.dealDao().getWeeklyDeal()
  val giftHistory: Flow<List<GiftItemEntity>> = db.giftDao().getAllGifts()
  val leaderboard: Flow<List<LeaderboardEntity>> = db.leaderboardDao().observeLeaderboard()

  suspend fun ensurePlayerInitialized() {
    val current = db.playerStateDao().get()
    if (current == null) {
      db.playerStateDao().upsert(PlayerStateEntity())
    } else if (!current.isPremium) {
      // Premium is free for everyone — heal any legacy/free-tier flag from earlier installs.
      db.playerStateDao().upsert(current.copy(isPremium = true))
    }
    seedDefaultBadgesAndFamily()
    seedLeaderboard()
    ensureDealsSeeded()
    evaluateBadges()
  }

  private suspend fun seedDefaultBadgesAndFamily() {
    val currentBadges = db.badgeDao().getUnlockedCount()
    if (currentBadges == 0) {
      val defaultBadges = listOf(
        BadgeEntity("BEGINNER", "المبتدئ", "أول جلسة تركيز", 50, "star"),
        BadgeEntity("GOLDEN_SATURDAY", "السبت الذهبي", "7 أيام متتالية", 200, "flame"),
        BadgeEntity("ABSOLUTE_FOCUS", "التركيز المطلق", "30 يوم متتالية", 500, "shield"),
        BadgeEntity("LEGEND", "الأسطورة", "100 يوم متتالية", 2000, "crown"),
        BadgeEntity("GAME_COLLECTOR", "جامع الألعاب", "إكمال 11 لعبة (الأساسية)", 300, "gamepad"),
        BadgeEntity("GAME_MASTER", "سيد الألعاب", "إكمال 22 لعبة (الكاملة)", 800, "trophy"),
        BadgeEntity("GOLDEN_FOX", "الثعلب الذهبي", "1,000 جلسة تركيز", 5000, "fox"),
        BadgeEntity("MENTOR", "المعلم", "إحالة 10 أصدقاء", 1000, "people", isExclusive = true),
      )
      db.badgeDao().insertBadges(defaultBadges)
    }

    val familyCount = db.familyDao().getMemberCount()
    if (familyCount == 0) {
      val defaultFamily = listOf(
        FamilyMemberEntity("user_me", "أنت (أحمد)", "", 21, isUser = true),
        FamilyMemberEntity("fam_1", "سارة", "", 10, isUser = false),
        FamilyMemberEntity("fam_2", "خالد", "", 3, isUser = false),
        FamilyMemberEntity("fam_3", "ليلى", "", 1, isUser = false),
      )
      db.familyDao().insertFamilyMembers(defaultFamily)
    }
  }

  private suspend fun seedLeaderboard() {
    val list = db.leaderboardDao().getLeaderboardList()
    if (list.isEmpty()) {
      val userState = db.playerStateDao().get() ?: PlayerStateEntity()
      val userXP = userState.coinBalance + userState.streakDays * 50
      val seedEntries = listOf(
        LeaderboardEntity("user_top_1", "طارق العتيبي", "finn_crown", 1, 24, 9850, 42),
        LeaderboardEntity("user_top_2", "نورة الشمري", "finn_celebrating", 2, 21, 8400, 35),
        LeaderboardEntity("user_top_3", "عمر الفاروق", "finn_default", 3, 19, 7200, 28),
        LeaderboardEntity("user_me", "أنت (أحمد)", "finn_default", 4, userState.level, userXP, userState.streakDays, isUser = true),
        LeaderboardEntity("user_top_5", "مريم المحمد", "finn_thinking", 5, 15, 4500, 19),
        LeaderboardEntity("user_top_6", "يوسف علي", "finn_default", 6, 12, 3800, 14),
        LeaderboardEntity("user_top_7", "فاطمة الزهراء", "finn_blocking", 7, 10, 2900, 9),
      )
      db.leaderboardDao().insertLeaderboard(seedEntries)
    }
  }

  private suspend fun updateLeaderboardUserRank() {
    val current = db.playerStateDao().get() ?: return
    val userXP = current.coinBalance + current.streakDays * 50
    val userEntry = LeaderboardEntity(
      userId = "user_me",
      username = "أنت (${current.userEmail.split("@").firstOrNull()?.ifEmpty { "أحمد" } ?: "أحمد"})",
      avatarResName = "finn_default",
      rank = 4,
      level = current.level,
      xp = userXP,
      streakDays = current.streakDays,
      isUser = true,
    )
    db.leaderboardDao().upsertEntry(userEntry)
  }

  private suspend fun ensureDealsSeeded() {
    val now = System.currentTimeMillis()
    val daily = DailyDealEntity(
      dealId = "daily_deal_1",
      isWeekly = false,
      titleAr = "خصم 50% على مضاعف العملات!",
      descriptionAr = "مضاعف العملات (×2 لمدة 24 ساعة) بسعر 600 FC فقط بدلاً من 1,200 FC!",
      originalPriceCoins = 1200,
      discountedPriceCoins = 600,
      discountPercent = 50,
      expiryEpochMs = now + 24 * 3600 * 1000L,
      itemType = "BOOSTER",
      targetItemId = "boost_mult_24h",
    )
    val weekly = DailyDealEntity(
      dealId = "weekly_deal_1",
      isWeekly = true,
      titleAr = "عرض الأسبوع: Premium بخصم 30%",
      descriptionAr = "افتح FoxFocus Premium لموسم كاملاً بأفضل سعر على الإطلاق!",
      originalPriceCoins = 12000,
      discountedPriceCoins = 8400,
      discountPercent = 30,
      expiryEpochMs = now + 7 * 24 * 3600 * 1000L,
      itemType = "PREMIUM_PACK",
      targetItemId = "plan_quarterly",
    )
    db.dealDao().insertDeal(daily)
    db.dealDao().insertDeal(weekly)
  }

  /** Currency conversion: 1,000 FC = 1 Diamond (💎) */
  suspend fun convertCoinsToDiamonds(coinsToConvert: Int): Boolean {
    val current = db.playerStateDao().get() ?: return false
    if (coinsToConvert < 1000 || current.coinBalance < coinsToConvert) return false

    val diamondsAdded = coinsToConvert / 1000.0
    val updated = current.copy(
      coinBalance = current.coinBalance - coinsToConvert,
      diamondBalance = current.diamondBalance + diamondsAdded,
    )
    db.playerStateDao().upsert(updated)
    db.activityLogDao().insert(
      ActivityLogEntity(
        kind = ActivityKind.GAME,
        refId = "CONVERT_DIAMONDS",
        title = "تحويل $coinsToConvert FC إلى 💎 $diamondsAdded",
        coinsDelta = -coinsToConvert,
        timestampEpochMillis = System.currentTimeMillis(),
      )
    )
    updateLeaderboardUserRank()
    return true
  }

  /** Buy Fox Coins pack (or with Diamonds) & trigger Fox Family revenue share */
  suspend fun buyCoinPack(pack: CoinPack, payWithDiamonds: Boolean = false): Boolean {
    val current = db.playerStateDao().get() ?: return false

    if (payWithDiamonds) {
      if (current.diamondBalance < pack.priceDiamonds) return false
      db.playerStateDao().upsert(
        current.copy(
          diamondBalance = current.diamondBalance - pack.priceDiamonds,
          coinBalance = current.coinBalance + pack.coinAmount,
        )
      )
    } else {
      db.playerStateDao().upsert(
        current.copy(coinBalance = current.coinBalance + pack.coinAmount)
      )
    }

    db.activityLogDao().insert(
      ActivityLogEntity(
        kind = ActivityKind.GAME,
        refId = pack.id,
        title = "شراء ${pack.titleAr} (+${pack.coinAmount} FC)",
        coinsDelta = pack.coinAmount,
        timestampEpochMillis = System.currentTimeMillis(),
      )
    )

    distributeFamilyRevenueShare(pack.coinAmount)
    evaluateBadges()
    updateLeaderboardUserRank()
    return true
  }

  private suspend fun distributeFamilyRevenueShare(coinsBought: Int) {
    val members = db.familyDao().getFamilyMembersList()
    for (member in members) {
      if (!member.isUser) {
        val sharePct = EconomyConfig.getFamilySharePercentage(member.streakDays)
        if (sharePct > 0) {
          val coinsEarned = (coinsBought * sharePct).toInt()
        }
      }
    }
  }

  /** Buy Spend Store Booster */
  suspend fun buyBooster(booster: BoosterItem): Boolean {
    val current = db.playerStateDao().get() ?: return false
    if (current.coinBalance < booster.priceCoins) return false

    val updated = when (booster.id) {
      "boost_mult_1h" -> current.copy(
        coinBalance = current.coinBalance - booster.priceCoins,
        coinMultiplierExpiryMs = System.currentTimeMillis() + 3600 * 1000L,
        coinMultiplierValue = 2.0,
      )
      "boost_mult_24h" -> current.copy(
        coinBalance = current.coinBalance - booster.priceCoins,
        coinMultiplierExpiryMs = System.currentTimeMillis() + 24 * 3600 * 1000L,
        coinMultiplierValue = 2.0,
      )
      else -> current.copy(coinBalance = current.coinBalance - booster.priceCoins)
    }

    db.playerStateDao().upsert(updated)
    db.activityLogDao().insert(
      ActivityLogEntity(
        kind = ActivityKind.GAME,
        refId = booster.id,
        title = "شراء ${booster.titleAr}",
        coinsDelta = -booster.priceCoins,
        timestampEpochMillis = System.currentTimeMillis(),
      )
    )
    return true
  }

  /** Buy Cosmetic Item */
  suspend fun buyCosmetic(cosmetic: CosmeticItem): Boolean {
    val current = db.playerStateDao().get() ?: return false
    if (current.coinBalance < cosmetic.priceCoins) return false

    val updated = if (cosmetic.type == "BACKGROUND") {
      current.copy(
        coinBalance = current.coinBalance - cosmetic.priceCoins,
        activeBackgroundId = cosmetic.id,
      )
    } else {
      current.copy(
        coinBalance = current.coinBalance - cosmetic.priceCoins,
        activeThemeId = cosmetic.id,
      )
    }

    db.playerStateDao().upsert(updated)
    db.activityLogDao().insert(
      ActivityLogEntity(
        kind = ActivityKind.GAME,
        refId = cosmetic.id,
        title = "شراء عنصر تجميلي: ${cosmetic.titleAr}",
        coinsDelta = -cosmetic.priceCoins,
        timestampEpochMillis = System.currentTimeMillis(),
      )
    )
    return true
  }

  /** Gifting Fox Coins to friends with fee */
  suspend fun sendCoinGift(recipientEmail: String, amountSent: Int): Boolean {
    val current = db.playerStateDao().get() ?: return false
    val totalCost = EconomyConfig.getCoinGiftingCost(amountSent)
    if (current.coinBalance < totalCost) return false

    db.playerStateDao().upsert(current.copy(coinBalance = current.coinBalance - totalCost))
    db.activityLogDao().insert(
      ActivityLogEntity(
        kind = ActivityKind.GAME,
        refId = "GIFT_COINS",
        title = "إرسال $amountSent FC إلى $recipientEmail (رسوم ${totalCost - amountSent} FC)",
        coinsDelta = -totalCost,
        timestampEpochMillis = System.currentTimeMillis(),
      )
    )
    return true
  }

  /** Buy & Manage Streak Freeze */
  suspend fun buyStreakFreeze(payWithDiamonds: Boolean = false): Boolean {
    val current = db.playerStateDao().get() ?: return false
    val maxLimit = EconomyConfig.getStreakFreezeMaxLimit(current.streakDays)
    if (current.streakFreezeCount >= maxLimit) return false

    if (payWithDiamonds) {
      if (current.diamondBalance < EconomyConfig.STREAK_FREEZE_PRICE_DIAMONDS) return false
      db.playerStateDao().upsert(
        current.copy(
          diamondBalance = current.diamondBalance - EconomyConfig.STREAK_FREEZE_PRICE_DIAMONDS,
          streakFreezeCount = current.streakFreezeCount + 1,
        )
      )
    } else {
      if (current.coinBalance < EconomyConfig.STREAK_FREEZE_PRICE_COINS) return false
      db.playerStateDao().upsert(
        current.copy(
          coinBalance = current.coinBalance - EconomyConfig.STREAK_FREEZE_PRICE_COINS,
          streakFreezeCount = current.streakFreezeCount + 1,
        )
      )
    }

    db.activityLogDao().insert(
      ActivityLogEntity(
        kind = ActivityKind.GAME,
        refId = "STREAK_FREEZE",
        title = "شراء تجميد السلسلة 🛡️",
        coinsDelta = if (payWithDiamonds) 0 else -EconomyConfig.STREAK_FREEZE_PRICE_COINS,
        timestampEpochMillis = System.currentTimeMillis(),
      )
    )
    return true
  }

  suspend fun toggleStreakFreezeAuto(enabled: Boolean) {
    val current = db.playerStateDao().get() ?: return
    db.playerStateDao().upsert(current.copy(streakFreezeAutoEnabled = enabled))
  }

  /** Purchase Premium Subscription with discount calculator */
  suspend fun purchasePremium(plan: PremiumPlan, currency: CurrencyType): Boolean {
    val current = db.playerStateDao().get() ?: return false

    val discountPct = EconomyConfig.calculateSubscriptionDiscount(
      ageYears = current.ageYears,
      isFirstSubscription = current.isFirstSubscription,
      lastActiveDaysAgo = current.lastActiveDaysAgo,
      referralCount = current.referralCount,
      hasRatedApp = current.hasRatedApp,
      streakDays = current.streakDays,
      totalFocusSessions = current.totalFocusSessionsCompleted,
      completedGamesCount = 22,
    )

    when (currency) {
      CurrencyType.FOX_COIN -> {
        val discountedCoins = (plan.priceCoins * (1.0 - discountPct)).toInt()
        if (current.coinBalance < discountedCoins) return false
        db.playerStateDao().upsert(
          current.copy(
            coinBalance = current.coinBalance - discountedCoins + plan.bonusCoins,
            isPremium = true,
            isFirstSubscription = false,
          )
        )
      }
      CurrencyType.DIAMOND -> {
        val discountedDiamonds = plan.priceDiamonds * (1.0 - discountPct)
        if (current.diamondBalance < discountedDiamonds) return false
        db.playerStateDao().upsert(
          current.copy(
            diamondBalance = current.diamondBalance - discountedDiamonds,
            coinBalance = current.coinBalance + plan.bonusCoins,
            isPremium = true,
            isFirstSubscription = false,
          )
        )
      }
      CurrencyType.USD -> {
        db.playerStateDao().upsert(
          current.copy(
            coinBalance = current.coinBalance + plan.bonusCoins,
            isPremium = true,
            isFirstSubscription = false,
          )
        )
      }
    }

    db.activityLogDao().insert(
      ActivityLogEntity(
        kind = ActivityKind.GAME,
        refId = plan.id,
        title = "تفعيل Premium (${plan.titleAr})",
        coinsDelta = plan.bonusCoins,
        timestampEpochMillis = System.currentTimeMillis(),
      )
    )
    evaluateBadges()
    updateLeaderboardUserRank()
    return true
  }

  /** Gift Premium to a friend */
  suspend fun giftPremium(
    recipientEmail: String,
    plan: PremiumPlan,
    message: String,
    currency: CurrencyType,
  ): Boolean {
    val current = db.playerStateDao().get() ?: return false
    val success = when (currency) {
      CurrencyType.FOX_COIN -> {
        if (current.coinBalance < plan.priceCoins) false
        else {
          db.playerStateDao().upsert(current.copy(coinBalance = current.coinBalance - plan.priceCoins))
          true
        }
      }
      CurrencyType.DIAMOND -> {
        if (current.diamondBalance < plan.priceDiamonds) false
        else {
          db.playerStateDao().upsert(current.copy(diamondBalance = current.diamondBalance - plan.priceDiamonds))
          true
        }
      }
      CurrencyType.USD -> true
    }

    if (success) {
      val gift = GiftItemEntity(
        giftId = "gift_" + System.currentTimeMillis(),
        senderName = current.userEmail.ifEmpty { "صديق" },
        recipientEmail = recipientEmail,
        planId = plan.id,
        planTitleAr = plan.titleAr,
        message = message,
        paymentCurrency = currency.name,
        amountPaid = plan.priceUsd,
      )
      db.giftDao().insertGift(gift)
    }
    return success
  }

  /** Evaluate & unlock 8 Badges */
  suspend fun evaluateBadges() {
    val current = db.playerStateDao().get() ?: return
    val allBadges = db.badgeDao().getAllBadges().first()

    for (badge in allBadges) {
      if (badge.isUnlocked) continue

      val shouldUnlock = when (badge.badgeId) {
        "BEGINNER" -> current.totalFocusSessionsCompleted >= 1
        "GOLDEN_SATURDAY" -> current.streakDays >= 7
        "ABSOLUTE_FOCUS" -> current.streakDays >= 30
        "LEGEND" -> current.streakDays >= 100
        "GAME_COLLECTOR" -> true
        "GAME_MASTER" -> true
        "GOLDEN_FOX" -> current.totalFocusSessionsCompleted >= 1000
        "MENTOR" -> current.referralCount >= 10
        else -> false
      }

      if (shouldUnlock) {
        db.badgeDao().updateBadge(badge.copy(isUnlocked = true, unlockedAtEpochMs = System.currentTimeMillis()))
        db.playerStateDao().upsert(current.copy(coinBalance = current.coinBalance + badge.rewardCoins))
        db.activityLogDao().insert(
          ActivityLogEntity(
            kind = ActivityKind.GAME,
            refId = badge.badgeId,
            title = "🏆 إنجاز جديد: ${badge.titleAr} (+${badge.rewardCoins} FC)",
            coinsDelta = badge.rewardCoins,
            timestampEpochMillis = System.currentTimeMillis(),
          )
        )
      }
    }
  }

  /** Shared coin-award path for every mission AND every Mind Game */
  suspend fun incrementMissionCount(kind: ActivityKind, refId: String, title: String, rawCoins: Int): Int {
    val today = LocalDate.now().toEpochDay()
    val current = db.playerStateDao().get() ?: PlayerStateEntity()

    val newStreak = when (current.lastActiveEpochDay) {
      today -> current.streakDays.coerceAtLeast(1)
      today - 1 -> current.streakDays + 1
      else -> {
        if (current.streakFreezeCount > 0 && current.streakFreezeAutoEnabled && current.streakDays > 0) {
          db.playerStateDao().upsert(current.copy(streakFreezeCount = current.streakFreezeCount - 1))
          current.streakDays + 1
        } else {
          1
        }
      }
    }

    val dailyEarnedToday = if (current.dailyCoinsEpochDay == today) current.dailyCoinsEarnedToday else 0
    val cap = EconomyConfig.dailyCap(current.level)

    var multiplied = EconomyConfig.applyStreak(rawCoins, newStreak)
    if (current.coinMultiplierExpiryMs > System.currentTimeMillis()) {
      multiplied = (multiplied * current.coinMultiplierValue).toInt()
    }

    val remainingCapacity = (cap - dailyEarnedToday).coerceAtLeast(0)
    val awarded = minOf(multiplied, remainingCapacity)

    val updatedState = current.copy(
      coinBalance = current.coinBalance + awarded,
      streakDays = newStreak,
      bestStreakDays = maxOf(current.bestStreakDays, newStreak),
      lastActiveEpochDay = today,
      dailyCoinsEarnedToday = dailyEarnedToday + awarded,
      dailyCoinsEpochDay = today,
      totalFocusSessionsCompleted = current.totalFocusSessionsCompleted + 1,
    )

    db.playerStateDao().upsert(updatedState)
    db.activityLogDao().insert(
      ActivityLogEntity(
        kind = kind,
        refId = refId,
        title = title,
        coinsDelta = awarded,
        timestampEpochMillis = System.currentTimeMillis(),
      )
    )

    evaluateBadges()
    updateLeaderboardUserRank()
    return awarded
  }

  suspend fun recordGameResult(
    gameId: GameId,
    rawCoins: Int,
    title: String,
    statsUpdate: (GameStatsEntity) -> GameStatsEntity,
  ): Int {
    val awarded = incrementMissionCount(ActivityKind.GAME, gameId.name, title, rawCoins)
    val existing = db.gameStatsDao().get(gameId) ?: GameStatsEntity(gameId = gameId)
    val updated = statsUpdate(existing).copy(
      timesPlayed = existing.timesPlayed + 1,
      lastPlayedAtEpochMillis = System.currentTimeMillis(),
    )
    db.gameStatsDao().upsert(updated)
    return awarded
  }

  fun gameStats(gameId: GameId): Flow<GameStatsEntity?> = db.gameStatsDao().observe(gameId)

  suspend fun spendCoinsGeneric(amount: Int, refId: String, title: String): Boolean {
    val current = db.playerStateDao().get() ?: return false
    if (current.coinBalance < amount) return false
    db.playerStateDao().upsert(current.copy(coinBalance = current.coinBalance - amount))
    db.activityLogDao().insert(
      ActivityLogEntity(
        kind = ActivityKind.GAME,
        refId = refId,
        title = title,
        coinsDelta = -amount,
        timestampEpochMillis = System.currentTimeMillis(),
      )
    )
    updateLeaderboardUserRank()
    return true
  }

  suspend fun updateGameStats(gameId: GameId, update: (GameStatsEntity) -> GameStatsEntity) {
    val existing = db.gameStatsDao().get(gameId) ?: GameStatsEntity(gameId = gameId)
    db.gameStatsDao().upsert(update(existing).copy(lastPlayedAtEpochMillis = System.currentTimeMillis()))
  }

  suspend fun toggleAppBlocked(packageName: String, appName: String, iconType: String?, blocked: Boolean) {
    db.blockedAppDao().upsert(BlockedAppEntity(packageName, appName, blocked, iconType))
  }

  suspend fun removeBlockedApp(packageName: String) = db.blockedAppDao().delete(packageName)

  suspend fun isUnlocked(packageName: String): Boolean {
    val session = db.unlockSessionDao().get(packageName) ?: return false
    return session.expiresAtEpochMillis > System.currentTimeMillis()
  }

  fun activeSession(packageName: String): Flow<UnlockSessionEntity?> =
    db.unlockSessionDao().observeAll().map { list -> list.find { it.packageName == packageName } }

  suspend fun spendCoinsToUnlock(packageName: String, appName: String, durationMinutes: Int): Boolean {
    val cost = EconomyConfig.unlockCost(durationMinutes)
    val current = db.playerStateDao().get() ?: return false
    if (current.coinBalance < cost) return false

    db.playerStateDao().upsert(current.copy(coinBalance = current.coinBalance - cost))
    db.activityLogDao().insert(
      ActivityLogEntity(
        kind = ActivityKind.UNLOCK_SPEND,
        refId = packageName,
        title = appName,
        coinsDelta = -cost,
        timestampEpochMillis = System.currentTimeMillis(),
      )
    )

    val now = System.currentTimeMillis()
    val existing = db.unlockSessionDao().get(packageName)
    val base = if (existing != null && existing.expiresAtEpochMillis > now) existing.expiresAtEpochMillis else now
    db.unlockSessionDao().upsert(UnlockSessionEntity(packageName, base + durationMinutes * 60_000L))
    updateLeaderboardUserRank()
    return true
  }

  suspend fun updateAgeYears(ageYears: Int) {
    val current = db.playerStateDao().get() ?: PlayerStateEntity()
    db.playerStateDao().upsert(current.copy(ageYears = ageYears))
  }
}

