package com.foxfocus.app.economy

import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

data class GameEconomy(
  val baseCoins: Int = 0,
  val bonusCoins: Int = 0,
  val bonusThreshold: Int = 0,
  val performanceMax: Int = 0,
  val minReward: Int = 0,
  val rounds: Int = 0,
  val gridSize: Int = 0,
)

enum class CurrencyType { FOX_COIN, DIAMOND, USD }

data class CoinPack(
  val id: String,
  val titleAr: String,
  val coinAmount: Int,
  val priceUsd: Double,
  val priceDiamonds: Double,
  val savingsLabel: String = "",
)

data class BoosterItem(
  val id: String,
  val titleAr: String,
  val descriptionAr: String,
  val priceCoins: Int,
)

data class CosmeticItem(
  val id: String,
  val titleAr: String,
  val priceCoins: Int,
  val type: String, // BACKGROUND, THEME
  val isFree: Boolean = false,
)

data class PremiumPlan(
  val id: String,
  val titleAr: String,
  val durationMonths: Int, // 0 for weekly or lifetime
  val priceUsd: Double,
  val priceCoins: Int,
  val priceDiamonds: Double,
  val bonusCoins: Int = 0,
  val savingsLabel: String = "",
)

object EconomyConfig {

  // --- Currency Conversion ---
  const val COINS_PER_DIAMOND = 1000

  // --- Fox Coin Paid Store Packs ---
  val COIN_PACKS = listOf(
    CoinPack("pack_small", "حزمة صغيرة", 1000, 0.99, 1.0),
    CoinPack("pack_medium", "حزمة متوسطة", 5500, 4.99, 5.5, "توفير 10%"),
    CoinPack("pack_large", "حزمة كبيرة", 12000, 9.99, 12.0, "توفير 17%"),
    CoinPack("pack_giant", "حزمة عملاقة", 25000, 19.99, 25.0, "توفير 20%"),
    CoinPack("pack_legendary", "حزمة أسطورية", 60000, 44.99, 60.0, "توفير 25%"),
  )

  // --- Spend Store Boosters ---
  val BOOSTERS = listOf(
    BoosterItem("boost_time_5m", "تمديد المؤقت (+5 دقائق)", "يمدد جلسة التركيز الحالية 5 دقائق", 100),
    BoosterItem("boost_time_15m", "تمديد المؤقت (+15 دقيقة)", "يمدد جلسة التركيز الحالية 15 دقيقة", 250),
    BoosterItem("boost_mult_1h", "مضاعف العملات (×2 لمدة ساعة)", "يضاعف مكاسب Fox Coins لساعة كاملة", 400),
    BoosterItem("boost_mult_24h", "مضاعف العملات (×2 ليوم كامل)", "يضاعف مكاسب Fox Coins لـ 24 ساعة", 1200),
    BoosterItem("boost_skip_daily", "تخطي التحدي اليومي", "يتجاوز التحدي اليومي دون خسارة السلسلة", 150),
  )

  // --- Cosmetics ---
  val COSMETICS = listOf(
    CosmeticItem("bg_default", "خلفية افتراضية", 0, "BACKGROUND", isFree = true),
    CosmeticItem("bg_base_1", "خلفية أساسية 1", 150, "BACKGROUND"),
    CosmeticItem("bg_base_2", "خلفية أساسية 2", 300, "BACKGROUND"),
    CosmeticItem("bg_rare", "خلفية نادرة", 500, "BACKGROUND"),
    CosmeticItem("theme_rare_limited", "موضوع نادر (إصدار محدود)", 2500, "THEME"),
  )

  // --- Coin Gifting Fees ---
  fun getCoinGiftingCost(amountSent: Int): Int = when (amountSent) {
    100 -> 110  // 10% fee
    500 -> 530  // 6% fee
    1000 -> 1040 // 4% fee
    else -> (amountSent * 1.10).roundToInt()
  }

  // --- Streak Freeze Mechanics ---
  const val STREAK_FREEZE_PRICE_COINS = 150
  const val STREAK_FREEZE_PRICE_DIAMONDS = 0.15

  fun getStreakFreezeMaxLimit(streakDays: Int): Int = when {
    streakDays >= 200 -> 5
    streakDays >= 100 -> 4
    else -> 3
  }

  // --- Premium Subscription Plans ---
  val PREMIUM_PLANS = listOf(
    PremiumPlan("plan_weekly", "أسبوعي", 0, 2.49, 2000, 2.0),
    PremiumPlan("plan_monthly", "شهري", 1, 4.99, 4500, 5.0, bonusCoins = 500),
    PremiumPlan("plan_quarterly", "ربع سنوي (3 أشهر)", 3, 12.99, 12000, 12.0, savingsLabel = "توفير 13%"),
    PremiumPlan("plan_semi_annual", "نصف سنوي (6 أشهر)", 6, 23.99, 22000, 22.0, savingsLabel = "توفير 20%"),
    PremiumPlan("plan_annual", "سنوي (12 شهر)", 12, 39.99, 37000, 37.0, savingsLabel = "توفير 33%"),
    PremiumPlan("plan_lifetime", "مدى الحياة (دفعة واحدة)", -1, 79.99, 75000, 75.0),
  )

  // --- Discount Engine ---
  fun calculateSubscriptionDiscount(
    ageYears: Int,
    isFirstSubscription: Boolean,
    lastActiveDaysAgo: Int,
    referralCount: Int,
    hasRatedApp: Boolean,
    streakDays: Int,
    totalFocusSessions: Int,
    completedGamesCount: Int,
  ): Double {
    if (ageYears in 18..24) {
      return 0.20
    }

    var dynamicSum = 0.0

    if (isFirstSubscription) dynamicSum += 0.30
    if (lastActiveDaysAgo >= 30) dynamicSum += 0.25
    if (hasRatedApp) dynamicSum += 0.10

    val referralDiscount = min(0.50, referralCount * 0.15)
    dynamicSum += referralDiscount

    when {
      streakDays >= 30 -> dynamicSum += 0.20
      streakDays >= 14 -> dynamicSum += 0.10
      streakDays >= 7 -> dynamicSum += 0.05
    }

    when {
      totalFocusSessions >= 1000 -> dynamicSum += 0.40
      totalFocusSessions >= 500 -> dynamicSum += 0.25
      totalFocusSessions >= 100 -> dynamicSum += 0.10
    }

    if (completedGamesCount >= 22) dynamicSum += 0.15

    return min(0.55, dynamicSum)
  }

  // --- Fox Family Revenue Share Schedule ---
  fun getFamilySharePercentage(recipientStreakDays: Int): Double = when {
    recipientStreakDays >= 21 -> 0.50
    recipientStreakDays >= 14 -> 0.45
    recipientStreakDays >= 7 -> 0.35
    recipientStreakDays >= 3 -> 0.25
    else -> 0.0
  }

  // --- Game Economy Map ---
  val GAME_ECONOMY: Map<GameId, GameEconomy> = mapOf(
    GameId.SCHULTE to GameEconomy(baseCoins = 6, bonusCoins = 2, bonusThreshold = 20, gridSize = 5),
    GameId.MAZE to GameEconomy(baseCoins = 6, bonusCoins = 2, bonusThreshold = 30, gridSize = 11),
    GameId.MEMORY_MATCH to GameEconomy(baseCoins = 6, bonusCoins = 2, bonusThreshold = 8, gridSize = 4),
    GameId.QUICK_MATH to GameEconomy(performanceMax = 6, minReward = 2, rounds = 10),
    GameId.NUMBER_BALANCE to GameEconomy(performanceMax = 5, minReward = 2, rounds = 8),
    GameId.STROOP to GameEconomy(performanceMax = 12, minReward = 2, rounds = 10),
    GameId.COLOR_SORT to GameEconomy(baseCoins = 12, bonusCoins = 2, bonusThreshold = 10),
    GameId.FLOW_FREE to GameEconomy(baseCoins = 14, bonusCoins = 2, bonusThreshold = 15, gridSize = 5),
    GameId.REFLEX to GameEconomy(performanceMax = 12, minReward = 2, rounds = 8),
    GameId.SIMON_SAYS to GameEconomy(baseCoins = 20, bonusCoins = 5, bonusThreshold = 12, rounds = 12),
    GameId.DUAL_N_BACK to GameEconomy(baseCoins = 22, minReward = 2, rounds = 20),
    GameId.SLIDING_PUZZLE to GameEconomy(baseCoins = 20, bonusCoins = 2, bonusThreshold = 20, gridSize = 3),
  )

  fun economyFor(gameId: GameId): GameEconomy = GAME_ECONOMY.getValue(gameId)

  /** Daily coin cap tiers, keyed by player level. */
  val DAILY_CAP_BY_LEVEL: Map<Int, Int> = mapOf(1 to 100, 2 to 150, 3 to 200, 4 to 300)

  fun dailyCap(level: Int): Int = DAILY_CAP_BY_LEVEL[level.coerceIn(1, 4)] ?: 100

  /** x1.0 at day 1, scaling linearly to x2.0 at 10+ consecutive days. */
  fun streakMultiplier(streakDays: Int): Double {
    val effectiveDays = streakDays.coerceAtLeast(1)
    if (effectiveDays >= 10) return 2.0
    return 1.0 + (effectiveDays - 1) / 9.0
  }

  fun applyStreak(rawCoins: Int, streakDays: Int): Int =
    floor(rawCoins * streakMultiplier(streakDays)).toInt()

  const val COINS_PER_MINUTE_UNLOCK = 2
  val UNLOCK_DURATIONS_MINUTES = listOf(10, 20, 30)

  fun unlockCost(minutes: Int): Int = minutes * COINS_PER_MINUTE_UNLOCK

  // --- Per-Game Constants ---
  const val ODD_EASY_COINS = 2
  const val ODD_MEDIUM_COINS = 3
  const val ODD_HARD_COINS = 4
  const val ODD_MAX_WRONG_ATTEMPTS = 3
  const val ODD_HINT_COST = 5
  const val ODD_STREAK_BONUS_5 = 5
  const val ODD_STREAK_BONUS_10 = 15
  const val ODD_STREAK_BONUS_20 = 30

  fun oddOneOutTierForStreak(streak: Int): Tier = when {
    streak <= 2 -> Tier.EASY
    streak <= 5 -> Tier.MEDIUM
    else -> Tier.HARD
  }

  fun oddOneOutCoinsForTier(tier: Tier): Int = when (tier) {
    Tier.EASY -> ODD_EASY_COINS
    Tier.MEDIUM -> ODD_MEDIUM_COINS
    Tier.HARD -> ODD_HARD_COINS
  }

  const val GAME_2048_WIN_BONUS = 3

  const val PUZZLE8_BASE = 3
  const val PUZZLE8_BONUS = 2
  const val PUZZLE8_BONUS_MOVES = 20

  const val STEADY_HAND_BASE = 2
  const val STEADY_HAND_BONUS = 1

  const val WHERE_BALL_PER_ROUND = 1
  const val WHERE_BALL_ROUNDS = 3
  const val WHERE_BALL_STREAK_BONUS = 2

  const val FALLING_PER_10_BONUS = 1
  const val FALLING_20_TOTAL_BONUS = 2

  const val CONNECT_DOTS_BASE = 2
  const val CONNECT_DOTS_BONUS = 1
  const val CONNECT_DOTS_BONUS_SECONDS = 30

  const val RPS_ROUNDS = 10
  const val RPS_POINTS_PER_COIN = 3
  const val RPS_BONUS_THRESHOLD = 8
  const val RPS_BONUS_COINS = 2

  const val GUESS_MAX_ATTEMPTS = 7
  fun guessNumberReward(attempts: Int): Int = when (attempts) {
    1 -> 5
    2, 3 -> 3
    4, 5 -> 2
    6, 7 -> 1
    else -> 0
  }

  const val TTT_WIN = 3
  const val TTT_DRAW = 1
  const val TTT_LOSS = 0
}

/** Simon Says helper */
fun simonSaysReward(economy: GameEconomy, roundsCleared: Int, won: Boolean): Int =
  if (won) economy.baseCoins + economy.bonusCoins else maxOf(0, roundsCleared)

/** Dual N-Back helper */
fun dualNBackReward(economy: GameEconomy, correctCount: Int): Int =
  maxOf(economy.minReward, (correctCount * 1.1).roundToInt())

/** Reflex Test helper */
fun reflexReward(economy: GameEconomy, correctCount: Int): Int =
  maxOf(economy.minReward, min(economy.performanceMax, (correctCount * 1.5).roundToInt()))

/** Stroop helper */
fun perCorrectReward(economy: GameEconomy, correctCount: Int): Int =
  maxOf(economy.minReward, correctCount)

/** Quick Math / Number Balance helper */
fun scaledReward(economy: GameEconomy, correctCount: Int): Int =
  maxOf(economy.minReward, ((correctCount.toDouble() / economy.rounds) * economy.performanceMax).roundToInt())
