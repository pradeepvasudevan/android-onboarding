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

    override fun create() {
        super.create()
        checkNfcLoadUrl()
    }
    override fun start() {
        //checkNfcLoadUrl()
    }

    private fun checkNfcLoadUrl() {
        if (Onboarding.checkNfc) {
            if (Onboarding.nfcAvailable && !Onboarding.nfcEnabled) {
                displayEnableNfcPrompt()
            } else {
                loadUrl()
            }
        } else {
            loadUrl()
        }
    }

    private fun displayEnableNfcPrompt() {
        view.showAlertDiaog(
            ID_NFC_ENABLE_PROMPT,
            context.getString(R.string.onboarding_lib_enable_nfc),
            context.getString(R.string.onboarding_lib_enable_nfc_message),
            listOf(
                AlertButton(
                    AlertButton.ACTION.OK,
                    AlertButton.TYPE.POSITIVE,
                    context.getString(R.string.onboarding_lib_button_ok)
                ),
                AlertButton(
                    AlertButton.ACTION.CANCEL,
                    AlertButton.TYPE.NEGATIVE,
                    context.getString(R.string.onboarding_lib_button_cancel)
                )
            )
        )
    }

    private fun loadUrl() {
        webViewUrl?.let {
            Log.i(tag, "Loading url $it")
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

        view.showAlertDiaog(
            ID_PAGE_LOAD_ERROR,
            context.getString(R.string.onboarding_lib_we_re_sorry),
            context.getString(R.string.onboarding_lib_std_error_message),
            listOf(
                AlertButton(
                    AlertButton.ACTION.OK,
                    AlertButton.TYPE.POSITIVE,
                    context.getString(R.string.onboarding_lib_button_ok)
                ),
                AlertButton(
                    AlertButton.ACTION.RETRY,
                    AlertButton.TYPE.NEGATIVE,
                    context.getString(R.string.onboarding_lib_button_retry)
                )
            )
        )
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
        onPageLoadError(errorCode = error.primaryError)
    }

    /* found out overload methods has issues, use this method if no hard coding or client secret in the app */
    @JavascriptInterface
    fun postMessageEx(idvSessionId: String, clientId: String, clientSecret: String, env: String) {
        Onboarding.init(clientId, clientSecret, env)
        startDdv(idvSessionId)
    }

    @JavascriptInterface
    fun postMessage(idvSessionId: String) {
        if (Onboarding.clientId.isNullOrEmpty() || Onboarding.clientSecret.isNullOrEmpty()) {
            Log.i(tag, "No Client id/client secret is set")
            showIDVError()
        } else {
            startDdv(idvSessionId)
        }
    }

    @JavascriptInterface
    fun exit() {
        (context as Activity).runOnUiThread {
            view.close()
        }
    }

    @JavascriptInterface
    fun openUrl(url: String) {
        (context as Activity).runOnUiThread {
            if (Onboarding.isValidUrl(url)) {
                view.openInBrowser(url)
                view.close()
            }
        }
    }

    @JavascriptInterface
    fun openEmailClient() {
        (context as Activity).runOnUiThread {
            if (!view.openEmailClient()) {
                view.showAlertDiaog(ID_PAGE_NO_EMAIL_CLIENT_ERROR,
                    context.getString(R.string.onboarding_lib_we_re_sorry),
                    context.getString(R.string.onboarding_lib_no_email_client),
                    listOf(
                        AlertButton(
                            AlertButton.ACTION.OK,
                            AlertButton.TYPE.POSITIVE,
                            context.getString(R.string.onboarding_lib_button_ok)
                        )
                    )
                )
            }
        }
    }

    private fun startDdv(idvSessionId: String) {
        Log.i(tag, "starting ddv, sessionid $idvSessionId")
        try {
            val conf = ConsumerData.Config(
                clientId = Onboarding.clientId,
                clientSecret = Onboarding.clientSecret,
                environment = Onboarding.ddvEnvironment,
                listOfCertificateSHA256Keys = Onboarding.ddvCertKeys,
                listOfCertificateReferences = Onboarding.certResourceIds
            )
            var gassCredentials: ConsumerData.GassCredentials? = null
            var dynatraceConfig: ConsumerData.DynatraceConfig? = null
            Onboarding.dynatraceAppId?.let {
                dynatraceConfig = ConsumerData.DynatraceConfig(
                    dynatraceAppId =  Onboarding.dynatraceAppId,
                    dynatraceBeaconUrl =  Onboarding.dynatraceBeaconUrl,
                    dynatraceUserOptIn =  Onboarding.dynatraceUserOptIn
                )
            }

            Onboarding.sourceSystemId?.let {
                gassCredentials = ConsumerData.GassCredentials(
                    userId = Onboarding.userId,
                    sourceSystemId = Onboarding.sourceSystemId
                )
            }

            Onboarding.registerDDV()

            Ddv.start(
                context as Activity,
                sessionTokenId = idvSessionId,
                dynatraceConfig = dynatraceConfig,
                gassCredentials = gassCredentials,
                config = conf
            )
        } catch (e: Exception) {
            e.printStackTrace()
            showIDVError()
        }
    }

    private fun showIDVError() {
        view.showAlertDiaog(
            ID_PAGE_IDV_STD_ERROR,
            context.getString(R.string.onboarding_lib_we_re_sorry),
            context.getString(R.string.onboarding_lib_std_idv_error_message),
            listOf(
                AlertButton(
                    AlertButton.ACTION.OK,
                    AlertButton.TYPE.POSITIVE,
                    context.getString(R.string.onboarding_lib_button_ok)
                )
            )
        )
    }

    fun onUserAlertAction(id: Int, action: AlertButton.ACTION) {
        when (id) {
            ID_PAGE_LOAD_ERROR -> {
                if (action == AlertButton.ACTION.RETRY) {
                    webViewUrl?.let {
                        view.showUrl(it)
                    }
                } else if (action == AlertButton.ACTION.OK) {
                    view.close()
                }
            }
            ID_NFC_ENABLE_PROMPT -> {
                if (action == AlertButton.ACTION.OK) {
                    view.showNfcSettings()
                    view.close()
                } else {
                    loadUrl()
                }
            }
        }
    }

    companion object {
        const val NO_ERROR = 100
        const val ID_PAGE_LOAD_ERROR = -200
        const val ID_PAGE_IDV_STD_ERROR = -300
        const val ID_PAGE_NO_EMAIL_CLIENT_ERROR = -400
        const val ID_NFC_ENABLE_PROMPT = 1000
    }
}