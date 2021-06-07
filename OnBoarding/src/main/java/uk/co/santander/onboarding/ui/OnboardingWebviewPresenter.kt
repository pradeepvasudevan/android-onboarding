package uk.co.santander.onboarding.ui

import android.app.Activity
import android.net.http.SslError
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import uk.co.santander.ddv.Ddv
import uk.co.santander.ddv.common.utils.otherconfigs.ConsumerData
import uk.co.santander.onboarding.Onboarding
import uk.co.santander.onboarding.R
import uk.co.santander.onboarding.base.SanBasePresenter

class OnboardingWebviewPresenter(
    override val view: OnboardingWebviewView,
    private val webViewUrl: String?
) : SanBasePresenter() {
    private val tag: String = this@OnboardingWebviewPresenter.javaClass.simpleName
    private var webClientErrorCode = NO_ERROR
    override fun start() {
        webViewUrl?.let {
            view.showUrl(it)
        }
    }

    fun onPageLoadStarted(webView: WebView, url: String) {
        webClientErrorCode = NO_ERROR
        view.startProgress()
        view.hideWebContent()
    }

    fun onLoadResource(title: String?) {

    }

    fun onPageLoadFinished(webView: WebView, url: String) {
        view.hideProgress()
        if (webClientErrorCode != NO_ERROR) {
            view.hideWebContent()
            handlePageLoadError()
        } else {
            view.showWebContent()
        }
    }

    private fun handlePageLoadError() {

        view.showAlertDiaog(ID_PAGE_LOAD_ERROR,
            context.getString(R.string.onboarding_lib_we_re_sorry),
            context.getString(R.string.onboarding_lib_std_error_message),
            listOf(AlertButton(AlertButton.ACTION.OK,
                    AlertButton.TYPE.POSITIVE,
                    context.getString(R.string.onboarding_lib_button_ok)),
                    AlertButton(AlertButton.ACTION.RETRY,
                    AlertButton.TYPE.NEGATIVE,
                    context.getString(R.string.onboarding_lib_button_retry))))
    }

    fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
        return false
    }

    fun onPageLoadError(errorCode: Int) {
        view.hideProgress()
        webClientErrorCode = errorCode
    }

    fun useExistingWebViewWindow(webView: WebView, url: String) {

    }

    fun handleExternalLink(url: String, mimeType: String) {

    }

    fun onSslError(error: SslError) {

    }

    @JavascriptInterface
    fun postMessage(sessionToken: String) {
        Log.i(tag, "message from web: $sessionToken")
        if (Onboarding.clientId == null || Onboarding.clientSecret == null) {
            Log.i(tag, "No Client id/client secret is set")
        } else {
            Log.i(tag, "Client id ${Onboarding.clientId}, Client secret ${Onboarding.clientSecret}")
            val conf = ConsumerData.Config(
                clientId = Onboarding.clientId,
                clientSecret = Onboarding.clientSecret
            )
            var head: ConsumerData.Headers? = null
            Onboarding.dynatraceAppId?.let {
                head = ConsumerData.Headers(
                    Onboarding.sourceSystemId,
                    Onboarding.clientId,
                    Onboarding.dynatraceAppId,
                    Onboarding.dynatraceBeaconUrl,
                    Onboarding.dynatraceUserOptIn
                )
            }

            Ddv.start(
                view as Activity,
                headers = head,
                sessionTokenId = sessionToken,
                config = conf
            )
        }

    }

    fun onUserAlertAction(id: Int, action: AlertButton.ACTION) {
        if (id == ID_PAGE_LOAD_ERROR) {
            if (action == AlertButton.ACTION.RETRY) {
                webViewUrl?.let {
                    view.showUrl(it)
                }
            }
        }
    }

    companion object {
        private const val NO_ERROR = 100
        private const val ID_PAGE_LOAD_ERROR = -100
    }
}