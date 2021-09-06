package uk.co.santander.onboarding

import android.webkit.URLUtil
import io.mockk.*
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

// to be done: fix mockObject bleeding issue
class OnboardingTest {
    @Before
    fun setUp() {
        mockkObject(Onboarding)
        mockkStatic(URLUtil::class)
    }

    @Test
    fun isValidUrl() {
        // given
        every { URLUtil.isValidUrl(any()) } returns true
        Onboarding.setWhitelistDomains(null)
        // then
        assertTrue(Onboarding.isValidUrl("http://www.bing.com"))
    }

    @Test
    fun isValidUrlWithDomainsNotInDomainList() {
        // given
        every { URLUtil.isValidUrl(any()) } returns true
        Onboarding.setWhitelistDomains("www.google.com,cnn.com")
        // then
        assertFalse(Onboarding.isValidUrl("http://www.bing.com"))
    }

    @Test
    fun isValidUrlWithDomainsInDomainList() {
        // given
        every { URLUtil.isValidUrl(any()) } returns true
        Onboarding.setWhitelistDomains("www.google.com,cnn.com,bing.com")
        // then
        assertFalse(Onboarding.isValidUrl("http://www.bing.com"))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}