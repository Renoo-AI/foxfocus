package com.foxfocus.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ActivityKind { MISSION, GAME, UNLOCK_SPEND }

@Entity(tableName = "activity_log")
data class ActivityLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val kind: ActivityKind,
  val refId: String,
  val title: String,
  val coinsDelta: Int,
  val timestampEpochMillis: Long,
)
