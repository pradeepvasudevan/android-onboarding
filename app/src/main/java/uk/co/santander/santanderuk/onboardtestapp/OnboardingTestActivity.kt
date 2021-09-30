package uk.co.santander.santanderuk.onboardtestapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
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

        val prefs = getPreferences(Context.MODE_PRIVATE)
        val urlEditText = findViewById<EditText>(R.id.startUrlOnboardingUrl)
        urlEditText.setText(prefs.getString("url", ""))

        urlEditText.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                findViewById<Button>(R.id.startUrlOnboardingLib).callOnClick()
            }

            true
        }
    }

    fun onStartUrlOnboarding(view: View) {
        setupKeys()

        var url = findViewById<EditText>(R.id.startUrlOnboardingUrl).text.toString();

        if (!url.startsWith("http://") && !url.startsWith("https://")){
            url = "http://$url"
        }

        val prefs = getPreferences(Context.MODE_PRIVATE)
        prefs.edit().putString("url", url).commit()

        Onboarding.start(url) // URL
    }

    fun onStartPCAOnboarding(view: View) {
        setupKeys()

        Onboarding.start("https://app-p.dev.sirius.tlzproject.com/") // PCA
    }

    fun onStartBCAOnboarding(view: View) {
        setupKeys()

        Onboarding.start("https://app-b.dev.sirius.tlzproject.com/") // BCA
    }

    private fun setupKeys() {
        Onboarding.init(
            clientId = "36213840-0472-4c14-b674-b576a7d0b959",
            clientSecret = "oL6bX8cT5jN8rV5bE0kB2xG8pD0fU4uX2qK4gM3uC0sE7yO6lN", environment = "TEST"
        )
        Onboarding.setDdvCertificateResources(arrayOf(R.raw.ca_root, R.raw.diassl))
        Onboarding.setDdvCertificateKeys(
            arrayOf(
                "sha256/polcTnx1xyuZGP8FimtR+MEQPaBM+RZaJ4uNR/79A6Q=",
                "sha256/8kGWrpQHhmc0jwLo43RYo6bmqtHgsNxhARjM5yFCe/w=",
                "sha256/8BWfbvnT80Bs3xyNRpaDNcwOBESRgwwORGZt9yP2ow4=",
                "sha256/ixm49zzGIzvNWD/lqCWaIcYRSFfs24VRBX1NahyReAA=",
                "sha256/V873dynHqhybr+XZOQ5QIzJN9um5pYiD2tTNFnT35YU="
            )
        )
    }

    fun onJavascriptInterfaceTest(view: View) {
        Onboarding.init(clientId = "36213840-0472-4c14-b674-b576a7d0b959",
            clientSecret = "oL6bX8cT5jN8rV5bE0kB2xG8pD0fU4uX2qK4gM3uC0sE7yO6lN", environment = "TEST")
        Onboarding.start("file:///android_asset/test.html")
    }
}