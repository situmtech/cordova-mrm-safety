package com.situm.cordova.inertial;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import es.situm.inertial.eventsdetection.InertialEventsDetection;

public class DetectionForegroundService extends Service {

    public static final String ACTION_START_TAP = "com.situm.cordova.inertial.action.START_TAP";
    public static final String ACTION_STOP_TAP = "com.situm.cordova.inertial.action.STOP_TAP";
    public static final String ACTION_START_FALL = "com.situm.cordova.inertial.action.START_FALL";
    public static final String ACTION_STOP_FALL = "com.situm.cordova.inertial.action.STOP_FALL";
    public static final String ACTION_START_INACTIVITY = "com.situm.cordova.inertial.action.START_INACTIVITY";
    public static final String ACTION_STOP_INACTIVITY = "com.situm.cordova.inertial.action.STOP_INACTIVITY";
    public static final String ACTION_STOP_ALL = "com.situm.cordova.inertial.action.STOP_ALL";

    public static final String EXTRA_TAP_COUNT = "extra_tap_count";
    public static final String EXTRA_TAP_SENSITIVITY = "extra_tap_sensitivity";
    public static final String EXTRA_FALL_SENSITIVITY = "extra_fall_sensitivity";
    public static final String EXTRA_FALL_LIE_TIME = "extra_fall_lie_time";
    public static final String EXTRA_INACTIVITY_SENSITIVITY = "extra_inactivity_sensitivity";
    public static final String EXTRA_INACTIVITY_IDLE_TIME = "extra_inactivity_idle_time";
    public static final String EXTRA_INACTIVITY_IGNORE_HORIZONTAL = "extra_inactivity_ignore_horizontal";

    private static final int NOTIFICATION_ID = 7091;
    private static final String CHANNEL_ID = "cordova_inertial_detection";

    private InertialEventsDetection inertialEventsDetection;

    private boolean tapEnabled;
    private boolean fallEnabled;
    private boolean inactivityEnabled;

    @Override
    public void onCreate() {
        super.onCreate();
        inertialEventsDetection = InertialEventsDetection.getInstance(getApplicationContext());
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        if (ACTION_START_TAP.equals(action)) {
            int taps = intent.getIntExtra(EXTRA_TAP_COUNT, 3);
            int sensitivity = intent.getIntExtra(EXTRA_TAP_SENSITIVITY, 8);
            inertialEventsDetection.startTapDetection(taps, sensitivity);
            tapEnabled = true;
            ensureForeground();
        } else if (ACTION_STOP_TAP.equals(action)) {
            inertialEventsDetection.stopTapDetection();
            tapEnabled = false;
            stopIfIdle();
        } else if (ACTION_START_FALL.equals(action)) {
            float sensitivity = intent.getFloatExtra(EXTRA_FALL_SENSITIVITY, 0.5f);
            float lieTime = intent.getFloatExtra(EXTRA_FALL_LIE_TIME, 30f);
            inertialEventsDetection.startFallDetection(sensitivity, lieTime);
            fallEnabled = true;
            ensureForeground();
        } else if (ACTION_STOP_FALL.equals(action)) {
            inertialEventsDetection.stopFallDetection();
            fallEnabled = false;
            stopIfIdle();
        } else if (ACTION_START_INACTIVITY.equals(action)) {
            float sensitivity = intent.getFloatExtra(EXTRA_INACTIVITY_SENSITIVITY, 0.5f);
            float idleTime = intent.getFloatExtra(EXTRA_INACTIVITY_IDLE_TIME, 30f);
            boolean ignoreHorizontal = intent.getBooleanExtra(EXTRA_INACTIVITY_IGNORE_HORIZONTAL, true);
            inertialEventsDetection.startInactivityDetection(sensitivity, idleTime, ignoreHorizontal);
            inactivityEnabled = true;
            ensureForeground();
        } else if (ACTION_STOP_INACTIVITY.equals(action)) {
            inertialEventsDetection.stopInactivityDetection();
            inactivityEnabled = false;
            stopIfIdle();
        } else if (ACTION_STOP_ALL.equals(action)) {
            stopAllAndSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopAllAndSelf();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        inertialEventsDetection.stopAll();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void ensureForeground() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("Inertial detection running")
            .setContentText("Tap/Fall/Inactivity detection is active")
            .setOngoing(true)
            .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void stopIfIdle() {
        if (tapEnabled || fallEnabled || inactivityEnabled) {
            return;
        }
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    private void stopAllAndSelf() {
        inertialEventsDetection.stopAll();
        tapEnabled = false;
        fallEnabled = false;
        inactivityEnabled = false;
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            "Inertial detection",
            NotificationManager.IMPORTANCE_LOW
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
