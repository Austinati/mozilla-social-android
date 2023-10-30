package org.mozilla.social

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.mozilla.social.core.navigation.NavigationRelay
import org.mozilla.social.core.ui.postcard.PostCardNavigation
import org.mozilla.social.core.navigation.NavDestination
import org.mozilla.social.core.navigation.NavigateTo
import org.mozilla.social.core.navigation.NavigationEventFlow
import org.mozilla.social.ui.PostCardNavigationImpl

val mainModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    single<PostCardNavigation> { PostCardNavigationImpl(get()) }
}