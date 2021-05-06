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


    fun onStartOnboardingLib(view: View) {
        Onboarding.start("https://app.sandev.gb.astodev.io/auth/register")
    }
}