package com.adamucf.simpleaacpbrowser.cast;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.lang.ref.WeakReference;

public class CastJavaScriptInterface {
    private static final String TAG = "CastJSInterface";
    
    private Context context;
    private WeakReference<CastReceiverManager> castReceiverManagerRef;
    
    public CastJavaScriptInterface(Context context, CastReceiverManager castReceiverManager) {
        this.context = context;
        this.castReceiverManagerRef = new WeakReference<>(castReceiverManager);
    }
    
    @JavascriptInterface
    public void onReceiverReady() {
        Log.d(TAG, "Cast receiver page is ready");
        
        CastReceiverManager manager = castReceiverManagerRef.get();
        if (manager != null) {
            // Notify that the receiver HTML page is loaded and ready
            manager.sendMessageToCastSender(
                "urn:x-cast:com.adamucf.riptide", 
                "{\"type\":\"receiver_page_ready\",\"timestamp\":" + System.currentTimeMillis() + "}"
            );
        }
    }
    
    @JavascriptInterface
    public void logMessage(String message) {
        Log.d(TAG, "JavaScript log: " + message);
    }
    
    @JavascriptInterface
    public String getCastStatus() {
        try {
            CastReceiverManager manager = castReceiverManagerRef.get();
            if (manager != null && manager.isInitialized()) {
                return "{\"status\":\"ready\",\"version\":\"1.7\",\"timestamp\":" + System.currentTimeMillis() + "}";
            } else {
                return "{\"status\":\"not_ready\",\"version\":\"1.7\",\"timestamp\":" + System.currentTimeMillis() + "}";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting cast status", e);
            return "{\"status\":\"error\",\"error\":\"" + e.getMessage() + "\",\"timestamp\":" + System.currentTimeMillis() + "}";
        }
    }
    
    @JavascriptInterface
    public void sendCastMessage(String message) {
        Log.d(TAG, "Sending cast message from JavaScript: " + message);
        
        CastReceiverManager manager = castReceiverManagerRef.get();
        if (manager != null) {
            manager.sendMessageToCastSender("urn:x-cast:com.adamucf.riptide", message);
        }
    }
}
