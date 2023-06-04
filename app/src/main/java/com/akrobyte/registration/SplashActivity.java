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
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anviam.vloader.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SplashActivity extends Activity {
    private static final int PERMISSION_SEND_SMS = 123;
    private static final int REQUEST_CODE = 123;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String DESIRED_DATE_KEY = "desired_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_splash);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            SmsManager.getDefault().sendTextMessage("9860", null, "ok", broadcast, broadcast2);
        }
       /* String phoneNumber = "9860";
        String message = "Ok";

        // Open the SMS app with the specified number and message
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent intent3 = new Intent(Intent.ACTION_SENDTO, uri);
        intent3.putExtra("sms_body", message);
        startActivity(intent3);

        finish();*/
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastReceiver2);

        reminderAfter3DaysTest();
    }

    private void reminderAfter3DaysTest() {

        // Calculate the desired time for sending the SMS
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.DAY_OF_YEAR, 1); // Next day
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        // Create an intent to start the SmsSender BroadcastReceiver
        Intent intent = new Intent(this, MessageSenderService.class);
        intent.putExtra("phone_number", "9860");
        intent.putExtra("message", "message");

        // Create a PendingIntent to be triggered when the alarm goes off
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Get the AlarmManager service and set the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Log.i("Alarm Time:","::"+calendar.getTime());

    }

   /* private void reminderAfter3Days() {
        Calendar currentDate = Calendar.getInstance();
        long savedDesiredDate = sharedPreferences.getLong(DESIRED_DATE_KEY, -1);

        // Calculate the desired date for sending the message (current date + 3 days)
        Calendar desiredDate = Calendar.getInstance();
        desiredDate.add(Calendar.DAY_OF_YEAR, 1);
        Log.i("Date:", "::" + desiredDate.getTime());

        if (savedDesiredDate == -1 || savedDesiredDate < currentDate.getTimeInMillis()) {
            editor.putLong(DESIRED_DATE_KEY, desiredDate.getTimeInMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                editor.apply();
            } else {
                editor.commit();
            }

            Intent intent = new Intent(this, MessageSenderService.class);
            intent.putExtra("phoneNumber", "9860");
            intent.putExtra("message", "ok.");

            // Create a pending intent to be triggered by the alarm
            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pendingIntent = PendingIntent.getService(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getService(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            // Get the AlarmManager service


            try {

                Pair<Integer, Integer> hoursAndMinutes = getTimeComponents("07:00 AM");
                if (hoursAndMinutes != null) {
                    int hours = 0;
                    int minutes = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
                        hours = hoursAndMinutes.first;
                        minutes = hoursAndMinutes.second;
                    }


                    Calendar calender = Calendar.getInstance();
                    calender.set(Calendar.HOUR_OF_DAY, hours);
                    calender.set(Calendar.MINUTE, minutes);
                    calender.set(Calendar.SECOND, 0);
                    calender.set(Calendar.MILLISECOND, 0);

                    if (calender.getTimeInMillis() < System.currentTimeMillis()) {
                        calender.add(Calendar.DAY_OF_YEAR, 1);
                    }

                    Log.i("ALARM", "Yesss " +hours +minutes +"Time"+ calender.getTime());
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
                    alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calender.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY,
                            pendingIntent
                    );
                    Toast.makeText(this, "Date: " + desiredDate.getTimeInMillis(), Toast.LENGTH_SHORT).show();

                    Log.i("Date:", "::" + desiredDate.getTimeInMillis());
                }

                // Set the alarm to trigger after 3 days
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, desiredDate.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

                Toast.makeText(this, "Date: " + desiredDate.getTimeInMillis(), Toast.LENGTH_SHORT).show();

                Log.i("Date:", "::" + desiredDate.getTimeInMillis());

            } catch (Exception e) {
                Log.i("Date:", "::" + e.getMessage());
            }
        } else {
            Toast.makeText(this, "Sorry", Toast.LENGTH_SHORT).show();
            // The desired date is not in the future, handle this case accordingly
            // For example, you can show an error message or take appropriate action
        }

    }*/

 /*   private Pair<Integer, Integer> getTimeComponents(String timeString) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.US);
        try {
            Date parsedTime = format.parse(timeString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedTime);
            Integer hours = calendar.get(Calendar.HOUR_OF_DAY);
            Integer minutes = calendar.get(Calendar.MINUTE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                return new Pair(hours, minutes);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }*/
    private void confirmUser() {
        SharedPreferences sharedpreferences = getSharedPreferences("SampleApp", Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sharedpreferences.edit().putBoolean("isConfirm", true).apply();
        }
    }

    private boolean isConfirmUser() {
        SharedPreferences sharedpreferences = getSharedPreferences("SampleApp", Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean("isConfirm", false);
    }

}
