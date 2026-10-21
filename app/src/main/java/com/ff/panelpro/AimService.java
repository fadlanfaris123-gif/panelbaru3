package com.ff.panelpro;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;

public class AimService extends AccessibilityService {
    private static AimService instance;
    public static AimService getInstance() { return instance; }
    
    private float pendingTargetX;
    private float pendingTargetY;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    public void snapShoot(float targetX, float targetY, int centerX, int centerY) {
        Path swipePath = new Path();
        swipePath.moveTo(centerX, centerY); 
        swipePath.lineTo(targetX, targetY);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 1)); 
        dispatchGesture(builder.build(), null, null);

        pendingTargetX = targetX;
        pendingTargetY = targetY;

        new Handler(Looper.getMainLooper()).postDelayed(new TapRunnable(this), 5);
    }

    public void doTap() {
        Path tapPath = new Path();
        tapPath.moveTo(pendingTargetX, pendingTargetY);
        GestureDescription.Builder tapBuilder = new GestureDescription.Builder();
        tapBuilder.addStroke(new GestureDescription.StrokeDescription(tapPath, 0, 1));
        dispatchGesture(tapBuilder.build(), null, null);
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}
}
