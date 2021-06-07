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
        Onboarding.init(clientId = "75a7aa14-421d-4973-8e92-173309a12994",
            clientSecret = "X0oN6tP4uQ5pA5oR6vT1dU6yW4jD2tU6nD2pI1dL5nC6aD7mQ8")
        Onboarding.start("https://app.sandev.gb.astodev.io/auth/register")
    }
}