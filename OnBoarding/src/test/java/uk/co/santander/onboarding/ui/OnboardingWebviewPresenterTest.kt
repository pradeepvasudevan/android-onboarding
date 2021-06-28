package uk.co.santander.onboarding.ui


import android.app.Activity
import android.content.Context
import android.net.http.SslCertificate
import android.net.http.SslError
import android.webkit.WebView
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import uk.co.santander.ddv.Ddv
import uk.co.santander.ddv.common.utils.otherconfigs.ConsumerData
import uk.co.santander.onboarding.Onboarding
import uk.co.santander.onboarding.R

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
    fun onCompleted() {
        //given
        val onCompUrl = "$url${Onboarding.QUERY_PARAM_IDV_COMPLETE}"
        mockkObject(Onboarding)
        every { Onboarding.onCompleteUrl } returns  onCompUrl

        // when
        presenter.onCompleted()

        // then
        verify(view).showUrl(onCompUrl)
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
        every { Ddv.start(any(), any(), any(), any()) } answers { Unit }
        every { Onboarding.clientId } returns "client_id"
        every { Onboarding.clientSecret } returns  "client_secret"

        val conf = ConsumerData.Config(
            clientId = Onboarding.clientId,
            clientSecret = Onboarding.clientSecret
        )

        `when`(view.getContext()).thenReturn(activity)

        // when
        presenter.postMessage("session_id")

        //then
        verify(exactly = 1) { Ddv.start(any(), eq("session_id"), any(), eq(conf)) }
    }
}