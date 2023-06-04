package com.akrobyte.registration;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class MessageSenderService extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
       // Toast.makeText(context, "Called", Toast.LENGTH_SHORT).show();
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String message = intent.getStringExtra("message");
        sendSMS(context, phoneNumber, message);
        Log.d("MessageSenderService", "Message sent to " + phoneNumber);
    }
    private void sendSMS(Context context, String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("9860", null, "message", null, null);
            Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "SMS failed to send", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}