package org.mozilla.social.core.ui.common.utils

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import org.mozilla.social.core.designsystem.theme.MoSoTheme
import org.mozilla.social.core.navigation.navigationModule

@Composable
fun PreviewTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    KoinApplication(application = {
        modules(navigationModule)
    }) {
        MoSoTheme(
            darkTheme = darkTheme,
        ) {
            content()
        }
    }
}