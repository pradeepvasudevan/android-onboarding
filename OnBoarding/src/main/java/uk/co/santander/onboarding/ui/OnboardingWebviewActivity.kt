package uk.co.santander.onboarding.ui

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.onboarding_lib_activity.*
import uk.co.santander.onboarding.BuildConfig
import uk.co.santander.onboarding.R
import uk.co.santander.onboarding.base.SanBaseActivity
import uk.co.santander.onboarding.base.SanWebView


class OnboardingWebviewActivity : SanBaseActivity<OnboardingWebviewPresenter>(),
    OnboardingWebviewView {
    lateinit var webContentView: SanWebView
    lateinit var progressViewGroup: ViewGroup
    lateinit var progressView: ProgressBar

    private val tag: String = this@OnboardingWebviewActivity.javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_lib_activity)
        webContentView = onboarding_webview
        progressView = app_progress
        progressViewGroup = app_progress_layout
        setupWebView()
        setupChromeClient()
        setupDownloadListener()
        presenter.onCreate()
    }

    override fun setupDependencies() {
        val url = intent.getStringExtra(OnboardingWebviewView.EXTRA_WEBVIEW_URL)
        presenter = OnboardingWebviewPresenter(this, url)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val url = intent.getStringExtra(OnboardingWebviewView.EXTRA_WEBVIEW_URL)
        presenter = OnboardingWebviewPresenter(this, url)
        presenter.onCreate()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun setupWebView() {
        webContentView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                Log.w(tag, "onPageStarted: $url")
                presenter.onPageLoadStarted(view, url)
            }

            override fun onLoadResource(view: WebView, url: String) {
                Log.w(tag, "onLoadResource: $url")
                presenter.onLoadResource(view.title)
            }

            override fun onPageFinished(view: WebView, url: String) {
                Log.w(tag, "onPageFinished: $url")
                presenter.onPageLoadFinished(view, url)
            }

            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return shouldOverrideUrlLoading(view, request.url.toString())
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.d(tag, "shouldOverrideUrlLoading $url")
                return presenter.shouldOverrideUrlLoading(view, url)
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                Log.d(tag, "onReceivedError: ${request.url}")
                if (request.isForMainFrame) {
                    Log.d(tag, "onReceivedError -- for mainframe: ${request.url}")
                    // Redirect to deprecated method, so it can be used in all SDK versions
                    onReceivedError(
                        view,
                        error.errorCode,
                        error.description.toString(),
                        request.url.toString()
                    )
                }
                var errorMessage: CharSequence = ""
                when (error.errorCode) {
                    ERROR_CONNECT -> errorMessage = "connection failed to connect"
                    ERROR_FAILED_SSL_HANDSHAKE -> errorMessage = "connection SSL handshake issue"
                    ERROR_FILE_NOT_FOUND -> errorMessage = "connection file not found"
                    ERROR_PROXY_AUTHENTICATION -> errorMessage = "connection proxy issue"
                    ERROR_TIMEOUT -> errorMessage = "connection timeout"
                    ERROR_IO -> errorMessage = "connection io failed"
                    ERROR_UNKNOWN -> errorMessage = "connection error unknown"
                }
                Log.w(tag, "onReceivedError: $errorMessage")
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedHttpError(
                view: WebView,
                request: WebResourceRequest,
                errorResponse: WebResourceResponse
            ) {
                Log.w(tag, "onReceivedHttpError: ${errorResponse.reasonPhrase}")
                // Redirect to deprecated method, so it can be used in all SDK versions
                if (request.isForMainFrame) {
                    onReceivedError(
                        view,
                        ERROR_UNKNOWN,
                        errorResponse.reasonPhrase + " " + errorResponse.statusCode,
                        request.url.toString()
                    )
                }
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                Log.e(
                    tag,
                    "onReceivedError - Failing url: $failingUrl Error: $errorCode $description"
                )
                presenter.onPageLoadError(errorCode)
            }

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                Log.w(tag, "onReceivedSslError: $error")

                if (BuildConfig.IGNORE_SSL_ERRORS_IN_WEBVIEW) {
                    Log.w(tag, "Ignored SSL error $error")
                    handler.proceed()
                } else {
                    Log.e(tag, "Failed to load, error $error")
                    presenter.onSslError(error)
                }
            }
        }

        webContentView.settings.run {
            javaScriptEnabled = true
            setAppCacheEnabled(false)
            allowContentAccess = false
            allowFileAccess = false
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false
            cacheMode = WebSettings.LOAD_NO_CACHE
            setSupportZoom(true)
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            setSupportMultipleWindows(true)
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        webContentView.addJavascriptInterface(presenter, OnboardingWebviewView.JS_INTERFACE_NAME)
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webContentView, true)
        if (BuildConfig.DEBUG || BuildConfig.DEBUG_WEBVIEW_WINDOW) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }

    private fun setupChromeClient() {
        webContentView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                // no progress bar
            }

            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                val result = view.hitTestResult
                val url = result?.extra
                return if (url != null) {
                    Log.d(tag, "onCreateWindow - url: $url - open in same webview")
                    presenter.useExistingWebViewWindow(view, url)
                    false
                } else {
                    Log.d(tag, "onCreateWindow - no url found - send to new webview")
                    val targetWebView = createNewTargetWebView()
                    view.addView(targetWebView)
                    val transport = resultMsg.obj as WebView.WebViewTransport
                    transport.webView = targetWebView
                    resultMsg.sendToTarget()
                    true
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    fun createNewTargetWebView(): WebView {
        val targetWebView = WebView(getContext())
        targetWebView.webViewClient = object : WebViewClient() {
            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return shouldOverrideUrlLoading(view, request.url.toString())
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.d(tag, "onCreateWindow - shouldOverrideUrlLoading $url")
                return presenter.shouldOverrideUrlLoading(view, url)
            }
        }
        targetWebView.setDownloadListener { url2: String, _: String, _: String, mimeType: String, _: Long ->
            Log.w(tag, "onCreateWindow - onDownloadStart - url: $url2 - mimetype: $mimeType")
            presenter.handleExternalLink(url2, mimeType)
        }

        return targetWebView
    }

    private fun setupDownloadListener() {
        webContentView.setDownloadListener { url: String, _: String, contentDisposition: String, mimeType: String, _: Long ->
            Log.w(
                tag,
                "onDownloadStart url: $url mimeType: $mimeType contentDisposition: $contentDisposition"
            )
            presenter.handleExternalLink(url, mimeType)
        }
    }

    override fun clearWebCache() {
        Log.d(tag, "clearing web cache")
        webContentView.clearFormData()
        webContentView.clearHistory()
        webContentView.clearCache(true)
    }

    override fun showNfcSettings() {
        val intent = Intent(Settings.ACTION_NFC_SETTINGS)
        startActivity(intent)
    }

    override fun openInBrowser(url:String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun openEmailClient(): Boolean {
        return try {
            val emailIntent = getEmailClientIntents().toTypedArray()
            if (emailIntent.isNotEmpty()) {
                val chooserIntent = Intent.createChooser(Intent(), getString(R.string.onboarding_lib_open_email))
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, emailIntent)
                startActivity(chooserIntent)
                true
            } else {
                false
            }
        } catch (e: ActivityNotFoundException) {
            false
        }
    }

    private fun getEmailClientIntents() : List<Intent> {
        val emailClientLauncherIntents = mutableListOf<Intent>()
        val emailAppIntent = Intent(Intent.ACTION_VIEW)
        emailAppIntent.data = Uri.parse("mailto:")
        val emailClients = packageManager.queryIntentActivities(emailAppIntent, 0)
        for (resolveInfo in emailClients) {
            val packageName = resolveInfo.activityInfo.packageName
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                emailClientLauncherIntents.add(launchIntent)
            }
        }
        return emailClientLauncherIntents
    }

    override fun clearAll() {
        Log.d(tag, "clearing all web caches, cookies")
        clearCookies()
        webContentView.clearFormData()
        webContentView.clearHistory()
        webContentView.clearCache(true)
    }

    private fun clearCookies() {
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
    }

    override fun showUrl(url: String) {
        webContentView.loadUrl(url)
    }

    override fun showWebContent() {
        setVisibility(View.VISIBLE, webContentView)
    }

    override fun hideWebContent() {
        setVisibility(View.GONE, webContentView)
    }

    override fun canGoBackToPreviousWebPage(): Boolean {
        return webContentView.canGoBack()
    }

    override fun goBackToPreviousWebPage() {
        webContentView.goBack()
    }

    override fun getPreviousLink(): String {
        val webBackForwardList = webContentView.copyBackForwardList()
        return webBackForwardList.getItemAtIndex(webBackForwardList.currentIndex - 1).url
    }

    override fun getCurrentLink(): String {
        return webContentView.url.orEmpty()
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun processExternalLink(url: String) {
        val target = Intent(Intent.ACTION_VIEW)
        target.data = Uri.parse(url)
        startActivity(target)
    }

    override fun showAlertDiaog(
        id: Int,
        title: String,
        message: String,
        alertButtons: List<AlertButton>
    ) {
        val alert = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
        for (button in alertButtons) {
            if (button.type == AlertButton.TYPE.POSITIVE) {
                alert.setPositiveButton(button.text) { _, _ ->
                    presenter.onUserAlertAction(
                        id,
                        button.action
                    )
                }
            }
            if (button.type == AlertButton.TYPE.NEGATIVE) {
                alert.setNegativeButton(button.text) { _, _ ->
                    presenter.onUserAlertAction(
                        id,
                        button.action
                    )
                }
            }
            if (button.type == AlertButton.TYPE.NEUTRAL) {
                alert.setNeutralButton(button.text) { _, _ ->
                    presenter.onUserAlertAction(
                        id,
                        button.action
                    )
                }
            }
        }
        alert.create().show()
    }

    override fun startProgress() {
        setVisibility(View.VISIBLE, progressViewGroup, progressView)
    }

    override fun hideProgress() {
        setVisibility(View.GONE, progressViewGroup, progressView)
    }
}