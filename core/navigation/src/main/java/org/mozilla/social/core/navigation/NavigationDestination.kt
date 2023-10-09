package org.mozilla.social.core.navigation

sealed class NavigationDestination(
    val route: String,
) {
    data object MyAccount: NavigationDestination(
        route = "myAccount"
    )

    data object Account: NavigationDestination(
        route = "account"
    ) {
        const val NAV_PARAM_ACCOUNT_ID = "accountId"
        val fullRoute = "$route?$NAV_PARAM_ACCOUNT_ID={$NAV_PARAM_ACCOUNT_ID}"

        fun route(accountId: String): String = "$route?$NAV_PARAM_ACCOUNT_ID=$accountId"
    }

    data object Auth: NavigationDestination(
        route = "auth"
    )

    data object Feed: NavigationDestination(
        route = "feed"
    )

    data object Followers: NavigationDestination(
        route = "followers"
    ) {
        const val NAV_PARAM_ACCOUNT_ID = "accountId"
        val fullRoute = "$route?$NAV_PARAM_ACCOUNT_ID={$NAV_PARAM_ACCOUNT_ID}"

        fun route(accountId: String): String = "$route?${NAV_PARAM_ACCOUNT_ID}=$accountId"
    }

    data object Following: NavigationDestination(
        route = "following"
    ) {
        const val NAV_PARAM_ACCOUNT_ID = "accountId"
        val fullRoute = "$route?$NAV_PARAM_ACCOUNT_ID={$NAV_PARAM_ACCOUNT_ID}"

        fun route(accountId: String): String = "$route?${NAV_PARAM_ACCOUNT_ID}=$accountId"
    }

    data object HashTag: NavigationDestination(
        route = "hashTag"
    ) {
        const val NAV_PARAM_HASH_TAG = "hashTagValue"
        val fullRoute = "$route?$NAV_PARAM_HASH_TAG={$NAV_PARAM_HASH_TAG}"

        fun route(hashTagValue: String) = "$route?$NAV_PARAM_HASH_TAG=$hashTagValue"
    }

    data object NewPost: NavigationDestination(
        route = "newPost"
    ) {
        const val NAV_PARAM_REPLY_STATUS_ID = "replyStatusId"
        val fullRoute = "$route?$NAV_PARAM_REPLY_STATUS_ID={$NAV_PARAM_REPLY_STATUS_ID}"

        fun route(replyStatusId: String?): String =
            when {
                replyStatusId != null -> "$route?$NAV_PARAM_REPLY_STATUS_ID=$replyStatusId"
                else -> route
            }
    }

    data object Report: NavigationDestination(
        route = "report"
    ) {
        const val NAV_PARAM_REPORT_STATUS_ID = "reportStatusId"
        const val NAV_PARAM_REPORT_ACCOUNT_ID = "reportAccountId"
        val fullRoute = "$route?" +
                "$NAV_PARAM_REPORT_STATUS_ID={$NAV_PARAM_REPORT_STATUS_ID}" +
                "&$NAV_PARAM_REPORT_ACCOUNT_ID={$NAV_PARAM_REPORT_ACCOUNT_ID}"

        fun route(
            reportAccountId: String,
            reportStatusId: String? = null,
        ): String =
            when {
                reportStatusId != null -> "$route?" +
                        "$NAV_PARAM_REPORT_STATUS_ID=$reportStatusId" +
                        "&$NAV_PARAM_REPORT_ACCOUNT_ID=$reportAccountId"
                else -> "$route?$NAV_PARAM_REPORT_ACCOUNT_ID=$reportAccountId"
            }
    }

    data object Search: NavigationDestination(
        route = "search"
    )

    data object Settings: NavigationDestination(
        route = "settings"
    )

    data object Thread: NavigationDestination(
        route = "thread"
    ) {
        const val NAV_PARAM_STATUS_ID = "statusId"
        val fullRoute = "$route?$NAV_PARAM_STATUS_ID={$NAV_PARAM_STATUS_ID}"

        fun route(statusId: String) = "$route?$NAV_PARAM_STATUS_ID=$statusId"
    }
}



