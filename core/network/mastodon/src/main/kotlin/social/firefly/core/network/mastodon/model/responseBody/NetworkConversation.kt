package social.firefly.core.network.mastodon.model.responseBody

/**
 * Represents a conversation with "direct message" visibility.
 */
data class NetworkConversation(
    val conversationId: String,
    /**
     * Participants in the conversation.
     */
    val participants: List<social.firefly.core.network.mastodon.model.responseBody.NetworkAccount>,
    /**
     * Is the conversation currently marked as unread?
     */
    val isUnread: Boolean,
    /**
     * The last status in the conversation, optionally used for display.
     */
    val lastStatus: social.firefly.core.network.mastodon.model.responseBody.NetworkStatus? = null,
)