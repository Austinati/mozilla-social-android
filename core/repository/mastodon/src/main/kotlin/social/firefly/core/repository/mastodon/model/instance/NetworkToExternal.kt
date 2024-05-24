package social.firefly.core.repository.mastodon.model.instance

import social.firefly.core.model.Instance
import social.firefly.core.model.InstanceRule
import social.firefly.core.model.InstanceStats
import social.firefly.core.model.InstanceUrls
import social.firefly.core.network.mastodon.model.responseBody.NetworkInstance
import social.firefly.core.network.mastodon.model.responseBody.NetworkInstanceRule
import social.firefly.core.network.mastodon.model.responseBody.NetworkInstanceStats
import social.firefly.core.network.mastodon.model.responseBody.NetworkInstanceUrls
import social.firefly.core.repository.mastodon.model.status.toExternalModel

fun social.firefly.core.network.mastodon.model.responseBody.NetworkInstanceRule.toExternalModel(): InstanceRule =
    InstanceRule(
        id = id,
        text = text,
    )

fun List<social.firefly.core.network.mastodon.model.responseBody.NetworkInstanceRule>.toExternalModel(): List<InstanceRule> = map { it.toExternalModel() }

fun social.firefly.core.network.mastodon.model.responseBody.NetworkInstance.toExternalModel(): Instance =
    Instance(
        uri = domain,
        title = title,
        description = description,
        version = version,
        languages = languages,
        thumbnail = thumbnail.url,
        contactAccount = contact?.account?.toExternalModel(),
        contactEmail = contact?.email,
        rules = rules.toExternalModel(),
    )

fun social.firefly.core.network.mastodon.model.responseBody.NetworkInstanceUrls.toExternalModel(): InstanceUrls =
    InstanceUrls(streamingApiUrl = streamingApiUrl)

fun social.firefly.core.network.mastodon.model.responseBody.NetworkInstanceStats.toExternalModel() =
    InstanceStats(
        userCount = userCount,
        statusCount = statusCount,
        domainCount = domainCount,
    )
