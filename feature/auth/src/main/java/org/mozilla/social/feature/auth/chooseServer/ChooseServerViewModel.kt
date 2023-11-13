package org.mozilla.social.feature.auth.chooseServer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mozilla.social.common.utils.edit
import org.mozilla.social.core.usecase.mastodon.auth.Login
import timber.log.Timber

class ChooseServerViewModel(
    private val login: Login,
) : ViewModel(), ChooseServerInteractions {

    private val _uiState = MutableStateFlow(ChooseServerUiState())
    val uiState = _uiState.asStateFlow()

    override fun onServerTextChanged(text: String) {
        val isUrl = URL_REGEX.toRegex().matches(text)
        _uiState.edit { copy(
            serverText = text,
            nextButtonEnabled = isUrl,
        ) }
    }

    //TODO remove context
    override fun onNextClicked(context: Context) {
        _uiState.edit { copy(
            isLoading = true,
            loginFailed = false,
        ) }
        viewModelScope.launch {
            try {
                login(
                    context,
                    uiState.value.serverText
                )
            } catch (e: Login.LoginFailedException) {
                _uiState.edit { copy(
                    loginFailed = true,
                ) }
                Timber.e(e)
            } finally {
                _uiState.edit { copy(
                    isLoading = false,
                ) }
            }
        }
    }

    companion object {
        @Suppress("MaxLineLength")
        const val URL_REGEX = "(https://www\\.|http://www\\.|https://|http://)?[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)(\\.[a-zA-Z0-9]+)?"
    }
}