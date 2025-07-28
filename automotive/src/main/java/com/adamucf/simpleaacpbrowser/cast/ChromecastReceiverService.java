package com.adamucf.simpleaacpbrowser.cast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.adamucf.simpleaacpbrowser.MainActivity;
import com.adamucf.simpleaacpbrowser.R;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;
import com.google.android.gms.cast.framework.media.CastMediaOptions;

import java.util.List;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class ChromecastReceiverService extends Service {
    private static final String TAG = "ChromecastReceiver";
    private static final int NOTIFICATION_ID = 1001;
    private static final String CAST_SERVICE_TYPE = "_googlecast._tcp.local.";
    
    private JmDNS jmdns;
    private WifiManager.MulticastLock multicastLock;
    private CastReceiverManager castReceiverManager;
    private NotificationManager notificationManager;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ChromecastReceiverService onCreate");
        
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
        
        // Initialize Cast Receiver
        initializeCastReceiver();
        
        // Start mDNS service discovery
        startMdnsDiscovery();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ChromecastReceiverService started");
        
        // Start as foreground service
        startForeground(NOTIFICATION_ID, createNotification());
        
        return START_STICKY; // Restart if killed
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ChromecastReceiverService destroyed");
        
        // Clean up resources
        stopMdnsDiscovery();
        if (castReceiverManager != null) {
            castReceiverManager.destroy();
        }
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                getString(R.string.cast_notification_channel_id),
                getString(R.string.cast_notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Chromecast receiver service");
            channel.setShowBadge(false);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    private Notification createNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        return new NotificationCompat.Builder(this, getString(R.string.cast_notification_channel_id))
            .setContentTitle(getString(R.string.cast_receiver_name))
            .setContentText("Cast receiver is running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
    }
    
    private void initializeCastReceiver() {
        try {
            castReceiverManager = new CastReceiverManager(this);
            castReceiverManager.initialize();
            Log.d(TAG, "Cast receiver initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize cast receiver", e);
        }
    }
    
    private void startMdnsDiscovery() {
        try {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifi.createMulticastLock("chromecast_discovery");
            multicastLock.setReferenceCounted(true);
            multicastLock.acquire();
            
            InetAddress localAddress = getLocalIpAddress();
            if (localAddress != null) {
                jmdns = JmDNS.create(localAddress, "ChromecastReceiver");
                
                // Register our service
                ServiceInfo serviceInfo = ServiceInfo.create(
                    CAST_SERVICE_TYPE,
                    getString(R.string.cast_receiver_name),
                    8009, // Standard Chromecast port
                    "id=" + getString(R.string.cast_app_id) + 
                    ",ca=5,st=0,ic=/setup/icon.png,fn=" + getString(R.string.cast_receiver_name)
                );
                
                jmdns.registerService(serviceInfo);
                Log.d(TAG, "mDNS service registered successfully");
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to start mDNS discovery", e);
        }
    }
    
    private void stopMdnsDiscovery() {
        try {
            if (jmdns != null) {
                jmdns.unregisterAllServices();
                jmdns.close();
                jmdns = null;
            }
            
            if (multicastLock != null && multicastLock.isHeld()) {
                multicastLock.release();
                multicastLock = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping mDNS discovery", e);
        }
    }
    
    private InetAddress getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting local IP address", e);
        }
        return null;
    }
    
    // Cast Options Provider for Framework initialization
    public static class CastOptionsProvider implements OptionsProvider {
        @Override
        public CastOptions getCastOptions(Context context) {
            CastMediaOptions mediaOptions = new CastMediaOptions.Builder()
                .setNotificationOptions(null)
                .setExpandedControllerActivityClassName(MainActivity.class.getName())
                .build();
            
            return new CastOptions.Builder()
                .setReceiverApplicationId(context.getString(R.string.cast_app_id))
                .setCastMediaOptions(mediaOptions)
                .build();
        }
        
        @Override
        public List<SessionProvider> getAdditionalSessionProviders(Context context) {
            return null;
        }
    }
}
