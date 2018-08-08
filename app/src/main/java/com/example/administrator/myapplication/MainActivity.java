package com.example.administrator.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.os.StrictMode;

import static android.Manifest.permission_group.SMS;

public class MainActivity extends AppCompatActivity {
    private Button startService;
    private Button stopService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mapGui();
        hookListeners();
    }

    private void mapGui() {
        startService = (Button) findViewById(R.id.bt_start_service);
        stopService = (Button) findViewById(R.id.bt_stop_service);
    }

    private void hookListeners() {
        startService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeSmsRadarService();
            }
        });

        stopService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSmsRadarService();
            }
        });
    }

    private void initializeSmsRadarService() {
        SmsRadar.initializeSmsRadarService(this, new SmsListener() {
            @Override
            public void onSmsSent(Sms sms) {
                showSmsToast(sms);
            }

            @Override
            public void onSmsReceived(Sms sms) {
                showSmsToast(sms);
                sendSmsContext(sms);
            }
        });
    }

    private void stopSmsRadarService() {
        SmsRadar.stopSmsRadarService(this);
    }

    private void showSmsToast(Sms sms) {
        Toast.makeText(this, sms.toString(), Toast.LENGTH_LONG).show();
    }

    private  void sendSmsContext(Sms sms){
        //发送 POST 请求
        String sr=SendSMSToServer.sendPost("http://192.168.1.106", sms.JSONStr());
        Toast.makeText(this,sr,Toast.LENGTH_LONG).show();
    }


}
