package uk.co.santander.onboarding.ui


import android.app.Activity
import android.content.Context
import android.net.http.SslCertificate
import android.net.http.SslError
import android.webkit.URLUtil
import android.webkit.WebView
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import uk.co.santander.ddv.Ddv
import uk.co.santander.ddv.common.utils.otherconfigs.ConsumerData
import uk.co.santander.ddv.common.utils.otherconfigs.DdvEnvironment
import uk.co.santander.onboarding.Onboarding
import uk.co.santander.onboarding.R
import uk.co.santander.onboarding.base.WhitelistDelegate

@RunWith(MockitoJUnitRunner::class)
class OnboardingWebviewPresenterTest {

    val view: OnboardingWebviewView = mock(OnboardingWebviewView::class.java)
    val url = "https://testurl.com"
    val webView: WebView = mock(WebView::class.java)
    val context = mock(Context::class.java)
    val activity = mock(Activity::class.java)


    private val presenter: OnboardingWebviewPresenter by lazy {
        OnboardingWebviewPresenter(view, url)
    }

    @Before
    fun setUp() {
        `when`(view.getContext()).thenReturn(context)
        `when`(context.getString(R.string.onboarding_lib_we_re_sorry)).thenReturn("sorry")
        `when`(context.getString(R.string.onboarding_lib_std_error_message)).thenReturn("error")
        `when`(context.getString(R.string.onboarding_lib_std_idv_error_message)).thenReturn("error")
        `when`(context.getString(R.string.onboarding_lib_button_ok)).thenReturn("ok")
        `when`(context.getString(R.string.onboarding_lib_button_retry)).thenReturn("retry")
    }

    @Test
    fun onPageLoadStarted() {
        // when
        presenter.onPageLoadStarted(webView, url)

        //then
        verify(view).startProgress()
        verify(view).hideWebContent()
    }

    @Test
    fun onPageLoadFinishedNoError() {
        // when
        presenter.onPageLoadFinished(webView, url)

        // then
        verify(view).showWebContent()
    }

    @Test
    fun onSslError() {

        // when
        presenter.onSslError(error =SslError(SslError.SSL_INVALID, SslCertificate(null), url))
        // and when
        presenter.onPageLoadFinished(webView, url)

        // then
        verify(view, times(2)).hideProgress()
        verify(view).hideWebContent()
        verify(view).showAlertDiaog(
            OnboardingWebviewPresenter.ID_PAGE_LOAD_ERROR,
            context.getString(R.string.onboarding_lib_we_re_sorry),
            context.getString(R.string.onboarding_lib_std_error_message),
            listOf(AlertButton(AlertButton.ACTION.OK,
                AlertButton.TYPE.POSITIVE,
                context.getString(R.string.onboarding_lib_button_ok)),
                AlertButton(AlertButton.ACTION.RETRY,
                    AlertButton.TYPE.NEGATIVE,
                    context.getString(R.string.onboarding_lib_button_retry))))
    }

    @Test
    fun onPostMessageIDVEmptyClientInfo() {
        // given
        mockkObject(Onboarding)
        every { Onboarding.clientId } returns ""
        every { Onboarding.clientSecret } returns  ""

        // when
        presenter.postMessage("session_id")

        //then
        verify(view).showAlertDiaog(OnboardingWebviewPresenter.ID_PAGE_IDV_STD_ERROR,
        context.getString(R.string.onboarding_lib_we_re_sorry),
        context.getString(R.string.onboarding_lib_std_idv_error_message),
        listOf(AlertButton(AlertButton.ACTION.OK,
            AlertButton.TYPE.POSITIVE,
            context.getString(R.string.onboarding_lib_button_ok))))
    }

    @Test
    fun onPostMessageStartIDV() {
        // given
        mockkObject(Onboarding)
        mockkObject(Ddv)
        every { Ddv.start(any(), any(), any(), any(), any()) } answers { Unit }
        every { Onboarding.clientId } returns "client_id"
        every { Onboarding.clientSecret } returns  "client_secret"
        every { Onboarding.ddvEnvironment } returns DdvEnvironment.TEST

        val conf = ConsumerData.Config(
            clientId = Onboarding.clientId,
            clientSecret = Onboarding.clientSecret,
            listOfCertificateReferences = listOf(),
            listOfCertificateSHA256Keys = listOf(),
            environment = DdvEnvironment.TEST
        )

        `when`(view.getContext()).thenReturn(activity)

        // when
        presenter.postMessage("session_id")

        //then
        verify(exactly = 1) { Ddv.start(any(), eq("session_id"), any(), any(), eq(conf)) }
    }

    @Test
    fun exit() {
        // given
        Mockito.`when`(view.getContext()).thenReturn(activity)
        val runOnUiArgCaptor = ArgumentCaptor.forClass(Runnable::class.java)

        // when
        presenter.exit()

        // then
        verify(activity).runOnUiThread(runOnUiArgCaptor.capture())
        runOnUiArgCaptor.value.run()
        verify(view).close()
    }

    @Test
    fun onBackPressedGoBackToPrevPage() {
        // given
        `when`(view.canGoBackToPreviousWebPage()).thenReturn(true)

        // then
        presenter.onBackPressed()

        // then
        verify(view).goBackToPreviousWebPage()
    }

    @Test
    fun onBackPressedClosePage() {
        // given
        `when`(view.canGoBackToPreviousWebPage()).thenReturn(false)

        // then
        presenter.onBackPressed()

        // then
        verify(view).close()
    }

    @Test
    fun shouldOverrideUrlLoadingSantanderDomain() {
        // given
        mockkObject(WhitelistDelegate)
        val url = "https://www.santander.co.uk/test"
        every { WhitelistDelegate.getHost(url) } answers {"santander.co.uk"}

        // then
        presenter.shouldOverrideUrlLoading(webView, url)

        //then
        verify(view).processExternalLink(url)
    }

    @Test
    fun shouldOverrideUrlLoading() {
        // given
        mockkObject(WhitelistDelegate)
        val url = "https://www.abc.co.uk/test"
        every { WhitelistDelegate.getHost(url) } answers {"abc.co.uk"}


        // then
        val ret = presenter.shouldOverrideUrlLoading(webView, url)

        //then
        assert(!ret)
    }

    //un mock all
    @After
    fun cleanUp() {
        unmockkAll()
    }

//    @Test
//    fun openUrl() {
//        // given
//        val url = "http://abc.com"
////        every { view.getContext() } returns activity
////        mockkObject(Onboarding)
////        every { Onboarding.isValidUrl(any()) } answers { true }
////        val viewex = slot<OnboardingWebviewView>()
////        every { activity.runOnUiThread {
////             capture(viewex)
////        } }
////
////        presenter.openUrl(url)
////        verify { viewex.captured.openInBrowser(url) }
//
//
//    }
}