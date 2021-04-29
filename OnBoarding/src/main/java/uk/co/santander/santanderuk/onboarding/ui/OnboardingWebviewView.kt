package uk.co.santander.santanderuk.onboarding.ui

import uk.co.santander.santanderuk.onboarding.base.SanBaseView

interface OnboardingWebviewView : SanBaseView {
    fun showUrl(url: String)
    companion object {
        const val JS_INTERFACE_NAME = "astoWebView"
        const val EXTRA_WEBVIEW_URL = "webview_url"
    }
}