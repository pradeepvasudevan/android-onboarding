package uk.co.santander.santanderuk.onboarding.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import uk.co.santander.santanderuk.onboarding.R

class OnboardingWebviewActivity : AppCompatActivity(), OnboardingWebviewView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_lib_activity)
    }

    override fun onStart() {
        super.onStart()
        val url = intent.getStringExtra(OnboardingWebviewView.EXTRA_WEBVIEW_URL)
        url?.let{
            val wv = findViewById<WebView>(R.id.onboarding_webview)
            wv.loadUrl(it)
        }
    }
}