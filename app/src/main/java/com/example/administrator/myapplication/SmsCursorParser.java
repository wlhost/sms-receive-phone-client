package com.example.administrator.myapplication;

import java.util.Date;

import android.database.Cursor;

class SmsCursorParser {
    private static final String ADDRESS_COLUMN_NAME = "address";
    private static final String DATE_COLUMN_NAME = "date";
    private static final String BODY_COLUMN_NAME = "body";
    private static final String TYPE_COLUMN_NAME = "type";
    private static final String ID_COLUMN_NAME = "_id";
    private static final int SMS_MAX_AGE_MILLIS = 5000;

    private SmsStorage smsStorage;
    private TimeProvider timeProvider;

    SmsCursorParser(SmsStorage smsStorage, TimeProvider timeProvider) {
        this.smsStorage = smsStorage;
        this.timeProvider = timeProvider;
    }

    Sms parse(Cursor cursor) {

        if (!canHandleCursor(cursor) || !cursor.moveToNext()) {
            return null;
        }

        Sms smsParsed = extractSmsInfoFromCursor(cursor);

        int smsId = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
        String date = cursor.getString(cursor.getColumnIndex(DATE_COLUMN_NAME));
        Date smsDate = new Date(Long.parseLong(date));

        if (shouldParseSms(smsId, smsDate)) {
            updateLastSmsParsed(smsId);
        } else {
            smsParsed = null;
        }

        return smsParsed;
    }

    private void updateLastSmsParsed(int smsId) {
        smsStorage.updateLastSmsIntercepted(smsId);
    }

    private boolean shouldParseSms(int smsId, Date smsDate) {
        boolean isFirstSmsParsed = isFirstSmsParsed();
        boolean isOld = isOld(smsDate);
        boolean shouldParseId = shouldParseSmsId(smsId);
        return (isFirstSmsParsed && !isOld) || (!isFirstSmsParsed && shouldParseId);
    }

    private boolean isOld(Date smsDate) {
        Date now = timeProvider.getDate();
        return now.getTime() - smsDate.getTime() > SMS_MAX_AGE_MILLIS;
    }

    private boolean shouldParseSmsId(int smsId) {
        if (smsStorage.isFirstSmsIntercepted()) {
            return false;
        }
        int lastSmsIdIntercepted = smsStorage.getLastSmsIntercepted();
        return smsId > lastSmsIdIntercepted;
    }

    private boolean isFirstSmsParsed() {
        return smsStorage.isFirstSmsIntercepted();
    }

    private Sms extractSmsInfoFromCursor(Cursor cursor) {
        String address = cursor.getString(cursor.getColumnIndex(ADDRESS_COLUMN_NAME));
        String date = cursor.getString(cursor.getColumnIndex(DATE_COLUMN_NAME));
        String msg = cursor.getString(cursor.getColumnIndex(BODY_COLUMN_NAME));
        String type = cursor.getString(cursor.getColumnIndex(TYPE_COLUMN_NAME));

        return new Sms(address, date, msg, SmsType.fromValue(Integer.parseInt(type)));
    }

    private boolean canHandleCursor(Cursor cursor) {
        return cursor != null && cursor.getCount() > 0;
    }
}
