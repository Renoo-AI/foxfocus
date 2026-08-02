package com.foxfocus.app.ui.games

import com.foxfocus.app.data.db.entity.GameStatsEntity

data class GameResult(
  val rawCoins: Int,
  val statsUpdate: (GameStatsEntity) -> GameStatsEntity = { it },
)
