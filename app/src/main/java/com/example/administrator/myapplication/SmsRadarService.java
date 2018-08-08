package com.example.administrator.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class SmsRadarService extends Service {

    private static final String CONTENT_SMS_URI = "content://sms";
    private static final int ONE_SECOND = 1000;


    private ContentResolver contentResolver;
    private SmsObserver smsObserver;
    private AlarmManager alarmManager;
    private TimeProvider timeProvider;
    private boolean initialized;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!initialized) {
            initializeService();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finishService();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        restartService();
    }

    private void initializeService() {
        initialized = true;
        initializeDependencies();
        registerSmsContentObserver();
    }

    private void initializeDependencies() {
        if (!areDependenciesInitialized()) {
            initializeContentResolver();
            initializeSmsObserver();
        }
    }

    private boolean areDependenciesInitialized() {
        return contentResolver != null && smsObserver != null;
    }

    private void initializeSmsObserver() {
        Handler handler = new Handler();
        SmsCursorParser smsCursorParser = initializeSmsCursorParser();
        this.smsObserver = new SmsObserver(contentResolver, handler, smsCursorParser);
    }

    private SmsCursorParser initializeSmsCursorParser() {
        SharedPreferences preferences = getSharedPreferences("sms_preferences", MODE_PRIVATE);
        SmsStorage smsStorage = new SharedPreferencesSmsStorage(preferences);
        return new SmsCursorParser(smsStorage, getTimeProvider());
    }

    private void initializeContentResolver() {
        this.contentResolver = getContentResolver();
    }

    private void finishService() {
        initialized = false;
        unregisterSmsContentObserver();
    }


    private void registerSmsContentObserver() {
        Uri smsUri = Uri.parse(CONTENT_SMS_URI);
        boolean notifyForDescendents = true;
        contentResolver.registerContentObserver(smsUri, notifyForDescendents, smsObserver);
    }

    private void unregisterSmsContentObserver() {
        contentResolver.unregisterContentObserver(smsObserver);
    }

    private void restartService() {
        Intent intent = new Intent(this, SmsRadarService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        long now = getTimeProvider().getDate().getTime();
        getAlarmManager().set(AlarmManager.RTC_WAKEUP, now + ONE_SECOND, pendingIntent);
    }

    private TimeProvider getTimeProvider() {
        return timeProvider != null ? timeProvider : new TimeProvider();
    }

    private AlarmManager getAlarmManager() {
        return alarmManager != null ? alarmManager : (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    /*
     * Test methods. This methods has been created to modify the service dependencies in test runtime because
     * without dependency injection we can't provide this entities.
     */

    void setSmsObserver(SmsObserver smsObserver) {
        this.smsObserver = smsObserver;
    }

    void setContentResolver(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    void setAlarmManager(AlarmManager alarmManager) {
        this.alarmManager = alarmManager;
    }

    void setTimeProvider(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }
}

