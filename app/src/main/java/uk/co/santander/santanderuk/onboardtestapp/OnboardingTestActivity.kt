package uk.co.santander.santanderuk.onboardtestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import uk.co.santander.santanderuk.onboarding.Onboarding

class OnboardingTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_test_activity)
    }

    fun onStartOnboardingLib(view: View) {

        Onboarding.start("https://app.sandev.gb.astodev.io/auth/register")
    }
}