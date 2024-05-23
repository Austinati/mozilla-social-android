package social.firefly.core.ui.common.media

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import social.firefly.core.designsystem.theme.FfRadius
import social.firefly.core.model.Attachment
import social.firefly.core.ui.common.utils.getMaxWidth
import social.firefly.core.ui.common.utils.media
import kotlin.math.roundToInt

@Suppress("MagicNumber")
@Composable
fun MediaDisplay(
    attachments: List<Attachment>,
    onAttachmentClicked: (attachment: Attachment) -> Unit = {},
) {
    Column {
        when (attachments.size) {
            1 -> {
                SingleAttachment(
                    attachment = attachments.first(),
                    onAttachmentClicked = onAttachmentClicked,
                )
            }

            2 -> {
                AttachmentRow(
                    attachment1 = attachments.first(),
                    attachment2 = attachments[1],
                    onAttachmentClicked = onAttachmentClicked,
                )
            }

            3 -> {
                SingleAttachment(
                    attachment = attachments.first(),
                    onAttachmentClicked = onAttachmentClicked,
                )
                AttachmentRow(
                    attachment1 = attachments[1],
                    attachment2 = attachments[2],
                    onAttachmentClicked = onAttachmentClicked,
                )
            }

            4 -> {
                AttachmentRow(
                    attachment1 = attachments.first(),
                    attachment2 = attachments[1],
                    onAttachmentClicked = onAttachmentClicked,
                )
                AttachmentRow(
                    attachment1 = attachments[2],
                    attachment2 = attachments[3],
                    onAttachmentClicked = onAttachmentClicked,
                )
            }
        }
    }
}

@Composable
private fun SingleAttachment(
    attachment: Attachment,
    onAttachmentClicked: (attachment: Attachment) -> Unit
) {
    when (attachment) {
        is Attachment.Image -> {
            val aspectRatio by remember {
                mutableFloatStateOf(attachment.meta?.calculateAspectRatio() ?: 1f)
            }
            // For some reason, just using fillMaxWidth causes issues on some posts
            // in conjunction with aspect ratio.  It has something to do with using
            // .height(IntrinsicSize.Min) in PostCard.kt
//            val width = getMaxWidth()
            Attachment(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio),
                attachment = attachment,
                onAttachmentClicked = onAttachmentClicked,
            )
        }

        is Attachment.Gifv -> {
            val aspectRatio by remember {
                mutableFloatStateOf(attachment.meta?.calculateAspectRatio() ?: 1f)
            }
            // For some reason, just using fillMaxWidth causes issues on some posts
            // in conjunction with aspect ratio.  It has something to do with using
            // .height(IntrinsicSize.Min) in PostCard.kt
//            val width = getMaxWidth()
            attachment.url?.toUri()?.let {
                VideoPlayer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio),
                    uri = it,
                    onVideoClicked = { onAttachmentClicked(attachment) },
                )
            }
        }

        is Attachment.Video -> {
            val aspectRatio by remember {
                mutableFloatStateOf(attachment.meta?.calculateAspectRatio() ?: 1f)
            }
            // For some reason, just using fillMaxWidth causes issues on some posts
            // in conjunction with aspect ratio.  It has something to do with using
            // .height(IntrinsicSize.Min) in PostCard.kt
//            val width = getMaxWidth()
            attachment.url?.toUri()?.let {
                VideoPlayer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio),
                    uri = it,
                    onVideoClicked = { onAttachmentClicked(attachment) },
                )
            }
        }

        else -> {}
    }
}

/**
 * For some reason the server might not return an aspect ratio
 */
fun Attachment.Gifv.Meta.calculateAspectRatio(): Float =
    when {
        aspectRatio != null -> aspectRatio!!
        original?.width != null && original?.height != null ->
            (original!!.width!!.toFloat() / original!!.height!!.toFloat())

        else -> 1f
    }

fun Attachment.Image.Meta.calculateAspectRatio(): Float =
    when {
        original?.aspectRatio != null -> original!!.aspectRatio!!
        original?.width != null && original?.height != null ->
            (original!!.width!!.toFloat() / original!!.height!!.toFloat())

        else -> 1f
    }

fun Attachment.Video.Meta.calculateAspectRatio(): Float =
    when {
        aspectRatio != null -> aspectRatio!!
        original?.width != null && original?.height != null ->
            (original!!.width!!.toFloat() / original!!.height!!.toFloat())

        else -> 1f
    }

@Composable
private fun Attachment(
    modifier: Modifier = Modifier,
    attachment: Attachment,
    onAttachmentClicked: (attachment: Attachment) -> Unit,
) {
    AsyncImage(
        modifier = modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(FfRadius.media))
            .clickable { onAttachmentClicked(attachment) },
        model = attachment.previewUrl,
        contentDescription = attachment.description,
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun AttachmentRow(
    attachment1: Attachment,
    attachment2: Attachment,
    onAttachmentClicked: (attachment: Attachment) -> Unit
) {
    Row {
        Attachment(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
            attachment = attachment1,
            onAttachmentClicked = onAttachmentClicked,
        )
        Attachment(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
            attachment = attachment2,
            onAttachmentClicked = onAttachmentClicked,
        )
    }
}
