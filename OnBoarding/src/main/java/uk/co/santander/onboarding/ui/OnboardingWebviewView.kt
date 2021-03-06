package uk.co.santander.onboarding.ui

import android.view.View
import uk.co.santander.onboarding.base.SanBaseView

interface OnboardingWebviewView : SanBaseView {
    fun showUrl(url: String)
    fun showWebContent()
    fun hideWebContent()
    fun goBackToPreviousWebPage()
    fun canGoBackToPreviousWebPage(): Boolean
    fun getCurrentLink(): String
    fun getPreviousLink(): String
    fun clearAll()
    fun clearWebCache()
    fun showNfcSettings()
    fun openInBrowser(url:String)
    fun openEmailClient(): Boolean
    fun processExternalLink(url: String)
    fun showAlertDiaog(id: Int, title: String, message: String, alertButtons: List<AlertButton>)
    companion object {
        const val JS_INTERFACE_NAME = "onboardingWebView"
        const val EXTRA_WEBVIEW_URL = "webview_url"
    }
}