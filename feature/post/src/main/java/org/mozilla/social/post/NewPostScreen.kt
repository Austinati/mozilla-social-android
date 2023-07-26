@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
)

package org.mozilla.social.post

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.mozilla.social.common.LoadState
import org.mozilla.social.common.utils.toFile
import org.mozilla.social.core.designsystem.theme.MozillaSocialTheme
import org.mozilla.social.core.designsystem.utils.NoIndication
import org.mozilla.social.core.ui.TransparentNoTouchOverlay
import org.mozilla.social.core.ui.VerticalDivider
import org.mozilla.social.core.ui.VisibilityDropDownButton
import org.mozilla.social.core.ui.media.MediaUpload
import org.mozilla.social.core.ui.transparentTextFieldColors
import org.mozilla.social.model.ImageState
import org.mozilla.social.model.entity.StatusVisibility
import org.mozilla.social.post.NewPostViewModel.Companion.MAX_POST_LENGTH
import org.mozilla.social.post.interactions.ImageInteractions
import org.mozilla.social.post.interactions.PollInteractions
import org.mozilla.social.post.poll.Poll
import org.mozilla.social.post.poll.PollDuration
import org.mozilla.social.post.poll.PollDurationDropDown
import org.mozilla.social.post.poll.PollStyle
import org.mozilla.social.post.poll.PollStyleDropDown

@Composable
internal fun NewPostRoute(
    onStatusPosted: () -> Unit,
    onCloseClicked: () -> Unit,
    viewModel: NewPostViewModel = koinViewModel(parameters = { parametersOf(onStatusPosted) })
) {
    NewPostScreen(
        statusText = viewModel.statusText.collectAsState().value,
        onStatusTextChanged = viewModel::onStatusTextUpdated,
        onPostClicked = viewModel::onPostClicked,
        onCloseClicked = onCloseClicked,
        sendButtonEnabled = viewModel.sendButtonEnabled.collectAsState().value,
        imageStates = viewModel.imageStates.collectAsState().value,
        addImageButtonEnabled = viewModel.addImageButtonEnabled.collectAsState().value,
        imageInteractions = viewModel,
        isSendingPost = viewModel.isSendingPost.collectAsState().value,
        visibility = viewModel.visibility.collectAsState().value,
        onVisibilitySelected = viewModel::onVisibilitySelected,
        poll = viewModel.poll.collectAsState().value,
        pollInteractions = viewModel,
        pollButtonEnabled = viewModel.pollButtonEnabled.collectAsState().value,
    )

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.errorToastMessage.collect {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
private fun NewPostScreen(
    statusText: String,
    onStatusTextChanged: (String) -> Unit,
    onPostClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    sendButtonEnabled: Boolean,
    imageStates: Map<Uri, ImageState>,
    addImageButtonEnabled: Boolean,
    imageInteractions: ImageInteractions,
    isSendingPost: Boolean,
    visibility: StatusVisibility,
    onVisibilitySelected: (StatusVisibility) -> Unit,
    poll: Poll?,
    pollInteractions: PollInteractions,
    pollButtonEnabled: Boolean,
) {
    val context = LocalContext.current
    val multipleMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = (NewPostViewModel.MAX_IMAGES - imageStates.size).coerceAtLeast(2)
        )
    ) { uris ->
        uris.forEach {
            imageInteractions.onImageInserted(it, it.toFile(context))
        }
    }
    val singleMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { imageInteractions.onImageInserted(it, it.toFile(context)) }
    }
    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
            TopBar(
                onPostClicked = onPostClicked,
                onCloseClicked = onCloseClicked,
                sendButtonEnabled = sendButtonEnabled,
                visibility = visibility,
                onVisibilitySelected = onVisibilitySelected,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                MainBox(
                    statusText = statusText,
                    onStatusTextChanged = onStatusTextChanged,
                    imageStates = imageStates,
                    imageInteractions = imageInteractions,
                    poll = poll,
                    pollInteractions = pollInteractions,
                )
            }
            BottomBar(
                onUploadImageClicked = {
                    val mediaRequest =
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    if (NewPostViewModel.MAX_IMAGES - imageStates.size <= 1) {
                        singleMediaLauncher.launch(mediaRequest)
                    } else {
                        multipleMediaLauncher.launch(mediaRequest)
                    }
                },
                addImageButtonEnabled = addImageButtonEnabled,
                characterCount = statusText.count(),
                pollInteractions = pollInteractions,
                pollButtonEnabled = pollButtonEnabled,
            )
        }
        if (isSendingPost) {
            TransparentNoTouchOverlay()
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun TopBar(
    onPostClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    sendButtonEnabled: Boolean,
    visibility: StatusVisibility,
    onVisibilitySelected: (StatusVisibility) -> Unit,
) {
    Column {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // left side
            IconButton(
                onClick = { onCloseClicked() },
            ) {
                Icon(Icons.Default.Close, "close")
            }

            // right side
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                VisibilityDropDownButton(
                    visibility = visibility,
                    onVisibilitySelected = onVisibilitySelected,
                )
                Spacer(modifier = Modifier.padding(start = 16.dp))
                val keyboard = LocalSoftwareKeyboardController.current
                IconButton(
                    enabled = sendButtonEnabled,
                    onClick = {
                        onPostClicked()
                        keyboard?.hide()
                    },
                ) {
                    Icon(Icons.Default.Send, "post")
                }
            }
        }
        Divider(
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun BottomBar(
    onUploadImageClicked: () -> Unit,
    addImageButtonEnabled: Boolean,
    characterCount: Int,
    pollInteractions: PollInteractions,
    pollButtonEnabled: Boolean,
) {
    val characterCountText = remember(characterCount) { "${MAX_POST_LENGTH - characterCount}" }
    Column {
        Divider(
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Box(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
        ) {
            // left row
            Row(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                IconButton(
                    onClick = { onUploadImageClicked() },
                    enabled = addImageButtonEnabled,
                ) {
                    Icon(Icons.Default.Add, "attach image")
                }
                IconButton(
                    onClick = { pollInteractions.onNewPollClicked() },
                    enabled = pollButtonEnabled,
                ) {
                    Icon(Icons.Default.Poll, "attach image")
                }
            }
            // right row
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                Text(text = characterCountText)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MainBox(
    statusText: String,
    onStatusTextChanged: (String) -> Unit,
    imageStates: Map<Uri, ImageState>,
    imageInteractions: ImageInteractions,
    poll: Poll?,
    pollInteractions: PollInteractions,
) {
    val localIndication = LocalIndication.current
    // disable ripple on click for the background
    CompositionLocalProvider(
        LocalIndication provides NoIndication
    ) {
        val keyboard = LocalSoftwareKeyboardController.current
        val textFieldFocusRequester = remember { FocusRequester() }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    textFieldFocusRequester.requestFocus()
                    keyboard?.show()
                }
        ) {
            // re-enable ripple
            CompositionLocalProvider(
                LocalIndication provides localIndication
            ) {
                LazyColumn {
                    item {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(textFieldFocusRequester),
                            value = statusText,
                            onValueChange = onStatusTextChanged,
                            label = {
                                Text(
                                    text = "What's happening?"
                                )
                            },
                            colors = transparentTextFieldColors()
                        )
                    }

                    poll?.let { Poll(poll = poll, pollInteractions = pollInteractions) }

                    items(imageStates.size) { index ->
                        ImageUploadBox(
                            imageState = imageStates.entries.elementAt(index),
                            imageInteractions = imageInteractions,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageUploadBox(
    imageState: Map.Entry<Uri, ImageState>,
    imageInteractions: ImageInteractions,
) {
    val outlineShape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .padding(16.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = outlineShape
            )
            .clip(
                outlineShape
            )
            .fillMaxWidth(),
    ) {
        MediaUpload(
            uri = imageState.key,
            loadState = imageState.value.loadState,
            onRetryClicked = imageInteractions::onImageInserted,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (imageState.value.loadState == LoadState.LOADED) {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = imageState.value.description,
                    onValueChange = { imageInteractions.onImageDescriptionTextUpdated(imageState.key, it) },
                    label = {
                        Text(
                            text = "Add a description"
                        )
                    },
                    colors = transparentTextFieldColors(),
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            IconButton(
                onClick = {
                    imageInteractions.onDeleteImageClicked(imageState.key)
                }
            ) {
                Icon(Icons.Default.Delete, "delete")
            }
        }
    }
}

private fun LazyListScope.Poll(
    poll: Poll,
    pollInteractions: PollInteractions,
) {
    items(poll.options.size) {index ->
        OutlinedTextField(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp),
            value = poll.options[index],
            onValueChange = { pollInteractions.onPollOptionTextChanged(index, it) },
            label = {
                Text(
                    text = "Choice ${index + 1}"
                )
            }
        )
    }
    item {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 16.dp, top = 16.dp, bottom = 0.dp),
        ) {
            IconButton(
                onClick = {
                    pollInteractions.onAddPollOptionClicked()
                }
            ) {
                Icon(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp),
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = "add poll option",
                )
            }

            Row(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                VerticalDivider(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .height(40.dp)
                )
                PollDurationDropDown(poll = poll, pollInteractions = pollInteractions)
                VerticalDivider(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .height(40.dp)
                )
                PollStyleDropDown(poll = poll, pollInteractions = pollInteractions)
            }
        }
        Row(
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                .clickable { pollInteractions.onHideCountUntilEndClicked() }
        ) {
            Checkbox(
                modifier = Modifier.align(Alignment.CenterVertically),
                checked = poll.hideTotals,
                onCheckedChange = { pollInteractions.onHideCountUntilEndClicked() },
            )
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = "Hide results until complete",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Preview
@Composable
private fun NewPostScreenPreview() {
    MozillaSocialTheme {
        NewPostScreen(
            statusText = "",
            onStatusTextChanged = {},
            onPostClicked = {},
            onCloseClicked = {},
            sendButtonEnabled = true,
            imageStates = mapOf(),
            addImageButtonEnabled = true,
            imageInteractions = object : ImageInteractions {},
            isSendingPost = false,
            visibility = StatusVisibility.Private,
            onVisibilitySelected = {},
            poll = Poll(
                options = listOf("option 1", "option 2"),
                style = PollStyle.SINGLE_CHOICE,
                pollDuration = PollDuration.ONE_DAY,
                hideTotals = false
            ),
            pollInteractions = object : PollInteractions {},
            pollButtonEnabled = true
        )
    }
}
