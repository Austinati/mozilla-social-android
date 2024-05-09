package social.firefly.feature.post.bottombar

import social.firefly.feature.post.NewPostViewModel

data class BottomBarState(
    val imageButtonEnabled: Boolean = false,
    val videoButtonEnabled: Boolean = false,
    val pollButtonEnabled: Boolean = false,
    val contentWarningText: String? = null,
    val characterCountText: String = "",
    val maxImages: Int = NewPostViewModel.MAX_IMAGES,
)