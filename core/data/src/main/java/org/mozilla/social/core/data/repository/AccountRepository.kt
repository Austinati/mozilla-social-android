package org.mozilla.social.core.data.repository

import kotlinx.coroutines.coroutineScope
import org.mozilla.social.core.data.repository.model.account.toExternal
import org.mozilla.social.core.data.repository.model.followers.FollowersPagingWrapper
import org.mozilla.social.core.data.repository.model.status.toExternalModel
import org.mozilla.social.core.database.SocialDatabase
import org.mozilla.social.core.network.AccountApi
import org.mozilla.social.model.Account
import org.mozilla.social.model.Relationship
import org.mozilla.social.model.Status
import retrofit2.HttpException
import retrofit2.Response

class AccountRepository internal constructor(
    private val accountApi: AccountApi,
    private val socialDatabase: SocialDatabase,
) {

    suspend fun verifyUserCredentials(): Account {
        return accountApi.verifyCredentials().toExternalModel()
    }

    suspend fun getAccount(accountId: String): Account =
        coroutineScope {
            accountApi.getAccount(accountId).toExternalModel()
        }

    suspend fun getAccountRelationships(
        accountIds: List<String>,
    ): List<Relationship> = accountApi.getRelationships(accountIds.toTypedArray()).map { it.toExternal() }

    suspend fun getAccountFollowers(
        accountId: String,
        olderThanId: String? = null,
        newerThanId: String? = null,
        loadSize: Int? = null,
    ): FollowersPagingWrapper {
        val response = accountApi.getAccountFollowers(
            accountId = accountId,
            olderThanId = olderThanId,
            newerThanId = newerThanId,
            limit = loadSize,
        )
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        return FollowersPagingWrapper(
            accounts = response.body()?.map { it.toExternalModel() } ?: emptyList(),
            link = response.headers().get("link"),
        )
    }

    suspend fun getAccountFollowing(
        accountId: String,
        olderThanId: String? = null,
        newerThanId: String? = null,
        loadSize: Int? = null,
    ): FollowersPagingWrapper {
        val response = accountApi.getAccountFollowing(
            accountId = accountId,
            olderThanId = olderThanId,
            newerThanId = newerThanId,
            limit = loadSize,
        )
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        return FollowersPagingWrapper(
            accounts = response.body()?.map { it.toExternalModel() } ?: emptyList(),
            link = response.headers().get("link"),
        )
    }

    suspend fun getAccountStatuses(
        accountId: String,
        olderThanId: String? = null,
        immediatelyNewerThanId: String? = null,
        loadSize: Int? = null,
        onlyMedia: Boolean = false,
        excludeReplies: Boolean = false,
    ): List<Status> =
        accountApi.getAccountStatuses(
            accountId = accountId,
            olderThanId = olderThanId,
            immediatelyNewerThanId = immediatelyNewerThanId,
            limit = loadSize,
            onlyMedia = onlyMedia,
            excludeReplies = excludeReplies,
        ).map { it.toExternalModel() }

    suspend fun getAccountBookmarks(): List<Status> =
        accountApi.getAccountBookmarks().map { it.toExternalModel() }

    suspend fun getAccountFavourites(): List<Status> =
        accountApi.getAccountFavourites().map { it.toExternalModel() }

    suspend fun followAccount(accountId: String) {
        try {
            socialDatabase.relationshipsDao().updateFollowing(accountId, true)
            accountApi.followAccount(accountId)
        } catch (e: Exception) {
            socialDatabase.relationshipsDao().updateFollowing(accountId, false)
            throw e
        }
    }

    suspend fun unfollowAccount(accountId: String) {
        try {
            socialDatabase.relationshipsDao().updateFollowing(accountId, false)
            accountApi.unfollowAccount(accountId)
        } catch (e: Exception) {
            socialDatabase.relationshipsDao().updateFollowing(accountId, true)
            throw e
        }
    }

    /**
     * remove posts from any timelines before blocking
     */
    suspend fun blockAccount(accountId: String) {
        try {
            socialDatabase.homeTimelineDao().remotePostsFromAccount(accountId)
            socialDatabase.relationshipsDao().updateBlocked(accountId, true)
            accountApi.blockAccount(accountId)
        } catch (e: Exception) {
            socialDatabase.relationshipsDao().updateBlocked(accountId, false)
            throw e
        }
    }

    suspend fun unblockAccount(accountId: String) {
        try {
            socialDatabase.relationshipsDao().updateBlocked(accountId, false)
            accountApi.unblockAccount(accountId)
        } catch (e: Exception) {
            socialDatabase.relationshipsDao().updateBlocked(accountId, true)
            throw e
        }
    }

    /**
     * remove posts from any timelines before muting
     */
    suspend fun muteAccount(accountId: String) {
        try {
            socialDatabase.homeTimelineDao().remotePostsFromAccount(accountId)
            socialDatabase.relationshipsDao().updateMuted(accountId, true)
            accountApi.muteAccount(accountId)
        } catch (e: Exception) {
            socialDatabase.relationshipsDao().updateMuted(accountId, false)
            throw e
        }
    }

    suspend fun unmuteAccount(accountId: String) {
        try {
            socialDatabase.relationshipsDao().updateMuted(accountId, false)
            accountApi.unmuteAccount(accountId)
        } catch (e: Exception) {
            socialDatabase.relationshipsDao().updateMuted(accountId, true)
            throw e
        }
    }
}