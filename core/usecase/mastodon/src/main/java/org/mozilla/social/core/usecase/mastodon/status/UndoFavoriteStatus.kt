package org.mozilla.social.core.usecase.mastodon.status

import androidx.room.withTransaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.mozilla.social.common.utils.StringFactory
import org.mozilla.social.core.database.SocialDatabase
import org.mozilla.social.core.navigation.usecases.ShowSnackbar
import org.mozilla.social.core.repository.mastodon.StatusRepository
import org.mozilla.social.core.usecase.mastodon.R

class UndoFavoriteStatus internal constructor(
    private val externalScope: CoroutineScope,
    private val socialDatabase: SocialDatabase,
    private val statusRepository: StatusRepository,
    private val saveStatusToDatabase: SaveStatusToDatabase,
    private val showSnackbar: ShowSnackbar,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(
        statusId: String,
    ) = externalScope.async(dispatcherIo) {
        try {
            socialDatabase.withTransaction {
                socialDatabase.statusDao().updateFavoriteCount(statusId, -1)
                socialDatabase.statusDao().updateFavorited(statusId, false)
            }
            val status = statusRepository.unFavoriteStatus(statusId)
            saveStatusToDatabase(status)
        } catch (e: Exception) {
            socialDatabase.withTransaction {
                socialDatabase.statusDao().updateFavoriteCount(statusId, 1)
                socialDatabase.statusDao().updateFavorited(statusId, true)
            }
            showSnackbar(
                text = StringFactory.resource(R.string.error_removing_favorite),
                isError = true,
            )
            throw UndoFavoriteStatusFailedException(e)
        }
    }.await()

    class UndoFavoriteStatusFailedException(e: Exception) : Exception(e)
}