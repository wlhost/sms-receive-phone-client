package com.example.administrator.myapplication;

interface SmsStorage {
    void updateLastSmsIntercepted(int smsId);

    int getLastSmsIntercepted();

    boolean isFirstSmsIntercepted();
}
