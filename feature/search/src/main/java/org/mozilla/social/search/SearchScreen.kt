package org.mozilla.social.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.mozilla.social.core.designsystem.icon.MoSoIcons
import org.mozilla.social.core.designsystem.theme.MoSoSpacing
import org.mozilla.social.core.designsystem.theme.MoSoTheme
import org.mozilla.social.core.ui.common.MoSoSearchBar
import org.mozilla.social.core.ui.common.MoSoSurface
import org.mozilla.social.core.ui.common.appbar.MoSoCloseableTopAppBar
import org.mozilla.social.core.ui.common.button.MoSoButton

@Composable
internal fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SearchScreen(
        uiState = uiState,
        searchInteractions = viewModel,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    uiState: SearchUiState,
    searchInteractions: SearchInteractions,
) {
    MoSoSurface {
        Column(Modifier.systemBarsPadding()) {
            var active by remember {
                mutableStateOf(true)
            }

            val searchFocusRequester = remember { FocusRequester() }

            MoSoSearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.hasFocus) active = true
                    }
                    .focusRequester(searchFocusRequester)
                    .padding(horizontal = MoSoSpacing.md),
                query = uiState.query,
                onQueryChange = { searchInteractions.onQueryTextChanged(it) },
                onSearch = {
                    if (uiState.query.isNotBlank()) {
                        active = !active
                    }
                },
                active = active,
                onActiveChange = { },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = MoSoIcons.magnifyingGlass(),
                        contentDescription = "",
                        tint = MoSoTheme.colors.iconPrimary,
                    )
                }
            ) {
                Text(text = "Search suggestions go here")
                LaunchedEffect(Unit) {
                    searchFocusRequester.requestFocus()
                }
            }
        }
    }
}