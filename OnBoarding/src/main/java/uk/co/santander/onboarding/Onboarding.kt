package uk.co.santander.onboarding

import android.content.Context
import android.content.Intent
import android.nfc.NfcManager
import android.util.Log
import android.webkit.URLUtil
import androidx.annotation.RawRes
import org.jetbrains.annotations.NotNull
import uk.co.santander.ddv.Ddv
import uk.co.santander.ddv.DdvListener
import uk.co.santander.ddv.common.utils.otherconfigs.DdvEnvironment
import uk.co.santander.onboarding.ui.OnboardingWebviewActivity
import uk.co.santander.onboarding.ui.OnboardingWebviewView
import java.net.URL

class Onboarding {
    companion object {
        fun start(@NotNull url: String) {
            initOnCompleteUrl(url)
            val nfcUrl = urlWithNfc(url)
            startActivity(nfcUrl)
        }

        /**
         * env (accepted values = TEST or PROD)
         */
        fun init(clientId: String, clientSecret: String, environment: String) {
            this.clientId = clientId
            this.clientSecret = clientSecret
            this.ddvEnvironment =
                if (DDV_SANDBOX == environment) DdvEnvironment.TEST else DdvEnvironment.PROD
        }

        /**
         * not null, pass an empty list in the production env
         */
        fun setDdvCertificateKeys(ddvCertKeys: Array<String>) {
            this.ddvCertKeys = ddvCertKeys.toList()
        }

        /**
         * not null, pass an empty list in the production env
         */
        fun setDdvCertificateResources(@RawRes certResourceIds: Array<Int>) {
            this.certResourceIds = certResourceIds.toList()
        }

        /**
         * optional, set dynatrace params for ID&V library to use
         */
        fun setDdVDynatraceParams(
            dynatraceAppId: String? = null,
            dynatraceBeaconUrl: String? = null,
            dynatraceUserOptIn: Boolean = false
        ) {
            this.dynatraceAppId = dynatraceAppId
            this.dynatraceBeaconUrl = dynatraceBeaconUrl
            this.dynatraceUserOptIn = dynatraceUserOptIn
        }

        fun setIDVGassParams(
            sourceSystemId: String? = null,
            userId: String? = null
        ) {
            this.sourceSystemId = Companion.sourceSystemId
            this.userId = Companion.userId
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

        /**
         * optional call, checks if nfc is enabled on start up. default = true
         * set to false if nfc check needs to be skipped
         */
        fun checkNfcEnabled(check: Boolean) {
            checkNfc = check
        }

        /**
         * optional call, can set a comma separated list of domains to do an extra validation
         * if set, any domains not in the list are not opened in the external browser
         * when openUrl method is called from the websdk.
         */
        fun setWhitelistDomains(domains: String?) {
            validDomains = if (!domains.isNullOrEmpty()) {
                domains.split(",").toList()
            } else {
                listOf()
            }
        }

        private fun initOnCompleteUrl(urlString: String) {
            val url = URL(urlString)
            onCompleteUrl = "${url.protocol}://${url.authority}/$QUERY_PARAM_IDV_COMPLETE"
        }

        internal val nfcEnabled: Boolean
            get() {
                val manager =
                    OnboardingInitProvider.appContext.getSystemService(Context.NFC_SERVICE) as NfcManager
                return manager.defaultAdapter != null && manager.defaultAdapter.isEnabled
            }

        internal val nfcAvailable by lazy {
            val manager =
                OnboardingInitProvider.appContext.getSystemService(Context.NFC_SERVICE) as NfcManager
            return@lazy manager.defaultAdapter != null
        }

        internal fun isValidUrl(url: String): Boolean {
            return URLUtil.isValidUrl(url) && isWhiteListDomain(url)
        }

        private fun isWhiteListDomain(url: String): Boolean {
            var valid = true
            if (!validDomains.isNullOrEmpty()) {
                val domain = URL(url).host.replaceFirst("^www.*?\\.", "")
                valid = validDomains.contains(domain)
            }
            return valid
        }

        internal fun urlWithNfc(url: String): String {
            val sep = if (url.contains("?")) "&" else "?"
            return "$url$sep$NFC_ENABLED=$nfcEnabled"
        }

        internal fun registerDDV() {
            Ddv.registerListener(ddvListener)
        }

        private val ddvListener = object : DdvListener {
            override fun onCompleted() {
                Log.i(this::class.java.simpleName, "ID&V completed")
                Ddv.unregisterListener(this)
                onCompleteUrl?.let {
                    Log.i(
                        this::class.java.simpleName,
                        "Finishing off, Loading url:  $onCompleteUrl"
                    )
                    startActivity(onCompleteUrl)
                }
            }
        }

        var ddvCertKeys: List<String> = emptyList()
            private set
        var certResourceIds: List<Int> = emptyList()
            private set
        lateinit var ddvEnvironment: DdvEnvironment
            private set
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
        var checkNfc: Boolean =
            false // set to false for the initial release where the user is redirect to the browser
            private set
        private var validDomains: List<String> = listOf()
        const val QUERY_PARAM_IDV_COMPLETE = "?idvComplete"
        const val NFC_ENABLED = "nfcEnabled"
        const val DDV_SANDBOX = "TEST"

        private fun startActivity(url: String?) {
            val intent =
                Intent(OnboardingInitProvider.appContext, OnboardingWebviewActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            intent.putExtra(OnboardingWebviewView.EXTRA_WEBVIEW_URL, url)
            OnboardingInitProvider.appContext.startActivity(intent)
        }
    }
}
