package org.mozilla.social.core.domain.report

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.mozilla.social.common.utils.StringFactory
import org.mozilla.social.core.domain.R
import org.mozilla.social.core.navigation.usecases.ShowSnackbar
import org.mozilla.social.core.network.ReportApi
import org.mozilla.social.core.network.model.request.NetworkReportCreate

class Report(
    private val externalScope: CoroutineScope,
    private val showSnackbar: ShowSnackbar,
    private val reportApi: ReportApi,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO,
) {

    /**
     * @throws ReportFailedException if any error occurred
     */
    suspend operator fun invoke(
        accountId: String,
        statusIds: List<String>? = null,
        comment: String? = null,
        forward: Boolean? = null,
        category: String? = null,
        ruleViolations: List<Int>? = null,
    ) = externalScope.async(dispatcherIo) {
        try {
            reportApi.report(
                body = NetworkReportCreate(
                    accountId = accountId,
                    statusIds = statusIds,
                    comment = comment,
                    forward = forward,
                    category = category,
                    ruleViolations = ruleViolations
                )
            )
        } catch (e: Exception) {
            showSnackbar(
                text = StringFactory.resource(R.string.error_sending_report_toast),
                isError = true,
            )
            throw ReportFailedException(e)
        }
    }.await()

    class ReportFailedException(e: Exception) : Exception(e)
}