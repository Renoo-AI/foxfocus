package com.foxfocus.app.navigation

sealed class FoxTab(val route: String) {
  data object Home : FoxTab("home")
  data object Missions : FoxTab("missions")
  data object Blocker : FoxTab("blocker")
  data object Store : FoxTab("store")
  data object Family : FoxTab("family")
  data object Badges : FoxTab("badges")
  data object History : FoxTab("history")
  data object Profile : FoxTab("profile")
}

const val ROUTE_GAME = "game/{gameId}"
fun gameRoute(gameId: String) = "game/$gameId"

const val ROUTE_SETTINGS = "settings"
const val ROUTE_PC_PAIRING = "pc_pairing"
