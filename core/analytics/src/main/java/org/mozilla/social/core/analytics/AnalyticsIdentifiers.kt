package org.mozilla.social.core.analytics

object AnalyticsIdentifiers {
    const val DISCOVER_RECOMMENDATION_IMPRESSION = "discover.recommendation.impression"
    const val DISCOVER_RECOMMENDATION_OPEN = "discover.recommendation.open"
    const val DISCOVER_SCREEN_IMPRESSION = "discover.screen.impression"

    const val ACCOUNTS_SCREEN_IMPRESSION = "account.screen.impression"
    const val ACCOUNTS_SCREEN_FOLLOW = "profile.follow_btn.follow"
    const val ACCOUNTS_SCREEN_UNFOLLOW = "profile.follow_btn.unfollow"
    const val PROFILE_MORE_SHARE_ACCOUNT = "profile.more.share-account"
    const val PROFILE_EDIT_PROFILE = "profile.edit.profile"

    const val SETTINGS_SCREEN_IMPRESSION = "settings.screen.impression"
    const val SETTINGS_ACCOUNT_IMPRESSION = "settings.account.impression"
    const val SETTINGS_CONTENT_PREFERENCES_IMPRESSION = "settings.content-preferences.impression"
    const val MUTED_USERS_SCREEN_IMPRESSION = "muted.users.screen.impression"
    const val BLOCKED_USERS_SCREEN_IMPRESSION = "blocked.users.screen.impression"
    const val SETTINGS_PRIVACY_IMPRESSION = "settings.privacy.impression"
    const val SETTINGS_ABOUT_IMPRESSION = "settings.about.impression"

    const val NEW_POST_SCREEN_IMPRESSION = "new.post.screen.impression"

    const val FEED_SCREEN_IMPRESSION = "feed.screen.impression"
    const val FEED_POST_VOTE = "feed.post.vote"
    const val FEED_POST_REPLY = "feed.post.open-reply"
    const val FEED_POST_BOOST = "feed.post.reblog"
    const val FEED_POST_UNBOOST = "feed.post.unreblog"
    const val FEED_POST_FAVORITE = "feed.post.favourite"
    const val FEED_POST_UNFAVORITE = "feed.post.unfavourite"
    const val FEED_POST_LINK_TAPPED = "post.link.tap"

    const val FEED_PREFIX_PROFILE = "profile"
    const val FEED_PREFIX_HOME = "home"
    const val FEED_PREFIX_LOCAL = "local"
    const val FEED_PREFIX_FEDERATED = "federated"
    const val FEED_PREFIX_HASHTAG = "hashtag"
    const val FEED_PREFIX_THREAD = "thread"

    const val REPORT_SCREEN_IMPRESSION = "report.screen.impression"

    const val THREAD_SCREEN_IMPRESSION = "thread.screen.impression"

    const val FOLLOWERS_SCREEN_IMPRESSION = "followers.screen.impression"

    const val HASHTAG_SCREEN_IMPRESSION = "hashtag.screen.impression"

    const val AUTH_SCREEN_IMPRESSION = "auth.screen.impression"
    const val AUTH_SCREEN_SIGN_IN_SIGN_UP = "auth.screen.sign-in-sign-up"
    const val AUTH_SCREEN_CHOOSE_A_SERVER = "auth.screen.choose-a-server"

    const val CHOOSE_A_SERVER_SCREEN_IMPRESSION = "choose.a.server.screen.impression"
    const val CHOOSE_A_SERVER_SCREEN_SUBMIT_SERVER = "choose.a.server.screen.submit-server"
}
