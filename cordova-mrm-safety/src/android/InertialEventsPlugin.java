package com.situm.cordova.inertial;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.situm.inertial.eventsdetection.InertialEventsDetection;
import es.situm.inertial.eventsdetection.InertialEventsDetectionListener;

public class InertialEventsPlugin extends CordovaPlugin {

    private static final int DEFAULT_TAP_COUNT = 3;
    private static final int DEFAULT_TAP_SENSITIVITY = 8;
    private static final float DEFAULT_FALL_SENSITIVITY = 0.5f;
    private static final float DEFAULT_FALL_LIE_TIME = 30f;
    private static final float DEFAULT_INACTIVITY_SENSITIVITY = 0.5f;
    private static final float DEFAULT_INACTIVITY_IDLE_TIME = 30f;
    private static final boolean DEFAULT_INACTIVITY_IGNORE_HORIZONTAL = true;

    private InertialEventsDetection inertialEventsDetection;
    private CallbackContext eventsCallback;
    private boolean backgroundEnabled;

    private final InertialEventsDetectionListener listener = new InertialEventsDetectionListener() {
        @Override
        public void onTapDetected() {
            emitEvent("tap");
        }

        @Override
        public void onFallDetected() {
            emitEvent("fall");
        }

        @Override
        public void onInactivityDetected() {
            emitEvent("inactivity");
        }

        @Override
        public void onError(String message) {
            emitError(message);
        }
    };

    @Override
    protected void pluginInitialize() {
        inertialEventsDetection = InertialEventsDetection.getInstance(cordova.getContext().getApplicationContext());
        inertialEventsDetection.addListener(listener);
    }

    @Override
    public void onDestroy() {
        if (inertialEventsDetection != null) {
            inertialEventsDetection.removeListener(listener);
            inertialEventsDetection.stopAll();
        }
        eventsCallback = null;
        super.onDestroy();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "setListener":
                setListener(callbackContext);
                return true;
            case "clearListener":
                clearListener(callbackContext);
                return true;
            case "startTap":
                startTap(args, callbackContext);
                return true;
            case "stopTap":
                stopTap(callbackContext);
                return true;
            case "startFall":
                startFall(args, callbackContext);
                return true;
            case "stopFall":
                stopFall(callbackContext);
                return true;
            case "startInactivity":
                startInactivity(args, callbackContext);
                return true;
            case "stopInactivity":
                stopInactivity(callbackContext);
                return true;
            case "enableBackground":
                backgroundEnabled = true;
                callbackContext.success();
                return true;
            case "disableBackground":
                backgroundEnabled = false;
                callbackContext.success();
                return true;
            case "stopAll":
                stopAll(callbackContext);
                return true;
            default:
                return false;
        }
    }

    private void setListener(CallbackContext callbackContext) {
        eventsCallback = callbackContext;
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    private void clearListener(CallbackContext callbackContext) {
        eventsCallback = null;
        callbackContext.success();
    }

    private void startTap(JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject config = args.length() > 0 ? args.getJSONObject(0) : new JSONObject();
        int taps = config.optInt("taps", DEFAULT_TAP_COUNT);
        int sensitivity = config.optInt("sensitivity", DEFAULT_TAP_SENSITIVITY);

        if (backgroundEnabled) {
            Intent intent = new Intent(cordova.getContext(), DetectionForegroundService.class);
            intent.setAction(DetectionForegroundService.ACTION_START_TAP);
            intent.putExtra(DetectionForegroundService.EXTRA_TAP_COUNT, taps);
            intent.putExtra(DetectionForegroundService.EXTRA_TAP_SENSITIVITY, sensitivity);
            startForegroundService(intent);
        } else {
            inertialEventsDetection.startTapDetection(taps, sensitivity);
        }

        callbackContext.success();
    }

    private void stopTap(CallbackContext callbackContext) {
        if (backgroundEnabled) {
            Intent intent = new Intent(cordova.getContext(), DetectionForegroundService.class);
            intent.setAction(DetectionForegroundService.ACTION_STOP_TAP);
            cordova.getContext().startService(intent);
        } else {
            inertialEventsDetection.stopTapDetection();
        }
        callbackContext.success();
    }

    private void startFall(JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject config = args.length() > 0 ? args.getJSONObject(0) : new JSONObject();
        float sensitivity = (float) config.optDouble("sensitivity", DEFAULT_FALL_SENSITIVITY);
        float lieTime = (float) config.optDouble("lieTimeSec", DEFAULT_FALL_LIE_TIME);

        if (backgroundEnabled) {
            Intent intent = new Intent(cordova.getContext(), DetectionForegroundService.class);
            intent.setAction(DetectionForegroundService.ACTION_START_FALL);
            intent.putExtra(DetectionForegroundService.EXTRA_FALL_SENSITIVITY, sensitivity);
            intent.putExtra(DetectionForegroundService.EXTRA_FALL_LIE_TIME, lieTime);
            startForegroundService(intent);
        } else {
            inertialEventsDetection.startFallDetection(sensitivity, lieTime);
        }

        callbackContext.success();
    }

    private void stopFall(CallbackContext callbackContext) {
        if (backgroundEnabled) {
            Intent intent = new Intent(cordova.getContext(), DetectionForegroundService.class);
            intent.setAction(DetectionForegroundService.ACTION_STOP_FALL);
            cordova.getContext().startService(intent);
        } else {
            inertialEventsDetection.stopFallDetection();
        }
        callbackContext.success();
    }

    private void startInactivity(JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject config = args.length() > 0 ? args.getJSONObject(0) : new JSONObject();
        float sensitivity = (float) config.optDouble("sensitivity", DEFAULT_INACTIVITY_SENSITIVITY);
        float idleTimeSec = (float) config.optDouble("idleTimeSec", DEFAULT_INACTIVITY_IDLE_TIME);
        boolean ignoreHorizontal = config.optBoolean("ignoreHorizontal", DEFAULT_INACTIVITY_IGNORE_HORIZONTAL);

        if (backgroundEnabled) {
            Intent intent = new Intent(cordova.getContext(), DetectionForegroundService.class);
            intent.setAction(DetectionForegroundService.ACTION_START_INACTIVITY);
            intent.putExtra(DetectionForegroundService.EXTRA_INACTIVITY_SENSITIVITY, sensitivity);
            intent.putExtra(DetectionForegroundService.EXTRA_INACTIVITY_IDLE_TIME, idleTimeSec);
            intent.putExtra(DetectionForegroundService.EXTRA_INACTIVITY_IGNORE_HORIZONTAL, ignoreHorizontal);
            startForegroundService(intent);
        } else {
            inertialEventsDetection.startInactivityDetection(sensitivity, idleTimeSec, ignoreHorizontal);
        }

        callbackContext.success();
    }

    private void stopInactivity(CallbackContext callbackContext) {
        if (backgroundEnabled) {
            Intent intent = new Intent(cordova.getContext(), DetectionForegroundService.class);
            intent.setAction(DetectionForegroundService.ACTION_STOP_INACTIVITY);
            cordova.getContext().startService(intent);
        } else {
            inertialEventsDetection.stopInactivityDetection();
        }
        callbackContext.success();
    }

    private void stopAll(CallbackContext callbackContext) {
        if (backgroundEnabled) {
            Intent intent = new Intent(cordova.getContext(), DetectionForegroundService.class);
            intent.setAction(DetectionForegroundService.ACTION_STOP_ALL);
            cordova.getContext().startService(intent);
        } else {
            inertialEventsDetection.stopAll();
        }
        callbackContext.success();
    }

    private void startForegroundService(Intent intent) {
        Context context = cordova.getContext();
        ContextCompat.startForegroundService(context, intent);
    }

    private void emitEvent(String eventType) {
        if (eventsCallback == null) {
            return;
        }

        try {
            JSONObject payload = new JSONObject();
            payload.put("type", eventType);
            payload.put("timestamp", System.currentTimeMillis());

            PluginResult result = new PluginResult(PluginResult.Status.OK, payload);
            result.setKeepCallback(true);
            eventsCallback.sendPluginResult(result);
        } catch (JSONException ignored) {
        }
    }

    private void emitError(String message) {
        if (eventsCallback == null) {
            return;
        }

        try {
            JSONObject payload = new JSONObject();
            payload.put("type", "error");
            payload.put("message", message);
            payload.put("timestamp", System.currentTimeMillis());

            PluginResult result = new PluginResult(PluginResult.Status.OK, payload);
            result.setKeepCallback(true);
            eventsCallback.sendPluginResult(result);
        } catch (JSONException ignored) {
        }
    }
}
