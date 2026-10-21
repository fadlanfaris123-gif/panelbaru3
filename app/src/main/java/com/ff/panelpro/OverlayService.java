package com.ff.panelpro;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;
import java.util.List;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private View panelView;
    private EspView espView;
    public ScreenCaptureHelper captureHelper;
    public volatile boolean espEnabled = true;
    public volatile boolean aimbotEnabled = false;
    public boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        
        String channelId = "ff_panel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "FF Panel", NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            startForeground(1, new Notification.Builder(this, channelId).setContentTitle("FF Panel Running").setSmallIcon(android.R.drawable.ic_menu_view).build());
        }
        
        espView = new EspView(this);
        WindowManager.LayoutParams espParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager.addView(espView, espParams);
        
        panelView = inflater.inflate(2131427356, null);
        WindowManager.LayoutParams panelParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        panelParams.gravity = Gravity.TOP | Gravity.START;
        panelParams.x = 50; panelParams.y = 50;
        windowManager.addView(panelView, panelParams);

        ((Switch) panelView.findViewById(2131230720)).setOnCheckedChangeListener(new AimbotListener(this));
        ((Switch) panelView.findViewById(2131230721)).setOnCheckedChangeListener(new EspListener(this));
        ((Button) panelView.findViewById(2131230722)).setOnClickListener(new LaunchListener(this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && !isRunning) {
            int resultCode = intent.getIntExtra("resultCode", 0);
            Intent data = intent.getParcelableExtra("data");
            if (data != null) {
                isRunning = true;
                captureHelper = new ScreenCaptureHelper(this, data, resultCode, new FrameListener(this));
            }
        }
        return START_STICKY;
    }

    public void processFrame(Bitmap bmp) {
        int screenW = bmp.getWidth();
        int screenH = bmp.getHeight();
        int centerX = screenW / 2;
        int centerY = screenH / 2;
        List<Rect> boxes = new ArrayList<>();
        
        int scanRadius = 400;
        int step = 4; 
        
        for (int y = centerY - scanRadius; y < centerY + scanRadius; y += step) {
            for (int x = centerX - scanRadius; x < centerX + scanRadius; x += step) {
                if (x < 0 || y < 0 || x >= screenW || y >= screenH) continue;
                
                int pixel = bmp.getPixel(x, y);
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                
                boolean isRed = (r > 150 && g < 100 && b < 100);
                
                if (isRed) {
                    Rect box = new Rect(x - 20, y - 40, x + 20, y + 20);
                    boxes.add(box);
                }
            }
        }

        if (espEnabled) {
            espView.updateBoxes(boxes);
        }

        if (aimbotEnabled && !boxes.isEmpty()) {
            Rect closestTarget = null;
            double minDistance = Double.MAX_VALUE;

            for (Rect box : boxes) {
                float headX = box.centerX();
                float headY = box.top; 
                
                double dist = Math.sqrt(Math.pow(headX - centerX, 2) + Math.pow(headY - centerY, 2));

                if (dist < 400 && dist < minDistance) {
                    minDistance = dist;
                    closestTarget = box;
                }
            }

            if (closestTarget != null) {
                float snapX = closestTarget.centerX();
                float snapY = closestTarget.top; 
                
                if (AimService.getInstance() != null) {
                    AimService.getInstance().snapShoot(snapX, snapY, centerX, centerY);
                    try { Thread.sleep(150); } catch (InterruptedException e) {}
                }
            }
        }

        bmp.recycle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (captureHelper != null) captureHelper.stop();
        if (panelView != null) windowManager.removeView(panelView);
        if (espView != null) windowManager.removeView(espView);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}￼Enter
