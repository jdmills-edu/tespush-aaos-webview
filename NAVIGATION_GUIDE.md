# Navigation Guide - Accessing Cast Features

## 🎯 **How to Access Cast Receiver Pages**

### **3-finger Navigation Menu**
The app uses a **3-finger touch gesture** to access all navigation options:

1. **Touch the screen with 3 fingers simultaneously**
2. **Hold for a brief moment** until the navigation menu appears
3. **Select your desired option** from the horizontal menu

### **Navigation Menu Options**

The popover menu now includes **7 buttons**:

| Button | Icon | Function | Description |
|--------|------|----------|-------------|
| **Home** | 🏠 | Default URL | Returns to the configured home page |
| **Back** | ⬅️ | Browser Back | Navigate to previous page (if available) |
| **Forward** | ➡️ | Browser Forward | Navigate to next page (if available) |
| **Top Padding** | 👇 | Toggle Margin | Add/remove 100px top margin |
| **Right Padding** | 👈 | Toggle Margin | Add/remove 200px right margin |
| **Cast Status** | 📡 | Cast Receiver | **NEW: Access cast receiver status page** |
| **Cast Test** | 🧪 | Test Interface | **NEW: Access cast sender test page** |

## 📡 **Cast Status Page**

### **Accessing the Cast Status Page:**
1. **3-finger touch** to open navigation menu
2. **Tap the 📡 (Cast Status) button**
3. View comprehensive cast receiver information

### **What You'll See:**
- **🌊 Riptide Cast Status** - Live status dashboard
- **📡 Cast Receiver** - Service status, discovery name, App ID, namespace
- **🚗 Automotive Features** - Distraction optimization, immersive mode status
- **🌐 WebView Features** - JavaScript, cookies, Vue.js, Widevine DRM status
- **🎛️ Supported Commands** - JSON examples for all cast commands
- **Interactive Buttons** - Test JavaScript, get cast status, refresh

### **Features of Status Page:**
- **Live Status Updates** - Real-time service monitoring
- **Interactive Testing** - Built-in JavaScript test functionality  
- **Command Examples** - Copy-ready JSON command samples
- **Visual Indicators** - Green/red status lights for services
- **Responsive Design** - Optimized for automotive displays

## 🧪 **Cast Test Interface**

### **Accessing the Test Interface:**
1. **3-finger touch** to open navigation menu
2. **Tap the 🧪 (Cast Test) button**
3. Access the full cast sender test interface

### **What You'll Find:**
- **Connection Controls** - Initialize Cast API, connect/disconnect
- **URL Control** - Load any URL remotely in the WebView
- **JavaScript Execution** - Execute arbitrary JavaScript code
- **Navigation Control** - Remote 3-finger menu activation
- **Response Log** - Real-time message and response logging

### **Using the Test Interface:**
1. **Initialize Cast** - Click to set up the Cast API
2. **Connect to Riptide** - Establish connection to cast receiver
3. **Test Commands** - Use the various control buttons to test functionality
4. **Monitor Responses** - Watch the response log for real-time feedback

## 🔄 **Navigation Flow**

### **Typical Usage Pattern:**
```
App Launch → Default URL
     ↓
3-Finger Touch → Navigation Menu
     ↓
📡 Cast Status → Monitor receiver status
     ↓  
🧪 Cast Test → Test remote control
     ↓
🏠 Home → Return to default URL
```

### **Quick Access Methods:**
- **Status Check**: 3-finger → 📡 
- **Remote Testing**: 3-finger → 🧪
- **Return Home**: 3-finger → 🏠
- **Browser Navigation**: 3-finger → ⬅️ or ➡️

## 🎮 **Advanced Usage**

### **Remote Control from External Devices:**
1. **Find Device**: Look for "Riptide Cast Receiver" in Google Home app or Chrome cast menu
2. **Connect**: Establish cast session with the automotive device
3. **Send Commands**: Use JSON commands to control the WebView remotely
4. **Monitor Status**: Check the 📡 Cast Status page for connection health

### **Development and Testing:**
1. **Local Testing**: Use 🧪 Cast Test page for local functionality testing
2. **Remote Testing**: Use external device with `cast_test_sender.html` 
3. **Status Monitoring**: Use 📡 Cast Status page for service health checks
4. **JavaScript Debugging**: Execute test code through cast interface

## 🛠️ **Troubleshooting Navigation**

### **3-Finger Touch Not Working:**
- **Ensure all 3 fingers touch simultaneously**
- **Hold briefly** - don't tap and release immediately
- **Use finger pads**, not fingertips
- **Touch anywhere on the screen** - the entire WebView area is active

### **Navigation Menu Disappears:**
- **10-second timeout** - menu auto-hides after 10 seconds
- **Outside touch** - touching outside the menu closes it
- **Re-activate** with another 3-finger touch

### **Cast Pages Not Loading:**
- **Check file access** - ensure assets folder permissions
- **JavaScript enabled** - verify WebView JavaScript is active
- **Clear cache** - try refreshing with browser back/forward navigation

### **Button Responsiveness:**
- **120dp buttons** - designed for automotive touch targets
- **Haptic feedback** - buttons provide visual feedback on press
- **Single tap** - avoid multiple rapid taps

## 📱 **Mobile Integration**

### **Casting from Phone/Tablet:**
1. **Same WiFi Network** - ensure both devices on same network
2. **Google Home App** - find "Riptide Cast Receiver"
3. **Chrome Browser** - use cast icon in Chrome
4. **Custom Apps** - integrate with Cast SDK using App ID `CC1AD845`

### **Cross-Device Testing:**
- **Automotive Display**: Use 🧪 Cast Test page locally
- **Mobile Device**: Use external browser with `cast_test_sender.html`
- **Development Machine**: Web browser testing interface
- **Multiple Devices**: Simultaneous cast sender testing

This navigation system provides comprehensive access to all cast receiver functionality while maintaining the distraction-optimized, automotive-friendly interface design.
