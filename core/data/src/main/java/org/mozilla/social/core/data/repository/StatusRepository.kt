package org.mozilla.social.core.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.mozilla.social.core.network.MastodonApi
import org.mozilla.social.model.ImageState
import org.mozilla.social.model.MediaUpdateRequestBody
import org.mozilla.social.model.entity.request.StatusCreate

class StatusRepository(
    private val mastodonApi: MastodonApi,
) {

    suspend fun sendPost(
        statusText: String,
        imageStates: List<ImageState>,
    ) {
        coroutineScope {
            // asynchronously update all attachment descriptions before sending post
            imageStates.map { imageState ->
                if (imageState.attachmentId != null && imageState.description.isNotBlank()) {
                    async {
                        mastodonApi.updateMedia(
                            imageState.attachmentId!!,
                            MediaUpdateRequestBody(imageState.description)
                        )
                    }
                } else {
                    null
                }
            }.forEach {
                it?.await()
            }
            mastodonApi.postStatus(
                StatusCreate(
                    status = statusText,
                    mediaIds = imageStates.mapNotNull { it.attachmentId }
                )
            )
        }
    }
}