package com.example.networkbasemodel;


import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NewDetailsActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mWebView = findViewById(R.id.webView);
        configureWebViewSecurity();
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
        if (getIntent() != null) {
            String url = getIntent().getStringExtra("url");
            if (!TextUtils.isEmpty(url)) {
                mWebView.loadUrl(url);
            }
        }
    }

    private void configureWebViewSecurity() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true); // 必需时启用JS
        settings.setAllowFileAccess(false); // 禁用文件访问
        settings.setAllowFileAccessFromFileURLs(false); // 禁用File协议跨域
        settings.setAllowUniversalAccessFromFileURLs(false);
        // 移除危险接口
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        // 安全JS接口
        mWebView.addJavascriptInterface(new SecureJSBridge(), "secureBridge");
        mWebView.setWebViewClient(new SecureWebViewClient());
    }

    private void loadSafeContent() {
        // 加载本地HTML（需校验内容完整性）
        mWebView.loadUrl("file:///android_asset/safe_page.html");

        // 或加载远程HTTPS（需证书校验）
        // mWebView.loadUrl(ALLOWED_DOMAIN);
    }

    // 安全JS交互接口
    public static class SecureJSBridge {
        @JavascriptInterface
        public String safeMethod(String param) {
            // 输入验证
            if (!isValidParam(param)) return "invalid";
            return "Processed: " + param;
        }

        private boolean isValidParam(String input) {
            return !input.contains("<script>");
        }
    }

    // 自定义WebViewClient
    private static class SecureWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // URL白名单校验
            if (!url.startsWith("信任域名")) {
                return true; // 拦截非信任域名
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.cancel(); // 拒绝不安全的SSL连接
        }
    }
}