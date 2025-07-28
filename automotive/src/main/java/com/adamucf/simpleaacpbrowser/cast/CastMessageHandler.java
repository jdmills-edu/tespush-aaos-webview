package com.adamucf.simpleaacpbrowser.cast;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class CastMessageHandler {
    private static final String TAG = "CastMessageHandler";
    
    private CastReceiverManager castReceiverManager;
    
    public CastMessageHandler(CastReceiverManager castReceiverManager) {
        this.castReceiverManager = castReceiverManager;
    }
    
    public void handleMessage(String namespace, String message) {
        Log.d(TAG, "Received cast message: " + message);
        
        try {
            JSONObject jsonMessage = new JSONObject(message);
            String type = jsonMessage.optString("type", "");
            
            switch (type) {
                case "load_url":
                    handleLoadUrl(jsonMessage);
                    break;
                    
                case "execute_js":
                    handleExecuteJavaScript(jsonMessage);
                    break;
                    
                case "toggle_navigation":
                    handleToggleNavigation(jsonMessage);
                    break;
                    
                case "get_status":
                    handleGetStatus(jsonMessage);
                    break;
                    
                case "ping":
                    handlePing(jsonMessage);
                    break;
                    
                default:
                    Log.w(TAG, "Unknown message type: " + type);
                    sendErrorResponse("Unknown message type: " + type);
                    break;
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse cast message JSON", e);
            sendErrorResponse("Invalid JSON format");
        } catch (Exception e) {
            Log.e(TAG, "Error handling cast message", e);
            sendErrorResponse("Internal error: " + e.getMessage());
        }
    }
    
    private void handleLoadUrl(JSONObject message) {
        try {
            String url = message.getString("url");
            
            if (url != null && !url.trim().isEmpty()) {
                castReceiverManager.loadUrlFromCast(url);
                sendSuccessResponse("URL loaded successfully");
                Log.d(TAG, "Handled load_url: " + url);
            } else {
                sendErrorResponse("URL parameter is required");
            }
            
        } catch (JSONException e) {
            sendErrorResponse("Missing or invalid URL parameter");
        }
    }
    
    private void handleExecuteJavaScript(JSONObject message) {
        try {
            String javascript = message.getString("javascript");
            
            if (javascript != null && !javascript.trim().isEmpty()) {
                castReceiverManager.executeJavaScriptFromCast(javascript);
                sendSuccessResponse("JavaScript executed successfully");
                Log.d(TAG, "Handled execute_js: " + javascript);
            } else {
                sendErrorResponse("JavaScript parameter is required");
            }
            
        } catch (JSONException e) {
            sendErrorResponse("Missing or invalid JavaScript parameter");
        }
    }
    
    private void handleToggleNavigation(JSONObject message) {
        castReceiverManager.toggleNavigationFromCast();
        sendSuccessResponse("Navigation toggled successfully");
        Log.d(TAG, "Handled toggle_navigation");
    }
    
    private void handleGetStatus(JSONObject message) {
        try {
            JSONObject status = new JSONObject();
            status.put("type", "status_response");
            status.put("receiver_ready", true);
            status.put("version", "1.9");
            status.put("app_name", "Riptide");
            status.put("distraction_optimized", true);
            status.put("immersive_mode", true);
            status.put("enhanced_navigation", true);
            status.put("cast_status_page", true);
            status.put("timestamp", System.currentTimeMillis());
            
            castReceiverManager.sendMessageToCastSender(
                "urn:x-cast:com.adamucf.riptide", 
                status.toString()
            );
            Log.d(TAG, "Handled get_status");
            
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create status response", e);
            sendErrorResponse("Failed to create status response");
        }
    }
    
    private void handlePing(JSONObject message) {
        try {
            JSONObject pong = new JSONObject();
            pong.put("type", "pong");
            pong.put("timestamp", System.currentTimeMillis());
            
            // Echo back any additional data from ping
            if (message.has("data")) {
                pong.put("data", message.get("data"));
            }
            
            castReceiverManager.sendMessageToCastSender(
                "urn:x-cast:com.adamucf.riptide", 
                pong.toString()
            );
            Log.d(TAG, "Handled ping");
            
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create pong response", e);
            sendErrorResponse("Failed to create pong response");
        }
    }
    
    private void sendSuccessResponse(String message) {
        try {
            JSONObject response = new JSONObject();
            response.put("type", "response");
            response.put("status", "success");
            response.put("message", message);
            response.put("timestamp", System.currentTimeMillis());
            
            castReceiverManager.sendMessageToCastSender(
                "urn:x-cast:com.adamucf.riptide", 
                response.toString()
            );
            
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create success response", e);
        }
    }
    
    private void sendErrorResponse(String error) {
        try {
            JSONObject response = new JSONObject();
            response.put("type", "response");
            response.put("status", "error");
            response.put("error", error);
            response.put("timestamp", System.currentTimeMillis());
            
            castReceiverManager.sendMessageToCastSender(
                "urn:x-cast:com.adamucf.riptide", 
                response.toString()
            );
            
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create error response", e);
        }
    }
}
