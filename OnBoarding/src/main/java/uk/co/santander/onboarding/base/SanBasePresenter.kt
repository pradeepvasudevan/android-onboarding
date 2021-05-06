package uk.co.santander.onboarding.base

import android.content.Context


abstract class SanBasePresenter {
    // public for testing
    abstract val view: SanBaseView

    protected val context: Context
        get() = view.getContext()

    fun onCreate(){
        create()
    }

    protected fun create() {
    }

    fun onStart(){
        start()
    }

    protected open fun start() {
    }

    fun onStop(){
        stop()
    }

    protected fun stop() {
    }

    fun onPause() {
        pause()
    }

    protected fun pause() {
    }

}