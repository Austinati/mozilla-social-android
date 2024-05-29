package social.firefly.core.repository.paging

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import social.firefly.core.datastore.dataStoreModule
import social.firefly.core.repository.mastodon.mastodonRepositoryModule
import social.firefly.core.repository.paging.pagers.status.AccountTimelinePager
import social.firefly.core.repository.paging.pagers.accounts.BlocksPager
import social.firefly.core.repository.paging.pagers.accounts.FollowersPager
import social.firefly.core.repository.paging.pagers.accounts.FollowingsPager
import social.firefly.core.repository.paging.pagers.status.BookmarksPager
import social.firefly.core.repository.paging.pagers.status.FavoritesPager
import social.firefly.core.repository.paging.pagers.status.FederatedTimelinePager
import social.firefly.core.repository.paging.pagers.hashTags.FollowedHashTagsPager
import social.firefly.core.repository.paging.pagers.accounts.MutesPager
import social.firefly.core.repository.paging.pagers.hashTags.TrendingHashTagPager
import social.firefly.core.repository.paging.pagers.status.LocalTimelinePager
import social.firefly.core.repository.paging.pagers.status.TrendingStatusPager
import social.firefly.core.repository.paging.remotemediators.notifications.AllNotificationsRemoteMediator
import social.firefly.core.repository.paging.remotemediators.notifications.FollowNotificationsRemoteMediator
import social.firefly.core.repository.paging.remotemediators.notifications.MentionNotificationsRemoteMediator
import social.firefly.core.repository.paging.remotemediators.HashTagTimelineRemoteMediator
import social.firefly.core.repository.paging.remotemediators.HomeTimelineRemoteMediator
import social.firefly.core.repository.paging.remotemediators.SearchAccountsRemoteMediator
import social.firefly.core.repository.paging.remotemediators.SearchStatusesRemoteMediator
import social.firefly.core.repository.paging.remotemediators.SearchedHashTagsRemoteMediator
import social.firefly.core.repository.paging.sources.DomainBlocksPagingSource
import social.firefly.core.usecase.mastodon.mastodonUsecaseModule

val pagingModule = module {
    includes(
        mastodonRepositoryModule,
        mastodonUsecaseModule,
        dataStoreModule,
    )

    factoryOf(::HomeTimelineRemoteMediator)
    factoryOf(::AllNotificationsRemoteMediator)
    factoryOf(::MentionNotificationsRemoteMediator)
    factoryOf(::FollowNotificationsRemoteMediator)

    factory { parametersHolder ->
        HashTagTimelineRemoteMediator(
            get(),
            get(),
            get(),
            get(),
            parametersHolder[0],
        )
    }

    factory { parametersHolder ->
        SearchAccountsRemoteMediator(
            searchRepository = get(),
            databaseDelegate = get(),
            accountRepository = get(),
            relationshipRepository = get(),
            query = parametersHolder[0],
        )
    }

    factory { parametersHolder ->
        SearchStatusesRemoteMediator(
            searchRepository = get(),
            databaseDelegate = get(),
            getInReplyToAccountNames = get(),
            saveStatusToDatabase = get(),
            query = parametersHolder[0],
        )
    }

    factory { parametersHolder ->
        SearchedHashTagsRemoteMediator(
            databaseDelegate = get(),
            searchRepository = get(),
            hashtagRepository = get(),
            query = parametersHolder[0],
        )
    }

    factoryOf(::BlocksPager)
    factoryOf(::BookmarksPager)
    factoryOf(::DomainBlocksPagingSource)
    factoryOf(::FavoritesPager)
    factoryOf(::FederatedTimelinePager)
    factoryOf(::FollowedHashTagsPager)
    factoryOf(::LocalTimelinePager)
    factoryOf(::MutesPager)
    factoryOf(::TrendingStatusPager)
    factoryOf(::TrendingHashTagPager)

    factory { parametersHolder ->
        AccountTimelinePager(
            accountRepository = get(),
            saveStatusToDatabase = get(),
            databaseDelegate = get(),
            timelineRepository = get(),
            getInReplyToAccountNames = get(),
            accountId = parametersHolder[0],
            timelineType = parametersHolder[1],
        )
    }

    factory { parametersHolder ->
        FollowersPager(
            accountRepository = get(),
            databaseDelegate = get(),
            followersRepository = get(),
            relationshipRepository = get(),
            accountId = parametersHolder[0],
        )
    }

    factory { parametersHolder ->
        FollowingsPager(
            accountRepository = get(),
            databaseDelegate = get(),
            followingsRepository = get(),
            relationshipRepository = get(),
            accountId = parametersHolder[0],
        )
    }
}
