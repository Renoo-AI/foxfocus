package com.foxfocus.app.data.db

import androidx.room.TypeConverter
import com.foxfocus.app.data.db.entity.ActivityKind
import com.foxfocus.app.economy.GameId

class Converters {
  @TypeConverter
  fun fromGameId(value: GameId): String = value.name

  @TypeConverter
  fun toGameId(value: String): GameId = GameId.valueOf(value)

  @TypeConverter
  fun fromActivityKind(value: ActivityKind): String = value.name

  @TypeConverter
  fun toActivityKind(value: String): ActivityKind = ActivityKind.valueOf(value)
}
