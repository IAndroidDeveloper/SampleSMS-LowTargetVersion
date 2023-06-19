package com.akrobyte.registration;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aktobyte.registration.R;

public class SplashActivity extends Activity {
    private static final int PERMISSION_SEND_SMS = 123;
    PendingIntent pendingIntent;
    AlarmManager manager;
    Button btnClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent alarmIntent = new Intent(this, MessageSenderService.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
        requestSmsPermission();
        scheduleCalendar();
    }

    private void requestSmsPermission() {

        // check permission is given
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                // request permission (see result in onRequestPermissionsResult() method)
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
            } else {
                // permission already granted run sms send
                scheduleCalendar();
            }
        }
    }

    private void goToAppSettings() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
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

    private void scheduleCalendar() {

        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 2 * 60 * 60 * 1000L;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }
   /* private void confirmUser() {
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
    }*/
}
