package com.adamucf.simpleaacpbrowser.cast;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;

import com.adamucf.simpleaacpbrowser.MainActivity;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;

import java.lang.ref.WeakReference;

public class CastReceiverManager {
    private static final String TAG = "CastReceiverManager";
    private static final String CAST_NAMESPACE = "urn:x-cast:com.adamucf.riptide";
    
    private Context context;
    private WeakReference<MainActivity> mainActivityRef;
    private CastContext castContext;
    private SessionManager sessionManager;
    private CastMessageHandler messageHandler;
    private boolean isInitialized = false;
    
    public CastReceiverManager(Context context) {
        this.context = context;
        this.messageHandler = new CastMessageHandler(this);
    }
    
    public void initialize() {
        try {
            // Initialize Cast Context
            castContext = CastContext.getSharedInstance(context);
            sessionManager = castContext.getSessionManager();
            
            // Set up session listener
            sessionManager.addSessionManagerListener(sessionManagerListener, CastSession.class);
            
            isInitialized = true;
            Log.d(TAG, "Cast receiver manager initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize cast receiver manager", e);
        }
    }
    
    public void setMainActivity(MainActivity activity) {
        this.mainActivityRef = new WeakReference<>(activity);
    }
    
    public void destroy() {
        try {
            if (sessionManager != null) {
                sessionManager.removeSessionManagerListener(sessionManagerListener, CastSession.class);
            }
            isInitialized = false;
            Log.d(TAG, "Cast receiver manager destroyed");
        } catch (Exception e) {
            Log.e(TAG, "Error destroying cast receiver manager", e);
        }
    }
    
    public boolean isInitialized() {
        return isInitialized;
    }
    
    // Handle incoming cast messages
    public void handleCastMessage(String namespace, String message) {
        if (messageHandler != null) {
            messageHandler.handleMessage(namespace, message);
        }
    }
    
    // Send message to cast sender
    public void sendMessageToCastSender(String namespace, String message) {
        try {
            CastSession castSession = sessionManager.getCurrentCastSession();
            if (castSession != null && castSession.isConnected()) {
                castSession.sendMessage(namespace, message);
                Log.d(TAG, "Message sent to cast sender: " + message);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to send message to cast sender", e);
        }
    }
    
    // Load URL in WebView from cast command
    public void loadUrlFromCast(String url) {
        MainActivity activity = mainActivityRef != null ? mainActivityRef.get() : null;
        if (activity != null) {
            activity.runOnUiThread(() -> {
                WebView webView = activity.getWebView();
                if (webView != null) {
                    webView.loadUrl(url);
                    Log.d(TAG, "Loaded URL from cast: " + url);
                }
            });
        }
    }
    
    // Execute JavaScript in WebView from cast command
    public void executeJavaScriptFromCast(String javascript) {
        MainActivity activity = mainActivityRef != null ? mainActivityRef.get() : null;
        if (activity != null) {
            activity.runOnUiThread(() -> {
                WebView webView = activity.getWebView();
                if (webView != null) {
                    webView.evaluateJavascript(javascript, null);
                    Log.d(TAG, "Executed JavaScript from cast: " + javascript);
                }
            });
        }
    }
    
    // Show/hide navigation menu from cast command
    public void toggleNavigationFromCast() {
        MainActivity activity = mainActivityRef != null ? mainActivityRef.get() : null;
        if (activity != null) {
            activity.runOnUiThread(() -> {
                activity.togglePopoverMenuFromCast();
                Log.d(TAG, "Toggled navigation from cast");
            });
        }
    }
    
    private final SessionManagerListener<CastSession> sessionManagerListener = 
        new SessionManagerListener<CastSession>() {
            
            @Override
            public void onSessionStarted(CastSession castSession, String sessionId) {
                Log.d(TAG, "Cast session started: " + sessionId);
                
                // Set up message channel
                try {
                    castSession.setMessageReceivedCallbacks(
                        CAST_NAMESPACE,
                        new Cast.MessageReceivedCallback() {
                            @Override
                            public void onMessageReceived(CastDevice castDevice, 
                                                         String namespace, 
                                                         String message) {
                                handleCastMessage(namespace, message);
                            }
                        }
                    );
                    
                    // Send ready message to sender
                    sendMessageToCastSender(CAST_NAMESPACE, "{\"type\":\"receiver_ready\"}");
                    
                } catch (Exception e) {
                    Log.e(TAG, "Failed to set up message channel", e);
                }
            }
            
            @Override
            public void onSessionResumed(CastSession castSession, boolean wasSuspended) {
                Log.d(TAG, "Cast session resumed, wasSuspended: " + wasSuspended);
            }
            
            @Override
            public void onSessionSuspended(CastSession castSession, int reason) {
                Log.d(TAG, "Cast session suspended, reason: " + reason);
            }
            
            @Override
            public void onSessionEnded(CastSession castSession, int error) {
                Log.d(TAG, "Cast session ended, error: " + error);
            }
            
            @Override
            public void onSessionStarting(CastSession castSession) {
                Log.d(TAG, "Cast session starting");
            }
            
            @Override
            public void onSessionStartFailed(CastSession castSession, int error) {
                Log.e(TAG, "Cast session start failed, error: " + error);
            }
            
            @Override
            public void onSessionEnding(CastSession castSession) {
                Log.d(TAG, "Cast session ending");
            }
            
            @Override
            public void onSessionResuming(CastSession castSession, String sessionId) {
                Log.d(TAG, "Cast session resuming: " + sessionId);
            }
            
            @Override
            public void onSessionResumeFailed(CastSession castSession, int error) {
                Log.e(TAG, "Cast session resume failed, error: " + error);
            }
        };
}
