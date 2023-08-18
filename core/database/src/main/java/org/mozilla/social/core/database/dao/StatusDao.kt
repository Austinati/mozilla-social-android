package org.mozilla.social.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import org.mozilla.social.core.database.model.DatabaseStatus
import org.mozilla.social.core.database.model.wrappers.StatusWrapper

@Dao
interface StatusDao : BaseDao<DatabaseStatus> {

    @Transaction
    @Query(
        "SELECT * FROM statuses " +
        "WHERE statusId = :statusId"
    )
    suspend fun getStatus(statusId: String): StatusWrapper?

    @Query(
        "UPDATE statuses " +
        "SET isBoosted = :isBoosted " +
        "WHERE statusID = :statusId"
    )
    suspend fun updateBoosted(statusId: String, isBoosted: Boolean)

    @Query("DELETE FROM statuses")
    fun deleteAll()
}