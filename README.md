# 🌊 Riptide - Enterprise Android Automotive WebView with Chromecast Receiver

An advanced Android Automotive OS (AAOS) WebView application that transforms your vehicle's infotainment system into a powerful, remotely controllable web browsing and media platform. Originally designed to support devices like Carlinkit for CarPlay/Android Auto integration, Riptide has evolved into a full-featured automotive web browser with enterprise-grade capabilities.

## 🚗 **Core Features**

### **Advanced WebView Engine**
- **Enterprise-Grade Configuration**: Hardware acceleration, Widevine DRM support, and comprehensive security settings
- **Modern Web Standards**: Full JavaScript support, DOM storage, cookies, and mixed content handling
- **Vue.js & RTSP Ready**: Optimized for modern web applications and streaming media content
- **Local Network Access**: Seamless integration with automotive systems and local network resources
- **Privacy-Focused**: Minimal permissions with user privacy as a priority

### **3-Finger Touch Navigation System**
- **Intuitive Activation**: Touch the screen with 3 fingers simultaneously to activate the navigation menu
- **Horizontal Popover Design**: Automotive-optimized layout with large, accessible buttons
- **Smart Auto-Hide**: Menu automatically disappears after 10 seconds or when touching outside
- **Visual Navigation Controls**:
  - 🏠 **Home** - Return to default URL
  - ⬅️ **Back** - Browser back navigation with state awareness
  - ➡️ **Forward** - Browser forward navigation with state awareness
  - 👇 **Top Padding Toggle** - Add/remove 100px top margin for UI customization
  - 👈 **Right Padding Toggle** - Add/remove 200px right margin for UI customization

### **Always-Running Chromecast Receiver** ⭐
- **Background Service**: Persistent foreground service that runs continuously
- **mDNS Discovery**: Automatic network discovery as "Riptide Cast Receiver"
- **Remote Control**: Full WebView control from any Cast-enabled device
- **Bi-directional Communication**: Real-time messaging between sender and receiver
- **Enterprise Reliability**: Auto-restart capability and robust error handling

## 🎯 **Chromecast Receiver Capabilities**

### **Remote URL Control**
```json
{
  "type": "load_url",
  "url": "https://example.com"
}
```
Load any web page remotely from phones, tablets, or Chrome browsers.

### **JavaScript Execution**
```json
{
  "type": "execute_js",
  "javascript": "document.body.style.backgroundColor = 'red';"
}
```
Execute arbitrary JavaScript code in the WebView context for dynamic control.

### **Navigation Menu Control**
```json
{
  "type": "toggle_navigation"
}
```
Remotely trigger the 3-finger navigation menu without physical touch.

### **Device Status Monitoring**
```json
{
  "type": "get_status"
}
```
Real-time status reporting including connection health and device information.

### **Connection Testing**
```json
{
  "type": "ping",
  "data": {"message": "Hello"}
}
```
Ping/pong functionality for connection validation and latency testing.

## 🔧 **Technical Specifications**

### **Platform Requirements**
- **Android Automotive OS**: Minimum SDK 32 (Android 12L)
- **Target SDK**: 35 (Android 15)
- **Hardware**: WiFi capability required for Cast functionality
- **Architecture**: Single Activity with hardware-accelerated WebView

### **Network & Connectivity**
- **Cast Protocol**: Google Cast Framework v21.3.0 integration
- **Service Discovery**: Standards-compliant mDNS/DNS-SD implementation
- **Local Network**: Comprehensive local network access and cleartext traffic support
- **Security**: Network security configuration for automotive-grade communication

### **Performance Optimizations**
- **Hardware Acceleration**: GPU-accelerated rendering for smooth performance
- **Memory Management**: Optimized for automotive hardware constraints
- **Battery Efficiency**: Low-power design with intelligent background processing
- **Network Efficiency**: Efficient multicast communication with minimal bandwidth usage

## 🚀 **Quick Start**

### **Installation**
```bash
# Clone the repository
git clone https://github.com/jdmills-edu/tespush-aaos-webview.git
cd tespush-aaos-webview

# Build the application
./gradlew assembleDebug

# Install on Android Automotive device
adb install automotive/build/outputs/apk/debug/automotive-debug.apk
```

### **Configuration**
Edit `automotive/src/main/res/values/strings.xml`:
```xml
<string name="default_url">https://your-default-url.com</string>
<string name="app_name">Your App Name</string>
```

### **Testing Chromecast Functionality**
1. Launch Riptide on your Android Automotive device
2. Open `cast_test_sender.html` in Chrome browser on any device on the same WiFi network
3. Click "Initialize Cast" → "Connect to Riptide"
4. Test remote control functionality with the provided interface

## 📱 **Usage Scenarios**

### **Automotive Integration**
- **CarPlay/Android Auto Support**: Use with Carlinkit adapters for wireless connectivity
- **Fleet Management**: Remote content management for commercial vehicles
- **Passenger Entertainment**: Remote control of entertainment content by passengers
- **Digital Signage**: Automotive advertising and information displays

### **Development & Testing**
- **Remote Debugging**: Execute JavaScript remotely for development and testing
- **Content Deployment**: Push web applications to automotive displays
- **Quality Assurance**: Remote testing of web applications on automotive hardware
- **Demonstration**: Show web content to clients/stakeholders remotely

### **Enterprise Applications**
- **Kiosk Mode**: Controlled web browsing in commercial vehicles
- **Training Systems**: Interactive training content with remote instructor control
- **Monitoring Dashboards**: Display real-time data with remote updates
- **Media Hubs**: Centralized content distribution in transportation fleets

## 🔐 **Security & Privacy**

### **Network Security**
- **Local Network Only**: Cast receiver accepts connections only from local network
- **Encrypted Communication**: Google Cast protocol provides end-to-end encryption
- **No Internet Dependency**: Works entirely on local WiFi networks
- **Firewall Friendly**: Standard ports with configurable network security

### **Privacy Protection**
- **Minimal Permissions**: Only essential permissions requested
- **No Data Collection**: No telemetry or user data transmission
- **Local Processing**: All operations performed locally on device
- **Audit Trail**: Comprehensive logging for security monitoring

### **Code Integrity**
- **Open Source**: Full source code available for security auditing
- **Build Transparency**: Reproducible builds with clear dependency management
- **Regular Updates**: Security patches and dependency updates
- **Community Reviewed**: Open to community security contributions

## 📊 **Version History**

- **v1.9** (Current) - Enhanced navigation with integrated cast status and testing interfaces
- **v1.8** - Complete automotive platform optimization with distraction optimization and immersive mode compliance
- **v1.7** - Chromecast receiver with always-running background service
- **v1.6** - Enterprise features: hardware acceleration, Widevine DRM, local network access
- **v1.5** - Privacy improvements: removed unnecessary permissions
- **v1.4** - Enhanced WebView: cookies, Vue.js support, RTSP streaming
- **v1.3** - Horizontal navigation menu optimization
- **v1.2** - 3-finger touch navigation system
- **v1.1** - Basic automotive WebView functionality

## 🛠 **Development**

### **Architecture Overview**
```
MainActivity (UI Controller)
├── WebView (Enterprise Configuration)
├── 3-Finger Touch System
├── Navigation Popover Menu
└── Chromecast Integration
    ├── ChromecastReceiverService (Background Service)
    ├── CastReceiverManager (Session Management)
    ├── CastMessageHandler (Command Processing)
    └── CastJavaScriptInterface (JS Bridge)
```

### **Key Components**
- **MainActivity.java**: Main application logic and WebView management
- **ChromecastReceiverService.java**: Always-running Cast receiver service
- **CastReceiverManager.java**: Cast session lifecycle management
- **CastMessageHandler.java**: JSON command parsing and execution
- **activity_main.xml**: Responsive UI layout with navigation controls

### **Build Requirements**
- **Java**: Version 11 or higher
- **Android Gradle Plugin**: 8.x
- **Gradle**: 8.13+
- **Android SDK**: API 32-35

### **Dependencies**
- Google Cast Framework (21.3.0)
- AndroidX Media Router (1.6.0)
- AndroidX Media (1.7.0)
- JmDNS (3.5.8) for network discovery

## 🤝 **Contributing**

We welcome contributions! Please see our [contribution guidelines](CONTRIBUTING.md) for details on:
- Code style and standards
- Testing requirements
- Security considerations
- Documentation standards

### **Common Contribution Areas**
- **UI/UX Improvements**: Enhanced automotive interface design
- **Performance Optimization**: Memory and battery efficiency improvements
- **Security Enhancements**: Additional security features and auditing
- **Feature Extensions**: New Cast commands and WebView capabilities
- **Testing**: Automated testing and device compatibility testing

## 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 **Support**

### **Documentation**
- [Chromecast Setup Guide](CHROMECAST_SETUP.md) - Detailed setup and troubleshooting
- [Changes Summary](CHANGES_SUMMARY.md) - Complete version history and features
- [API Documentation](docs/API.md) - Cast API and command reference

### **Troubleshooting**
- **Device Discovery Issues**: Check WiFi network and firewall settings
- **Cast Connection Problems**: Verify Google Play Services and Chrome updates
- **Performance Issues**: Review hardware acceleration and memory settings
- **Build Problems**: Check Java version and Android SDK configuration

### **Community**
- **Issues**: [GitHub Issues](https://github.com/jdmills-edu/tespush-aaos-webview/issues)
- **Discussions**: [GitHub Discussions](https://github.com/jdmills-edu/tespush-aaos-webview/discussions)
- **Security**: Report security issues privately via email

---

**Riptide** transforms Android Automotive infotainment systems into powerful, remotely controllable web platforms. Whether you're enabling CarPlay/Android Auto compatibility, developing enterprise applications, or creating innovative automotive experiences, Riptide provides the foundation for advanced automotive web integration.
