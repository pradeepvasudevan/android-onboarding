package uk.co.santander.onboarding

import android.content.Context
import android.content.Intent
import org.jetbrains.annotations.NotNull
import uk.co.santander.onboarding.ui.OnboardingWebviewView
import uk.co.santander.onboarding.ui.OnboardingWebviewActivity

class Onboarding {
    companion object {
        fun start(@NotNull url: String) {
            val intent = Intent(OnboardingInitProvider.appContext, OnboardingWebviewActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            intent.putExtra(OnboardingWebviewView.EXTRA_WEBVIEW_URL, url)
            OnboardingInitProvider.appContext.startActivity(intent)
        }

        fun init(clientId: String, clientSecret: String) {
            this.clientId = clientId
            this.clientSecret = clientSecret
        }

        fun setDynatraceParams(sourceSystemId: String? = null,
                               userId: String? = null,
                               dynatraceAppId: String? = null,
                               dynatraceBeaconUrl: String? = null,
                               dynatraceUserOptIn: Boolean = false) {
            this.sourceSystemId = sourceSystemId
            this.userId = userId
            this.dynatraceAppId = dynatraceAppId
            this.dynatraceBeaconUrl = dynatraceBeaconUrl
            this.dynatraceUserOptIn = dynatraceUserOptIn
        }

        lateinit var clientId: String
        lateinit var clientSecret: String
        var sourceSystemId: String? = null
        var userId: String? = null
        var dynatraceAppId: String? = null
        var dynatraceBeaconUrl: String? = null
        var dynatraceUserOptIn: Boolean = false
    }
}
