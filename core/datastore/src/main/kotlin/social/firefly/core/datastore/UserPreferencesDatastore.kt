package social.firefly.core.datastore

import android.content.Context
import androidx.annotation.GuardedBy
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import social.firefly.core.datastore.UserPreferences.ThreadType
import timber.log.Timber
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesDatastore internal constructor(
    val fileName: String,
    serializer: Serializer<UserPreferences>,
    context: Context,
) {
    private val Context.dataStore: DataStore<UserPreferences> by dataStore(
        fileName = fileName,
        serializer = serializer
    )

    private val dataStore = context.dataStore

    private suspend fun preloadData() {
        try {
            val data = dataStore.data.first()
            println(data)
        } catch (ioException: IOException) {
            Timber.e(t = ioException, message = "Problem preloading data")
        }
    }

    init {
        GlobalScope.launch {
            preloadData()
        }
    }

    val isSignedIn: Flow<Boolean> =
        dataStore.data.mapLatest {
            !it.accountId.isNullOrBlank() && !it.accessToken.isNullOrBlank()
        }.distinctUntilChanged()

    suspend fun clearData() {
        dataStore.updateData {
            it.toBuilder()
                .clear()
                .build()
        }
    }

    val accessToken: Flow<String?> =
        dataStore.data.mapLatest {
            it.accessToken
        }

    val domain: Flow<String> =
        dataStore.data.mapLatest {
            it.domain
        }

    val accountId: Flow<String> =
        dataStore.data.mapLatest {
            it.accountId
        }

    val serializedPushKeys: Flow<String> =
        dataStore.data.mapLatest {
            it.serializedPushKeys
        }.distinctUntilChanged()

    suspend fun saveSerializedPushKeyPair(serializedPushKeyPair: String) {
        dataStore.updateData {
            it.toBuilder()
                .setSerializedPushKeys(serializedPushKeyPair)
                .build()
        }
    }

    val lastSeenHomeStatusId: Flow<String> =
        dataStore.data.mapLatest {
            it.lastSeenHomeStatusId
        }.distinctUntilChanged()

    suspend fun saveLastSeenHomeStatusId(statusId: String) {
        dataStore.updateData {
            it.toBuilder()
                .setLastSeenHomeStatusId(statusId)
                .build()
        }
    }

    val threadType: Flow<ThreadType> =
        dataStore.data.mapLatest {
            it.threadType
        }.distinctUntilChanged()

    suspend fun saveThreadType(threadType: ThreadType) {
        dataStore.updateData {
            it.toBuilder()
                .setThreadType(threadType)
                .build()
        }
    }

    companion object {
        @Suppress("MaxLineLength")
        const val HOST_NAME_REGEX = "[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)+"
    }
}