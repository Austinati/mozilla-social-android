package org.mozilla.social.feature.auth.chooseServer

data class ChooseServerUiState(
    val serverText: String = "",
    val nextButtonEnabled: Boolean = false,
)