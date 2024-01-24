package org.mozilla.social.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.mozilla.social.common.Resource
import org.mozilla.social.common.utils.edit
import org.mozilla.social.core.analytics.Analytics
import org.mozilla.social.core.analytics.AnalyticsIdentifiers
import org.mozilla.social.core.analytics.EngagementType
import org.mozilla.social.core.model.AccountTimelineType
import org.mozilla.social.core.navigation.NavigationDestination
import org.mozilla.social.core.navigation.usecases.NavigateTo
import org.mozilla.social.core.repository.mastodon.TimelineRepository
import org.mozilla.social.core.repository.paging.AccountTimelineRemoteMediator
import org.mozilla.social.core.ui.postcard.PostCardDelegate
import org.mozilla.social.core.ui.postcard.toPostCardUiState
import org.mozilla.social.core.usecase.mastodon.account.BlockAccount
import org.mozilla.social.core.usecase.mastodon.account.FollowAccount
import org.mozilla.social.core.usecase.mastodon.account.GetDetailedAccount
import org.mozilla.social.core.usecase.mastodon.account.GetLoggedInUserAccountId
import org.mozilla.social.core.usecase.mastodon.account.MuteAccount
import org.mozilla.social.core.usecase.mastodon.account.UnblockAccount
import org.mozilla.social.core.usecase.mastodon.account.UnfollowAccount
import org.mozilla.social.core.usecase.mastodon.account.UnmuteAccount
import timber.log.Timber

class AccountViewModel(
    private val analytics: Analytics,
    getLoggedInUserAccountId: GetLoggedInUserAccountId,
    timelineRepository: TimelineRepository,
    private val getDetailedAccount: GetDetailedAccount,
    private val navigateTo: NavigateTo,
    private val followAccount: FollowAccount,
    private val unfollowAccount: UnfollowAccount,
    private val blockAccount: BlockAccount,
    private val unblockAccount: UnblockAccount,
    private val muteAccount: MuteAccount,
    private val unmuteAccount: UnmuteAccount,
    initialAccountId: String?,
) : ViewModel(), AccountInteractions, KoinComponent {

    val postCardDelegate: PostCardDelegate by inject {
        parametersOf(
            viewModelScope,
            AnalyticsIdentifiers.FEED_PREFIX_PROFILE
        )
    }

    /**
     * The account ID of the logged in user
     */
    private val usersAccountId: String = getLoggedInUserAccountId()

    /**
     * if an account Id was passed in the constructor, the use that,
     * otherwise get the user's account Id
     */
    private val accountId: String = initialAccountId ?: usersAccountId

    /**
     * true if we are viewing the logged in user's profile
     */
    val isOwnProfile = usersAccountId == accountId

    val shouldShowCloseButton = initialAccountId != null

    private val _uiState = MutableStateFlow<Resource<AccountUiState>>(Resource.Loading())
    val uiState = _uiState.asStateFlow()

    private var getAccountJob: Job? = null

    private val postsRemoteMediator: AccountTimelineRemoteMediator by inject {
        parametersOf(
            accountId,
            AccountTimelineType.POSTS,
        )
    }

    private val postsAndRepliesRemoteMediator: AccountTimelineRemoteMediator by inject {
        parametersOf(
            accountId,
            AccountTimelineType.POSTS_AND_REPLIES,
        )
    }

    private val mediaRemoteMediator: AccountTimelineRemoteMediator by inject {
        parametersOf(
            accountId,
            AccountTimelineType.MEDIA,
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    val postsFeed = timelineRepository.getAccountTimelinePager(
        accountId = accountId,
        timelineType = AccountTimelineType.POSTS,
        remoteMediator = postsRemoteMediator,
    ).map { pagingData ->
        pagingData.map {
            it.toPostCardUiState(usersAccountId)
        }
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalPagingApi::class)
    val postsAndRepliesFeed = timelineRepository.getAccountTimelinePager(
        accountId = accountId,
        timelineType = AccountTimelineType.POSTS_AND_REPLIES,
        remoteMediator = postsAndRepliesRemoteMediator,
    ).map { pagingData ->
        pagingData.map {
            it.toPostCardUiState(usersAccountId)
        }
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalPagingApi::class)
    val mediaFeed = timelineRepository.getAccountTimelinePager(
        accountId = accountId,
        timelineType = AccountTimelineType.MEDIA,
        remoteMediator = mediaRemoteMediator,
    ).map { pagingData ->
        pagingData.map {
            it.toPostCardUiState(usersAccountId)
        }
    }.cachedIn(viewModelScope)

    private val _timeline = MutableStateFlow(
        Timeline(
            type = AccountTimelineType.POSTS,
            postsFeed = postsFeed,
            postsAndRepliesFeed = postsAndRepliesFeed,
            mediaFeed = mediaFeed,
        )
    )
    val timeline = _timeline.asStateFlow()

    init {
        loadAccount()
    }

    private fun loadAccount() {
        // ensure only one job happens at a time
        getAccountJob?.cancel()
        getAccountJob =
            viewModelScope.launch {
                getDetailedAccount(
                    accountId = accountId,
                    coroutineScope = viewModelScope,
                ) { account, relationship ->
                    account.toUiState(relationship)
                }.collect {
                    _uiState.edit { it }
                }
            }
    }

    override fun onScreenViewed() {
        analytics.uiImpression(
            mastodonAccountId = accountId,
            uiIdentifier = AnalyticsIdentifiers.ACCOUNTS_SCREEN_IMPRESSION,
        )
    }

    override fun onOverflowFavoritesClicked() {
        navigateTo(
            navDestination = NavigationDestination.Favorites
        )
    }

    override fun onOverflowShareClicked() {
        analytics.uiEngagement(
            uiIdentifier = AnalyticsIdentifiers.PROFILE_OVERFLOW_SHARE,
            mastodonAccountId = accountId,
            mastodonAccountHandle = usersAccountId
        )
    }

    override fun onOverflowMuteClicked() {
        analytics.uiEngagement(
            engagementType = EngagementType.GENERAL,
            uiIdentifier = AnalyticsIdentifiers.PROFILE_OVERFLOW_MUTE,
            mastodonAccountId = accountId,
            mastodonAccountHandle = usersAccountId,
        )
        viewModelScope.launch {
            try {
                muteAccount(accountId)
            } catch (e: MuteAccount.MuteFailedException) {
                Timber.e(e)
            }
        }
    }

    override fun onOverflowUnmuteClicked() {
        analytics.uiEngagement(
            engagementType = EngagementType.GENERAL,
            uiIdentifier = AnalyticsIdentifiers.PROFILE_OVERFLOW_UNMUTE,
            mastodonAccountId = accountId,
            mastodonAccountHandle = usersAccountId,
        )
        viewModelScope.launch {
            try {
                unmuteAccount(accountId)
            } catch (e: UnmuteAccount.UnmuteFailedException) {
                Timber.e(e)
            }
        }
    }

    override fun onOverflowBlockClicked() {
        analytics.uiEngagement(
            engagementType = EngagementType.GENERAL,
            uiIdentifier = AnalyticsIdentifiers.PROFILE_OVERFLOW_BLOCK,
            mastodonAccountId = accountId,
            mastodonAccountHandle = usersAccountId,
        )
        viewModelScope.launch {
            try {
                blockAccount(accountId)
            } catch (e: BlockAccount.BlockFailedException) {
                Timber.e(e)
            }
        }
    }

    override fun onOverflowUnblockClicked() {
        analytics.uiEngagement(
            engagementType = EngagementType.GENERAL,
            uiIdentifier = AnalyticsIdentifiers.PROFILE_OVERFLOW_UNBLOCK,
            mastodonAccountId = accountId,
            mastodonAccountHandle = usersAccountId,
        )
        viewModelScope.launch {
            try {
                unblockAccount(accountId)
            } catch (e: UnblockAccount.UnblockFailedException) {
                Timber.e(e)
            }
        }
    }

    override fun onOverflowReportClicked() {
        analytics.uiEngagement(
            engagementType = EngagementType.GENERAL,
            uiIdentifier = AnalyticsIdentifiers.PROFILE_OVERFLOW_REPORT,
            mastodonAccountId = accountId,
            mastodonAccountHandle = usersAccountId,
        )
        (uiState.value as? Resource.Loaded)?.data?.webFinger?.let { webFinger ->
            navigateTo(
                NavigationDestination.Report(
                    accountId,
                    webFinger,
                ),
            )
        }
    }

    override fun onFollowersClicked() {
        uiState.value.data?.displayName?.let { displayName ->
            navigateTo(
                NavigationDestination.Followers(
                    accountId = accountId,
                    displayName = displayName,
                    startingTab = NavigationDestination.Followers.StartingTab.FOLLOWERS,
                )
            )
        }
    }

    override fun onFollowingClicked() {
        uiState.value.data?.displayName?.let { displayName ->
            navigateTo(
                NavigationDestination.Followers(
                    accountId = accountId,
                    displayName = displayName,
                    startingTab = NavigationDestination.Followers.StartingTab.FOLLOWING,
                )
            )
        }
    }

    override fun onFollowClicked() {
        analytics.uiEngagement(
            uiIdentifier = AnalyticsIdentifiers.ACCOUNTS_SCREEN_FOLLOW,
            mastodonAccountId = accountId,
            mastodonAccountHandle = usersAccountId
        )
        viewModelScope.launch {
            try {
                followAccount(
                    accountId = accountId,
                    loggedInUserAccountId = usersAccountId,
                )
            } catch (e: FollowAccount.FollowFailedException) {
                Timber.e(e)
            }
        }
    }

    override fun onUnfollowClicked() {
        analytics.uiEngagement(
            uiIdentifier = AnalyticsIdentifiers.ACCOUNTS_SCREEN_UNFOLLOW,
            mastodonAccountId = accountId,
            mastodonAccountHandle = usersAccountId
        )
        viewModelScope.launch {
            try {
                unfollowAccount(
                    accountId = accountId,
                    loggedInUserAccountId = usersAccountId,
                )
            } catch (e: UnfollowAccount.UnfollowFailedException) {
                Timber.e(e)
            }
        }
    }

    override fun onRetryClicked() {
        loadAccount()
    }

    override fun onTabClicked(timelineType: AccountTimelineType) {
        _timeline.edit { copy(
            type = timelineType
        ) }
        when (timelineType) {
            AccountTimelineType.POSTS ->
                analytics.uiEngagement(uiIdentifier = AnalyticsIdentifiers.PROFILE_FEED_POSTS)
            AccountTimelineType.POSTS_AND_REPLIES ->
                analytics.uiEngagement(uiIdentifier = AnalyticsIdentifiers.PROFILE_FEED_POSTS_AND_REPLIES,)
            AccountTimelineType.MEDIA ->
                analytics.uiEngagement(uiIdentifier = AnalyticsIdentifiers.PROFILE_FEED_MEDIA,)
        }
    }

    override fun onSettingsClicked() {
        navigateTo(NavigationDestination.Settings)
    }

    override fun onEditAccountClicked() {
        analytics.uiEngagement(
            uiIdentifier = AnalyticsIdentifiers.PROFILE_EDIT_PROFILE,
            mastodonAccountId = accountId,
            mastodonAccountHandle = usersAccountId,
        )
        navigateTo(NavigationDestination.EditAccount)
    }
}
