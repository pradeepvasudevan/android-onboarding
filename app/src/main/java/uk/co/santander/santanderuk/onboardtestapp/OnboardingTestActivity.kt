package uk.co.santander.santanderuk.onboardtestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import uk.co.santander.onboarding.Onboarding

class OnboardingTestActivity : AppCompatActivity() {
    var magicLinkStarted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_test_activity)
    }

    override fun onStart() {
        super.onStart()
        Log.i("OnboardingTestActivity", intent.dataString ?: "***no data string***")
        if (intent.dataString != null && !magicLinkStarted) {
            Onboarding.start(intent.dataString ?: "")
            magicLinkStarted = true
        }
    }

    fun onStartPCAOnboarding(view: View) {
        Onboarding.init(clientId = "36213840-0472-4c14-b674-b576a7d0b959",
            clientSecret = "oL6bX8cT5jN8rV5bE0kB2xG8pD0fU4uX2qK4gM3uC0sE7yO6lN", environment = "TEST")

        Onboarding.setDdvCertificateResources(arrayOf(R.raw.ca_root, R.raw.diassl))

        Onboarding.setDdvCertificateKeys(arrayOf("sha256/polcTnx1xyuZGP8FimtR+MEQPaBM+RZaJ4uNR/79A6Q=",
            "sha256/8kGWrpQHhmc0jwLo43RYo6bmqtHgsNxhARjM5yFCe/w=",
            "sha256/8BWfbvnT80Bs3xyNRpaDNcwOBESRgwwORGZt9yP2ow4=",
            "sha256/ixm49zzGIzvNWD/lqCWaIcYRSFfs24VRBX1NahyReAA=",
            "sha256/V873dynHqhybr+XZOQ5QIzJN9um5pYiD2tTNFnT35YU="))

        Onboarding.start("https://app-p.dev.sirius.tlzproject.com/") // PCA
    }

    fun onStartBCAOnboarding(view: View) {
        Onboarding.init(clientId = "36213840-0472-4c14-b674-b576a7d0b959",
            clientSecret = "oL6bX8cT5jN8rV5bE0kB2xG8pD0fU4uX2qK4gM3uC0sE7yO6lN", environment = "TEST")
        Onboarding.setDdvCertificateResources(arrayOf(R.raw.ca_root, R.raw.diassl))
        Onboarding.setDdvCertificateKeys(arrayOf("sha256/polcTnx1xyuZGP8FimtR+MEQPaBM+RZaJ4uNR/79A6Q=",
            "sha256/8kGWrpQHhmc0jwLo43RYo6bmqtHgsNxhARjM5yFCe/w=",
            "sha256/8BWfbvnT80Bs3xyNRpaDNcwOBESRgwwORGZt9yP2ow4=",
            "sha256/ixm49zzGIzvNWD/lqCWaIcYRSFfs24VRBX1NahyReAA=",
            "sha256/V873dynHqhybr+XZOQ5QIzJN9um5pYiD2tTNFnT35YU="))

        Onboarding.start("https://app-b.dev.sirius.tlzproject.com/") // BCA
    }

    fun onJavascriptInterfaceTest(view: View) {
        Onboarding.init(clientId = "36213840-0472-4c14-b674-b576a7d0b959",
            clientSecret = "oL6bX8cT5jN8rV5bE0kB2xG8pD0fU4uX2qK4gM3uC0sE7yO6lN", environment = "TEST")
        Onboarding.start("file:///android_asset/test.html")
    }
}