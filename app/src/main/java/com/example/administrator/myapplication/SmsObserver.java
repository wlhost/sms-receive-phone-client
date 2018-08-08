package com.example.administrator.myapplication;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

class SmsObserver extends ContentObserver {

    private static final Uri SMS_URI = Uri.parse("content://sms/");
    private static final Uri SMS_SENT_URI = Uri.parse("content://sms/sent");
    private static final Uri SMS_INBOX_URI = Uri.parse("content://sms/inbox");
    private static final String PROTOCOL_COLUM_NAME = "protocol";
    private static final String SMS_ORDER = "date DESC";

    private ContentResolver contentResolver;
    private SmsCursorParser smsCursorParser;

    SmsObserver(ContentResolver contentResolver, Handler handler, SmsCursorParser smsCursorParser) {
        super(handler);
        this.contentResolver = contentResolver;
        this.smsCursorParser = smsCursorParser;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Cursor cursor = null;
        try {
            cursor = getSmsContentObserverCursor();
            if (cursor != null && cursor.moveToFirst()) {
                processSms(cursor);
            }
        } finally {
            close(cursor);
        }
    }

    private void processSms(Cursor cursor) {
        Cursor smsCursor = null;
        try {
            String protocol = cursor.getString(cursor.getColumnIndex(PROTOCOL_COLUM_NAME));
            smsCursor = getSmsCursor(protocol);
            Sms sms = parseSms(smsCursor);
            notifySmsListener(sms);
        } finally {
            close(smsCursor);
        }
    }

    private void notifySmsListener(Sms sms) {
        if (sms != null && SmsRadar.smsListener != null) {
            if (SmsType.SENT == sms.getType()) {
                SmsRadar.smsListener.onSmsSent(sms);
            } else {
                SmsRadar.smsListener.onSmsReceived(sms);
            }
        }
    }

    private Cursor getSmsCursor(String protocol) {
        return getSmsDetailsCursor(protocol);
    }

    private Cursor getSmsDetailsCursor(String protocol) {
        Cursor smsCursor;
        if (isProtocolForOutgoingSms(protocol)) {
            //SMS Sent
            smsCursor = getSmsDetailsCursor(SmsContext.SMS_SENT.getUri());
        } else {
            //SMSReceived
            smsCursor = getSmsDetailsCursor(SmsContext.SMS_RECEIVED.getUri());
        }
        return smsCursor;
    }

    private Cursor getSmsContentObserverCursor() {
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        return contentResolver.query(SMS_URI, projection, selection, selectionArgs, sortOrder);
    }

    private boolean isProtocolForOutgoingSms(String protocol) {
        return protocol == null;
    }

    private Cursor getSmsDetailsCursor(Uri smsUri) {

        return smsUri != null ? this.contentResolver.query(smsUri, null, null, null, SMS_ORDER) : null;
    }

    private Sms parseSms(Cursor cursor) {
        return smsCursorParser.parse(cursor);
    }

    private void close(Cursor cursor) {
        if (cursor != null && !cursor.isClosed())
            cursor.close();
    }

    /**
     * Represents the SMS origin.
     */
    private enum SmsContext {
        SMS_SENT {
            @Override
            Uri getUri() {
                return SMS_SENT_URI;
            }
        }, SMS_RECEIVED {
            @Override
            Uri getUri() {
                return SMS_INBOX_URI;
            }
        };

        abstract Uri getUri();
    }
}
