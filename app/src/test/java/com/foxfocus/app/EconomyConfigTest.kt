package com.foxfocus.app

import com.foxfocus.app.economy.EconomyConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class EconomyConfigTest {

  @Test
  fun testCurrencyConversionRate() {
    assertEquals(1000, EconomyConfig.COINS_PER_DIAMOND)
  }

  @Test
  fun testStreakFreezeMaxLimit() {
    assertEquals(3, EconomyConfig.getStreakFreezeMaxLimit(50))
    assertEquals(4, EconomyConfig.getStreakFreezeMaxLimit(100))
    assertEquals(5, EconomyConfig.getStreakFreezeMaxLimit(200))
  }

  @Test
  fun testStudentDiscount() {
    val discount = EconomyConfig.calculateSubscriptionDiscount(
      ageYears = 20,
      isFirstSubscription = true,
      lastActiveDaysAgo = 0,
      referralCount = 5,
      hasRatedApp = true,
      streakDays = 30,
      totalFocusSessions = 1000,
      completedGamesCount = 22
    )
    assertEquals(0.20, discount, 0.001)
  }

  @Test
  fun testDiscountStackingCap() {
    val discount = EconomyConfig.calculateSubscriptionDiscount(
      ageYears = 30,
      isFirstSubscription = true, // 30%
      lastActiveDaysAgo = 35, // 25%
      referralCount = 2, // 30%
      hasRatedApp = true, // 10%
      streakDays = 30, // 20%
      totalFocusSessions = 500, // 25%
      completedGamesCount = 22 // 15%
    )
    // Total exceeds 55%, must be capped strictly at 0.55
    assertEquals(0.55, discount, 0.001)
  }

  @Test
  fun testFoxFamilySharePercentage() {
    assertEquals(0.0, EconomyConfig.getFamilySharePercentage(1), 0.001)
    assertEquals(0.25, EconomyConfig.getFamilySharePercentage(5), 0.001)
    assertEquals(0.35, EconomyConfig.getFamilySharePercentage(10), 0.001)
    assertEquals(0.45, EconomyConfig.getFamilySharePercentage(15), 0.001)
    assertEquals(0.50, EconomyConfig.getFamilySharePercentage(25), 0.001)
  }
}
