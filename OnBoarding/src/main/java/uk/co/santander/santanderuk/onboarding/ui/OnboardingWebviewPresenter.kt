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
        TODO("Not yet implemented")
    }

    fun onPageLoadError(errorCode: Int) {
        view.hideProgress()
    }

    fun useExistingWebViewWindow(webView: WebView, url: String) {
        TODO("Not yet implemented")
    }

    fun handleExternalLink(url: String, mimeType: String) {
        TODO("Not yet implemented")
    }

    fun onSslError(error: SslError) {
        TODO("Not yet implemented")
    }
}