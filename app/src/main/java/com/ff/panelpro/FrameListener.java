package com.ff.panelpro;

import android.graphics.Bitmap;

public class FrameListener implements ScreenCaptureHelper.OnFrameCapturedListener {
    private OverlayService service;
    public FrameListener(OverlayService service) { this.service = service; }
    @Override public void onFrame(Bitmap bitmap) { service.processFrame(bitmap); }
}￼Enter
