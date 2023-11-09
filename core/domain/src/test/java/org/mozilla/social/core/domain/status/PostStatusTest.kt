package org.mozilla.social.core.domain.status

import io.mockk.mockk
import kotlinx.coroutines.test.TestScope
import kotlinx.datetime.Instant
import org.mozilla.social.core.data.repository.StatusRepository
import org.mozilla.social.core.data.repository.TimelineRepository
import org.mozilla.social.core.domain.BaseDomainTest
import org.mozilla.social.core.domain.utils.TestModels
import org.mozilla.social.core.network.model.NetworkAccount
import org.mozilla.social.core.network.model.NetworkStatus
import org.mozilla.social.core.network.model.NetworkStatusVisibility
import org.mozilla.social.model.StatusVisibility
import kotlin.test.BeforeTest
import kotlin.test.Test

class PostStatusTest : BaseDomainTest() {

    private val statusRepository = mockk<StatusRepository>(relaxed = true)
    private val timelineRepository = mockk<TimelineRepository>(relaxed = true)

    private lateinit var subject: PostStatus

    private val networkStatus = TestModels.networkStatus

    @BeforeTest
    fun setup() {
        subject = PostStatus(
            externalScope = TestScope(testDispatcher),
            showSnackbar = showSnackbar,
            statusApi = statusApi,
            mediaApi = mediaApi,
            statusRepository = statusRepository,
            timelineRepository = timelineRepository,
            dispatcherIo = testDispatcher,
        )
    }

    @Test
    fun testCancelledScope() {
        testOuterScopeCancelled(
            delayedCallBlock = {
                statusApi.postStatus(any())
            },
            delayedCallBlockReturnValue = networkStatus,
            subjectCallBlock = {
                subject(
                    statusText = "test",
                    imageStates = emptyList(),
                    visibility = StatusVisibility.Public,
                    pollCreate = null,
                    contentWarningText = null,
                    inReplyToId = null,
                )
            },
            verifyBlock = {
                statusRepository.saveStatusToDatabase(any())
            }
        )
    }

    @Test
    fun testCancelledScopeWithError() {
        testOuterScopeCancelledAndInnerException(
            delayedCallBlock = {
                statusApi.postStatus(any())
            },
            subjectCallBlock = {
                subject(
                    statusText = "test",
                    imageStates = emptyList(),
                    visibility = StatusVisibility.Public,
                    pollCreate = null,
                    contentWarningText = null,
                    inReplyToId = null,
                )
            },
            verifyBlock = {
                showSnackbar(any(), any())
            }
        )
    }
}