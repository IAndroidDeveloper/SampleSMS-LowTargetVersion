package com.akrobyte.registration;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

    public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = MyAccessibilityService.class
            .getSimpleName();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(TAG, "ACC::onAccessibilityEvent: " + event.getEventType());

        //TYPE_WINDOW_STATE_CHANGED == 32
        if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event
                .getEventType()) {
            AccessibilityNodeInfo nodeInfo = event.getSource();
            Log.i(TAG, "ACC::onAccessibilityEvent: nodeInfo=" + nodeInfo);
            if (nodeInfo == null) {
                return;
            }

            List<AccessibilityNodeInfo> list = nodeInfo
                    .findAccessibilityNodeInfosByViewId("com.android.settings:id/left_button");
            for (AccessibilityNodeInfo node : list) {
                Log.i(TAG, "ACC::onAccessibilityEvent: left_button " + node);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            list = nodeInfo
                    .findAccessibilityNodeInfosByViewId("android:id/button1");
            for (AccessibilityNodeInfo node : list) {
                Log.i(TAG, "ACC::onAccessibilityEvent: button1 " + node);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

    }

    @Override
    public void onServiceConnected() {
        Log.i(TAG, "ACC::onServiceConnected: ");
    }

    @Override
    public void onInterrupt() {
        // TODO Auto-generated method stub

    }
}