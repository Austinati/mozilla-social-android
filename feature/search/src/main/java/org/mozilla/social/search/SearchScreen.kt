package org.mozilla.social.search

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.mozilla.social.common.Resource
import org.mozilla.social.core.designsystem.icon.MoSoIcons
import org.mozilla.social.core.designsystem.theme.MoSoRadius
import org.mozilla.social.core.designsystem.theme.MoSoSpacing
import org.mozilla.social.core.designsystem.theme.MoSoTheme
import org.mozilla.social.core.designsystem.utils.NoRipple
import org.mozilla.social.core.ui.accountfollower.AccountFollowingButton
import org.mozilla.social.core.ui.common.MoSoSearchBar
import org.mozilla.social.core.ui.common.MoSoSurface
import org.mozilla.social.core.ui.common.MoSoTab
import org.mozilla.social.core.ui.common.MoSoTabRow
import org.mozilla.social.core.ui.common.account.quickview.AccountQuickViewBox
import org.mozilla.social.core.ui.common.appbar.MoSoCloseableTopAppBar
import org.mozilla.social.core.ui.common.divider.MoSoDivider
import org.mozilla.social.core.ui.common.error.GenericError
import org.mozilla.social.core.ui.common.loading.MaxSizeLoading
import org.mozilla.social.core.ui.postcard.PostCard
import org.mozilla.social.core.ui.postcard.PostCardInteractions
import org.mozilla.social.feature.search.R

@Composable
internal fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SearchScreen(
        uiState = uiState,
        searchInteractions = viewModel,
        postCardInteractions = viewModel.postCardDelegate,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun SearchScreen(
    uiState: SearchUiState,
    searchInteractions: SearchInteractions,
    postCardInteractions: PostCardInteractions,
) {
    MoSoSurface {
        Column(Modifier.systemBarsPadding()) {
            val searchFocusRequester = remember { FocusRequester() }
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusManager = LocalFocusManager.current
            var searchHasFocus by remember {
                mutableStateOf(uiState.query.isBlank())
            }

            LaunchedEffect(Unit) {
                if (uiState.query.isBlank()) {
                    searchFocusRequester.requestFocus()
                }
            }

            MoSoCloseableTopAppBar(
                actions = {
                    MoSoSearchBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                searchHasFocus = it.hasFocus
                            }
                            .focusRequester(searchFocusRequester)
                            .padding(
                                end = MoSoSpacing.md,
                                start = 60.dp,
                            ),
                        query = uiState.query,
                        onQueryChange = { searchInteractions.onQueryTextChanged(it) },
                        onSearch = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            searchInteractions.onSearchClicked()
                        },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                painter = MoSoIcons.magnifyingGlass(),
                                contentDescription = stringResource(id = R.string.search),
                                tint = MoSoTheme.colors.iconSecondary,
                            )
                        },
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box {
                Column {
                    Tabs(
                        uiState = uiState,
                        searchInteractions = searchInteractions,
                    )
                    ListContent(
                        uiState = uiState,
                        searchInteractions = searchInteractions,
                        postCardInteractions = postCardInteractions,
                    )
                }
                androidx.compose.animation.AnimatedVisibility(
                    modifier = Modifier.background(MoSoTheme.colors.layer1),
                    visible = searchHasFocus,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Suggestions(
                        uiState = uiState,
                        searchInteractions = searchInteractions,
                    )
                }
            }
        }
    }
}

@Composable
private fun Tabs(
    uiState: SearchUiState,
    searchInteractions: SearchInteractions,
) {
    val context = LocalContext.current

    MoSoTabRow(selectedTabIndex = uiState.selectedTab.ordinal) {
        SearchTab.entries.forEach { tabType ->
            MoSoTab(
                modifier =
                Modifier
                    .height(40.dp),
                selected = uiState.selectedTab == tabType,
                onClick = { searchInteractions.onTabClicked(tabType) },
                content = {
                    Text(
                        text = tabType.tabTitle.build(context),
                        style = MoSoTheme.typography.labelMedium,
                    )
                },
            )
        }
    }
}

@Composable
private fun ListContent(
    uiState: SearchUiState,
    searchInteractions: SearchInteractions,
    postCardInteractions: PostCardInteractions,
) {
    when (uiState.topResource) {
        is Resource.Loading -> {
            MaxSizeLoading()
        }
        is Resource.Error -> {
            GenericError(
                onRetryClicked = { searchInteractions.onRetryClicked() }
            )
        }
        is Resource.Loaded -> {
            when (uiState.selectedTab) {
                SearchTab.TOP -> {
                    TopList(
                        searchResultUiState = uiState.topResource.data,
                        searchInteractions = searchInteractions,
                        postCardInteractions = postCardInteractions,
                    )
                }
                SearchTab.ACCOUNTS -> {}
                SearchTab.HASHTAGS -> {}
                SearchTab.POSTS -> {}
            }
        }
    }
}

@Composable
private fun TopList(
    searchResultUiState: SearchResultUiState,
    searchInteractions: SearchInteractions,
    postCardInteractions: PostCardInteractions,
) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
    ) {
        if (searchResultUiState.accountUiStates.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .padding(start = MoSoSpacing.md, end = MoSoSpacing.md, top = MoSoSpacing.md)
                        .clickable { searchInteractions.onTabClicked(SearchTab.ACCOUNTS) }
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(id = R.string.accounts_tab),
                        style = MoSoTheme.typography.titleMedium,
                    )
                    Icon(painter = MoSoIcons.caretRight(), contentDescription = "")
                }
                LazyRow {
                    items(
                        count = searchResultUiState.accountUiStates.count(),
                        key = { searchResultUiState.accountUiStates[it].quickViewUiState.accountId },
                    ) { index ->
                        val item = searchResultUiState.accountUiStates[index]
                        NoRipple {
                            AccountQuickViewBox(
                                modifier = Modifier
                                    .padding(MoSoSpacing.md)
                                    .border(
                                        width = 1.dp,
                                        color = MoSoTheme.colors.borderPrimary,
                                        shape = RoundedCornerShape(MoSoRadius.lg_16_dp)
                                    )
                                    .clickable {
                                        searchInteractions.onAccountClicked(item.quickViewUiState.accountId)
                                    }
                                    .width(256.dp)
                                    .padding(MoSoSpacing.md),
                                uiState = item.quickViewUiState,
                                buttonSlot = {
                                    AccountFollowingButton(
                                        onButtonClicked = {
                                            searchInteractions.onFollowClicked(
                                                item.quickViewUiState.accountId,
                                                item.isFollowing
                                            )
                                        },
                                        isFollowing = item.isFollowing
                                    )
                                },
                            )
                        }
                    }
                }

                MoSoDivider()
            }
        }

        if (searchResultUiState.postCardUiStates.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .padding(MoSoSpacing.md)
                        .clickable { searchInteractions.onTabClicked(SearchTab.POSTS) }
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(id = R.string.posts_tab),
                        style = MoSoTheme.typography.titleMedium,
                    )
                    Icon(painter = MoSoIcons.caretRight(), contentDescription = "")
                }
            }

            items(
                count = searchResultUiState.postCardUiStates.count(),
                key = { searchResultUiState.postCardUiStates[it].statusId },
            ) { index ->
                val item = searchResultUiState.postCardUiStates[index]
                PostCard(
                    post = item,
                    postCardInteractions = postCardInteractions,
                )
                if (index < searchResultUiState.postCardUiStates.count()) {
                    MoSoDivider()
                }
            }
        }
    }
}

@Suppress("UnusedParameter")
@Composable
private fun Suggestions(
    uiState: SearchUiState,
    searchInteractions: SearchInteractions,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "search suggestions")
    }

}