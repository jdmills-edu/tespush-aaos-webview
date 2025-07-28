# Chromecast Receiver Setup and Usage Guide

## Overview
Version 1.7 of the Riptide Android Automotive WebView app now includes a comprehensive Chromecast receiver that runs continuously in the background, allowing remote control of the WebView from any Cast-enabled device.

## Features Added

### Always-Running Background Service
- **Persistent Operation**: Chromecast receiver runs as a foreground service that automatically starts with the app
- **Auto-Restart**: Service automatically restarts if terminated by the system
- **Low Resource Usage**: Optimized for automotive systems with minimal battery impact

### Remote Control Capabilities
1. **URL Loading**: Load any web page remotely via cast command
2. **JavaScript Execution**: Execute arbitrary JavaScript in the WebView context
3. **Navigation Control**: Remotely trigger the 3-finger navigation menu
4. **Status Monitoring**: Real-time device status and connection health
5. **Bi-directional Communication**: Full duplex messaging between sender and receiver

### Network Discovery
- **mDNS/DNS-SD**: Standards-compliant service discovery for automatic device detection
- **Local Network Support**: Works on local WiFi networks without internet connection
- **Multicast Messaging**: Efficient network communication with multicast support

## Installation

### 1. Build and Install
```bash
cd /path/to/tespush-aaos-webview
./gradlew assembleDebug
adb install automotive/build/outputs/apk/debug/automotive-debug.apk
```

### 2. Required Permissions
The app will request the following permissions on first launch:
- **Network Access**: Internet, WiFi state, network state
- **Location**: Required for WiFi-based service discovery  
- **Storage**: For WebView cache and data
- **Notifications**: For persistent service notification
- **Foreground Service**: For always-running cast receiver

### 3. Initial Setup
1. Launch the Riptide app on your Android Automotive device
2. The Chromecast receiver will automatically start in the background
3. Look for the persistent notification showing "Cast receiver is running"
4. The device will be discoverable as "Riptide Cast Receiver" on your local network

## Usage

### Finding Your Device
1. **Google Home App**: The device should appear in your list of available cast devices
2. **Chrome Browser**: Click the cast icon and look for "Riptide Cast Receiver"
3. **Custom Apps**: Use the Cast Application ID: `CC1AD845`

### Testing with Included Test Page
1. Open `cast_test_sender.html` in Chrome browser on any device on the same network
2. Click "Initialize Cast" to set up the Cast API
3. Click "Connect to Riptide" to connect to your automotive device
4. Use the control buttons to test functionality:
   - **Load URL**: Enter any URL and click "Load URL"
   - **Execute JS**: Enter JavaScript code and click "Execute JS"
   - **Toggle Navigation**: Remotely activate the 3-finger menu
   - **Get Status**: Check device status and connection health
   - **Ping**: Test round-trip communication

### Supported Commands

#### Load URL
```json
{
  "type": "load_url",
  "url": "https://example.com"
}
```

#### Execute JavaScript
```json
{
  "type": "execute_js", 
  "javascript": "document.body.style.backgroundColor = 'red';"
}
```

#### Toggle Navigation Menu
```json
{
  "type": "toggle_navigation"
}
```

#### Get Device Status
```json
{
  "type": "get_status"
}
```

#### Ping Device
```json
{
  "type": "ping",
  "data": {"timestamp": 1234567890}
}
```

### Response Format
All commands return a response in this format:
```json
{
  "type": "response",
  "status": "success|error",
  "message": "Description",
  "timestamp": 1234567890
}
```

## Development

### Custom Cast Sender Apps
To integrate with custom applications, use:
- **Cast Application ID**: `CC1AD845`
- **Custom Namespace**: `urn:x-cast:com.adamucf.riptide`
- **Message Format**: JSON with `type` field specifying command

### Example JavaScript Cast Integration
```javascript
// Initialize Cast API
const castContext = cast.framework.CastContext.getInstance();
castContext.setOptions({
    receiverApplicationId: 'CC1AD845',
    autoJoinPolicy: chrome.cast.AutoJoinPolicy.ORIGIN_SCOPED
});

// Send command
function sendCommand(command) {
    const session = castContext.getCurrentSession();
    if (session) {
        session.sendMessage('urn:x-cast:com.adamucf.riptide', JSON.stringify(command));
    }
}

// Load URL remotely
sendCommand({
    type: 'load_url',
    url: 'https://example.com'
});
```

## Troubleshooting

### Device Not Appearing
1. **Check WiFi**: Ensure both devices are on the same WiFi network
2. **Firewall**: Verify no firewall is blocking multicast traffic (UDP port 5353)
3. **Service Status**: Check if the foreground service notification is visible
4. **Restart**: Try force-stopping and restarting the Riptide app

### Connection Issues
1. **Network Discovery**: Wait 30-60 seconds for mDNS discovery to complete
2. **Cast Service**: Restart Google Play Services on the sender device
3. **Chrome Updates**: Ensure Chrome browser is up to date
4. **App Permissions**: Verify all requested permissions were granted

### Message Delivery Problems
1. **JSON Format**: Ensure messages are valid JSON with required `type` field
2. **Namespace**: Use the correct namespace `urn:x-cast:com.adamucf.riptide`
3. **Session State**: Verify cast session is active before sending messages
4. **Response Timeout**: Allow up to 10 seconds for command responses

### Performance Issues
1. **JavaScript Execution**: Avoid long-running JavaScript that could block the WebView
2. **URL Loading**: Some sites may take time to load on automotive hardware
3. **Background Limits**: Android may limit background services; check battery optimization settings

## Logs and Debugging

### Android Logs
```bash
# View Chromecast receiver logs
adb logcat -s ChromecastReceiver CastReceiverManager CastMessageHandler

# View all app logs  
adb logcat -s "MainActivity" "ChromecastReceiver*" "Cast*"
```

### Chrome Logs
1. Open Chrome Developer Tools (F12)
2. Check Console tab for Cast API messages
3. Network tab shows Cast communication traffic

## Security Considerations

### Network Security
- Cast receiver only accepts connections from local network
- No internet-based remote access
- Messages limited to predefined command types

### JavaScript Execution
- JavaScript runs with same permissions as WebView
- Consider implications of remote code execution
- Audit any custom JavaScript before remote execution

### Privacy
- No data is transmitted outside local network
- Cast sessions are encrypted using Google Cast protocol
- Service discovery uses standard mDNS (not proprietary tracking)

## Compatibility

### Android Automotive OS
- **Minimum SDK**: Android 32 (Android 12L)
- **Target SDK**: Android 35 (Android 15)
- **Hardware**: Requires WiFi capability for network discovery

### Cast Senders  
- **Google Home**: Android/iOS apps
- **Chrome Browser**: Desktop and mobile versions
- **Custom Apps**: Any app using Google Cast SDK
- **Smart Displays**: Google Nest Hub, etc.

### Network Requirements
- **WiFi**: Both devices must be on same WiFi network
- **Multicast**: Network must support multicast traffic
- **Ports**: UDP 5353 (mDNS), TCP 8009 (Cast receiver)

This implementation provides a robust, always-available Chromecast receiver that transforms your Android Automotive WebView into a remotely controllable media and web browsing hub.
