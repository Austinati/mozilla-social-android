package org.mozilla.social.core.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mozilla.social.core.data.repository.model.status.toExternalModel
import org.mozilla.social.core.database.SocialDatabase
import org.mozilla.social.core.database.model.statusCollections.HomeTimelineStatus
import org.mozilla.social.core.network.TimelineApi
import org.mozilla.social.model.Status

class TimelineRepository internal constructor(
    private val timelineApi: TimelineApi,
    private val socialDatabase: SocialDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun getHomeTimeline(
        olderThanId: String? = null,
        immediatelyNewerThanId: String? = null,
        loadSize: Int? = null,
    ): List<Status> =
        timelineApi.getHomeTimeline(
            olderThanId = olderThanId,
            immediatelyNewerThanId = immediatelyNewerThanId,
            limit = loadSize,
        ).map { it.toExternalModel() }

    suspend fun getPublicTimeline(
        localOnly: Boolean? = null,
        federatedOnly: Boolean? = null,
        mediaOnly: Boolean? = null,
        olderThanId: String? = null,
        immediatelyNewerThanId: String? = null,
        loadSize: Int? = null,
    ): List<Status> =
        timelineApi.getPublicTimeline(
            localOnly = localOnly,
            federatedOnly = federatedOnly,
            mediaOnly = mediaOnly,
            olderThanId = olderThanId,
            newerThanId = immediatelyNewerThanId,
            limit = loadSize,
        ).map { it.toExternalModel() }

    suspend fun getHashtagTimeline(
        hashTag: String,
        olderThanId: String? = null,
        immediatelyNewerThanId: String? = null,
        loadSize: Int? = null,
    ): List<Status> =
        timelineApi.getHashTagTimeline(
            hashTag = hashTag,
            olderThanId = olderThanId,
            immediatelyNewerThanId = immediatelyNewerThanId,
            limit = loadSize,
        ).map { it.toExternalModel() }

    suspend fun insertStatus(status: Status)
        = withContext(ioDispatcher) {
            socialDatabase.homeTimelineDao().insert(
                HomeTimelineStatus(
                    statusId = status.statusId,
                    createdAt = status.createdAt,
                    accountId = status.account.accountId,
                    pollId = status.poll?.pollId,
                    boostedStatusId = status.boostedStatus?.statusId,
                    boostedPollId = status.boostedStatus?.poll?.pollId,
                    boostedStatusAccountId = status.boostedStatus?.account?.accountId,
                )
            )
        }

}
