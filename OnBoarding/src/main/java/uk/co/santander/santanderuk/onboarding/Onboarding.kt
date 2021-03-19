package uk.co.santander.santanderuk.onboarding

import android.content.Context
import android.content.Intent
import org.jetbrains.annotations.NotNull
import uk.co.santander.santanderuk.onboarding.ui.OnboardingWebviewView
import uk.co.santander.santanderuk.onboarding.ui.OnboardingWebviewActivity

class Onboarding {
    companion object {
        fun start(@NotNull url: String) {
            val intent = Intent(OnboardingInitProvider.appContext, OnboardingWebviewActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            intent.putExtra(OnboardingWebviewView.EXTRA_WEBVIEW_URL, url)
            OnboardingInitProvider.appContext.startActivity(intent)
        }

        fun start(context: Context, url: String) {
            // tbd
        }
    }
}
