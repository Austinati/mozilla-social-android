package org.mozilla.social.core.usecase.mastodon.account

import kotlinx.coroutines.test.TestScope
import org.mozilla.social.core.usecase.mastodon.BaseDomainTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class BlockAccountTest : BaseDomainTest() {

    private lateinit var subject: BlockAccount

    @BeforeTest
    fun setup() {
        subject = BlockAccount(
            externalScope = TestScope(testDispatcher),
            showSnackbar = showSnackbar,
            accountRepository = accountApi,
            socialDatabase = socialDatabase,
            dispatcherIo = testDispatcher,
        )
    }

    @Test
    fun testCancelledScope() {
        val accountId = "id1"
        testOuterScopeCancelled(
            delayedCallBlock = {
                relationshipsDao.updateBlocked(any(), any())
            },
            subjectCallBlock = {
                subject(accountId)
            },
            verifyBlock = {
                accountApi.blockAccount(accountId)
            }
        )
    }

    @Test
    fun testCancelledScopeWithError() {
        testOuterScopeCancelledAndInnerException(
            delayedCallBlock = {
                relationshipsDao.updateBlocked(any(), true)
            },
            subjectCallBlock = {
                subject("id")
            },
            verifyBlock = {
                showSnackbar(any(), any())
            },
        )
    }
}