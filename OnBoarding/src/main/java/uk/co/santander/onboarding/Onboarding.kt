package uk.co.santander.onboarding

import android.content.Context
import android.content.Intent
import android.nfc.NfcManager
import android.util.Log
import android.webkit.URLUtil
import org.jetbrains.annotations.NotNull
import uk.co.santander.ddv.Ddv
import uk.co.santander.ddv.DdvListener
import uk.co.santander.ddv.data.oauth.repo.DDV_BACKEND_URL_PREFIX
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

        fun init(clientId: String, clientSecret: String) {
            this.clientId = clientId
            this.clientSecret = clientSecret
        }

        /**
         * optional, set dynatrace params for ID&V library to use
         */
        fun setIDVDynatraceParams(
            sourceSystemId: String? = null,
            userId: String? = null,
            dynatraceAppId: String? = null,
            dynatraceBeaconUrl: String? = null,
            dynatraceUserOptIn: Boolean = false
        ) {
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
                    Log.i(this::class.java.simpleName, "Finishing off, Loading url:  $onCompleteUrl")
                    startActivity(onCompleteUrl)
                }
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
        var checkNfc: Boolean =
            false // set to false for the initial release where the user is redirect to the browser
            private set
        private var validDomains: List<String> = listOf()
        const val QUERY_PARAM_IDV_COMPLETE = "?idvComplete"
        const val NFC_ENABLED = "nfcEnabled"

        private fun startActivity(url: String?) {
            val intent = Intent(OnboardingInitProvider.appContext, OnboardingWebviewActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            intent.putExtra(OnboardingWebviewView.EXTRA_WEBVIEW_URL, url)
            OnboardingInitProvider.appContext.startActivity(intent)
        }
    }
}
