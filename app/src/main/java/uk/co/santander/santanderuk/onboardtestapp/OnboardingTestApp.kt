package uk.co.santander.santanderuk.onboardtestapp

import android.app.Application
import com.facebook.stetho.Stetho

class OnboardingTestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}