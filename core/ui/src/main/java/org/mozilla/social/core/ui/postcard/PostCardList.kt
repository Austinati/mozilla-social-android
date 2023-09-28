package org.mozilla.social.core.ui.postcard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import org.mozilla.social.common.utils.StringFactory
import org.mozilla.social.core.designsystem.component.MoSoDivider
import org.mozilla.social.core.designsystem.component.MoSoToast
import org.mozilla.social.core.ui.R
import org.mozilla.social.core.ui.error.GenericError
import org.mozilla.social.core.ui.pullrefresh.PullRefreshIndicator
import org.mozilla.social.core.ui.pullrefresh.pullRefresh
import org.mozilla.social.core.ui.pullrefresh.rememberPullRefreshState
import org.mozilla.social.core.ui.recommendations.MoreInfoDialog
import org.mozilla.social.core.ui.recommendations.RecommendationCarousel
import org.mozilla.social.model.Recommendation

@Composable
fun PostCardList(
    feed: Flow<PagingData<PostCardUiState>>,
    errorToastMessage: SharedFlow<StringFactory>,
    reccs: List<Recommendation>? = null,
    postCardInteractions: PostCardInteractions,
    enablePullToRefresh: Boolean = false,
    headerContent: @Composable () -> Unit = {},
) {

    val lazyingPagingItems: LazyPagingItems<PostCardUiState> = feed.collectAsLazyPagingItems()

    val openAlertDialog = remember { mutableStateOf(false) }

    if (openAlertDialog.value) {
        MoreInfoDialog(
            onDismissRequest = { openAlertDialog.value = false },
            onConfirmation = { openAlertDialog.value = false },
            dialogTitle = stringResource(id = R.string.feed_recommendations_why_am_i_seeing_this),
            dialogText = stringResource(id = R.string.feed_recommendations_reason_you_are_seeing_this),
        )
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = lazyingPagingItems.loadState.refresh == LoadState.Loading,
        onRefresh = { lazyingPagingItems.refresh() }
    )

    Box(
        modifier = Modifier
            .pullRefresh(
                pullRefreshState,
                enabled = enablePullToRefresh,
            ),
    ) {

        if (lazyingPagingItems.loadState.refresh is LoadState.Error) {
            GenericError(
                modifier = Modifier
                    .fillMaxSize(),
                onRetryClicked = { lazyingPagingItems.refresh() }
            )
        }

        LazyColumn(
            Modifier
                .fillMaxSize(),
        ) {

            item { headerContent() }

            when (lazyingPagingItems.loadState.refresh) {
                is LoadState.Error -> {} // handle the error outside the lazy column
                else -> {
                    reccs?.let {
                        item {
                            RecommendationCarousel(reccs = it) { openAlertDialog.value = true }
                        }
                    }

                    items(
                        count = lazyingPagingItems.itemCount,
                        key = lazyingPagingItems.itemKey { it.statusId }
                    ) { index ->
                        lazyingPagingItems[index]?.let { item ->
                            PostCard(post = item, postCardInteractions)
                            if (index < lazyingPagingItems.itemCount) {
                                MoSoDivider()
                            }
                        }
                    }
                }
            }

            when (lazyingPagingItems.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .padding(16.dp)
                        )
                    }
                }

                is LoadState.Error -> {
                    item {
                        GenericError(
                            onRetryClicked = { lazyingPagingItems.retry() }
                        )
                    }
                }

                is LoadState.NotLoading -> {}
            }
        }

        if (enablePullToRefresh) {
            PullRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                refreshing = lazyingPagingItems.loadState.refresh == LoadState.Loading,
                state = pullRefreshState,
            )
        }
    }

    MoSoToast(toastMessage = errorToastMessage)
}

@Composable
fun PostCardList(
    items: List<PostCardUiState>,
    errorToastMessage: SharedFlow<StringFactory>,
    postCardInteractions: PostCardInteractions,
) {

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
    ) {
        items(
            count = items.count(),
            key = { items[it].statusId }
        ) { index ->
            val item = items[index]
            PostCard(post = item, postCardInteractions)
            if (index < items.count()) {
                MoSoDivider()
            }
        }
    }

    MoSoToast(toastMessage = errorToastMessage)
}
