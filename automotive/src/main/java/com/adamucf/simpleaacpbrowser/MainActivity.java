package com.adamucf.simpleaacpbrowser;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.PermissionRequest;
import androidx.appcompat.app.AppCompatActivity;

import com.adamucf.simpleaacpbrowser.cast.ChromecastReceiverService;
import com.adamucf.simpleaacpbrowser.cast.CastReceiverManager;
import com.adamucf.simpleaacpbrowser.cast.CastJavaScriptInterface;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private RelativeLayout rootLayout;
    private LinearLayout popoverMenu;
    private Button btnHome, btnBack, btnForward, btnToggleTopPadding, btnToggleRightPadding;
    private Handler hideHandler = new Handler();
    private Runnable hideRunnable;
    
    // Padding state tracking
    private boolean isTopPaddingEnabled = false;
    private boolean isRightPaddingEnabled = false;
    
    // Touch tracking for 3-finger detection
    private int fingerCount = 0;
    private boolean isThreeFingerTouch = false;
    
    // Chromecast receiver components
    private CastReceiverManager castReceiverManager;
    private Intent chromecastServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enterImmersiveMode();

        // Initialize views
        webView = findViewById(R.id.webview);
        rootLayout = findViewById(R.id.root_layout);
        popoverMenu = findViewById(R.id.popover_menu);
        btnHome = findViewById(R.id.btn_home);
        btnBack = findViewById(R.id.btn_back);
        btnForward = findViewById(R.id.btn_forward);
        btnToggleTopPadding = findViewById(R.id.btn_toggle_top_padding);
        btnToggleRightPadding = findViewById(R.id.btn_toggle_right_padding);

        // Configure WebView
        configureWebView();
        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebChromeClient(new CustomWebChromeClient()); // Needed for media and advanced features
        webView.loadUrl(getString(R.string.default_url));

        setupTouchHandling();
        setupPopoverButtons();
        
        // Initialize and start Chromecast receiver
        initializeChromecastReceiver();
    }

    private void configureWebView() {
        WebSettings settings = webView.getSettings();
        
        // Hardware acceleration must be enabled before other settings
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        
        // Basic JavaScript and DOM storage
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        
        // Enable cookies
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);
        
        // Media and file access for Vue apps and RTSP
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        
        // Network and caching
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadsImagesAutomatically(true);
        
        // Modern web standards for Vue applications
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false); // Disable zoom controls for automotive
        settings.setDisplayZoomControls(false);
        
        // Mixed content (HTTP/HTTPS) - needed for some Vue apps
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        // Local network access for automotive systems
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        
        // Widevine DRM support for protected content
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowContentAccess(true);
        
        // Additional settings for DRM and local network access
        settings.setLoadsImagesAutomatically(true);
        settings.setBlockNetworkImage(false);
        settings.setBlockNetworkLoads(false);
        
        // Enable WebRTC and other modern web features
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        
        // User agent - ensure modern web standards with DRM capabilities
        settings.setUserAgentString(settings.getUserAgentString() + " AutomotiveWebView/1.7 Widevine/1.0 ChromecastReceiver/1.0");
    }

    private void setupTouchHandling() {
        // Set up touch handling for 3-finger detection on WebView
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleMultiTouch(event);
                return false; // Allow normal touch processing
            }
        });
        
        // Set up touch handling for root layout to detect touches outside popover
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && popoverMenu.getVisibility() == View.VISIBLE) {
                    // Check if touch is outside the popover menu
                    int[] location = new int[2];
                    popoverMenu.getLocationOnScreen(location);
                    int x = (int) event.getRawX();
                    int y = (int) event.getRawY();
                    
                    if (x < location[0] || x > location[0] + popoverMenu.getWidth() ||
                        y < location[1] || y > location[1] + popoverMenu.getHeight()) {
                        hidePopoverMenu();
                        return true; // Consume the touch event
                    }
                }
                return false;
            }
        });
    }

    private void handleMultiTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                fingerCount = 1;
                isThreeFingerTouch = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                fingerCount = event.getPointerCount();
                if (fingerCount == 3) {
                    isThreeFingerTouch = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (isThreeFingerTouch && fingerCount == 3) {
                    togglePopoverMenu();
                }
                fingerCount = Math.max(0, fingerCount - 1);
                if (fingerCount == 0) {
                    isThreeFingerTouch = false;
                }
                break;
        }
    }

    private void setupPopoverButtons() {
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(getString(R.string.default_url));
                hidePopoverMenu();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                hidePopoverMenu();
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }
                hidePopoverMenu();
            }
        });

        btnToggleTopPadding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTopPadding();
                hidePopoverMenu();
            }
        });

        btnToggleRightPadding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRightPadding();
                hidePopoverMenu();
            }
        });
    }

    private void togglePopoverMenu() {
        if (popoverMenu.getVisibility() == View.VISIBLE) {
            hidePopoverMenu();
        } else {
            showPopoverMenu();
        }
    }

    private void showPopoverMenu() {
        // Cancel any pending hide operation
        if (hideRunnable != null) {
            hideHandler.removeCallbacks(hideRunnable);
        }

        // Show popover menu
        popoverMenu.setVisibility(View.VISIBLE);
        
        // Update button states
        updatePopoverButtonStates();

        // Schedule hiding after 10 seconds
        hideRunnable = new Runnable() {
            @Override
            public void run() {
                hidePopoverMenu();
            }
        };
        hideHandler.postDelayed(hideRunnable, 10000); // 10 seconds
    }

    private void hidePopoverMenu() {
        popoverMenu.setVisibility(View.GONE);
        if (hideRunnable != null) {
            hideHandler.removeCallbacks(hideRunnable);
        }
    }

    private void updatePopoverButtonStates() {
        // Update navigation button states
        btnBack.setEnabled(webView.canGoBack());
        btnForward.setEnabled(webView.canGoForward());
        btnBack.setAlpha(webView.canGoBack() ? 1.0f : 0.5f);
        btnForward.setAlpha(webView.canGoForward() ? 1.0f : 0.5f);
        
        // Update padding button states visual indication
        btnToggleTopPadding.setAlpha(isTopPaddingEnabled ? 1.0f : 0.7f);
        btnToggleRightPadding.setAlpha(isRightPaddingEnabled ? 1.0f : 0.7f);
    }

    private void toggleTopPadding() {
        isTopPaddingEnabled = !isTopPaddingEnabled;
        updateWebViewPadding();
    }

    private void toggleRightPadding() {
        isRightPaddingEnabled = !isRightPaddingEnabled;
        updateWebViewPadding();
    }

    private void updateWebViewPadding() {
        int topMargin = isTopPaddingEnabled ? dpToPx(100) : 0;
        int rightMargin = isRightPaddingEnabled ? dpToPx(200) : 0;
        
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) webView.getLayoutParams();
        params.setMargins(0, topMargin, rightMargin, 0);
        webView.setLayoutParams(params);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    
    private void enterImmersiveMode () {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hideHandler != null && hideRunnable != null) {
            hideHandler.removeCallbacks(hideRunnable);
        }
        
        // Clean up Chromecast receiver
        if (castReceiverManager != null) {
            castReceiverManager.destroy();
        }
        
        // Stop Chromecast service
        if (chromecastServiceIntent != null) {
            stopService(chromecastServiceIntent);
        }
    }
    
    private void initializeChromecastReceiver() {
        try {
            // Start Chromecast receiver service
            chromecastServiceIntent = new Intent(this, ChromecastReceiverService.class);
            startForegroundService(chromecastServiceIntent);
            
            // Initialize Cast receiver manager
            castReceiverManager = new CastReceiverManager(this);
            castReceiverManager.setMainActivity(this);
            castReceiverManager.initialize();
            
            // Add JavaScript interface for cast communication
            CastJavaScriptInterface jsInterface = new CastJavaScriptInterface(this, castReceiverManager);
            webView.addJavascriptInterface(jsInterface, "CastReceiver");
            
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Failed to initialize Chromecast receiver", e);
        }
    }
    
    // Public method to get WebView for cast operations
    public WebView getWebView() {
        return webView;
    }
    
    // Public method to toggle popover menu from cast
    public void togglePopoverMenuFromCast() {
        runOnUiThread(() -> togglePopoverMenu());
    }

    // Custom WebViewClient for local network and DRM support
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Allow all local network URLs
            if (url.startsWith("http://localhost") || 
                url.startsWith("http://127.0.0.1") ||
                url.startsWith("http://192.168.") ||
                url.startsWith("http://10.") ||
                url.startsWith("http://172.")) {
                return false; // Let WebView handle it
            }
            return false; // Let WebView handle all URLs
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // Handle network errors gracefully
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    // Custom WebChromeClient for DRM and media support
    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onPermissionRequest(android.webkit.PermissionRequest request) {
            // Grant permissions for media access and DRM
            request.grant(request.getResources());
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
            // Support popup windows for DRM authentication
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
    }
}
