package com.example.administrator.myapplication;

public interface SmsListener {

    /**
     * Invoked when an incoming sms is intercepted.
     *
     * @param sms intercepted.
     */
    public void onSmsSent(Sms sms);

    /**
     * Invoked when an outgoing sms is intercepted.
     *
     * @param sms
     */
    public void onSmsReceived(Sms sms);

}