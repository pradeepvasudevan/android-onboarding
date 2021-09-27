package uk.co.santander.onboarding.base

import java.net.MalformedURLException
import java.net.URL

object WhitelistDelegate {
    fun getHost(url: String?): String? {
        var host = ""
        try {
            val url = URL(url)
            val host = url.host
            return if (host.startsWith("www.")) host.substring(4) else host
        } catch (e: MalformedURLException) {
        }
        return host
    }

}