package org.mozilla.social.feature.account

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val accountModule = module {
    viewModel { parametersHolder ->
        AccountViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            parametersHolder[0],
            parametersHolder[1],
            parametersHolder[2],
        )
    }
    factory { parametersHolder ->
        AccountTimelineRemoteMediator(
            get(),
            get(),
            get(),
            parametersHolder[0],
            parametersHolder[1],
        )
    }
}