package social.firefly.core.repository.mastodon

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import social.firefly.common.parseMastodonLinkHeader
import social.firefly.core.database.dao.FavoritesTimelineStatusDao
import social.firefly.core.database.model.entities.statusCollections.FavoritesTimelineStatus
import social.firefly.core.database.model.entities.statusCollections.FavoritesTimelineStatusWrapper
import social.firefly.core.model.Status
import social.firefly.core.model.paging.MastodonPagedResponse
import social.firefly.core.model.paging.StatusPagingWrapper
import social.firefly.core.network.mastodon.FavoritesApi
import social.firefly.core.network.mastodon.utils.toMastodonPagedResponse
import social.firefly.core.repository.mastodon.model.status.toExternalModel
import kotlin.math.min

class FavoritesRepository(
    private val api: FavoritesApi,
    private val dao: FavoritesTimelineStatusDao,
) {
    suspend fun getFavorites(
        maxId: String? = null,
        sinceId: String? = null,
        minId: String? = null,
        limit: Int? = null,
    ): MastodonPagedResponse<Status> = api.getFavorites(
        maxId = maxId,
        sinceId = sinceId,
        minId = minId,
        limit = limit,
    ).toMastodonPagedResponse { it.toExternalModel() }

    @ExperimentalPagingApi
    fun getFavoritesPager(
        remoteMediator: RemoteMediator<Int, FavoritesTimelineStatusWrapper>,
        pageSize: Int = 20,
        initialLoadSize: Int = 40,
    ): Flow<PagingData<Status>> = Pager(
        config = PagingConfig(
            pageSize = pageSize,
            initialLoadSize = initialLoadSize,
        ),
        remoteMediator = remoteMediator,
    ) {
        dao.favoritesTimelinePagingSource()
    }.flow.map { pagingData ->
        pagingData.map {
            it.status.toExternalModel()
        }
    }

    suspend fun deleteFavoritesTimeline() {
        dao.deleteFavoritesTimelines()
    }

    suspend fun insertAll(
        statuses: List<FavoritesTimelineStatus>
    ) = dao.upsertAll(statuses)

    suspend fun insert(
        status: FavoritesTimelineStatus
    ) = dao.upsert(status)

    suspend fun deleteStatusFromTimeline(
        statusId: String,
    ) = dao.deletePost(statusId)

    suspend fun getStatusFromTimeline(
        statusId: String,
    ): FavoritesTimelineStatus = dao.getStatus(statusId)
}