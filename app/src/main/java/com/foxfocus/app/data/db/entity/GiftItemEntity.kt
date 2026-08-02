package com.foxfocus.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gift_records")
data class GiftItemEntity(
  @PrimaryKey val giftId: String,
  val senderName: String,
  val recipientEmail: String,
  val planId: String, // WEEKLY, MONTHLY, ANNUAL, LIFETIME
  val planTitleAr: String,
  val message: String = "",
  val paymentCurrency: String, // USD, FC, DIAMOND
  val amountPaid: Double,
  val status: String = "PENDING", // PENDING, ACCEPTED, CANCELLED
  val createdAtEpochMs: Long = System.currentTimeMillis(),
  val acceptedAtEpochMs: Long = 0L,
)
