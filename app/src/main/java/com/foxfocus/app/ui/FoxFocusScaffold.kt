package com.foxfocus.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.foxfocus.app.R
import com.foxfocus.app.auth.AuthRepository
import com.foxfocus.app.data.firestore.UserProfileRepository
import com.foxfocus.app.data.repo.FoxRepository
import com.foxfocus.app.navigation.FoxTab
import com.foxfocus.app.navigation.ROUTE_GAME
import com.foxfocus.app.navigation.ROUTE_PC_PAIRING
import com.foxfocus.app.navigation.ROUTE_SETTINGS
import com.foxfocus.app.theme.Primary
import com.foxfocus.app.theme.Surface
import com.foxfocus.app.theme.TextMuted
import com.foxfocus.app.ui.screens.badges.BadgesScreen
import com.foxfocus.app.ui.screens.blocker.BlockerScreen
import com.foxfocus.app.ui.screens.family.FoxFamilyScreen
import com.foxfocus.app.ui.screens.games.GamePlayScreen
import com.foxfocus.app.ui.screens.history.HistoryScreen
import com.foxfocus.app.ui.screens.home.HomeScreen
import com.foxfocus.app.ui.screens.missions.MissionsScreen
import com.foxfocus.app.ui.screens.pairing.PcPairingScreen
import com.foxfocus.app.ui.screens.profile.ProfileScreen
import com.foxfocus.app.ui.screens.settings.FullSettingsScreen
import com.foxfocus.app.ui.screens.store.StoreScreen
import kotlinx.coroutines.launch

private val tabs = listOf(
  FoxTab.Home,
  FoxTab.Missions,
  FoxTab.Blocker,
  FoxTab.Store,
  FoxTab.Family,
  FoxTab.Badges,
  FoxTab.History,
  FoxTab.Profile,
)

@Composable
fun FoxFocusScaffold(
  repository: FoxRepository,
  startTab: FoxTab,
  authRepository: AuthRepository,
  userProfileRepository: UserProfileRepository,
) {
  val navController = rememberNavController()
  val scope = androidx.compose.runtime.rememberCoroutineScope()
  val context = androidx.compose.ui.platform.LocalContext.current

  Scaffold(
    bottomBar = {
      val backStackEntry by navController.currentBackStackEntryAsState()
      val currentRoute = backStackEntry?.destination?.route
      if (currentRoute in tabs.map { it.route }) {
        NavigationBar(containerColor = Surface) {
          tabs.forEach { tab ->
            NavigationBarItem(
              selected = currentRoute == tab.route,
              onClick = {
                navController.navigate(tab.route) {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
                }
              },
              icon = { Icon(tabIcon(tab), contentDescription = null) },
              label = { Text(tabLabel(tab)) },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                selectedTextColor = Primary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = Surface,
              ),
            )
          }
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = startTab.route,
      modifier = Modifier.padding(innerPadding),
    ) {
      composable(FoxTab.Home.route) {
        HomeScreen(
          repository = repository,
          onNavigateToMissions = { navController.navigate(FoxTab.Missions.route) },
          onNavigateToStore = { navController.navigate(FoxTab.Store.route) },
          onNavigateToHistory = { navController.navigate(FoxTab.History.route) },
        )
      }
      composable(FoxTab.Missions.route) {
        MissionsScreen(repository, onPlayGame = { gameId -> navController.navigate(com.foxfocus.app.navigation.gameRoute(gameId.name)) })
      }
      composable(FoxTab.Blocker.route) { BlockerScreen(repository) }
      composable(FoxTab.Store.route) { StoreScreen(repository) }
      composable(FoxTab.Family.route) { FoxFamilyScreen(repository) }
      composable(FoxTab.Badges.route) { BadgesScreen(repository) }
      composable(FoxTab.History.route) { HistoryScreen(repository) }
      composable(FoxTab.Profile.route) {
        ProfileScreen(
          repository = repository,
          authRepository = authRepository,
          userProfileRepository = userProfileRepository,
          onOpenSettings = { navController.navigate(ROUTE_SETTINGS) },
          onOpenPcPairing = { navController.navigate(ROUTE_PC_PAIRING) },
        )
      }
      composable(ROUTE_SETTINGS) {
        FullSettingsScreen(
          repository = repository,
          onSignOut = { scope.launch { authRepository.signOut(context) } },
        )
      }
      composable(ROUTE_PC_PAIRING) {
        PcPairingScreen(
          repository = repository,
          authRepository = authRepository,
          userProfileRepository = userProfileRepository,
        )
      }
      composable(ROUTE_GAME) { backStackEntry ->
        val gameIdName = backStackEntry.arguments?.getString("gameId").orEmpty()
        GamePlayScreen(
          gameId = com.foxfocus.app.economy.GameId.valueOf(gameIdName),
          repository = repository,
          onExit = { navController.popBackStack() },
        )
      }
    }
  }
}

@Composable
private fun tabLabel(tab: FoxTab): String = when (tab) {
  FoxTab.Home -> stringResource(R.string.nav_home)
  FoxTab.Missions -> stringResource(R.string.nav_missions)
  FoxTab.Blocker -> stringResource(R.string.nav_blocker)
  FoxTab.Store -> stringResource(R.string.nav_store)
  FoxTab.Family -> stringResource(R.string.nav_family)
  FoxTab.Badges -> stringResource(R.string.nav_badges)
  FoxTab.History -> stringResource(R.string.nav_history)
  FoxTab.Profile -> stringResource(R.string.nav_profile)
}

private fun tabIcon(tab: FoxTab) = when (tab) {
  FoxTab.Home -> Icons.Filled.Home
  FoxTab.Missions -> Icons.Filled.SportsEsports
  FoxTab.Blocker -> Icons.Filled.Shield
  FoxTab.Store -> Icons.Filled.ShoppingBag
  FoxTab.Family -> Icons.Filled.Group
  FoxTab.Badges -> Icons.Filled.EmojiEvents
  FoxTab.History -> Icons.Filled.History
  FoxTab.Profile -> Icons.Filled.Person
}
