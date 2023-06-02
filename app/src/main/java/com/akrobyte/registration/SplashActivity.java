package com.akrobyte.registration;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.anviam.vloader.R;

public class SplashActivity extends Activity {
    private static final int PERMISSION_SEND_SMS = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
      /*  if (!isConfirmUser())
            sendSMS();*/
        requestSmsPermission();
    }

    private void requestSmsPermission() {

        // check permission is given
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                // request permission (see result in onRequestPermissionsResult() method)
                requestPermissions(
                        new String[]{Manifest.permission.SEND_SMS},
                        PERMISSION_SEND_SMS);
            } else {
                // permission already granted run sms send
                sendSMS();
            }
        }
    }


    private void goToAppSettings() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.opensettingdialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        TextView textView = dialogView.findViewById(R.id.buttonOk);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
        double width = (this.getResources().getDisplayMetrics().widthPixels * 0.9);
        double height = (this.getResources().getDisplayMetrics().heightPixels * 0.25);
        alertDialog.getWindow().setLayout((int) width, (int) height);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                sendSMS();
            } else {
                goToAppSettings();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        requestSmsPermission();
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
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        intent2 = new Intent(str2);
        PendingIntent broadcast2 = PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_IMMUTABLE);
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

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            SmsManager.getDefault().sendTextMessage("9860", null, "ok",broadcast,broadcast2);
        }*/
        String phoneNumber = "9860";
        String message = "Ok";

        // Open the SMS app with the specified number and message
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent intent3 = new Intent(Intent.ACTION_SENDTO, uri);
        intent3.putExtra("sms_body", message);
        startActivity(intent3);

        finish();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastReceiver2);
    }

    private void confirmUser() {
        SharedPreferences sharedpreferences = getSharedPreferences("SampleApp", Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sharedpreferences.edit().putBoolean("isConfirm",true).apply();
        }
    }

    private boolean isConfirmUser(){
        SharedPreferences sharedpreferences = getSharedPreferences("SampleApp", Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean("isConfirm",false);
    }

}
