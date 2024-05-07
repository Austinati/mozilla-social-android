package social.firefly.core.usecase.mastodon.auth

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import social.firefly.common.annotations.PreferUseCase
import social.firefly.core.datastore.UserPreferencesDatastoreManager
import social.firefly.core.model.Account
import social.firefly.core.navigation.NavigationDestination
import social.firefly.core.navigation.usecases.NavigateTo
import social.firefly.core.navigation.usecases.OpenLink
import social.firefly.core.repository.mastodon.VerificationRepository
import timber.log.Timber

/**
 * This use case handles all logic related to logging in
 */
class Login(
    private val userPreferencesDatastoreManager: UserPreferencesDatastoreManager,
    private val openLink: OpenLink,
    private val navigateTo: NavigateTo,
): KoinComponent {
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var host: String

    private lateinit var verificationRepository: VerificationRepository
    /**
     * When a logging in by registering this client with the given domain
     */
    @OptIn(PreferUseCase::class)
    suspend operator fun invoke(url: String) {
        try {
            host = extractHost(url)
            verificationRepository = getKoin().get<VerificationRepository> {
                parametersOf("https://$host")
            }
            val application = verificationRepository.createApplication(
                clientName = CLIENT_NAME,
                redirectUris = REDIRECT_URI,
                scopes = SCOPES,
                baseUrl = host,
            )
            clientId = application.clientId!!
            clientSecret = application.clientSecret!!
            openLink(
                HttpUrl.Builder()
                    .scheme(HTTPS)
                    .host(host)
                    .addPathSegments(OAUTH_AUTHORIZE)
                    .addQueryParameter(RESPONSE_TYPE_QUERY_PARAM, CODE)
                    .addQueryParameter(REDIRECT_URI_QUERY_PARAM, AUTH_SCHEME)
                    .addQueryParameter(SCOPE_QUERY_PARAM, SCOPES)
                    .addQueryParameter(CLIENT_ID_QUERY_PARAM, application.clientId!!)
                    .build()
                    .toString(),
            )
        } catch (exception: Exception) {
            throw LoginFailedException(exception)
        }
    }

    @OptIn(PreferUseCase::class)
    suspend fun onUserCodeReceived(code: String) {
        try {
            Timber.tag(TAG).d("user code received")
            val accessToken = verificationRepository.fetchOAuthToken(
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUri = REDIRECT_URI,
                code = code,
                grantType = AUTHORIZATION_CODE,
                baseUrl = host,
            )

            Timber.tag(TAG).d("access token received")

            val account: Account = verificationRepository.verifyUserCredentials(
                accessToken = accessToken,
                baseUrl = host,
            )
            userPreferencesDatastoreManager.createNewUserDatastore(
                domain = host,
                accessToken = accessToken,
                accountId = account.accountId,
                userName = account.displayName,
                avatarUrl = account.avatarUrl,
            )
            navigateTo(NavigationDestination.Tabs)
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    }

    private fun extractHost(domain: String): String {
        return buildString {
            append("https://")
            append(
                domain
                    .substringAfter("http://")
                    .substringAfter("https://")
            )
        }.toHttpUrl().host
    }

    companion object {
        const val CLIENT_NAME = "Mozilla Social Android"
        const val REDIRECT_URI = "mozsoc://auth"
        const val SCOPES = "read write push"
        const val AUTHORIZATION_CODE = "authorization_code"
        const val HTTPS = "https"
        const val OAUTH_AUTHORIZE = "oauth/authorize"
        const val AUTH_SCHEME = "mozsoc://auth"
        const val RESPONSE_TYPE_QUERY_PARAM = "response_type"
        const val CODE = "code"
        const val REDIRECT_URI_QUERY_PARAM = "redirect_uri"
        const val CLIENT_ID_QUERY_PARAM = "client_id"
        const val SCOPE_QUERY_PARAM = "scope"
        const val TAG = "Login"
    }

    class LoginFailedException(e: Exception) : Exception(e)
}
