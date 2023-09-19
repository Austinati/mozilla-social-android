package org.mozilla.social.feature.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mozilla.social.core.domain.IsSignedInFlow
import org.mozilla.social.core.domain.Login

class AuthViewModel(
    private val login: Login,
    isSignedInFlow: IsSignedInFlow,
) : ViewModel() {

    val isSignedIn: Flow<Boolean> = isSignedInFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        initialValue = false,
    )

    fun onLoginClicked(context: Context, domain: String) {
        viewModelScope.launch {
            login(context, domain)
        }
    }
}