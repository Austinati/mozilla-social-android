package org.mozilla.social.feature.account

interface AccountInteractions : OverflowInteractions {
    fun onFollowersClicked() = Unit
    fun onFollowingClicked() = Unit
    fun onFollowClicked() = Unit
    fun onUnfollowClicked() = Unit
}