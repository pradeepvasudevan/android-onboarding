package uk.co.santander.santanderuk.onboarding.ui

import android.net.http.SslError
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import uk.co.santander.santanderuk.onboarding.base.SanBasePresenter

class OnboardingWebviewPresenter(
    override val view: OnboardingWebviewView,
    private val webViewUrl: String?
) : SanBasePresenter() {
    private val tag: String = this@OnboardingWebviewPresenter.javaClass.simpleName

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

    @JavascriptInterface
    fun postMessage(message: String) {
        Log.i(tag, "message from web: $message")
//        Ddv.start(view as Activity, "109442d6-996f-489c-9f96-eb3366a14ce6")
    }
}