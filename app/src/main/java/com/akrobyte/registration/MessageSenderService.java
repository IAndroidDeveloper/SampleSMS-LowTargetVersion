package com.akrobyte.registration;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
class MessageSenderService extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Called", Toast.LENGTH_SHORT).show();
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String message = intent.getStringExtra("message");

        // Send the message
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message,null,null);
        }

        Log.d("MessageSenderService", "Message sent to " + phoneNumber);
    }
}