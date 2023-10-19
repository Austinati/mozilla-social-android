/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mozilla.social.core.designsystem.component

import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Wrapper for [SnackbarHost] for use with [MoSoSnackbar] and [MoSoSnackbarHostState], which
 * adds the [SnackbarType] parameter
 */
@Composable
fun MoSoSnackbarHost(
    hostState: MoSoSnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackbarData, SnackbarType) -> Unit = { snackbarData, snackbarType ->
        MoSoSnackbar(snackbarData = snackbarData, snackbarType = snackbarType)
    }
) {
    val currentSnackbarType = hostState.currentSnackbarType

    SnackbarHost(hostState.snackbarHostState, modifier, snackbar = { snackbarData ->
        currentSnackbarType?.let {
            snackbar(snackbarData, it)
        }
    })
}
