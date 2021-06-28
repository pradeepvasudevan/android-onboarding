package uk.co.santander.onboarding

import android.content.Context
import android.content.Intent
import org.jetbrains.annotations.NotNull
import uk.co.santander.onboarding.ui.OnboardingWebviewView
import uk.co.santander.onboarding.ui.OnboardingWebviewActivity

class Onboarding {
    companion object {
        fun start(@NotNull url: String) {
            onCompleteUrl = "$url$QUERY_PARAM_IDV_COMPLETE"
            val intent = Intent(OnboardingInitProvider.appContext, OnboardingWebviewActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            intent.putExtra(OnboardingWebviewView.EXTRA_WEBVIEW_URL, url)
            OnboardingInitProvider.appContext.startActivity(intent)
        }

        fun init(clientId: String, clientSecret: String) {
            this.clientId = clientId
            this.clientSecret = clientSecret
        }

        /**
         * optional, set dynatrace params for ID&V library to use
         */
        fun setIDVDynatraceParams(sourceSystemId: String? = null,
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

        /**
         * optional call, this url will be called when ID&V is completed its journey
         * will use the default url (start url + ?idvComplete) when ID&V is finished.
         */
        fun setIDVOnCompleteUrl(onCompleteUrl: String?) {
            onCompleteUrl?.let {
                this.onCompleteUrl = onCompleteUrl
            }
        }

        lateinit var clientId: String
            private set
        lateinit var clientSecret: String
            private set
        var sourceSystemId: String? = null
            private set
        var userId: String? = null
            private set
        var dynatraceAppId: String? = null
            private set
        var dynatraceBeaconUrl: String? = null
            private set
        var dynatraceUserOptIn: Boolean = false
            private set
        var onCompleteUrl: String? = null
            private set
        const val QUERY_PARAM_IDV_COMPLETE = "?idvComplete"
    }
}
