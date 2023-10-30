package org.mozilla.social.core.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.mozilla.social.common.utils.StringFactory

class EventRelay {
    private val _navigationEvents = MutableSharedFlow<Event>(replay = 1)
    val navigationEvents: SharedFlow<Event>
        get() = _navigationEvents

    fun emitEvent(navDestination: NavDestination) {
        emitEvent(Event.NavigateToDestination(navDestination))
    }

    fun emitEvent(event: Event) {
        println("navrelay emit $event")
        _navigationEvents.tryEmit(event)
    }
}

sealed class Event {
    data object PopBackStack : Event()

    data class OpenLink(val url: String) : Event()
    data class ShowSnackbar(val text: StringFactory, val isError: Boolean) : Event()

    data class NavigateToDestination(val destination: NavDestination) :
        Event()
}
