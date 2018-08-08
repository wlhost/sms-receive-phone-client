package com.example.administrator.myapplication;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

class SharedPreferencesSmsStorage implements SmsStorage {

    private static final String LAST_SMS_PARSED = "last_sms_parsed";
    private static final int DEFAULT_SMS_PARSED_VALUE = -1;

    private SharedPreferences preferences;

    SharedPreferencesSmsStorage(SharedPreferences preferences) {
        if (preferences == null) {
            throw new IllegalArgumentException("SharedPreferences param can't be null");
        }
        this.preferences = preferences;
    }

    @Override
    public void updateLastSmsIntercepted(int smsId) {
        Editor editor = preferences.edit();
        editor.putInt(LAST_SMS_PARSED, smsId);
        editor.commit();
    }

    @Override
    public int getLastSmsIntercepted() {
        return preferences.getInt(LAST_SMS_PARSED, DEFAULT_SMS_PARSED_VALUE);
    }

    @Override
    public boolean isFirstSmsIntercepted() {
        return getLastSmsIntercepted() == DEFAULT_SMS_PARSED_VALUE;
    }
}