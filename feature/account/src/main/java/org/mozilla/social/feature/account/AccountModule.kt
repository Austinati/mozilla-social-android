package org.mozilla.social.feature.account

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val accountModule = module {
    viewModel { parameters -> AccountViewModel(
        get(),
        get(),
        parameters[0],
        parameters[1],
        parameters[2]
    )}
}