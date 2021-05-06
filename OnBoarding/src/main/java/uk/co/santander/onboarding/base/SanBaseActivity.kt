package uk.co.santander.onboarding.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import uk.co.santander.onboarding.BuildConfig

abstract class SanBaseActivity<P: SanBasePresenter> : AppCompatActivity(), SanBaseView{
    lateinit var presenter: P
    protected abstract fun setupDependencies()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.USE_SECURE_WINDOW) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
            if (!BuildConfig.SECURE_FILTER_TOUCHES) {
                window.decorView.filterTouchesWhenObscured = true
            }
        }
        setupDependencies()
    }

    override fun getContext(): Context {
        return this.getContext()
    }

    override fun close() {
        finish()
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    protected open fun setVisibility(
        visibility: Int,
        vararg views: View?
    ) {
        if (views == null) {
            return
        }
        for (view in views) {
            if (view != null) {
                view.visibility = visibility
            }
        }
    }
}