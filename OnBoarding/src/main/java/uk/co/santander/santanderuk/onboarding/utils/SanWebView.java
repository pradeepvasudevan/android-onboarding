package uk.co.santander.santanderuk.onboarding.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

import java.nio.charset.StandardCharsets;

/**
 * A custom webview does take care of extra requirement like scroll event detection.
 */
public class SanWebView extends WebView {

    private static final String BASE_URL_ASSETS = "file:///android_asset/";
    private static final String MIMETYPE_TEXT_HTML = "text/html";

    private OnWebViewChangeListener onWebViewChangeListener;

    public interface OnWebViewChangeListener {
        void onOverScroll(int scrollX, int scrollY, boolean clampedX, boolean clampedY);
    }

    public SanWebView(Context context) {
        super(getFixedContext(context));
    }

    public SanWebView(Context context, AttributeSet attrs) {
        super(getFixedContext(context), attrs);
    }

    public SanWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SanWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(getFixedContext(context), attrs, defStyleAttr, defStyleRes);
    }

    private static Context getFixedContext(Context context) {
        //The fix is from https://stackoverflow.com/questions/41025200/android-view-inflateexception-error-inflating-class-android-webkit-webview/41721789#41721789
        //Please check this may be fixed from androidx_appcompat version > 1.1.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return context.createConfigurationContext(new Configuration());
        return context;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (onWebViewChangeListener != null) {
            onWebViewChangeListener.onOverScroll(scrollX, scrollY, clampedX, clampedY);
        }
    }

    public void setOnScrollChangedCallback(final OnWebViewChangeListener onWebViewChangeListener) {
        this.onWebViewChangeListener = onWebViewChangeListener;
    }

    public void loadHtml(String html) {
        loadDataWithBaseURL(BASE_URL_ASSETS, html, MIMETYPE_TEXT_HTML, StandardCharsets.UTF_8.name(), null);
    }

}
