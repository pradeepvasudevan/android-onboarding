package uk.co.santander.santanderuk.onboarding.base

import android.content.Context

interface SanBaseView {
    fun getContext(): Context
    fun close()
    fun startProgress()
    fun hideProgress()
}