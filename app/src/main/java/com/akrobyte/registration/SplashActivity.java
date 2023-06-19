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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anviam.vloader.R;

public class SplashActivity extends Activity {
    private static final int PERMISSION_SEND_SMS = 123;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    PendingIntent pendingIntent;
    AlarmManager manager;
    private static final String KEY_LAST_SAVED_TIME = "lastSavedTime";
    private static final String PREF_NAME = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_splash);
        requestSmsPermission();
        //registerReceiver(broadcastReceiver, new IntentFilter("ACTION_CUSTOM_BROADCAST"));
        Intent alarmIntent = new Intent(this, MessageSenderService.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sendSMS();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(broadcastReceiver);
    }

    private void requestSmsPermission() {

        // check permission is given
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                // request permission (see result in onRequestPermissionsResult() method)
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
            } else {
                // permission already granted run sms send
                sendSMS();
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
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            alertDialog.dismiss();
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

     /*    Intent intent;
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
        broadcastReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == -1) {
                    confirmUser();
                    return;
                }
                finish();
            }
        };*/
        scheduleCalendar();
    }

    private void scheduleCalendar() {


        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 2 * 60 * 60 * 1000L;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }


  /*  private void reminderAfter3DaysTest() {
        if (getSavedTime(sharedPreferences) == 0L) {
            saveCurrentTime(editor);
            scheduleCalendar();
            Log.i("reminderAfter3DaysTest", ":1:");
        } else {
            Log.i("reminderAfter3DaysTest", ":2:");
            if (hasThreeHourGap(getSavedTime(sharedPreferences))) {
                Log.i("reminderAfter3DaysTest", ":3:");
                saveCurrentTime(editor);
                scheduleCalendar();
            }
        }
    }*/

/*    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals("com.example.ACTION_MY_ACTION")) {
            // Extract any extra data from the intent if needed
            String value = intent.getStringExtra("key");

            // Call the desired function in your activity
            //reminderAfter3DaysTest();
        }
    }*/


   /* public static boolean hasThreeHourGap(long previousTimeMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        long timeDifferenceMillis = currentTimeMillis - previousTimeMillis;
        long threeHoursMillis = 3 * 60 * 60 * 1000L; // 3 hours in milliseconds

        return timeDifferenceMillis >= threeHoursMillis;
    }*/
}
