package social.firefly.core.ui.postcard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import social.firefly.common.utils.StringFactory
import social.firefly.common.utils.timeSinceNow
import social.firefly.common.utils.toPxInt
import social.firefly.core.designsystem.icon.FfIcons
import social.firefly.core.designsystem.theme.FfTheme
import social.firefly.core.ui.common.dialog.deleteStatusConfirmationDialog
import social.firefly.core.ui.common.dialog.unbookmarkAccountConfirmationDialog
import social.firefly.core.ui.common.dialog.unfavoriteAccountConfirmationDialog
import social.firefly.core.ui.common.utils.PreviewTheme
import social.firefly.core.ui.common.utils.getMaxWidth
import social.firefly.core.ui.common.utils.shareUrl
import social.firefly.core.ui.postcard.MainPostCardUiState
import social.firefly.core.ui.postcard.OverflowDropDownType
import social.firefly.core.ui.postcard.PostCardInteractions
import social.firefly.core.ui.postcard.PostCardInteractionsNoOp
import social.firefly.core.ui.postcard.PostContentUiState
import social.firefly.core.ui.postcard.postCardUiStatePreview

@Suppress("MagicNumber", "LongMethod")
@Composable
internal fun BottomRow(
    modifier: Modifier = Modifier,
    post: MainPostCardUiState,
    postCardInteractions: PostCardInteractions,
) {
    val context = LocalContext.current

    val unfavoriteStatusDialog = unfavoriteAccountConfirmationDialog {
        postCardInteractions.onFavoriteClicked(post.statusId, false)
    }

    val unbookmarkStatusDialog = unbookmarkAccountConfirmationDialog {
        postCardInteractions.onBookmarkClicked(post.statusId, false)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BottomIconButton(
            modifier = Modifier.offset(x = (-8).dp),
            onClick = { postCardInteractions.onReplyClicked(post.statusId) },
            painter = FfIcons.chatBubbles(),
            count = post.replyCount,
        )
        BottomIconButton(
            modifier = Modifier.offset(x = (-4).dp),
            onClick = { postCardInteractions.onBoostClicked(post.statusId, !post.userBoosted) },
            painter = FfIcons.boost(),
            count = post.boostCount,
            highlighted = post.userBoosted,
        )
        BottomIconButton(
            onClick = {
                if (post.shouldShowUnfavoriteConfirmation && post.isFavorited) {
                    unfavoriteStatusDialog.open()
                } else {
                    postCardInteractions.onFavoriteClicked(post.statusId, !post.isFavorited)
                }
            },
            painter = if (post.isFavorited) FfIcons.heartFilled() else FfIcons.heart(),
            count = post.favoriteCount,
            highlighted = post.isFavorited,
            highlightColor = FfTheme.colors.iconFavorite,
        )
        BottomIconButton(
            modifier = Modifier.offset(x = 4.dp),
            onClick = {
                if (post.shouldShowUnbookmarkConfirmation && post.isBookmarked) {
                    unbookmarkStatusDialog.open()
                } else {
                    postCardInteractions.onBookmarkClicked(post.statusId, !post.isBookmarked)
                }
            },
            painter = if (post.isBookmarked) FfIcons.bookmarkFill() else FfIcons.bookmark(),
            highlighted = post.isBookmarked,
            highlightColor = FfTheme.colors.iconBookmark,
        )
        BottomIconButton(
            modifier = Modifier.offset(x = 8.dp),
            onClick = {
                post.url?.let { url ->
                    shareUrl(url, context)
                }
            },
            painter = FfIcons.share(),
        )
    }
}

@Composable
private fun BottomIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    painter: Painter,
    count: String? = null,
    highlighted: Boolean = false,
    highlightColor: Color = FfTheme.colors.iconAccent,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClick,
        ) {
            Icon(
                painter = painter,
                contentDescription = "",
                tint = if (highlighted) {
                    highlightColor
                } else {
                    LocalContentColor.current
                },
            )
        }
        Text(
            modifier = Modifier
                .offset(x = (-8).dp),
            text = count ?: "",
            style = FfTheme.typography.labelXSmall,
            maxLines = 1,
        )
    }
}

@Suppress("MagicNumber", "MaxLineLength")
@Preview
@Composable
private fun BottomRowPreview() {
    PreviewTheme {
        Box(
            modifier = Modifier.width(250.dp)
        ) {
            BottomRow(
                post = postCardUiStatePreview,
                postCardInteractions = PostCardInteractionsNoOp,
            )
        }
    }
}

@Suppress("MagicNumber", "MaxLineLength")
@Preview
@Composable
private fun BottomRowLargePreview() {
    PreviewTheme {
        Box(
            modifier = Modifier.width(500.dp)
        ) {
            BottomRow(
                post = postCardUiStatePreview,
                postCardInteractions = PostCardInteractionsNoOp,
            )
        }
    }
}

@Suppress("MagicNumber", "MaxLineLength")
@Preview
@Composable
private fun BottomRowSmallPreview() {
    PreviewTheme {
        Box(
            modifier = Modifier.width(150.dp)
        ) {
            BottomRow(
                post = postCardUiStatePreview,
                postCardInteractions = PostCardInteractionsNoOp,
            )
        }
    }
}
