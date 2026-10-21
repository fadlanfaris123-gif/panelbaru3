package com.ff.panelpro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.nio.ByteBuffer;

public class ScreenCaptureHelper {
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private int width, height, density;
    public OnFrameCapturedListener listener;

    public interface OnFrameCapturedListener { void onFrame(Bitmap bitmap); }

    public ScreenCaptureHelper(Context context, Intent resultIntent, int resultCode, OnFrameCapturedListener listener) {
        this.listener = listener;
        MediaProjectionManager mpm = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mediaProjection = mpm.getMediaProjection(resultCode, resultIntent);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metrics);
        width = metrics.widthPixels; height = metrics.heightPixels; density = metrics.densityDpi;
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
        virtualDisplay = mediaProjection.createVirtualDisplay("FFScreenCapture", width, height, density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(), null, null);
        
        imageReader.setOnImageAvailableListener(new ImageListener(this), new android.os.Handler(android.os.Looper.getMainLooper()));
    }

    public void processImage(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        if (image != null) {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int rowPadding = planes[0].getRowStride() - planes[0].getPixelStride() * width;
            Bitmap bmp = Bitmap.createBitmap(width + rowPadding / planes[0].getPixelStride(), height, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(buffer);
            image.close();
            if (listener != null) listener.onFrame(bmp);
        }
    }

    public void stop() {
        if (virtualDisplay != null) virtualDisplay.release();
        if (imageReader != null) imageReader.close();
        if (mediaProjection != null) mediaProjection.stop();
    }
}
