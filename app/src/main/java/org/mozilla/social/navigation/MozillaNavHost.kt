package org.mozilla.social.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.mozilla.social.feature.auth.AUTH_ROUTE
import org.mozilla.social.feature.auth.authScreen
import org.mozilla.social.feature.settings.SettingsRoute
import social.mozilla.feed.FeedScreen

@Composable
fun MozillaNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AUTH_ROUTE) {
        authScreen(onAuthenticated = {
            navController.navigate(MAIN_ROUTE,
                navOptions = NavOptions.Builder()
                    .setPopUpTo(AUTH_ROUTE, true)
                    .build())
        })
        mainGraph(navController)
    }
}

private fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation(startDestination = "feed", MAIN_ROUTE) {
        composable("feed") { FeedScreen() }
        composable("settings") { SettingsRoute(onLogout = {
            navController.popBackStack()
        }) }
    }
}

const val MAIN_ROUTE = "main"