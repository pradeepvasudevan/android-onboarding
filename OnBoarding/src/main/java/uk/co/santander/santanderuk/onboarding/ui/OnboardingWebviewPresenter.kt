package uk.co.santander.santanderuk.onboarding.ui

import android.net.http.SslError
import android.webkit.WebView
import uk.co.santander.santanderuk.onboarding.base.SanBasePresenter

class OnboardingWebviewPresenter(
    override val view: OnboardingWebviewView,
    private val webViewUrl: String?
) : SanBasePresenter() {

    override fun start() {
        webViewUrl?.let {
            view.showUrl(it)
        }
    }

    fun onPageLoadStarted(webView: WebView, url: String) {
        view.startProgress()
    }

    fun onLoadResource(title: String?) {

    }

    fun onPageLoadFinished(webView: WebView, url: String) {
        view.hideProgress()
    }

    fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
        return false
    }

    fun onPageLoadError(errorCode: Int) {
        view.hideProgress()
    }

    fun useExistingWebViewWindow(webView: WebView, url: String) {

    }

    fun handleExternalLink(url: String, mimeType: String) {

    }

    fun onSslError(error: SslError) {

    }
}