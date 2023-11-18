package org.mozilla.social.feature.discover

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mozilla.social.core.navigation.BottomBarNavigationDestination

fun NavGraphBuilder.discoverScreen() {
    composable(
        route = BottomBarNavigationDestination.Discover.route,
    ) {
        DiscoverScreen()
    }
}
